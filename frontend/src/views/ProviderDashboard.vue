<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { providerApi, serviceApi, slotApi, orderApi } from '../api'
import { useUserStore } from '../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const store = useUserStore()
const provider = ref<any>(null)

// ---- Tab ----
const activeTab = ref('services')

// ---- Services ----
const services = ref<any[]>([])
const showServiceDialog = ref(false)
const serviceForm = ref({ title: '', description: '', price: 0, durationMin: 60, category: '' })
const serviceLoading = ref(false)

// ---- Slots ----
const slotForm = ref({ serviceId: 0, date: '', startTime: '', endTime: '' })
const slotLoading = ref(false)

// ---- Bookings ----
const bookings = ref<any[]>([])
const bookingLoading = ref(false)

async function loadProvider() {
  try {
    const res = await providerApi.byUser(store.userId)
    provider.value = res.data.data
    loadServices()
    loadBookings()
  } catch {
    provider.value = null
  }
}

async function loadServices() {
  if (!provider.value) return
  const res = await serviceApi.byProvider(provider.value.providerId)
  services.value = res.data.data
}

async function createService() {
  if (!provider.value) return
  serviceLoading.value = true
  try {
    await serviceApi.create({
      ...serviceForm.value,
      providerId: provider.value.providerId,
    })
    ElMessage.success('服务创建成功')
    showServiceDialog.value = false
    serviceForm.value = { title: '', description: '', price: 0, durationMin: 60, category: '' }
    loadServices()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.msg || '创建失败')
  } finally {
    serviceLoading.value = false
  }
}

async function deleteService(id: number) {
  try {
    await ElMessageBox.confirm('确定删除该服务？', '提示')
    await serviceApi.delete(id)
    ElMessage.success('已删除')
    loadServices()
  } catch { /* canceled */ }
}

async function generateSlots() {
  slotLoading.value = true
  try {
    const res = await slotApi.generate(slotForm.value)
    ElMessage.success(`已生成 ${res.data.data.length} 个时段`)
    slotForm.value = { serviceId: 0, date: '', startTime: '', endTime: '' }
  } catch (e: any) {
    ElMessage.error(e.response?.data?.msg || '生成失败')
  } finally {
    slotLoading.value = false
  }
}

async function loadBookings() {
  if (!provider.value) return
  bookingLoading.value = true
  try {
    const res = await orderApi.providerBookings(provider.value.providerId)
    bookings.value = res.data.data
  } finally {
    bookingLoading.value = false
  }
}

const statusMap: Record<string, string> = {
  PENDING: '待支付', CONFIRMED: '已确认', CANCELLED: '已取消', COMPLETED: '已完成',
}
const statusType: Record<string, string> = {
  PENDING: 'warning', CONFIRMED: 'success', CANCELLED: 'info', COMPLETED: 'primary',
}

onMounted(loadProvider)
</script>

<template>
  <div v-if="!provider">
    <el-result icon="warning" title="尚未注册为服务商" sub-title="注册后才能发布服务和管理预约">
      <template #extra>
        <el-button type="primary" @click="router.push('/provider/register')">立即注册</el-button>
      </template>
    </el-result>
    <router-view />
  </div>

  <div v-else-if="provider.status === 0">
    <el-result icon="info" title="审核中" sub-title="您的服务商申请正在等待管理员审核，请耐心等待">
      <template #extra>
        <el-tag type="warning" size="large">待审核</el-tag>
      </template>
    </el-result>
  </div>

  <div v-else-if="provider.status === 2">
    <el-result icon="error" title="审核未通过" sub-title="您的服务商申请已被拒绝，请联系管理员">
      <template #extra>
        <el-tag type="danger" size="large">已拒绝</el-tag>
      </template>
    </el-result>
  </div>

  <div v-else>
    <el-card style="margin-bottom: 16px">
      <el-descriptions :column="4">
        <el-descriptions-item label="商家名称">{{ provider.companyName }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ provider.category }}</el-descriptions-item>
        <el-descriptions-item label="评分">
          <el-rate v-model="provider.rating" disabled show-score text-color="#ff9900" score-template="{value}" />
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="我的服务" name="services">
          <div style="margin-bottom: 16px">
            <el-button type="primary" @click="showServiceDialog = true">+ 新建服务</el-button>
          </div>
          <el-table :data="services" stripe>
            <el-table-column prop="title" label="名称" />
            <el-table-column prop="price" label="价格" width="100">
              <template #default="{ row }">¥{{ row.price }}</template>
            </el-table-column>
            <el-table-column prop="durationMin" label="时长(分钟)" width="100" />
            <el-table-column prop="category" label="分类" width="100" />
            <el-table-column label="操作" width="160">
              <template #default="{ row }">
                <el-button type="danger" size="small" @click="deleteService(row.serviceId)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!services.length" description="暂无服务，点击上方按钮创建" />
        </el-tab-pane>

        <el-tab-pane label="生成时段" name="slots">
          <el-form :model="slotForm" label-width="80px" style="max-width: 500px">
            <el-form-item label="选择服务">
              <el-select v-model="slotForm.serviceId" placeholder="选择服务" style="width: 100%">
                <el-option v-for="s in services" :key="s.serviceId" :label="s.title" :value="s.serviceId" />
              </el-select>
            </el-form-item>
            <el-form-item label="日期">
              <el-date-picker v-model="slotForm.date" type="date" placeholder="选择日期"
                value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
            <el-form-item label="开始时间">
              <el-time-picker v-model="slotForm.startTime" placeholder="开始时间"
                value-format="HH:mm" style="width: 100%" />
            </el-form-item>
            <el-form-item label="结束时间">
              <el-time-picker v-model="slotForm.endTime" placeholder="结束时间"
                value-format="HH:mm" style="width: 100%" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="slotLoading" @click="generateSlots">生成时段</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="预约管理" name="bookings">
          <el-table :data="bookings" v-loading="bookingLoading" stripe>
            <el-table-column prop="orderId" label="订单号" width="80" />
            <el-table-column prop="serviceTitle" label="服务" width="150" />
            <el-table-column label="时间" width="200">
              <template #default="{ row }">{{ row.date }} {{ row.startTime }}-{{ row.endTime }}</template>
            </el-table-column>
            <el-table-column prop="totalAmount" label="金额" width="100">
              <template #default="{ row }">¥{{ row.totalAmount }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="statusType[row.status] || 'info'">{{ statusMap[row.status] || row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!bookings.length && !bookingLoading" description="暂无预约" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="showServiceDialog" title="新建服务" width="500px">
      <el-form :model="serviceForm" label-width="100px">
        <el-form-item label="名称"><el-input v-model="serviceForm.title" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="serviceForm.description" type="textarea" rows="3" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="serviceForm.price" :min="0" :precision="2" style="width: 100%" /></el-form-item>
        <el-form-item label="时长(分钟)"><el-input-number v-model="serviceForm.durationMin" :min="15" :step="15" style="width: 100%" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="serviceForm.category" style="width: 100%">
            <el-option label="医疗" value="医疗" /><el-option label="美容" value="美容" />
            <el-option label="健身" value="健身" /><el-option label="教育" value="教育" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showServiceDialog = false">取消</el-button>
        <el-button type="primary" :loading="serviceLoading" @click="createService">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>
