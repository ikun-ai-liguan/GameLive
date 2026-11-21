<template>
  <div class="home">
    <div class="toolbar">
      <el-select v-model="categoryId" placeholder="全部分类" clearable style="width: 180px" @change="loadRooms">
        <el-option label="全部分类" :value="null" />
        <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-input
        v-model="keyword"
        placeholder="搜索直播间标题"
        clearable
        style="width: 260px"
        @keyup.enter="loadRooms"
      >
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-button type="primary" @click="loadRooms">搜索</el-button>
    </div>

    <el-row v-loading="loading" :gutter="16">
      <el-col v-for="room in rooms" :key="room.id" :xs="24" :sm="12" :md="8" :lg="6">
        <el-card class="room-card" shadow="hover" @click="enterRoom(room.id)">
          <div class="cover">
            <el-icon v-if="!room.cover" class="cover-icon"><VideoCamera /></el-icon>
            <img v-if="room.cover" :src="room.cover" alt="封面" />
            <el-tag v-if="room.status === 'LIVING'" type="danger" class="status-tag">直播中</el-tag>
            <el-tag v-else-if="room.status === 'REPLAY'" type="info" class="status-tag">回放</el-tag>
          </div>
          <div class="room-title">{{ room.title }}</div>
          <div class="room-meta">在线 {{ room.viewerCount || 0 }} · 点赞 {{ room.likeCount || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="!loading && rooms.length === 0" description="暂无直播间" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { listRooms, listCategories } from '@/api/live'

const router = useRouter()
const rooms = ref([])
const categories = ref([])
const categoryId = ref(null)
const keyword = ref('')
const loading = ref(false)

const loadRooms = async () => {
  loading.value = true
  try {
    const res = await listRooms({ categoryId: categoryId.value, keyword: keyword.value, current: 1, size: 24 })
    rooms.value = res.records || []
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  categories.value = await listCategories()
}

const enterRoom = (id) => router.push(`/room/${id}`)

onMounted(() => {
  loadCategories()
  loadRooms()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}
.room-card {
  margin-bottom: 16px;
  cursor: pointer;
}
.cover {
  position: relative;
  height: 140px;
  background: #eee;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.cover-icon {
  font-size: 40px;
  color: #bbb;
}
.status-tag {
  position: absolute;
  top: 8px;
  left: 8px;
}
.room-title {
  margin-top: 10px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.room-meta {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}
</style>
