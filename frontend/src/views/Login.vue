<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '../api'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const store = useUserStore()
const form = ref({ username: '', password: '' })
const loading = ref(false)

async function login() {
  loading.value = true
  try {
    const res = await userApi.login(form.value)
    const d = res.data.data
    store.setUser(d.token, d.userId, d.username, d.role)
    ElMessage.success('登录成功')
    if (d.role === 'ADMIN') router.push('/admin')
    else router.push('/services')
  } catch (e: any) {
    ElMessage.error(e.response?.data?.msg || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div style="display: flex; justify-content: center; padding-top: 80px">
    <el-card style="width: 420px">
      <template #header><h2 style="text-align: center; margin: 0">登录</h2></template>
      <el-form :model="form" label-width="60px" @keyup.enter="login">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="login" style="width: 100%">登录</el-button>
        </el-form-item>
      </el-form>
      <div style="text-align: center">
        <router-link to="/register" style="color: #409eff">没有账号？去注册</router-link>
      </div>
    </el-card>
  </div>
</template>
