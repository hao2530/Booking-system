<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '../api'
import { useUserStore } from '../stores/user'

const router = useRouter()
const store = useUserStore()
const username = ref('')
const password = ref('')
const phone = ref('')
const error = ref('')

async function register() {
  try {
    const res = await userApi.register({ username: username.value, password: password.value, phone: phone.value })
    store.setUser(res.data.data.token, res.data.data.userId, res.data.data.username)
    router.push('/services')
  } catch (e: any) {
    error.value = e.response?.data?.msg || '注册失败'
  }
}
</script>

<template>
  <div class="page">
    <h2>注册</h2>
    <input v-model="username" placeholder="用户名" />
    <input v-model="password" type="password" placeholder="密码" />
    <input v-model="phone" placeholder="手机号" />
    <p v-if="error" class="error">{{ error }}</p>
    <button @click="register">注册</button>
    <router-link to="/login">已有账号？登录</router-link>
  </div>
</template>

<style scoped>
.page { max-width: 400px; margin: 50px auto; display: flex; flex-direction: column; gap: 12px; }
input { padding: 10px; font-size: 16px; }
button { padding: 10px; background: #42b883; color: #fff; border: none; cursor: pointer; }
.error { color: red; }
</style>
