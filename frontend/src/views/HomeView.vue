<template>
  <div class="home">
    <div class="hero">
      <div>
        <h1>发现精彩直播</h1>
        <p>游戏、娱乐、才艺，随时开播随时看</p>
      </div>
      <el-button type="primary" @click="router.push('/ranking')">查看排行榜</el-button>
    </div>

    <div class="toolbar">
      <div class="chips">
        <el-button
          size="small"
          :type="categoryId === null ? 'primary' : 'default'"
          @click="selectCategory(null)"
        >全部</el-button>
        <el-button
          v-for="c in categories"
          :key="c.id"
          size="small"
          :type="categoryId === c.id ? 'primary' : 'default'"
          @click="selectCategory(c.id)"
        >{{ c.name }}</el-button>
      </div>
      <div class="search-row">
        <el-input
          v-model="keyword"
          placeholder="搜索直播间标题"
          clearable
          @keyup.enter="loadRooms"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" @click="loadRooms">搜索</el-button>
      </div>
      <div class="sort-row">
        <el-radio-group v-model="sort" size="small" @change="applySort">
          <el-radio-button value="hot">热门</el-radio-button>
          <el-radio-button value="new">最新</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <el-row v-loading="loading" :gutter="16">
      <el-col v-for="room in rooms" :key="room.id" :xs="12" :sm="12" :md="8" :lg="6">
        <el-card class="room-card" shadow="hover" @click="enterRoom(room.id)">
          <div class="cover">
            <el-icon v-if="!room.cover" class="cover-icon"><VideoCamera /></el-icon>
            <img v-if="room.cover" :src="room.cover" alt="封面" />
            <el-tag v-if="room.status === 'LIVING'" type="danger" size="small" class="status-tag">直播中</el-tag>
            <el-tag v-else-if="room.status === 'REPLAY'" type="info" size="small" class="status-tag">回放</el-tag>
            <span class="viewers">
              <el-icon><View /></el-icon> {{ room.viewerCount || 0 }}
            </span>
          </div>
          <div class="room-title">{{ room.title }}</div>
          <div class="room-meta">点赞 {{ room.likeCount || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="!loading && rooms.length === 0" description="暂无直播间" />
    <div v-if="total > size" class="pager">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="size"
        v-model:current-page="page"
        @current-change="loadRooms"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { listRooms, listCategories } from '@/api/live'

const router = useRouter()
const allRooms = ref([])
const rooms = ref([])
const categories = ref([])
const categoryId = ref(null)
const keyword = ref('')
const sort = ref('hot')
const loading = ref(false)
const page = ref(1)
const size = 24
const total = ref(0)

const loadRooms = async () => {
  loading.value = true
  try {
    const res = await listRooms({
      categoryId: categoryId.value,
      keyword: keyword.value,
      current: page.value,
      size
    })
    allRooms.value = res?.records || []
    total.value = res?.total || 0
    applySort()
  } catch (error) {
    allRooms.value = []
    rooms.value = []
  } finally {
    loading.value = false
  }
}

const applySort = () => {
  const list = [...allRooms.value]
  if (sort.value === 'hot') {
    list.sort((a, b) => (b.viewerCount || 0) - (a.viewerCount || 0))
  } else {
    list.sort((a, b) => new Date(b.createTime || 0) - new Date(a.createTime || 0))
  }
  rooms.value = list
}

const selectCategory = (id) => {
  categoryId.value = id
  page.value = 1
  loadRooms()
}

const loadCategories = async () => {
  try {
    categories.value = (await listCategories()) || []
  } catch (error) {
    categories.value = []
  }
}

const enterRoom = (id) => router.push(`/room/${id}`)

onMounted(() => {
  loadCategories()
  loadRooms()
})
</script>

<style scoped>
.home { max-width: 1280px; margin: 0 auto; }
.hero { display: flex; align-items: center; justify-content: space-between; gap: 16px; padding: 18px 22px; margin-bottom: 18px; border-radius: 12px; background: linear-gradient(120deg, #1f3a5f, #2b6cb0); color: #fff; }
.hero h1 { margin: 0; font-size: 22px; }
.hero p { margin: 6px 0 0; opacity: .85; font-size: 13px; }
.hero .el-button { flex-shrink: 0; }
.toolbar { margin-bottom: 18px; }
.chips { display: flex; gap: 8px; overflow-x: auto; padding-bottom: 8px; margin-bottom: 12px; scrollbar-width: none; }
.chips::-webkit-scrollbar { display: none; }
.chips .el-button { flex-shrink: 0; }
.search-row { display: flex; gap: 8px; max-width: 480px; margin-bottom: 12px; }
.sort-row { margin-bottom: 6px; }
.room-card { margin-bottom: 16px; cursor: pointer; }
.cover { position: relative; height: 140px; background: #eef1f5; border-radius: 6px; display: flex; align-items: center; justify-content: center; overflow: hidden; }
.cover img { width: 100%; height: 100%; object-fit: cover; }
.cover-icon { font-size: 40px; color: #b8c0cc; }
.status-tag { position: absolute; top: 8px; left: 8px; }
.viewers { position: absolute; right: 8px; bottom: 8px; display: flex; align-items: center; gap: 3px; padding: 2px 6px; background: rgba(0,0,0,.6); color: #fff; border-radius: 4px; font-size: 12px; }
.room-title { margin-top: 10px; font-weight: 600; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.room-meta { margin-top: 4px; font-size: 12px; color: #909399; }
.pager { display: flex; justify-content: center; margin-top: 18px; }
@media (max-width: 768px) {
  .hero { padding: 14px 16px; }
  .hero h1 { font-size: 18px; }
  .hero p { font-size: 12px; }
  .search-row { max-width: none; }
}
</style>
