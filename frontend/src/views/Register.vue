<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '../api'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const store = useUserStore()
const form = ref({ username: '', password: '', phone: '' })
const loading = ref(false)

async function register() {
  loading.value = true
  try {
    const res = await userApi.register(form.value)
    store.setUser(res.data.data.token, res.data.data.userId, res.data.data.username)
    ElMessage.success('注册成功')
    router.push('/services')
  } catch (e: any) {
    ElMessage.error(e.response?.data?.msg || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div style="display: flex; justify-content: center; padding-top: 80px">
    <el-card style="width: 420px">
      <template #header><h2 style="text-align: center; margin: 0">注册</h2></template>
      <el-form :model="form" label-width="60px" @keyup.enter="register">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="register" style="width: 100%">注册</el-button>
        </el-form-item>
      </el-form>
      <div style="text-align: center">
        <router-link to="/login" style="color: #409eff">已有账号？去登录</router-link>
      </div>
    </el-card>
  </div>
</template>
