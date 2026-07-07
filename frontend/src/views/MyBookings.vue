<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi, reviewApi, slotApi } from '../api'
import { useUserStore } from '../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const store = useUserStore()
const bookings = ref<any[]>([])
const loading = ref(false)

const dialogVisible = ref(false)
const reviewForm = ref({ orderId: 0, rating: 5, comment: '' })

const modifyDialogVisible = ref(false)
const modifyForm = ref({ orderId: 0, serviceId: 0, date: '', slots: [] as any[], selectedSlotId: 0 })

async function load() {
  if (!store.userId) return router.push('/login')
  loading.value = true
  try {
    const res = await orderApi.my(store.userId)
    bookings.value = res.data.data
  } finally {
    loading.value = false
  }
}

async function cancel(id: number) {
  try {
    await ElMessageBox.confirm('确定取消该预约？', '提示')
    await orderApi.cancel(id)
    ElMessage.success('已取消')
    load()
  } catch { /* canceled */ }
}

function openReview(orderId: number) {
  reviewForm.value = { orderId, rating: 5, comment: '' }
  dialogVisible.value = true
}

async function submitReview() {
  await reviewApi.submit({
    userId: store.userId,
    orderId: reviewForm.value.orderId,
    rating: reviewForm.value.rating,
    comment: reviewForm.value.comment,
  })
  ElMessage.success('评价成功')
  dialogVisible.value = false
  load()
}

async function openModify(order: any) {
  modifyForm.value = { orderId: order.orderId, serviceId: order.serviceId, date: '', slots: [], selectedSlotId: 0 }
  modifyDialogVisible.value = true
}

async function loadModifySlots() {
  if (!modifyForm.value.date) return
  try {
    const res = await slotApi.getAvailable(modifyForm.value.serviceId, modifyForm.value.date)
    modifyForm.value.slots = res.data.data
  } catch (e: any) {
    ElMessage.error(e.response?.data?.msg || '加载时段失败')
  }
}

async function submitModify() {
  try {
    await orderApi.create({ userId: store.userId, slotId: modifyForm.value.selectedSlotId })
    await orderApi.cancel(modifyForm.value.orderId)
    ElMessage.success('修改成功')
    modifyDialogVisible.value = false
    load()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.msg || '修改失败')
  }
}

const statusMap: Record<string, string> = {
  PENDING: '待支付',
  CONFIRMED: '已确认',
  CANCELLED: '已取消',
  COMPLETED: '已完成',
}
const statusType: Record<string, string> = {
  PENDING: 'warning',
  CONFIRMED: 'success',
  CANCELLED: 'info',
  COMPLETED: 'primary',
}

onMounted(load)
</script>

<template>
  <el-card>
    <template #header><h2 style="margin: 0">我的预约</h2></template>

    <el-table :data="bookings" v-loading="loading" stripe style="width: 100%">
      <el-table-column prop="orderId" label="订单号" width="80" />
      <el-table-column prop="totalAmount" label="金额" width="100">
        <template #default="{ row }">¥{{ row.totalAmount }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="statusType[row.status] || 'info'">{{ statusMap[row.status] || row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="预约时间" width="180" />
      <el-table-column label="操作" min-width="250">
        <template #default="{ row }">
          <el-button v-if="row.status === 'CONFIRMED'" type="primary" size="small" @click="openModify(row)">修改时间</el-button>
          <el-button v-if="row.status === 'CONFIRMED'" type="danger" size="small" @click="cancel(row.orderId)">取消</el-button>
          <el-button v-if="row.status === 'COMPLETED'" type="primary" size="small" @click="openReview(row.orderId)">评价</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="!bookings.length && !loading" description="暂无预约记录" />
  </el-card>

  <el-dialog v-model="dialogVisible" title="评价" width="400px">
    <div style="text-align: center; margin-bottom: 16px">
      <el-rate v-model="reviewForm.rating" :max="5" show-text />
    </div>
    <el-input v-model="reviewForm.comment" type="textarea" rows="3" placeholder="写下你的评价..." />
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitReview">提交</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="modifyDialogVisible" title="修改预约时间" width="500px">
    <el-form :model="modifyForm" label-width="80px">
      <el-form-item label="选择日期">
        <el-date-picker v-model="modifyForm.date" type="date" placeholder="选择日期"
          value-format="YYYY-MM-DD" @change="loadModifySlots" style="width: 100%" />
      </el-form-item>
      <el-form-item label="选择时段">
        <el-radio-group v-model="modifyForm.selectedSlotId" v-if="modifyForm.slots.length">
          <el-button
            v-for="s in modifyForm.slots" :key="s.slotId"
            type="primary" plain
            @click="modifyForm.selectedSlotId = s.slotId"
            :class="{ 'is-active': modifyForm.selectedSlotId === s.slotId }"
          >
            {{ s.startTime }} - {{ s.endTime }}
          </el-button>
        </el-radio-group>
        <el-empty v-else-if="modifyForm.date" description="该日期暂无可用时段" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="modifyDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitModify" :disabled="!modifyForm.selectedSlotId">确认修改</el-button>
    </template>
  </el-dialog>
</template>
