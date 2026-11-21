import request from './request'

export const register = (data) => request.post('/auth/register', data)

export const login = (data) => request.post('/auth/login', data)

export const getProfile = () => request.get('/users/me')

export const updateProfile = (data) => request.put('/users/me', data)

export const followAnchor = (anchorId) => request.post(`/users/follow/${anchorId}`)

export const unfollowAnchor = (anchorId) => request.delete(`/users/follow/${anchorId}`)

export const listFans = (anchorId) => request.get(`/users/${anchorId}/fans`)
