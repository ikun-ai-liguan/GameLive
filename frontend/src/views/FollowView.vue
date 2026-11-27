<template>
  <div class="follows">
    <div class="page-head">
      <h2>我的关注</h2>
      <el-button text :icon="Refresh" :loading="loading" @click="load">刷新</el-button>
    </div>

    <el-row v-loading="loading" :gutter="12">
      <el-col v-for="anchor in anchors" :key="anchor.id" :xs="24" :sm="12" :md="8">
        <el-card class="anchor-card" shadow="hover">
          <div class="anchor-body">
            <el-avatar :size="48" :src="anchor.avatar || undefined">{{ (anchor.nickname || '主')[0] }}</el-avatar>
            <div class="anchor-info">
              <div class="anchor-name">{{ anchor.nickname || anchor.username }}</div>
              <div class="anchor-meta">粉丝 {{ anchor.fansCount || 0 }} · {{ anchor.signature || '这位主播很神秘' }}</div>
            </div>
          </div>
          <div class="card-actions">
            <el-button size="small" type="primary" @click="router.push('/')">去看直播</el-button>
            <el-button size="small" @click="unfollow(anchor)">取消关注</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="!loading && anchors.length === 0" description="还没有关注的主播，去首页看看吧" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { listFollows } from '@/api/interaction'
import { unfollowAnchor } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const anchors = ref([])
const loading = ref(false)

const load = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    anchors.value = []
    return
  }
  loading.value = true
  try {
    const res = await listFollows()
    anchors.value = Array.isArray(res) ? res : (res?.records || [])
  } catch (error) {
    anchors.value = []
  } finally {
    loading.value = false
  }
}

const unfollow = async (anchor) => {
  try {
    await ElMessageBox.confirm(`确定取消关注「${anchor.nickname || anchor.username}」吗？`, '提示', { type: 'warning' })
    await unfollowAnchor(anchor.id)
    ElMessage.success('已取消关注')
    await load()
  } catch (error) {
    // 用户取消确认时忽略
  }
}

onMounted(load)
</script>

<style scoped>
.follows { max-width: 1180px; margin: 0 auto; }
.page-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.page-head h2 { margin: 0; font-size: 20px; }
.anchor-card { margin-bottom: 12px; }
.anchor-body { display: flex; align-items: center; gap: 12px; }
.anchor-info { min-width: 0; }
.anchor-name { font-weight: 600; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.anchor-meta { font-size: 12px; color: #909399; margin-top: 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-actions { display: flex; gap: 8px; margin-top: 14px; }
</style>
