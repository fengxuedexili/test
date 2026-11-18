<template>
  <header class="site-header">
    <div class="container inner">
      <router-link class="brand" to="/">
        <span class="dot"></span>
        <span>{{ siteName }}</span>
      </router-link>
      <nav class="site-nav">
        <router-link to="/">首页</router-link>
        <router-link to="/product">产品中心</router-link>
        <router-link to="/news">新闻资讯</router-link>
        <router-link to="/store">门店查询</router-link>
        <router-link to="/booking">预约体验</router-link>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'

const store = useStore()
const siteName = computed(() => store.state.config.info.name || import.meta.env.VITE_SITE_NAME || 'site')
</script>
