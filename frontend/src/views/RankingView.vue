<template>
  <div class="ranking">
    <div class="page-head">
      <h2>排行榜</h2>
      <el-radio-group v-model="mode" @change="load">
        <el-radio-button value="hot">热门直播</el-radio-button>
        <el-radio-button value="gift">礼物榜</el-radio-button>
      </el-radio-group>
    </div>

    <template v-if="mode === 'hot'">
      <div v-loading="loading" class="board">
        <div
          v-for="(room, index) in hotRooms"
          :key="room.id"
          class="row"
          @click="router.push(`/room/${room.id}`)"
        >
          <span class="rank" :class="{ top: index < 3 }">{{ index + 1 }}</span>
          <div class="row-main">
            <div class="row-title">{{ room.title || `直播间 ${room.id}` }}</div>
            <div class="row-meta">在线 {{ room.viewerCount || 0 }} · 点赞 {{ room.likeCount || 0 }}</div>
          </div>
          <el-tag v-if="room.status === 'LIVING'" type="danger" size="small">直播中</el-tag>
        </div>
      </div>
      <el-empty v-if="!loading && hotRooms.length === 0" description="暂无直播间" />
    </template>

    <template v-else>
      <div class="picker">
        <el-select v-model="selectedRoom" placeholder="选择直播间" style="width: 240px" @change="loadGiftRank">
          <el-option v-for="room in roomsForPicker" :key="room.id" :label="room.title || `直播间 ${room.id}`" :value="room.id" />
        </el-select>
      </div>
      <div v-loading="giftLoading" class="board">
        <div v-for="(item, index) in giftRank" :key="item.userId || index" class="row">
          <span class="rank" :class="{ top: index < 3 }">{{ index + 1 }}</span>
          <div class="row-main">
            <div class="row-title">{{ item.nickname || `用户 ${item.userId}` }}</div>
            <div class="row-meta">礼物 {{ item.totalCount || 0 }} 件</div>
          </div>
          <strong class="amount">¥{{ formatAmount(item.totalAmount) }}</strong>
        </div>
      </div>
      <el-empty v-if="!giftLoading && selectedRoom && giftRank.length === 0" description="该直播间暂无礼物记录" />
    </template>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { listRooms } from '@/api/live'
import { getGiftRank } from '@/api/interaction'

const router = useRouter()
const mode = ref('hot')
const loading = ref(false)
const giftLoading = ref(false)
const hotRooms = ref([])
const roomsForPicker = ref([])
const selectedRoom = ref(null)
const giftRank = ref([])

const formatAmount = (value) => {
  const n = Number(value || 0)
  return Number.isFinite(n) ? n.toFixed(2) : '0.00'
}

const loadRooms = async () => {
  loading.value = true
  try {
    const res = await listRooms({ current: 1, size: 50 })
    const all = res?.records || []
    roomsForPicker.value = all
    hotRooms.value = [...all].sort((a, b) => (b.viewerCount || 0) - (a.viewerCount || 0)).slice(0, 20)
    if (!selectedRoom.value && all.length) {
      selectedRoom.value = all[0].id
      await loadGiftRank()
    }
  } catch (error) {
    hotRooms.value = []
  } finally {
    loading.value = false
  }
}

const loadGiftRank = async () => {
  if (!selectedRoom.value) return
  giftLoading.value = true
  try {
    const res = await getGiftRank(selectedRoom.value)
    giftRank.value = Array.isArray(res) ? res : []
  } catch (error) {
    giftRank.value = []
  } finally {
    giftLoading.value = false
  }
}

const load = () => {
  if (mode.value === 'hot') loadRooms()
  else loadGiftRank()
}

onMounted(loadRooms)
</script>

<style scoped>
.ranking { max-width: 900px; margin: 0 auto; }
.page-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; gap: 12px; flex-wrap: wrap; }
.page-head h2 { margin: 0; font-size: 20px; }
.picker { margin-bottom: 16px; }
.board { background: #fff; border-radius: 10px; overflow: hidden; }
.row { display: flex; align-items: center; gap: 14px; padding: 12px 16px; border-bottom: 1px solid #f0f2f5; cursor: pointer; }
.row:last-child { border-bottom: none; }
.row:hover { background: #fafbfc; }
.rank { width: 30px; height: 30px; border-radius: 50%; background: #eef1f5; color: #606266; display: flex; align-items: center; justify-content: center; font-weight: 700; flex-shrink: 0; }
.rank.top { background: #f56c6c; color: #fff; }
.row-main { flex: 1; min-width: 0; }
.row-title { font-weight: 600; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.row-meta { font-size: 12px; color: #909399; margin-top: 2px; }
.amount { color: #e6a23c; }
</style>
