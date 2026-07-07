import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/services' },
    { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
    { path: '/register', name: 'Register', component: () => import('../views/Register.vue') },
    { path: '/services', name: 'Services', component: () => import('../views/ServiceList.vue') },
    { path: '/services/:id', name: 'ServiceDetail', component: () => import('../views/ServiceDetail.vue') },
    { path: '/my-bookings', name: 'MyBookings', component: () => import('../views/MyBookings.vue') },
  ],
})

export default router
