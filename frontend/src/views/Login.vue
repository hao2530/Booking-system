<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '../api'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const store = useUserStore()
const form = ref({ username: '', password: '', role: 'USER', rememberMe: false })
const loading = ref(false)

onMounted(() => {
  const saved = localStorage.getItem('rememberedUser')
  if (saved) {
    const data = JSON.parse(saved)
    form.value.username = data.username
    form.value.password = data.password
    form.value.rememberMe = true
  }
})

async function login() {
  loading.value = true
  try {
    const res = await userApi.login(form.value)
    const d = res.data.data
    store.setUser(d.token, d.userId, d.username, d.role)
    
    if (form.value.rememberMe) {
      localStorage.setItem('rememberedUser', JSON.stringify({
        username: form.value.username,
        password: form.value.password
      }))
    } else {
      localStorage.removeItem('rememberedUser')
    }
    
    ElMessage.success('登录成功')
    if (d.role === 'ADMIN') router.push('/admin')
    else if (d.role === 'PROVIDER') router.push('/provider/dashboard')
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
    <el-card style="width: 460px">
      <template #header><h2 style="text-align: center; margin: 0">登录</h2></template>
      <el-form :model="form" label-width="100px" @keyup.enter="login">
        <el-form-item label="角色">
          <el-radio-group v-model="form.role">
            <el-radio value="USER">用户</el-radio>
            <el-radio value="ADMIN">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="用户名/邮箱">
          <el-input v-model="form.username" placeholder="请输入用户名或邮箱" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="form.rememberMe">记住密码</el-checkbox>
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
