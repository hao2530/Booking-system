<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { adminApi, auditApi } from '../api'
import { useUserStore } from '../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const store = useUserStore()
const activeTab = ref('providers')

/* 服务商审核 */
const providers = ref<any[]>([])
const statusMap: Record<number, string> = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
const statusType: Record<number, string> = { 0: 'warning', 1: 'success', 2: 'danger' }

async function loadProviders() {
  const res = await adminApi.listProviders()
  providers.value = res.data.data
}

async function audit(id: number, status: number) {
  const label = status === 1 ? '通过' : '拒绝'
  try {
    await ElMessageBox.confirm(`确定${label}该服务商？`, '提示')
    await adminApi.auditProvider(id, status)
    ElMessage.success(`${label}成功`)
    loadProviders()
  } catch { /* canceled */ }
}

/* 时段管理 */
const slots = ref<any[]>([])

async function loadSlots() {
  const res = await adminApi.listSlots()
  slots.value = res.data.data
}

async function deleteSlot(id: number) {
  try {
    await ElMessageBox.confirm('确定删除该时段？', '提示')
    await adminApi.deleteSlot(id)
    ElMessage.success('已删除')
    loadSlots()
  } catch { /* canceled */ }
}

/* 审计日志 */
const logs = ref<any[]>([])
const logPage = ref(1)
const logTotal = ref(0)
const filterStatus = ref('')

async function loadLogs() {
  const res = await auditApi.list({ page: logPage.value, size: 20, status: filterStatus.value })
  logs.value = res.data.data.records
  logTotal.value = res.data.data.total
}

async function reviewLog(logId: number, status: string) {
  const label = status === 'APPROVED' ? '通过' : '拒绝'
  try {
    await ElMessageBox.confirm(`确定${label}该审计记录？`, '提示')
    await auditApi.review({ logId, reviewerId: store.userId, status, remark: `${label} - 管理员审核` })
    ElMessage.success(`${label}成功`)
    loadLogs()
  } catch { /* canceled */ }
}

const actionLabels: Record<string, string> = {
  PROVIDER_REGISTER: '服务商注册',
  SERVICE_CREATE: '创建服务',
  SERVICE_UPDATE: '更新服务',
  SLOT_GENERATE: '生成时段',
  ORDER_CREATE: '创建订单',
  ORDER_CANCEL: '取消订单',
}

onMounted(() => {
  loadProviders()
  loadSlots()
  loadLogs()
})
</script>

<template>
  <el-card>
    <template #header><h2 style="margin: 0">管理后台</h2></template>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="服务商审核" name="providers">
        <el-table :data="providers" stripe>
          <el-table-column prop="providerId" label="ID" width="60" />
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="companyName" label="商家名称" width="200" />
          <el-table-column prop="category" label="分类" width="100" />
          <el-table-column prop="rating" label="评分" width="100" />
          <el-table-column prop="status" label="状态" width="120">
            <template #default="{ row }">
              <el-tag :type="statusType[row.status] || 'info'">{{ statusMap[row.status] || '未知' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button v-if="row.status === 0" type="success" size="small" @click="audit(row.providerId, 1)">通过</el-button>
              <el-button v-if="row.status === 0" type="danger" size="small" @click="audit(row.providerId, 2)">拒绝</el-button>
              <el-tag v-if="row.status === 1" type="success">已审核</el-tag>
              <el-tag v-if="row.status === 2" type="danger">已拒绝</el-tag>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!providers.length" description="暂无服务商" />
      </el-tab-pane>

      <el-tab-pane label="时段管理" name="slots">
        <el-table :data="slots" stripe>
          <el-table-column prop="slotId" label="ID" width="60" />
          <el-table-column prop="serviceId" label="服务ID" width="80" />
          <el-table-column prop="date" label="日期" width="120" />
          <el-table-column prop="startTime" label="开始" width="100" />
          <el-table-column prop="endTime" label="结束" width="100" />
          <el-table-column prop="isAvailable" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.isAvailable === 1 ? 'success' : 'danger'">
                {{ row.isAvailable === 1 ? '可预约' : '已占用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="danger" size="small" @click="deleteSlot(row.slotId)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!slots.length" description="暂无时段" />
      </el-tab-pane>

      <el-tab-pane label="审计日志" name="audit">
        <div style="margin-bottom: 12px">
          <el-select v-model="filterStatus" placeholder="筛选状态" clearable style="width: 140px" @change="loadLogs">
            <el-option label="全部" value="" />
            <el-option label="待审核" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
        </div>
        <el-table :data="logs" stripe>
          <el-table-column prop="logId" label="ID" width="60" />
          <el-table-column prop="action" label="操作" width="140">
            <template #default="{ row }">
              {{ actionLabels[row.action] || row.action }}
            </template>
          </el-table-column>
          <el-table-column prop="targetType" label="目标类型" width="120" />
          <el-table-column prop="targetId" label="目标ID" width="80" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.status === 'PENDING'" type="warning">待审核</el-tag>
              <el-tag v-else-if="row.status === 'APPROVED'" type="success">已通过</el-tag>
              <el-tag v-else-if="row.status === 'REJECTED'" type="danger">已拒绝</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" width="160" />
          <el-table-column prop="createdAt" label="时间" width="160" />
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button v-if="row.status === 'PENDING'" type="success" size="small" @click="reviewLog(row.logId, 'APPROVED')">通过</el-button>
              <el-button v-if="row.status === 'PENDING'" type="danger" size="small" @click="reviewLog(row.logId, 'REJECTED')">拒绝</el-button>
              <span v-else style="color: #999">已处理</span>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination v-if="logTotal > 20" background layout="prev,pager,next" :total="logTotal" v-model:current-page="logPage" @current-change="loadLogs" style="margin-top: 12px" />
        <el-empty v-if="!logs.length" description="暂无审计记录" />
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>
