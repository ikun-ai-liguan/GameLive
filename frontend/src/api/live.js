import request from './request'

// 直播间列表（分类筛选 + 关键词搜索 + 分页）
export const listRooms = (params) => request.get('/rooms', { params })

// 直播间详情
export const getRoom = (id) => request.get(`/rooms/${id}`)

// 创建直播间
export const createRoom = (data) => request.post('/rooms', data)

// 开播 / 下播
export const startLive = (id) => request.post(`/rooms/${id}/start`)
export const stopLive = (id) => request.post(`/rooms/${id}/stop`)

// 回放列表
export const listReplays = () => request.get('/rooms/replays')

// 游戏分类
export const listCategories = () => request.get('/categories')

// 礼物字典 / 发送礼物
export const listGifts = () => request.get('/gifts')
export const sendGift = (data) => request.post('/gifts/send', data)

// 历史弹幕
export const listDanmu = (roomId) => request.get(`/rooms/${roomId}/danmu`)

// ---------- 管理后台 ----------
export const adminListUsers = (params) => request.get('/admin/users', { params })
export const adminBanUser = (id, banned, reason) =>
  request.post(`/admin/users/${id}/ban`, null, { params: { banned, reason } })
export const adminListRooms = (params) => request.get('/admin/rooms', { params })
export const adminAuditRoom = (id, approved, reason) =>
  request.post(`/admin/rooms/${id}/audit`, null, { params: { approved, reason } })
export const adminStatistics = () => request.get('/admin/statistics')
