import axios, {
    type InternalAxiosRequestConfig,
    type AxiosResponse,
    type AxiosError,
    type AxiosRequestConfig
} from 'axios'
import type { AxiosInstance as AxiosInstanceType } from 'axios'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

// 接口响应数据格式
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 分页响应数据格式
export interface PageResponse<T = any> {
  records: T[]
  total: number
  current: number
  size: number
}

// 处理token过期的通用函数
const handleTokenExpired = (message: string) => {
  // 防止多次调用
  if (window.sessionStorage.getItem('token-expired-handled') === 'true') {
    return
  }

  // 标记已处理，避免重复处理
  window.sessionStorage.setItem('token-expired-handled', 'true')

  // 清除所有认证相关的本地存储
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  localStorage.removeItem('refreshToken')

  // 显示错误提示
  ElMessage.error(message)

  // 延迟跳转，确保提示消息能够显示
  setTimeout(() => {
    // 检查当前是否已经在登录页面，避免重复跳转
    if (window.location.pathname !== '/login') {
      window.location.href = '/login'
    }
    // 清除处理标记
    window.sessionStorage.removeItem('token-expired-handled')
  }, 1500)
}

// 创建axios实例
const service: AxiosInstanceType = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 添加JWT token到请求头
    const token = localStorage.getItem('token')
    if (token) {
      config.headers = config.headers || {}
      // 使用标准的Authorization Bearer token
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error: AxiosError) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const { code, message, data } = response.data

    // 请求成功
    if (code === 200) {
      return data
    }

    // 业务错误，直接抛出错误让组件处理
    return Promise.reject(new Error(message || '请求失败'))
  },
  (error: AxiosError<ApiResponse>) => {
    const { response } = error

    if (response) {
      const { status, data } = response

      // 尝试获取错误信息，优先使用后端返回的具体错误信息
      let errorMessage = '请求失败'

      if (data) {
          if (typeof data === 'object') {
              if (data.message) {
                  errorMessage = data.message
              }
          }
      }

      switch (status) {
        case 401:
          // 未授权，清除token并跳转到登录页
          handleTokenExpired('登录已过期，请重新登录')
          break
        case 403:
          // 权限不足或token过期，清除token并跳转到登录页
          handleTokenExpired(errorMessage || '登录已过期，请重新登录')
          break
        case 404:
          errorMessage = errorMessage || '请求的资源不存在'
          break
        case 500:
          errorMessage = errorMessage || '服务器内部错误'
          break
        default:
          errorMessage = errorMessage || `请求失败，状态码：${status}`
          break
      }

      return Promise.reject(new Error(errorMessage))
    } else if (error.code === 'ECONNABORTED') {
      return Promise.reject(new Error('请求超时，请检查网络连接'))
    } else {
      return Promise.reject(new Error('网络错误，请检查网络连接'))
    }
  }
)

// 封装请求方法
export const request = {
  get<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return service.get(url, config)
  },

  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return service.post(url, data, config)
  },

  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return service.put(url, data, config)
  },

  delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return service.delete(url, config)
  },

  patch<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return service.patch(url, data, config)
  }
}

export default service