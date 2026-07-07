<script setup lang="ts">
import { useUserStore } from './stores/user'
import { useRouter } from 'vue-router'

const store = useUserStore()
const router = useRouter()

function logout() {
  store.logout()
  router.push('/login')
}
</script>

<template>
  <div id="nav">
    <router-link to="/services">服务</router-link>
    <router-link to="/my-bookings" v-if="store.userId">我的预约</router-link>
    <span v-if="store.username">你好, {{ store.username }}</span>
    <router-link to="/login" v-if="!store.userId">登录</router-link>
    <a href="#" v-else @click.prevent="logout">退出</a>
  </div>
  <router-view />
</template>

<style>
#nav { display: flex; gap: 16px; align-items: center; padding: 12px 20px; background: #f5f5f5; border-bottom: 1px solid #ddd; }
#nav a { text-decoration: none; color: #333; }
#nav a:hover { color: #42b883; }
</style>
