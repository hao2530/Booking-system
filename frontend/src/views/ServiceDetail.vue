<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { serviceApi, slotApi, orderApi, reviewApi } from '../api'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const store = useUserStore()
const service = ref<any>({})
const slots = ref<any[]>([])
const selectedDate = ref('')
const reviews = ref<any[]>([])

async function load() {
  const id = Number(route.params.id)
  const res = await serviceApi.search({ page: 1, size: 100 })
  service.value = res.data.data.list.find((s: any) => s.serviceId === id) || {}
  const r = await reviewApi.byService(id)
  reviews.value = r.data.data
}

async function loadSlots() {
  if (!selectedDate.value) return
  const res = await slotApi.getAvailable(service.value.serviceId, selectedDate.value)
  slots.value = res.data.data
}

async function book(slotId: number) {
  if (!store.userId) return router.push('/login')
  try {
    await orderApi.create({ userId: store.userId, slotId })
    alert('预约成功！')
    selectedDate.value = ''
    slots.value = []
  } catch (e: any) {
    alert(e.response?.data?.msg || '预约失败')
  }
}

onMounted(load)
</script>

<template>
  <div class="page">
    <h2>{{ service.title }}</h2>
    <p>价格: ¥{{ service.price }} | 时长: {{ service.durationMin }}分钟 | 分类: {{ service.category }}</p>

    <h3>选择日期</h3>
    <input type="date" v-model="selectedDate" @change="loadSlots" />

    <div v-if="slots.length" class="slots">
      <button v-for="s in slots" :key="s.slotId" @click="book(s.slotId)" class="slot-btn">
        {{ s.startTime }} - {{ s.endTime }}
      </button>
    </div>
    <p v-else-if="selectedDate">该日期暂无可用时段</p>

    <h3>评价</h3>
    <div v-for="r in reviews" :key="r.reviewId" class="review">
      <strong>{{ '★'.repeat(r.rating) }}</strong>
      <p>{{ r.comment }}</p>
      <small>{{ r.createdAt }}</small>
    </div>
    <p v-if="!reviews.length">暂无评价</p>
  </div>
</template>

<style scoped>
.page { max-width: 600px; margin: 0 auto; padding: 20px; }
.slots { display: flex; flex-wrap: wrap; gap: 8px; margin: 12px 0; }
.slot-btn { padding: 8px 16px; background: #42b883; color: #fff; border: none; border-radius: 4px; cursor: pointer; }
.review { border-bottom: 1px solid #eee; padding: 8px 0; }
</style>
