<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { serviceApi } from '../api'

const router = useRouter()
const services = ref<any[]>([])
const keyword = ref('')
const loading = ref(false)

async function search() {
  loading.value = true
  try {
    const res = await serviceApi.search({ keyword: keyword.value, page: 1, size: 100 })
    services.value = res.data.data.list
  } finally {
    loading.value = false
  }
}

onMounted(search)
</script>

<template>
  <div>
    <el-card style="margin-bottom: 16px">
      <el-row :gutter="12">
        <el-col :span="8">
          <el-input v-model="keyword" placeholder="搜索服务名称..." clearable @keyup.enter="search" />
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="search" :loading="loading">
            <el-icon style="margin-right: 4px"><Search /></el-icon>搜索
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="16" v-loading="loading">
      <el-col :span="6" v-for="s in services" :key="s.serviceId" style="margin-bottom: 16px">
        <el-card shadow="hover" style="cursor: pointer" @click="router.push(`/services/${s.serviceId}`)">
          <h3 style="margin: 0 0 8px">{{ s.title }}</h3>
          <p style="margin: 4px 0; color: #f56c6c; font-size: 18px; font-weight: bold">¥{{ s.price }}</p>
          <p style="margin: 4px 0; color: #909399; font-size: 13px">{{ s.durationMin }} 分钟</p>
          <el-tag size="small" type="success">{{ s.category }}</el-tag>
        </el-card>
      </el-col>
      <el-empty v-if="!services.length && !loading" description="暂无服务" style="width: 100%" />
    </el-row>
  </div>
</template>
