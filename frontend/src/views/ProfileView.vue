<template>
  <div class="profile">
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
import { ElMessage } from 'element-plus'
import { getProfile, updateProfile, listFans } from '@/api/user'

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
</style>
