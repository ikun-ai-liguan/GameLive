<template>
  <div class="room" v-loading="loading">
    <el-row :gutter="16">
      <!-- 播放器区域 -->
      <el-col :xs="24" :lg="17">
        <el-card>
          <div class="player-wrap">
            <video ref="videoRef" class="player" controls autoplay playsinline muted></video>
            <div v-if="!playing" class="player-placeholder">
              <el-icon><VideoCamera /></el-icon>
              <span>暂无直播流（主播开播后自动播放）</span>
            </div>
          </div>
          <div class="room-head">
            <h2>{{ room.title || '直播间' }}</h2>
            <div class="meta">
              在线 {{ room.viewerCount || 0 }} · 点赞 {{ room.likeCount || 0 }}
              <el-tag v-if="room.status === 'LIVING'" type="danger" size="small" class="ml">直播中</el-tag>
            </div>
          </div>
          <el-alert v-if="room.notice" :title="room.notice" type="info" :closable="false" show-icon />

          <div class="room-actions">
            <el-button :type="liked ? 'danger' : 'default'" @click="sendLike">
              <el-icon><ThumbUp /></el-icon> {{ liked ? '已点赞' : '点赞' }}
            </el-button>
            <el-button @click="router.push('/ranking')"><el-icon><Trophy /></el-icon> 礼物榜</el-button>
            <el-button v-if="isLoggedIn && room.anchorId !== userStore.userId" @click="toggleFollow">
              {{ following ? '已关注' : '关注主播' }}
            </el-button>
            <el-button text type="danger" @click="openReport">举报</el-button>
          </div>
        </el-card>
      </el-col>

      <!-- 互动区域 -->
      <el-col :xs="24" :lg="7">
        <el-card class="interact">
          <template #header>
            <div class="interact-head">
              <span>弹幕互动</span>
              <el-tag size="small" :type="wsConnected ? 'success' : 'info'">
                {{ wsConnected ? '已连接' : '连接中' }}
              </el-tag>
            </div>
          </template>
          <div ref="danmuListRef" class="danmu-list">
            <div v-for="(d, i) in danmuList" :key="`${d.createTime || ''}-${i}`" class="danmu-item">
              <span class="danmu-user">{{ d.nickname || '观众' }}：</span>
              <span>{{ d.content }}</span>
            </div>
            <el-empty v-if="danmuList.length === 0" description="快来发第一条弹幕" :image-size="60" />
          </div>
          <div class="danmu-input">
            <el-input v-model="danmuInput" placeholder="发条弹幕…" maxlength="200" @keyup.enter="sendDanmu" />
            <el-button type="primary" @click="sendDanmu">发送</el-button>
          </div>

          <el-divider />
          <div class="actions">
            <el-select v-model="selectedGift" placeholder="选择礼物" style="width: 130px">
              <el-option v-for="g in gifts" :key="g.id" :label="`${g.name} ¥${g.price}`" :value="g.id" />
            </el-select>
            <el-button type="warning" :loading="giftSending" @click="sendGiftHandler">打赏</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="reportVisible" title="举报此直播间" width="420px">
      <el-form label-position="top">
        <el-form-item label="举报原因">
          <el-input v-model="reportReason" type="textarea" :rows="3" placeholder="请描述违规内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reportVisible = false">取消</el-button>
        <el-button type="danger" :loading="reporting" @click="submitReport">提交举报</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, onBeforeUnmount, ref, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import flvjs from 'flv.js'
import { getRoom, listGifts, sendGift, listDanmu } from '@/api/live'
import { followAnchor, unfollowAnchor } from '@/api/user'
import {
  likeRoom,
  isRoomLiked,
  recordHistory,
  createReport
} from '@/api/interaction'
import { getFlvPrefix, getWsBaseUrl } from '@/config'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const roomId = route.params.id
const isLoggedIn = computed(() => userStore.isLoggedIn)

const room = ref({})
const gifts = ref([])
const danmuList = ref([])
const danmuInput = ref('')
const selectedGift = ref(null)
const loading = ref(false)
const giftSending = ref(false)
const playing = ref(false)
const liked = ref(false)
const following = ref(false)
const wsConnected = ref(false)
const reportVisible = ref(false)
const reportReason = ref('')
const reporting = ref(false)

const videoRef = ref(null)
const danmuListRef = ref(null)
let player = null
let ws = null
let reconnectTimer = null

const loadRoom = async () => {
  loading.value = true
  try {
    room.value = await getRoom(roomId) || {}
    await Promise.all([loadDanmuHistory(), loadLiked()])
    setupPlayer()
    if (isLoggedIn.value) {
      await recordHistory(roomId).catch(() => {})
    }
  } finally {
    loading.value = false
  }
}

const loadDanmuHistory = async () => {
  try {
    const history = await listDanmu(roomId)
    danmuList.value = Array.isArray(history) ? history : []
    await scrollToBottom()
  } catch (error) {
    danmuList.value = []
  }
}

const loadLiked = async () => {
  if (!isLoggedIn.value) return
  try {
    liked.value = Boolean(await isRoomLiked(roomId))
  } catch (error) {
    liked.value = false
  }
}

