import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, logout, getCurrentUserInfo, refreshToken as refreshAuthToken, type LoginParams, type UserInfo } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>(localStorage.getItem('token') || '')
  const refreshToken = ref<string>(localStorage.getItem('refreshToken') || '')
  const userInfo = ref<UserInfo | null>(
    localStorage.getItem('userInfo') ? JSON.parse(localStorage.getItem('userInfo')!) : null
  )
  const loading = ref(false)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'admin')

  // 方法
  /**
   * 用户登录
   */
  async function handleLogin(loginParams: LoginParams): Promise<boolean> {
    try {
      loading.value = true
      const response = await login(loginParams)

      // 保存token
      token.value = response.token
      refreshToken.value = response.refreshToken
      localStorage.setItem('token', response.token)
      localStorage.setItem('refreshToken', response.refreshToken)

      // 登录成功后获取用户信息
      await fetchUserInfo()

      return true
    } catch (error: any) {
      throw new Error(error.message || '登录失败')
    } finally {
      loading.value = false
    }
  }

  /**
   * 用户登出
   */
  async function handleLogout(): Promise<void> {
    try {
      await logout()
    } catch (error) {
      // 即使登出接口失败，也要清除本地数据
    }

    // 清除本地数据
    token.value = ''
    refreshToken.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('userInfo')
  }

  /**
   * 获取用户信息
   */
  async function fetchUserInfo(): Promise<void> {
    try {
      const response = await getCurrentUserInfo()
      userInfo.value = response
      localStorage.setItem('userInfo', JSON.stringify(response))
    } catch (error: any) {
      throw new Error(error.message || '获取用户信息失败')
    }
  }

  /**
   * 刷新访问Token
   */
  async function refreshAccessToken(): Promise<boolean> {
    try {
      if (!refreshToken.value) {
        throw new Error('没有刷新Token')
      }

      loading.value = true
      const response = await refreshAuthToken(refreshToken.value)

      // 更新token
      token.value = response.token
      refreshToken.value = response.refreshToken
      localStorage.setItem('token', response.token)
      localStorage.setItem('refreshToken', response.refreshToken)

      return true
    } catch (error: any) {
      // 刷新失败，清除所有数据
      await handleLogout()
      throw new Error('Token刷新失败，请重新登录')
    } finally {
      loading.value = false
    }
  }

  /**
   * 初始化用户信息（页面刷新时调用）
   */
  async function initUserInfo(): Promise<void> {
    if (token.value && !userInfo.value) {
      try {
        await fetchUserInfo()
      } catch (error) {
        // 如果获取用户信息失败，尝试刷新token
        try {
          await refreshAccessToken()
          await fetchUserInfo()
        } catch (refreshError) {
          // 刷新也失败，清除数据
          await handleLogout()
        }
      }
    }
  }

  return {
    // 状态
    token,
    refreshToken,
    userInfo,
    loading,

    // 计算属性
    isLoggedIn,
    isAdmin,

    // 方法
    handleLogin,
    handleLogout,
    fetchUserInfo,
    refreshAccessToken,
    initUserInfo
  }
})