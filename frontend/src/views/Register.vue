<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '../api'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const store = useUserStore()
const form = ref({ username: '', password: '', phone: '', role: 'USER', adminKey: '' })
const loading = ref(false)

const isAdmin = computed(() => form.value.role === 'ADMIN')

async function register() {
  loading.value = true
  try {
    const res = await userApi.register(form.value)
    const d = res.data.data
    store.setUser(d.token, d.userId, d.username, d.role)
    ElMessage.success('注册成功')
    if (d.role === 'ADMIN') router.push('/admin')
    else router.push('/services')
  } catch (e: any) {
    ElMessage.error(e.response?.data?.msg || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div style="display: flex; justify-content: center; padding-top: 60px">
    <el-card style="width: 460px">
      <template #header><h2 style="text-align: center; margin: 0">注册</h2></template>
      <el-form :model="form" label-width="100px" @keyup.enter="register">
        <el-form-item label="角色">
          <el-radio-group v-model="form.role">
            <el-radio value="USER">客户</el-radio>
            <el-radio value="PROVIDER">商户</el-radio>
            <el-radio value="ADMIN">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="管理员密钥" v-if="isAdmin">
          <el-input v-model="form.adminKey" type="password" show-password placeholder="请输入管理员密钥" />
          <div style="color: #909399; font-size: 12px; margin-top: 4px">管理员密钥: Admin888</div>
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
