import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000
})

// 请求拦截器：携带当前用户 ID（骨架阶段，后续替换为 Token）
request.interceptors.request.use((config) => {
  const userStore = useUserStore()
  if (userStore.userId) {
    config.headers['X-User-Id'] = userStore.userId
  }
  return config
})

// 响应拦截器：统一解包 Result 结构
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res.data
    }
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    ElMessage.error(error.message || '网络异常')
    return Promise.reject(error)
  }
)

export default request
