import { defineStore } from 'pinia'

const STORAGE_KEY = 'gamelive_user'

const readCache = () => {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY) || '{}')
  } catch (error) {
    return {}
  }
}

/**
 * 当前登录用户状态。JWT token 持久化在 localStorage，
 * 所有请求由 axios 拦截器以 Authorization: Bearer <token> 携带。
 */
export const useUserStore = defineStore('user', {
  state: () => {
    const cached = readCache()
    return {
      token: cached.token || '',
      userId: cached.userId || null,
      nickname: cached.nickname || ''
    }
  },
  getters: {
    isLoggedIn: (state) => Boolean(state.token)
  },
  actions: {
    setUser({ token, userId, nickname } = {}) {
      this.token = token || ''
      this.userId = userId || null
      this.nickname = nickname || ''
      this.persist()
    },
    // 登录时先存 token，随后再请求 /users/me 补全用户信息
    setToken(token) {
      this.token = token || ''
      this.persist()
    },
    logout() {
      this.token = ''
      this.userId = null
      this.nickname = ''
      localStorage.removeItem(STORAGE_KEY)
    },
    persist() {
      localStorage.setItem(
        STORAGE_KEY,
        JSON.stringify({ token: this.token, userId: this.userId, nickname: this.nickname })
      )
    }
  }
})
