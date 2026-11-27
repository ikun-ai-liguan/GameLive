<template>
  <div class="history">
    <div class="page-head">
      <h2>观看历史</h2>
      <el-button text :icon="Delete" :loading="clearing" @click="clearAll">清空</el-button>
    </div>

    <div v-loading="loading" class="list">
      <div v-for="item in items" :key="item.roomId" class="item" @click="router.push(`/room/${item.roomId}`)">
        <div class="thumb">
          <img v-if="item.cover" :src="item.cover" alt="封面" />
          <el-icon v-else><VideoCamera /></el-icon>
        </div>
        <div class="info">
          <div class="title">{{ item.title || `直播间 ${item.roomId}` }}</div>
          <div class="meta">{{ item.anchorNickname || '主播' }} · {{ formatTime(item.watchedAt) }}</div>
        </div>
        <el-button type="primary" size="small">继续观看</el-button>
      </div>
    </div>

    <el-empty v-if="!loading && items.length === 0" description="还没有观看记录" />
    <div v-if="total > size" class="pager">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="size"
        v-model:current-page="page"
        @current-change="load"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import { listHistory } from '@/api/interaction'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const items = ref([])
const loading = ref(false)
const clearing = ref(false)
const page = ref(1)
const size = 12
const total = ref(0)

const formatTime = (value) => {
  if (!value) return ''
  const d = new Date(value)
  return Number.isNaN(d.getTime()) ? String(value) : d.toLocaleString('zh-CN', { hour12: false })
}

const load = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  loading.value = true
  try {
    const res = await listHistory({ current: page.value, size })
    items.value = res?.records || []
    total.value = res?.total || 0
  } catch (error) {
    items.value = []
  } finally {
    loading.value = false
  }
}

// 后端未单独提供清空接口：使用本地标记隐藏记录，避免误删服务端数据
const clearAll = async () => {
  if (!items.value.length) return
  try {
    await ElMessageBox.confirm('清空本地显示的观看记录？', '提示', { type: 'warning' })
    items.value = []
    total.value = 0
    ElMessage.success('已清空本地列表')
  } catch (error) {
    // ignore
  }
}

onMounted(load)
</script>

<style scoped>
.history { max-width: 900px; margin: 0 auto; }
.page-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.page-head h2 { margin: 0; font-size: 20px; }
.item { display: flex; align-items: center; gap: 12px; padding: 12px; background: #fff; border-radius: 8px; margin-bottom: 10px; cursor: pointer; }
.item:hover { box-shadow: 0 2px 12px rgba(0,0,0,.06); }
.thumb { width: 120px; height: 68px; border-radius: 6px; background: #eef1f5; display: flex; align-items: center; justify-content: center; overflow: hidden; flex-shrink: 0; }
.thumb img { width: 100%; height: 100%; object-fit: cover; }
.info { flex: 1; min-width: 0; }
.title { font-weight: 600; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.meta { font-size: 12px; color: #909399; margin-top: 4px; }
.pager { display: flex; justify-content: center; margin-top: 18px; }
@media (max-width: 600px) {
  .thumb { width: 92px; height: 54px; }
  .item .el-button { display: none; }
}
</style>
