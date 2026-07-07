<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { adminApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('providers')
const providers = ref<any[]>([])
const slots = ref<any[]>([])

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

async function deleteProvider(id: number) {
  try {
    await ElMessageBox.confirm('确定删除该商户？此操作不可撤销。', '警告', { type: 'warning' })
    await adminApi.deleteProvider(id)
    ElMessage.success('已删除')
    loadProviders()
  } catch { /* canceled */ }
}

onMounted(() => {
  loadProviders()
  loadSlots()
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
          <el-table-column label="操作" width="280">
            <template #default="{ row }">
              <el-button v-if="row.status === 0" type="success" size="small" @click="audit(row.providerId, 1)">通过</el-button>
              <el-button v-if="row.status === 0" type="danger" size="small" @click="audit(row.providerId, 2)">拒绝</el-button>
              <el-tag v-if="row.status === 1" type="success">已审核</el-tag>
              <el-tag v-if="row.status === 2" type="danger">已拒绝</el-tag>
              <el-button type="danger" size="small" style="margin-left: 8px" @click="deleteProvider(row.providerId)">删除</el-button>
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
    </el-tabs>
  </el-card>
</template>
