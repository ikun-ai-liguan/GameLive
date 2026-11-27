import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'Home', component: () => import('@/views/HomeView.vue') },
  { path: '/login', name: 'Login', component: () => import('@/views/LoginView.vue') },
  { path: '/register', name: 'Register', component: () => import('@/views/RegisterView.vue') },
  { path: '/room/:id', name: 'LiveRoom', component: () => import('@/views/LiveRoomView.vue') },
  { path: '/profile', name: 'Profile', component: () => import('@/views/ProfileView.vue') },
  { path: '/follows', name: 'Follows', component: () => import('@/views/FollowView.vue') },
  { path: '/history', name: 'History', component: () => import('@/views/HistoryView.vue') },
  { path: '/ranking', name: 'Ranking', component: () => import('@/views/RankingView.vue') },
  { path: '/notifications', name: 'Notifications', component: () => import('@/views/NotificationView.vue') },
  { path: '/settings', name: 'Settings', component: () => import('@/views/SettingsView.vue') },
  { path: '/anchor', name: 'Anchor', component: () => import('@/views/AnchorView.vue') },
  { path: '/admin', name: 'Admin', component: () => import('@/views/AdminView.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
