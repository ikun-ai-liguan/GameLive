<template>
  <div class="auth-wrap">
    <el-card class="auth-card">
      <h2 class="title">登录 GameLive</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent>
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-button type="primary" :loading="loading" class="submit" @click="submit">登录</el-button>
        <div class="tip">
          还没有账号？<router-link to="/register">立即注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const submit = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    // login() 内部已规范化并保存 token / userId / nickname
    await login(form)
    ElMessage.success(`登录成功，欢迎回来 ${userStore.nickname || '观众'}`)
    router.push('/')
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
