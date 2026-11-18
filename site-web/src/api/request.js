import axios from 'axios'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '',
  timeout: 10000
})

service.interceptors.response.use(
  response => {
    const body = response.data
    if (body && typeof body.code !== 'undefined' && body.code !== 200) {
      const error = new Error(body.msg || '请求失败')
      error.code = body.code
      return Promise.reject(error)
    }
    return body && Object.prototype.hasOwnProperty.call(body, 'data') ? body.data : body
  },
  error => {
    let message = error.message || '网络异常'
    if (message === 'Network Error') {
      message = '后端服务不可用'
    } else if (message.indexOf('timeout') > -1) {
      message = '请求超时'
    }
    return Promise.reject(new Error(message))
  }
)

export default service
