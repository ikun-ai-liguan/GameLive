<template>
  <div class="auth-wrap">
    <el-card class="auth-card">
      <h2 class="title">注册 GameLive</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent>
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" placeholder="4-20 位账号" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="可选，默认使用账号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="6-20 位密码" />
        </el-form-item>
        <el-button type="primary" :loading="loading" class="submit" @click="submit">注册</el-button>
        <div class="tip">
          已有账号？<router-link to="/login">去登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/user'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ username: '', nickname: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const submit = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-wrap {
  display: flex;
  justify-content: center;
  padding-top: 60px;
}
.auth-card {
  width: 380px;
}
.title {
  text-align: center;
  margin-bottom: 24px;
}
.submit {
  width: 100%;
}
.tip {
  margin-top: 12px;
  text-align: center;
  font-size: 13px;
  color: #909399;
}
</style>
