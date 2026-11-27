<template>
  <div class="profile">
    <div class="quick-links">
      <el-button text @click="router.push('/follows')"><el-icon><Star /></el-icon> 我的关注</el-button>
      <el-button text @click="router.push('/history')"><el-icon><Clock /></el-icon> 观看历史</el-button>
      <el-button text @click="router.push('/ranking')"><el-icon><Trophy /></el-icon> 排行榜</el-button>
      <el-button text @click="router.push('/settings')"><el-icon><Setting /></el-icon> 服务器设置</el-button>
    </div>
    <el-row :gutter="16">
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>个人信息</template>
          <el-form :model="form" label-position="top">
            <el-form-item label="昵称">
              <el-input v-model="form.nickname" />
            </el-form-item>
            <el-form-item label="头像地址">
              <el-input v-model="form.avatar" placeholder="头像图片 URL" />
            </el-form-item>
            <el-form-item label="个性签名">
              <el-input v-model="form.signature" type="textarea" />
            </el-form-item>
            <el-button type="primary" :loading="saving" @click="save">保存修改</el-button>
          </el-form>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>粉丝列表（{{ fans.length }}）</template>
          <el-table :data="fans" size="small">
            <el-table-column prop="nickname" label="昵称" />
            <el-table-column prop="username" label="账号" />
            <el-table-column prop="signature" label="签名" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProfile, updateProfile, listFans } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const form = reactive({ nickname: '', avatar: '', signature: '' })
const fans = ref([])
const saving = ref(false)
const currentUserId = ref(null)

const loadProfile = async () => {
  const profile = await getProfile()
  currentUserId.value = profile.id
  form.nickname = profile.nickname
  form.avatar = profile.avatar
  form.signature = profile.signature
  fans.value = await listFans(profile.id)
}

const save = async () => {
  saving.value = true
  try {
    await updateProfile(form)
    userStore.nickname = form.nickname
    userStore.persist()
    ElMessage.success('保存成功')
  } finally {
    saving.value = false
  }
}

onMounted(loadProfile)
</script>

<style scoped>
.profile {
  max-width: 1000px;
  margin: 0 auto;
}
.quick-links {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  margin-bottom: 14px;
}
</style>
