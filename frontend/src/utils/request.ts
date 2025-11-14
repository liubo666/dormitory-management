import axios, {
    type InternalAxiosRequestConfig,
    type AxiosResponse,
    type AxiosError,
    type AxiosRequestConfig
} from 'axios'
import type { AxiosInstance as AxiosInstanceType } from 'axios'
import { ElMessage } from 'element-plus'

// 接口响应数据格式
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 创建axios实例
const service: AxiosInstanceType = axios.create({
  baseURL: '/api',
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
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          localStorage.removeItem('refreshToken')
          ElMessage.error('登录已过期，请重新登录')
          window.location.href = '/login'
          break
        case 403:
          errorMessage = errorMessage || '权限不足'
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