import request from './request'

/** 直播间点赞 */
export const likeRoom = (roomId) => request.post(`/rooms/${roomId}/like`)

/** 当前用户是否已点赞该直播间 */
export const isRoomLiked = (roomId) => request.get(`/rooms/${roomId}/liked`)

/** 记录观看历史 */
export const recordHistory = (roomId) => request.post(`/rooms/${roomId}/history`)

/** 我的观看历史（分页） */
export const listHistory = (params) => request.get('/users/history', { params })

/** 我关注的主播列表 */
export const listFollows = () => request.get('/users/follows')

/** 直播间礼物榜 */
export const getGiftRank = (roomId) => request.get(`/rooms/${roomId}/gift-rank`)

/** 提交举报：{ targetType: 'ROOM' | 'DANMU', targetId, reason } */
export const createReport = (data) => request.post('/reports', data)

/** 站内通知（分页） */
export const listNotifications = (params) => request.get('/notifications', { params })

/** 标记通知已读 */
export const readNotification = (id) => request.post(`/notifications/${id}/read`)
