<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi, reviewApi } from '../api'
import { useUserStore } from '../stores/user'

const router = useRouter()
const store = useUserStore()
const bookings = ref<any[]>([])
const rating = ref(5)
const comment = ref('')
const showReview = ref(false)
const reviewOrderId = ref(0)

async function load() {
  if (!store.userId) return router.push('/login')
  const res = await orderApi.my(store.userId)
  bookings.value = res.data.data
}

async function cancel(id: number) {
  if (!confirm('确定取消？')) return
  await orderApi.cancel(id)
  load()
}

function openReview(orderId: number) {
  reviewOrderId.value = orderId
  showReview.value = true
}

async function submitReview() {
  await reviewApi.submit({ userId: store.userId, orderId: reviewOrderId.value, rating: rating.value, comment: comment.value })
  alert('评价成功')
  showReview.value = false
  load()
}

onMounted(load)
</script>

<template>
  <div class="page">
    <h2>我的预约</h2>
    <div v-for="b in bookings" :key="b.orderId" class="booking">
      <p>订单 #{{ b.orderId }} — ¥{{ b.totalAmount }} — 状态: {{ b.status }}</p>
      <p><small>{{ b.createTime }}</small></p>
      <button v-if="b.status === 'CONFIRMED'" @click="cancel(b.orderId)">取消</button>
      <button v-if="b.status === 'CONFIRMED'" @click="openReview(b.orderId)">评价</button>
    </div>
    <p v-if="!bookings.length">暂无预约</p>

    <div v-if="showReview" class="modal">
      <h3>评价</h3>
      <select v-model.number="rating">
        <option v-for="i in 5" :key="i" :value="i">{{ i }}星</option>
      </select>
      <textarea v-model="comment" placeholder="写下你的评价..."></textarea>
      <button @click="submitReview">提交</button>
      <button @click="showReview = false">取消</button>
    </div>
  </div>
</template>

<style scoped>
.page { max-width: 600px; margin: 0 auto; padding: 20px; }
.booking { border: 1px solid #ddd; padding: 12px; border-radius: 6px; margin-bottom: 12px; }
.booking button { margin-right: 8px; }
.modal { position: fixed; top: 50%; left: 50%; transform: translate(-50%,-50%); background: #fff; padding: 24px; border: 1px solid #ddd; border-radius: 8px; display: flex; flex-direction: column; gap: 12px; }
</style>
