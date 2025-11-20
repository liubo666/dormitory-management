import { ElMessage } from 'element-plus'

/**
 * 错误处理工具类
 */
export class ErrorHandler {
  /**
   * 处理API错误
   * @param error 错误对象
   * @param defaultMessage 默认错误信息
   */
  static handleApiError(error: any, defaultMessage: string = '操作失败') {
    let errorMessage = defaultMessage

    // 尝试从错误对象中提取具体错误信息
    if (error) {
      if (typeof error === 'string') {
        errorMessage = error
      } else if (error.message) {
        errorMessage = error.message
      } else if (error.data?.message) {
        errorMessage = error.data.message
      } else if (error.response?.data?.message) {
        errorMessage = error.response.data.message
      }
    }

    // 显示错误消息
    ElMessage.error(errorMessage)

    return errorMessage
  }

  /**
   * 处理表单验证错误
   * @param error 错误对象
   * @param defaultMessage 默认错误信息
   */
  static handleValidationError(error: any, defaultMessage: string = '表单验证失败') {
    let errorMessage = defaultMessage

    if (error) {
      if (typeof error === 'string') {
        errorMessage = error
      } else if (error.message) {
        errorMessage = error.message
      }
    }

    ElMessage.error(errorMessage)
    return errorMessage
  }

  /**
   * 处理网络错误
   * @param error 错误对象
   */
  static handleNetworkError(error: any) {
    let errorMessage = '网络连接失败，请检查网络设置'

    if (error?.code === 'ECONNABORTED') {
      errorMessage = '请求超时，请稍后重试'
    } else if (error?.message?.includes('Network Error')) {
      errorMessage = '网络连接失败，请检查网络设置'
    } else if (error?.message) {
      errorMessage = error.message
    }

    ElMessage.error(errorMessage)
    return errorMessage
  }

  /**
   * 处理权限错误
   * @param error 错误对象
   */
  static handleAuthError(error: any) {
    let errorMessage = '权限验证失败'

    if (error) {
      if (typeof error === 'string') {
        errorMessage = error
      } else if (error.message) {
        errorMessage = error.message
      }
    }

    ElMessage.error(errorMessage)

    // 如果是权限错误，清除本地存储的用户信息
    if (errorMessage.includes('未授权') || errorMessage.includes('登录已过期')) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('refreshToken')
      window.location.href = '/login'
    }

    return errorMessage
  }

  /**
   * 显示成功消息
   * @param message 成功消息
   */
  static showSuccess(message: string) {
    ElMessage.success(message)
  }

  /**
   * 显示警告消息
   * @param message 警告消息
   */
  static showWarning(message: string) {
    ElMessage.warning(message)
  }

  /**
   * 显示信息消息
   * @param message 信息消息
   */
  static showInfo(message: string) {
    ElMessage.info(message)
  }
}

/**
 * 错误处理的Hook函数
 */
export function useErrorHandler() {
  const handleError = ErrorHandler.handleApiError
  const handleValidationError = ErrorHandler.handleValidationError
  const handleNetworkError = ErrorHandler.handleNetworkError
  const handleAuthError = ErrorHandler.handleAuthError
  const showSuccess = ErrorHandler.showSuccess
  const showWarning = ErrorHandler.showWarning
  const showInfo = ErrorHandler.showInfo

  return {
    handleError,
    handleValidationError,
    handleNetworkError,
    handleAuthError,
    showSuccess,
    showWarning,
    showInfo
  }
}

export default ErrorHandler