<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { favoriteApi } from '../api'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const store = useUserStore()
const favorites = ref<any[]>([])
const loading = ref(false)

async function load() {
  if (!store.userId) return router.push('/login')
  loading.value = true
  try {
    const res = await favoriteApi.my(store.userId)
    favorites.value = res.data.data
  } finally {
    loading.value = false
  }
}

async function remove(serviceId: number) {
  try {
    await favoriteApi.remove(store.userId, serviceId)
    ElMessage.success('已取消收藏')
    load()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.msg || '操作失败')
  }
}

onMounted(load)
</script>

<template>
  <el-card>
    <template #header><h2 style="margin: 0">我的收藏</h2></template>

    <el-row :gutter="16" v-loading="loading">
      <el-col :span="6" v-for="s in favorites" :key="s.serviceId" style="margin-bottom: 16px">
        <el-card shadow="hover">
          <div style="cursor: pointer" @click="router.push(`/services/${s.serviceId}`)">
            <h3 style="margin: 0 0 8px">{{ s.title }}</h3>
            <p style="margin: 4px 0; color: #f56c6c; font-size: 18px; font-weight: bold">¥{{ s.price }}</p>
            <p style="margin: 4px 0; color: #909399; font-size: 13px">{{ s.durationMin }} 分钟</p>
            <el-tag size="small" type="success">{{ s.category }}</el-tag>
          </div>
          <div style="margin-top: 12px; text-align: right">
            <el-button type="text" @click="remove(s.serviceId)">取消收藏</el-button>
          </div>
        </el-card>
      </el-col>
      <el-empty v-if="!favorites.length && !loading" description="暂无收藏服务" style="width: 100%" />
    </el-row>
  </el-card>
</template>