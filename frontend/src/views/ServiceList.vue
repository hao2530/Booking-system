<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { serviceApi } from '../api'

const services = ref<any[]>([])
const keyword = ref('')

async function search() {
  const res = await serviceApi.search({ keyword: keyword.value, page: 1, size: 20 })
  services.value = res.data.data.list
}

onMounted(search)
</script>

<template>
  <div class="page">
    <h2>服务列表</h2>
    <div class="search-bar">
      <input v-model="keyword" placeholder="搜索服务..." @keyup.enter="search" />
      <button @click="search">搜索</button>
    </div>
    <div class="grid">
      <div v-for="s in services" :key="s.serviceId" class="card" @click="$router.push(`/services/${s.serviceId}`)">
        <h3>{{ s.title }}</h3>
        <p>¥{{ s.price }} · {{ s.durationMin }}分钟</p>
        <span class="tag">{{ s.category }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page { max-width: 800px; margin: 0 auto; padding: 20px; }
.search-bar { display: flex; gap: 8px; margin-bottom: 20px; }
.search-bar input { flex: 1; padding: 10px; }
.grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px; }
.card { border: 1px solid #ddd; padding: 16px; border-radius: 8px; cursor: pointer; }
.card:hover { box-shadow: 0 2px 8px rgba(0,0,0,.1); }
.tag { background: #e8f5e9; padding: 2px 8px; border-radius: 4px; font-size: 12px; }
</style>
