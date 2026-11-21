import { defineStore } from 'pinia'

const STORAGE_KEY = 'gamelive_user'

/**
 * 当前登录用户状态（骨架阶段直接存 localStorage，后续替换为 Token 鉴权）。
 */
export const useUserStore = defineStore('user', {
  state: () => {
    const cached = JSON.parse(localStorage.getItem(STORAGE_KEY) || '{}')
    return {
      token: cached.token || '',
      userId: cached.userId || null,
      nickname: cached.nickname || ''
    }
  },
  actions: {
    setUser({ token, userId, nickname }) {
      this.token = token
      this.userId = userId
      this.nickname = nickname
      localStorage.setItem(STORAGE_KEY, JSON.stringify({ token, userId, nickname }))
    },
    logout() {
      this.token = ''
      this.userId = null
      this.nickname = ''
      localStorage.removeItem(STORAGE_KEY)
    }
  }
})
