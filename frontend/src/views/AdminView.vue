<template>
  <div class="admin">
    <!-- 数据统计 -->
    <el-row :gutter="16" class="stats">
      <el-col :xs="12" :md="6" v-for="card in statCards" :key="card.label">
        <el-card shadow="hover">
          <div class="stat-label">{{ card.label }}</div>
          <div class="stat-value">{{ stats[card.key] ?? '-' }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-tabs v-model="activeTab">
      <!-- 用户管理 -->
      <el-tab-pane label="用户管理" name="users">
        <el-table :data="users" v-loading="loading" size="small">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="账号" />
          <el-table-column prop="nickname" label="昵称" />
          <el-table-column prop="role" label="角色" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'BANNED' ? 'danger' : 'success'">
                {{ row.status === 'BANNED' ? '已封禁' : '正常' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160">
            <template #default="{ row }">
              <el-button
                link
                :type="row.status === 'BANNED' ? 'success' : 'danger'"
                @click="toggleBan(row)"
              >
                {{ row.status === 'BANNED' ? '解封' : '封禁' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 直播间审核 -->
      <el-tab-pane label="直播间审核" name="rooms">
        <el-table :data="rooms" v-loading="loading" size="small">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="status" label="状态" width="100" />
          <el-table-column prop="auditStatus" label="审核状态" width="110" />
          <el-table-column prop="viewerCount" label="在线" width="90" />
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button link type="success" @click="audit(row, true)">通过</el-button>
              <el-button link type="danger" @click="audit(row, false)">驳回</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 分类管理 -->
      <el-tab-pane label="分类管理" name="categories">
        <div class="category-toolbar">
          <el-input v-model="newCategory.name" placeholder="分类名称" style="width: 200px" />
          <el-button type="primary" @click="addCategory">新增分类</el-button>
        </div>
        <el-table :data="categories" size="small">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="sort" label="排序" width="100" />
          <el-table-column prop="status" label="状态" width="100" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  adminListUsers,
  adminBanUser,
  adminListRooms,
  adminAuditRoom,
  adminStatistics,
  listCategories
} from '@/api/live'

const activeTab = ref('users')
const loading = ref(false)
const stats = ref({})
const users = ref([])
const rooms = ref([])
const categories = ref([])
const newCategory = reactive({ name: '' })

const statCards = [
  { label: '用户总数', key: 'userCount' },
  { label: '直播间总数', key: 'roomCount' },
  { label: '直播中', key: 'livingCount' },
  { label: '已封禁用户', key: 'bannedUserCount' }
]

const loadStats = async () => {
  stats.value = await adminStatistics()
}

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await adminListUsers({ current: 1, size: 50 })
    users.value = res.records || []
  } finally {
    loading.value = false
  }
}

const loadRooms = async () => {
  loading.value = true
  try {
    const res = await adminListRooms({ current: 1, size: 50 })
    rooms.value = res.records || []
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  categories.value = await listCategories()
}

const toggleBan = async (row) => {
  const banned = row.status !== 'BANNED'
  await adminBanUser(row.id, banned, banned ? '后台手动封禁' : '后台手动解封')
  ElMessage.success(banned ? '已封禁' : '已解封')
  loadUsers()
}

const audit = async (row, approved) => {
  await ElMessageBox.confirm(approved ? `确认通过直播间「${row.title}」？` : `确认驳回直播间「${row.title}」？`, '提示')
  await adminAuditRoom(row.id, approved, approved ? '审核通过' : '审核驳回')
  ElMessage.success('操作完成')
  loadRooms()
}

const addCategory = async () => {
  if (!newCategory.name) {
    ElMessage.warning('请输入分类名称')
    return
  }
  // TODO 接入分类新增接口后替换为真实调用
  ElMessage.info('分类新增接口待对接')
  newCategory.name = ''
}

onMounted(() => {
  loadStats()
  loadUsers()
  loadRooms()
  loadCategories()
})
</script>

<style scoped>
.stats {
  margin-bottom: 20px;
}
.stat-label {
  color: #909399;
  font-size: 13px;
}
.stat-value {
  font-size: 26px;
  font-weight: 700;
  margin-top: 6px;
}
.category-toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
</style>
