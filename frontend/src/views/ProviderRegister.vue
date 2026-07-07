<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { providerApi } from '../api'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const store = useUserStore()
const form = ref({ companyName: '', category: '' })
const loading = ref(false)
const submitted = ref(false)

async function submit() {
  if (!store.userId) return router.push('/login')
  loading.value = true
  try {
    await providerApi.register({ userId: store.userId, ...form.value })
    ElMessage.success('提交成功，等待管理员审核')
    submitted.value = true
  } catch (e: any) {
    ElMessage.error(e.response?.data?.msg || '提交失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div style="display: flex; justify-content: center; padding-top: 60px">
    <el-card style="width: 500px">
      <template #header><h2 style="text-align: center; margin: 0">申请成为商户</h2></template>
      
      <div v-if="submitted">
        <el-result icon="success" title="申请已提交" sub-title="请等待管理员审核，审核通过后即可成为商户">
          <template #extra>
            <el-button type="primary" @click="router.push('/services')">返回首页</el-button>
          </template>
        </el-result>
      </div>
      
      <el-form v-else :model="form" label-width="100px">
        <el-form-item label="商家名称">
          <el-input v-model="form.companyName" placeholder="如：阳光健身中心" />
        </el-form-item>
        <el-form-item label="服务分类">
          <el-select v-model="form.category" placeholder="选择分类" style="width: 100%">
            <el-option label="医疗" value="医疗" />
            <el-option label="美容" value="美容" />
            <el-option label="健身" value="健身" />
            <el-option label="教育" value="教育" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="submit" style="width: 100%">提交审核申请</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>
