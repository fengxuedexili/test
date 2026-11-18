<template>
  <div class="container">
    <div class="section-head" style="padding-top: 32px">
      <h2>新闻资讯</h2>
      <span class="en">NEWS</span>
    </div>

    <div v-if="list.length" class="news-list">
      <router-link v-for="item in list" :key="item.id" class="news-item" :to="'/news/' + item.id">
        <div class="news-cover">
          <img :src="item.cover ? resolveImageUrl(item.cover) : PLACEHOLDER" :alt="item.title" @error="onImgError" />
        </div>
        <div class="news-body">
          <div class="news-meta">
            <span v-if="item.top" class="news-badge">置顶</span>
            <span>{{ item.publishTime }}</span>
            <span v-if="item.author">· {{ item.author }}</span>
          </div>
          <h3>{{ item.title }}</h3>
          <p>{{ item.summary }}</p>
        </div>
      </router-link>
    </div>
    <div v-else class="empty">{{ loading ? '加载中…' : '暂无资讯' }}</div>

    <div class="pager" v-if="total > pageSize">
      <button :disabled="pageNum <= 1" @click="go(pageNum - 1)">上一页</button>
      <span class="info">{{ pageNum }} / {{ totalPages }}　共 {{ total }} 篇</span>
      <button :disabled="pageNum >= totalPages" @click="go(pageNum + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getArticlePage } from '@/api/site'
import { PLACEHOLDER, onImgError, resolveImageUrl } from '@/utils/image'
import { setSeo } from '@/utils/seo'

const route = useRoute()
const router = useRouter()

const list = ref([])
const total = ref(0)
const loading = ref(false)
const pageSize = 6
const pageNum = ref(1)

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize)))

function load() {
  loading.value = true
  pageNum.value = Number(route.query.page || 1)
  getArticlePage({ pageNum: pageNum.value, pageSize }).then(data => {
    list.value = (data && data.rows) || []
    total.value = (data && data.total) || 0
  }).catch(() => {
    list.value = []
    total.value = 0
  }).finally(() => {
    loading.value = false
  })
}

function go(page) {
  router.push({ path: '/news', query: { page } })
}

onMounted(() => {
  setSeo({ title: '新闻资讯 - ' + (import.meta.env.VITE_SITE_NAME || 'site'), description: '品牌动态、产品资讯与使用指南' })
})

watch(() => route.query, () => {
  if (route.path === '/news') {
    load()
  }
}, { immediate: true })
</script>
