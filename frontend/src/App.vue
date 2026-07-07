<script setup lang="ts">
import { useUserStore } from './stores/user'
import { useRouter, useRoute } from 'vue-router'

const store = useUserStore()
const router = useRouter()
const route = useRoute()

function handleSelect(index: string) {
  router.push(index)
}

function logout() {
  store.logout()
  router.push('/login')
}
</script>

<template>
  <el-container style="min-height: 100vh">
    <el-aside width="200px" style="background: #304156">
      <div style="height: 60px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 18px; font-weight: bold; border-bottom: 1px solid rgba(255,255,255,.1)">
        在线预约系统
      </div>
      <el-menu
        :default-active="route.path"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        @select="handleSelect"
      >
        <!-- 公开菜单 -->
        <el-menu-item index="/services">
          <el-icon><Search /></el-icon>
          <span>服务浏览</span>
        </el-menu-item>

        <!-- 客户/商户菜单 -->
        <template v-if="store.userId && store.role !== 'ADMIN'">
          <el-menu-item index="/my-bookings">
            <el-icon><Calendar /></el-icon>
            <span>我的预约</span>
          </el-menu-item>
          <el-menu-item index="/my-favorites">
            <el-icon><Star /></el-icon>
            <span>我的收藏</span>
          </el-menu-item>
          <el-sub-menu index="provider">
            <template #title>
              <el-icon><Shop /></el-icon>
              <span>服务商中心</span>
            </template>
            <el-menu-item index="/provider/register">注册为服务商</el-menu-item>
            <el-menu-item index="/provider/dashboard">工作台</el-menu-item>
          </el-sub-menu>
        </template>

        <!-- 管理员菜单 -->
        <el-menu-item v-if="store.role === 'ADMIN'" index="/admin">
          <el-icon><Setting /></el-icon>
          <span>管理后台</span>
        </el-menu-item>

        <!-- 未登录 -->
        <el-menu-item v-if="!store.userId" index="/login">
          <el-icon><User /></el-icon>
          <span>登录/注册</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background: #fff; border-bottom: 1px solid #e6e6e6; display: flex; align-items: center; justify-content: flex-end; padding: 0 20px">
        <template v-if="store.username">
          <el-tag size="small" style="margin-right: 12px">{{ store.role }}</el-tag>
          <span style="margin-right: 16px">你好，{{ store.username }}</span>
          <el-button type="danger" size="small" @click="logout">退出</el-button>
        </template>
        <template v-else>
          <el-button type="primary" size="small" @click="router.push('/login')">登录</el-button>
          <el-button size="small" @click="router.push('/register')" style="margin-left: 8px">注册</el-button>
        </template>
      </el-header>
      <el-main style="background: #f0f2f5; padding: 20px">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style>
body { margin: 0; font-family: -apple-system, 'Helvetica Neue', sans-serif; }
.el-menu { border-right: none; }
.el-aside { overflow: hidden; }
</style>
