<template>
  <div class="container">
    <div class="section-head" style="padding-top: 32px">
      <h2>产品中心</h2>
      <span class="en">PRODUCTS</span>
    </div>

    <div class="list-layout">
      <aside class="sidebar">
        <h3>产品分类</h3>
        <div class="group" v-for="root in categories" :key="root.id">
          <router-link :to="{ path: '/product', query: { categoryId: root.id } }"
                       :class="{ active: isActive(root.id) }">{{ root.name }}</router-link>
          <ul class="children" v-if="root.children && root.children.length">
            <li v-for="child in root.children" :key="child.id">
              <router-link :to="{ path: '/product', query: { categoryId: child.id } }"
                           :class="{ active: isActive(child.id) }">{{ child.name }}</router-link>
            </li>
          </ul>
        </div>
        <div v-if="!categories.length" class="empty">暂无分类</div>
      </aside>

      <section>
        <div class="toolbar">
          <input v-model="keyword" placeholder="搜索产品名称 / 型号" @keyup.enter="search" />
          <button class="btn btn-primary" @click="search">搜索</button>
        </div>

        <div class="product-grid" v-if="products.length">
          <ProductCard v-for="item in products" :key="item.id" :product="item" />
        </div>
        <div v-else class="empty">{{ loading ? '加载中…' : '暂无产品' }}</div>

        <div class="pager" v-if="total > pageSize">
          <button :disabled="pageNum <= 1" @click="go(pageNum - 1)">上一页</button>
          <span class="info">{{ pageNum }} / {{ totalPages }}　共 {{ total }} 件</span>
          <button :disabled="pageNum >= totalPages" @click="go(pageNum + 1)">下一页</button>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCategoryTree, getProductPage } from '@/api/site'
import ProductCard from '@/components/ProductCard.vue'
import { setSeo } from '@/utils/seo'

const route = useRoute()
const router = useRouter()

const categories = ref([])
const products = ref([])
const total = ref(0)
const pageSize = 8
const pageNum = ref(1)
const keyword = ref('')
const loading = ref(false)

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize)))

function currentCategoryId() {
  const value = route.query.categoryId
  return value ? Number(value) : undefined
}

function isActive(id) {
  return currentCategoryId() === id
}

function load() {
  loading.value = true
  pageNum.value = Number(route.query.page || 1)
  keyword.value = route.query.keyword || ''
  getProductPage({
    pageNum: pageNum.value,
    pageSize,
    categoryId: currentCategoryId(),
    keyword: keyword.value || undefined
  }).then(data => {
    products.value = (data && data.rows) || []
    total.value = (data && data.total) || 0
  }).catch(() => {
    products.value = []
    total.value = 0
  }).finally(() => {
    loading.value = false
  })
}

function search() {
  router.push({ path: '/product', query: { categoryId: route.query.categoryId, keyword: keyword.value || undefined, page: 1 } })
}

function go(page) {
  router.push({ path: '/product', query: { categoryId: route.query.categoryId, keyword: route.query.keyword, page } })
}

onMounted(() => {
  setSeo({ title: '产品中心 - ' + (import.meta.env.VITE_SITE_NAME || 'site'), description: '全部产品分类与筛选' })
  getCategoryTree(1).then(data => {
    categories.value = data || []
  }).catch(() => {
    categories.value = []
  })
})

watch(() => route.query, () => {
  if (route.path === '/product') {
    load()
  }
}, { immediate: true })
</script>
