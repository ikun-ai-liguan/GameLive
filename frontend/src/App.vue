<template>
  <el-container class="app-container">
    <el-header class="app-header">
      <div class="logo" @click="router.push('/')">
        <el-icon><VideoCamera /></el-icon>
        <span>GameLive</span>
      </div>
      <el-menu mode="horizontal" :ellipsis="false" router :default-active="$route.path" class="nav-menu">
        <el-menu-item index="/">直播首页</el-menu-item>
        <el-menu-item index="/follows">我的关注</el-menu-item>
        <el-menu-item index="/anchor">主播开播</el-menu-item>
        <el-menu-item index="/admin">管理后台</el-menu-item>
      </el-menu>
      <div class="header-right">
        <template v-if="userStore.isLoggedIn">
          <span class="nickname">{{ userStore.nickname || '观众' }}</span>
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

    <!-- 手机端底部标签栏 -->
    <nav class="mobile-tabs" aria-label="主导航">
      <router-link
        v-for="tab in tabs"
        :key="tab.path"
        :to="tab.path"
        class="mobile-tab"
        :class="{ 'is-active': isActive(tab.path) }"
      >
        <el-icon class="tab-icon"><component :is="tab.icon" /></el-icon>
        <span class="tab-label">{{ tab.label }}</span>
      </router-link>
    </nav>
  </el-container>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const tabs = [
  { path: '/', label: '首页', icon: 'House' },
  { path: '/follows', label: '关注', icon: 'Star' },
  { path: '/anchor', label: '开播', icon: 'VideoCamera' },
  { path: '/notifications', label: '消息', icon: 'Bell' },
  { path: '/profile', label: '我的', icon: 'User' }
]

const isActive = (path) =>
  path === '/' ? route.path === '/' : route.path === path || route.path.startsWith(`${path}/`)

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

/* 手机端底部标签栏：桌面隐藏 */
.mobile-tabs {
  display: none;
}

@media (max-width: 768px) {
  .app-header {
    height: 52px;
    padding: 0 12px;
    gap: 8px;
    justify-content: space-between;
    border-bottom: 1px solid #e8ecf1;
  }
  .logo {
    font-size: 16px;
  }
  .nav-menu {
    display: none;
  }
  .header-right {
    gap: 2px;
  }
  .nickname {
    display: none;
  }
  .app-main {
    padding: 12px 12px calc(72px + env(safe-area-inset-bottom, 0px));
    min-height: calc(100vh - 52px);
  }
  .mobile-tabs {
    display: flex;
    position: fixed;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 1000;
    height: calc(56px + env(safe-area-inset-bottom, 0px));
    padding-bottom: env(safe-area-inset-bottom, 0px);
    box-sizing: content-box;
    background: #fff;
    border-top: 1px solid #e8ecf1;
    box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.04);
    align-items: stretch;
    justify-content: space-around;
  }
  .mobile-tab {
    flex: 1;
    min-width: 44px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 2px;
    color: #8a94a6;
    text-decoration: none;
    font-size: 11px;
    -webkit-tap-highlight-color: transparent;
  }
  .mobile-tab .tab-icon {
    font-size: 20px;
    line-height: 1;
  }
  .mobile-tab.is-active {
    color: var(--gl-accent);
    font-weight: 600;
  }
}
</style>
