<template>
  <div class="room" v-loading="loading">
    <el-row :gutter="16">
      <!-- 播放器区域 -->
      <el-col :xs="24" :lg="17">
        <el-card>
          <div class="player-wrap">
            <video ref="videoRef" class="player" controls autoplay muted></video>
            <el-empty v-if="!playing" description="暂无直播流（开播后或配置流媒体服务后在此播放）" />
          </div>
          <div class="room-head">
            <h2>{{ room.title || '直播间' }}</h2>
            <div class="meta">在线 {{ room.viewerCount || 0 }} · 点赞 {{ room.likeCount || 0 }}</div>
          </div>
          <el-alert v-if="room.notice" :title="room.notice" type="info" :closable="false" show-icon />
        </el-card>
      </el-col>

      <!-- 互动区域 -->
      <el-col :xs="24" :lg="7">
        <el-card class="interact">
          <template #header>弹幕互动</template>
          <div ref="danmuListRef" class="danmu-list">
            <div v-for="(d, i) in danmuList" :key="i" class="danmu-item">
              <span class="danmu-user">{{ d.nickname || '观众' }}：</span>
              <span>{{ d.content }}</span>
            </div>
          </div>
          <div class="danmu-input">
            <el-input v-model="danmuInput" placeholder="发条弹幕…" @keyup.enter="sendDanmu" />
            <el-button type="primary" @click="sendDanmu">发送</el-button>
          </div>

          <el-divider />
          <div class="actions">
            <el-button @click="sendLike"><el-icon><ThumbUp /></el-icon> 点赞</el-button>
            <el-select v-model="selectedGift" placeholder="选择礼物" style="width: 130px">
              <el-option v-for="g in gifts" :key="g.id" :label="g.name" :value="g.id" />
            </el-select>
            <el-button type="warning" @click="sendGiftHandler">打赏</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, onBeforeUnmount, ref, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import flvjs from 'flv.js'
import { getRoom, listGifts, sendGift, listDanmu } from '@/api/live'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()
const roomId = route.params.id

const room = ref({})
const gifts = ref([])
const danmuList = ref([])
const danmuInput = ref('')
const selectedGift = ref(null)
const loading = ref(false)
const playing = ref(false)

const videoRef = ref(null)
const danmuListRef = ref(null)
let player = null
let ws = null

const loadRoom = async () => {
  loading.value = true
  try {
    room.value = await getRoom(roomId)
    await loadDanmuHistory()
    setupPlayer()
  } finally {
    loading.value = false
  }
}

const loadDanmuHistory = async () => {
  const history = await listDanmu(roomId)
  danmuList.value = history || []
}

// 初始化 flv.js 播放器（骨架阶段使用回放地址，真实直播流地址按部署填写）
const setupPlayer = () => {
  const video = videoRef.value
  if (!video || !flvjs.isSupported()) return
  const streamUrl = room.value.replayUrl || ''
  if (!streamUrl) return

  player = flvjs.createPlayer({ type: 'flv', url: streamUrl })
  player.attachMediaElement(video)
  player.load()
  player.play().catch(() => {})
  playing.value = true
}

// 建立 WebSocket 弹幕连接
const connectWs = () => {
  const base = import.meta.env.VITE_WS_BASE_URL
  ws = new WebSocket(`${base}/${roomId}`)
  ws.onmessage = (event) => {
    const msg = JSON.parse(event.data)
    danmuList.value.push(msg)
    scrollToBottom()
  }
  ws.onclose = () => {
    playing.value = false
  }
}

const sendDanmu = () => {
  const content = danmuInput.value.trim()
  if (!content) return
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify({
      userId: userStore.userId,
      nickname: userStore.nickname,
      content
    }))
  }
  danmuInput.value = ''
}

const sendLike = () => {
  // TODO 调用后端点赞接口，同步在线 / 点赞数
  ElMessage.success('感谢点赞')
}

const sendGiftHandler = async () => {
  if (!selectedGift.value) {
    ElMessage.warning('请先选择礼物')
    return
  }
  await sendGift({ roomId: Number(roomId), giftId: selectedGift.value, giftCount: 1 })
  ElMessage.success('打赏成功')
  selectedGift.value = null
}

const scrollToBottom = async () => {
  await nextTick()
  if (danmuListRef.value) {
    danmuListRef.value.scrollTop = danmuListRef.value.scrollHeight
  }
}

onMounted(() => {
  loadRoom()
  listGifts().then((res) => (gifts.value = res || []))
  connectWs()
})

onBeforeUnmount(() => {
  if (ws) ws.close()
  if (player) {
    player.destroy()
    player = null
  }
})
</script>

<style scoped>
.player-wrap {
  position: relative;
  background: #000;
  border-radius: 6px;
  min-height: 360px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.player {
  width: 100%;
  max-height: 480px;
}
.room-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 14px 0 10px;
}
.room-head h2 {
  margin: 0;
  font-size: 18px;
}
.meta {
  color: #909399;
  font-size: 13px;
}
.danmu-list {
  height: 260px;
  overflow-y: auto;
  margin-bottom: 12px;
}
.danmu-item {
  line-height: 1.8;
  font-size: 14px;
}
.danmu-user {
  color: #409eff;
}
.danmu-input {
  display: flex;
  gap: 8px;
}
.actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
</style>
