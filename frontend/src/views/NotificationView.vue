<template>
  <div class="notifications">
    <div class="page-head">
      <h2>消息通知</h2>
      <el-button text :icon="Refresh" :loading="loading" @click="load">刷新</el-button>
    </div>

    <div v-loading="loading" class="list">
      <div
        v-for="item in items"
        :key="item.id"
        class="item"
        :class="{ unread: !item.isRead }"
      >
        <div class="item-main">
          <div class="item-title">
            <span class="dot" v-if="!item.isRead" />
            {{ item.title || '系统通知' }}
          </div>
          <div class="item-content">{{ item.content }}</div>
          <div class="item-time">{{ formatTime(item.createTime) }}</div>
        </div>
        <el-button v-if="!item.isRead" size="small" @click="markRead(item)">标为已读</el-button>
      </div>
    </div>

    <el-empty v-if="!loading && items.length === 0" description="暂无消息" />
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
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { listNotifications, readNotification } from '@/api/interaction'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const items = ref([])
const loading = ref(false)
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
    const res = await listNotifications({ current: page.value, size })
    items.value = res?.records || []
    total.value = res?.total || 0
  } catch (error) {
    items.value = []
  } finally {
    loading.value = false
  }
}

const markRead = async (item) => {
  try {
    await readNotification(item.id)
    item.isRead = true
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(load)
</script>

<style scoped>
.notifications { max-width: 900px; margin: 0 auto; }
.page-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.page-head h2 { margin: 0; font-size: 20px; }
.list { background: #fff; border-radius: 10px; overflow: hidden; }
.item { display: flex; align-items: center; gap: 12px; padding: 14px 16px; border-bottom: 1px solid #f0f2f5; }
.item:last-child { border-bottom: none; }
.item.unread { background: #f5faff; }
.item-main { flex: 1; min-width: 0; }
.item-title { font-weight: 600; display: flex; align-items: center; gap: 6px; }
.dot { width: 8px; height: 8px; border-radius: 50%; background: #f56c6c; display: inline-block; flex-shrink: 0; }
.item-content { font-size: 13px; color: #606266; margin-top: 4px; }
.item-time { font-size: 12px; color: #a8abb2; margin-top: 4px; }
.pager { display: flex; justify-content: center; margin-top: 18px; }
</style>
