import request from './request'
import { useUserStore } from '@/stores/user'

export const register = (data) => request.post('/auth/register', data)

export const getProfile = () => request.get('/users/me')

export const updateProfile = (data) => request.put('/users/me', data)

export const followAnchor = (anchorId) => request.post(`/users/follow/${anchorId}`)

export const unfollowAnchor = (anchorId) => request.delete(`/users/follow/${anchorId}`)

export const listFans = (anchorId) => request.get(`/users/${anchorId}/fans`)

/** base64url 解码 JWT 中段 payload，作为 /users/me 失败时的兜底 */
const decodeJwtPayload = (token) => {
  try {
    const segment = String(token).split('.')[1]
    if (!segment) return null
    const base64 = segment.replace(/-/g, '+').replace(/_/g, '/')
    const padded = base64.padEnd(Math.ceil(base64.length / 4) * 4, '=')
    const binary = atob(padded)
    const bytes = Uint8Array.from(binary, (ch) => ch.charCodeAt(0))
    return JSON.parse(new TextDecoder().decode(bytes))
  } catch (error) {
    return null
  }
}

/**
 * 登录：后端返回 Result<String>（裸 JWT 字符串）。
 * 先保存 token，再请求 /users/me 补全 userId/nickname；
 * 若 /users/me 失败则解码 JWT payload 兜底，最终统一返回 { token, userId, nickname }。
 */
export const login = async (data) => {
  const result = await request.post('/auth/login', data)
  const token = typeof result === 'string' ? result : result && (result.token || result.data)
  if (!token) {
    throw new Error('登录失败：未获取到令牌')
  }

  const userStore = useUserStore()
  userStore.setToken(token)

  let profile = null
  try {
    profile = await getProfile()
  } catch (error) {
    profile = null
  }

  const payload = decodeJwtPayload(token) || {}
  const fallbackId = payload.sub || payload.userId || payload.uid || null
  const normalized = {
    token,
    userId:
      profile && profile.id
        ? profile.id
        : fallbackId !== null && Number.isFinite(Number(fallbackId))
          ? Number(fallbackId)
          : fallbackId,
    nickname: (profile && profile.nickname) || payload.nickname || payload.nick || ''
  }
  userStore.setUser(normalized)
  return normalized
}
