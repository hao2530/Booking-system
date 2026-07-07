<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { serviceApi, slotApi, orderApi, reviewApi, favoriteApi } from '../api'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const store = useUserStore()
const service = ref<any>({})
const slots = ref<any[]>([])
const selectedDate = ref('')
const reviews = ref<any[]>([])
const loading = ref(false)
const booking = ref(false)
const isFavorite = ref(false)

async function load() {
  const id = Number(route.params.id)
  const res = await serviceApi.search({ page: 1, size: 100 })
  service.value = res.data.data.list.find((s: any) => s.serviceId === id) || {}
  const r = await reviewApi.byService(id)
  reviews.value = r.data.data
  
  if (store.userId) {
    const favRes = await favoriteApi.check(store.userId, id)
    isFavorite.value = favRes.data.data.isFavorite
  }
}

async function loadSlots() {
  if (!selectedDate.value) return
  loading.value = true
  try {
    const res = await slotApi.getAvailable(service.value.serviceId, selectedDate.value)
    slots.value = res.data.data
  } finally {
    loading.value = false
  }
}

async function book(slotId: number) {
  if (!store.userId) return router.push('/login')
  booking.value = true
  try {
    await orderApi.create({ userId: store.userId, slotId })
    ElMessage.success('预约成功！')
    selectedDate.value = ''
    slots.value = []
  } catch (e: any) {
    ElMessage.error(e.response?.data?.msg || '预约失败')
  } finally {
    booking.value = false
  }
}

async function toggleFavorite() {
  if (!store.userId) return router.push('/login')
  try {
    if (isFavorite.value) {
      await favoriteApi.remove(store.userId, service.value.serviceId)
      isFavorite.value = false
      ElMessage.success('已取消收藏')
    } else {
      await favoriteApi.add(store.userId, service.value.serviceId)
      isFavorite.value = true
      ElMessage.success('收藏成功')
    }
  } catch (e: any) {
    ElMessage.error(e.response?.data?.msg || '操作失败')
  }
}

onMounted(load)
</script>

<template>
  <el-card>
    <template #header>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <h2 style="margin: 0">{{ service.title }}</h2>
        <div style="display: flex; gap: 8px">
          <el-tag type="success">{{ service.category }}</el-tag>
          <el-button 
            type="primary" 
            :icon="isFavorite ? 'StarFilled' : 'Star'"
            :text="isFavorite"
            @click="toggleFavorite"
          >
            {{ isFavorite ? '已收藏' : '收藏' }}
          </el-button>
        </div>
      </div>
    </template>

    <el-descriptions :column="3" border>
      <el-descriptions-item label="价格">
        <span style="color: #f56c6c; font-size: 20px; font-weight: bold">¥{{ service.price }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="时长">{{ service.durationMin }} 分钟</el-descriptions-item>
      <el-descriptions-item label="服务商ID">{{ service.providerId }}</el-descriptions-item>
    </el-descriptions>

    <el-divider />

    <h3>选择日期预约</h3>
    <el-date-picker v-model="selectedDate" type="date" placeholder="选择日期"
      value-format="YYYY-MM-DD" @change="loadSlots" style="width: 200px" />

    <div v-loading="loading" style="margin-top: 16px; min-height: 50px">
      <el-radio-group v-model="selectedDate" v-if="slots.length" style="display: flex; flex-wrap: wrap; gap: 8px">
        <el-button
          v-for="s in slots" :key="s.slotId"
          type="primary" plain
          :loading="booking"
          @click="book(s.slotId)"
        >
          {{ s.startTime }} - {{ s.endTime }}
        </el-button>
      </el-radio-group>
      <el-empty v-else-if="selectedDate" description="该日期暂无可用时段" />
    </div>

    <el-divider />

    <h3>用户评价</h3>
    <div v-if="reviews.length">
      <div v-for="r in reviews" :key="r.reviewId" style="border-bottom: 1px solid #eee; padding: 12px 0">
        <el-rate v-model="r.rating" disabled show-score text-color="#ff9900" score-template="{value}" />
        <p style="margin: 4px 0">{{ r.comment }}</p>
        <small style="color: #909399">{{ r.createdAt }}</small>
      </div>
    </div>
    <el-empty v-else description="暂无评价" />
  </el-card>
</template>
