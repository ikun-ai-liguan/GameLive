<template>
  <el-container class="app-container">
    <el-header class="app-header">
      <div class="logo" @click="router.push('/')">
        <el-icon><VideoCamera /></el-icon>
        <span>GameLive</span>
      </div>
      <el-menu mode="horizontal" :ellipsis="false" router :default-active="$route.path" class="nav-menu">
        <el-menu-item index="/">直播首页</el-menu-item>
        <el-menu-item index="/anchor">主播开播</el-menu-item>
        <el-menu-item index="/admin">管理后台</el-menu-item>
      </el-menu>
      <div class="header-right">
        <template v-if="userStore.userId">
          <span class="nickname">{{ userStore.nickname }}</span>
          <el-button link @click="router.push('/profile')">个人中心</el-button>
          <el-button link @click="logout">退出</el-button>
        </template>
        <template v-else>
          <el-button type="primary" @click="router.push('/login')">登录</el-button>
          <el-button @click="router.push('/register')">注册</el-button>
        </template>
      </div>
    </el-header>

    <el-main class="app-main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const logout = () => {
  userStore.logout()
  router.push('/')
}
</script>

<style scoped>
.app-container {
  min-height: 100vh;
}
.app-header {
  display: flex;
  align-items: center;
  gap: 24px;
  border-bottom: 1px solid #eee;
}
.logo {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  white-space: nowrap;
}
.nav-menu {
  flex: 1;
  border-bottom: none;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}
.nickname {
  margin-right: 8px;
}
.app-main {
  background: #f5f7fa;
  padding: 20px;
}
</style>
