<template>
  <div class="container">
    <div class="empty">
      <h2>404</h2>
      <p>页面不存在或已被移除</p>
      <router-link class="btn btn-primary" to="/">返回首页</router-link>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { setSeo } from '@/utils/seo'

onMounted(() => {
  setSeo({ title: '页面不存在 - ' + (import.meta.env.VITE_SITE_NAME || 'site'), robots: 'noindex,nofollow' })
})
</script>
