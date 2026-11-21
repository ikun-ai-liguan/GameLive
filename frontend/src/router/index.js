import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'Home', component: () => import('@/views/HomeView.vue') },
  { path: '/login', name: 'Login', component: () => import('@/views/LoginView.vue') },
  { path: '/register', name: 'Register', component: () => import('@/views/RegisterView.vue') },
  { path: '/room/:id', name: 'LiveRoom', component: () => import('@/views/LiveRoomView.vue') },
  { path: '/profile', name: 'Profile', component: () => import('@/views/ProfileView.vue') },
  { path: '/anchor', name: 'Anchor', component: () => import('@/views/AnchorView.vue') },
  { path: '/admin', name: 'Admin', component: () => import('@/views/AdminView.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
