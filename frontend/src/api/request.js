import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getApiBaseUrl } from '@/config'

const request = axios.create({
  timeout: 15000
})

// 请求拦截器：每次请求都从运行时配置解析 baseURL，并用 JWT 鉴权
request.interceptors.request.use((config) => {
  config.baseURL = getApiBaseUrl()
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers.Authorization = `Bearer ${userStore.token}`
  }
  return config
})

// 响应拦截器：统一解包 Result，同时兼容后端直接返回字符串/对象的情况
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res && typeof res === 'object' && Object.prototype.hasOwnProperty.call(res, 'code')) {
      if (res.code === 200) {
        return res.data
      }
      const message = res.message || '请求失败'
      ElMessage.error(message)
      return Promise.reject(new Error(message))
    }
    // raw string（如 JWT）或普通对象直接透传
    return res
  },
  (error) => {
    const status = error.response && error.response.status
    if (status === 401) {
      try {
        useUserStore().logout()
      } catch (e) {
        // Pinia 尚未初始化时忽略
      }
    }
    const message =
      (error.response && error.response.data && error.response.data.message) ||
      error.message ||
      '网络异常'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request