/** 优先播放 RTMP 对应的 HTTP-FLV 流；无直播流时回退到回放地址 */
const setupPlayer = () => {
  if (player) {
    try { player.destroy() } catch (e) { /* ignore */ }
    player = null
  }
  const video = videoRef.value
  playing.value = false
  if (!video || !flvjs.isSupported()) return

  const streamUrl = room.value.status === 'LIVING'
    ? `${String(getFlvPrefix()).replace(/\/$/, '')}/${room.value.streamKey || roomId}`
    : (room.value.replayUrl || '')
  if (!streamUrl) return

  try {
    player = flvjs.createPlayer({
      type: 'flv',
      url: streamUrl,
      isLive: room.value.status === 'LIVING'
    })
    player.attachMediaElement(video)
    player.load()
    player.play().catch(() => {})
    playing.value = true
  } catch (error) {
    playing.value = false
  }
}

/** WebSocket 弹幕：地址为 wsBaseUrl + 后端 /ws/danmu/{roomId} */
const connectWs = () => {
  if (ws && ws.readyState === WebSocket.OPEN) return
  const base = String(getWsBaseUrl()).replace(/\/$/, '')
  const url = `${base}/ws/danmu/${roomId}`
  try {
    ws = new WebSocket(url)
    ws.onopen = () => { wsConnected.value = true }
    ws.onmessage = (event) => {
      try {
        const msg = JSON.parse(event.data)
        danmuList.value.push(msg)
        if (danmuList.value.length > 200) danmuList.value.shift()
        scrollToBottom()
      } catch (error) {
        // 忽略非法消息
      }
    }
    ws.onclose = () => {
      wsConnected.value = false
      if (!reconnectTimer) {
        reconnectTimer = setTimeout(() => {
          reconnectTimer = null
          connectWs()
        }, 5000)
      }
    }
    ws.onerror = () => {
      wsConnected.value = false
    }
  } catch (error) {
    wsConnected.value = false
  }
}

const sendDanmu = () => {
  const content = danmuInput.value.trim()
  if (!content) return
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录后再发送弹幕')
    return
  }
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify({
      userId: userStore.userId,
      nickname: userStore.nickname || '观众',
      content
    }))
    danmuInput.value = ''
  } else {
    ElMessage.warning('弹幕连接尚未就绪，请稍后再试')
  }
}

const sendLike = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录后再点赞')
    return
  }
  try {
    await likeRoom(roomId)
    liked.value = true
    room.value.likeCount = (room.value.likeCount || 0) + 1
    ElMessage.success('感谢点赞')
  } catch (error) {
    // 拦截器已提示
  }
}

const toggleFollow = async () => {
  if (!room.value.anchorId) {
    ElMessage.warning('暂无主播信息')
    return
  }
  try {
    if (following.value) {
      await unfollowAnchor(room.value.anchorId)
      following.value = false
      ElMessage.success('已取消关注')
    } else {
      await followAnchor(room.value.anchorId)
      following.value = true
      ElMessage.success('关注成功')
    }
  } catch (error) {
    // ignore
  }
}

const sendGiftHandler = async () => {
  if (!selectedGift.value) {
    ElMessage.warning('请先选择礼物')
    return
  }
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录后再打赏')
    return
  }
  giftSending.value = true
  try {
    await sendGift({ roomId: Number(roomId), giftId: selectedGift.value, giftCount: 1 })
    ElMessage.success('打赏成功')
    selectedGift.value = null
  } catch (error) {
    // ignore
  } finally {
    giftSending.value = false
  }
}

const openReport = () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录后再举报')
    return
  }
  reportReason.value = ''
  reportVisible.value = true
}

const submitReport = async () => {
  if (!reportReason.value.trim()) {
    ElMessage.warning('请填写举报原因')
    return
  }
  reporting.value = true
  try {
    await createReport({ targetType: 'ROOM', targetId: Number(roomId), reason: reportReason.value.trim() })
    reportVisible.value = false
    ElMessage.success('举报已提交')
  } catch (error) {
    // ignore
  } finally {
    reporting.value = false
  }
}

const scrollToBottom = async () => {
  await nextTick()
  if (danmuListRef.value) {
    danmuListRef.value.scrollTop = danmuListRef.value.scrollHeight
  }
}

onMounted(() => {
  loadRoom()
  listGifts().then((res) => { gifts.value = res || [] }).catch(() => {})
  connectWs()
})

onBeforeUnmount(() => {
  if (reconnectTimer) clearTimeout(reconnectTimer)
  if (ws) ws.close()
  if (player) {
    try { player.destroy() } catch (e) { /* ignore */ }
    player = null
  }
})
</script>

<style scoped>
.player-wrap {
  position: relative;
  background: #000;
  border-radius: 6px;
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.player { width: 100%; max-height: 480px; }
.player-placeholder { display: flex; flex-direction: column; align-items: center; gap: 8px; color: #8a94a6; font-size: 13px; }
.player-placeholder .el-icon { font-size: 42px; }
.room-head { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin: 14px 0 10px; flex-wrap: wrap; }
.room-head h2 { margin: 0; font-size: 18px; }
.meta { color: #909399; font-size: 13px; }
.ml { margin-left: 8px; }
.room-actions { display: flex; gap: 8px; flex-wrap: wrap; margin-top: 14px; }
.interact-head { display: flex; align-items: center; justify-content: space-between; }
.danmu-list { height: 260px; overflow-y: auto; margin-bottom: 12px; }
.danmu-item { line-height: 1.8; font-size: 14px; }
.danmu-user { color: #409eff; }
.danmu-input { display: flex; gap: 8px; }
.actions { display: flex; gap: 8px; flex-wrap: wrap; }
@media (max-width: 768px) {
  .player-wrap { min-height: 210px; }
  .danmu-list { height: 220px; }
}
</style>
