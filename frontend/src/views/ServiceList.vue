<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { serviceApi } from '../api'

const router = useRouter()
const services = ref<any[]>([])
const keyword = ref('')
const selectedCategory = ref('')
const sortBy = ref('default')
const loading = ref(false)

const categories = ['全部', '医疗', '美容', '健身', '教育', '其他']

async function search() {
  loading.value = true
  try {
    const res = await serviceApi.search({ 
      keyword: keyword.value, 
      category: selectedCategory.value === '全部' ? '' : selectedCategory.value,
      sort: sortBy.value,
      page: 1, 
      size: 100 
    })
    services.value = res.data.data.list
  } finally {
    loading.value = false
  }
}

const filteredServices = computed(() => {
  return services.value
})

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
          <el-select v-model="selectedCategory" placeholder="分类" style="width: 100%" @change="search">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="sortBy" placeholder="排序" style="width: 100%" @change="search">
            <el-option label="默认" value="default" />
            <el-option label="价格从低到高" value="price_asc" />
            <el-option label="价格从高到低" value="price_desc" />
            <el-option label="评分最高" value="rating_desc" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="search" :loading="loading">搜索</el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card style="margin-bottom: 16px">
      <template #header><h3 style="margin: 0">热门服务</h3></template>
      <div style="display: flex; gap: 16px; flex-wrap: wrap">
        <div 
          v-for="s in (services.slice(0, 4))" 
          :key="s.serviceId" 
          style="width: 220px; cursor: pointer; padding: 12px; border: 1px solid #eee; border-radius: 8px"
          @click="router.push(`/services/${s.serviceId}`)"
        >
          <h4 style="margin: 0 0 8px">{{ s.title }}</h4>
          <p style="margin: 4px 0; color: #f56c6c; font-weight: bold">¥{{ s.price }}</p>
          <el-rate v-model="(s.rating || 5)" disabled :show-score="false" text-color="#ff9900" />
        </div>
        <el-empty v-if="services.length < 4" description="暂无热门服务" style="flex: 1" />
      </div>
    </el-card>

    <el-card>
      <template #header><h3 style="margin: 0">全部服务</h3></template>
      <el-row :gutter="16" v-loading="loading">
        <el-col :span="6" v-for="s in filteredServices" :key="s.serviceId" style="margin-bottom: 16px">
          <el-card shadow="hover" style="cursor: pointer" @click="router.push(`/services/${s.serviceId}`)">
            <h3 style="margin: 0 0 8px">{{ s.title }}</h3>
            <p style="margin: 4px 0; color: #f56c6c; font-size: 18px; font-weight: bold">¥{{ s.price }}</p>
            <p style="margin: 4px 0; color: #909399; font-size: 13px">{{ s.durationMin }} 分钟</p>
            <div style="display: flex; align-items: center; justify-content: space-between">
              <el-tag size="small" type="success">{{ s.category }}</el-tag>
              <el-rate v-model="(s.rating || 5)" disabled :show-score="false" text-color="#ff9900" />
            </div>
          </el-card>
        </el-col>
        <el-empty v-if="!services.length && !loading" description="暂无服务" style="width: 100%" />
      </el-row>
    </el-card>
  </div>
</template>
