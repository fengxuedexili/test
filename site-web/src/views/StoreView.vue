<template>
  <div class="container store-page">
    <div class="section-head" style="padding-top: 32px">
      <h2>门店查询</h2>
      <span class="en">STORES</span>
    </div>
    <p class="store-intro">全国门店与授权服务网点，欢迎到店体验；出发前建议先电话确认营业时间。</p>

    <div class="store-filter">
      <div class="filter-row">
        <span class="filter-label">省份</span>
        <div class="chips">
          <button :class="['chip', { active: !province }]" @click="province = ''">全部</button>
          <button v-for="p in provinces" :key="p" :class="['chip', { active: province === p }]"
                  @click="province = p">{{ p }}</button>
        </div>
      </div>
      <div class="filter-row">
        <span class="filter-label">类型</span>
        <div class="chips">
          <button :class="['chip', { active: !type }]" @click="type = ''">全部</button>
          <button v-for="t in typeOptions" :key="t.value" :class="['chip', { active: type === t.value }]"
                  @click="type = t.value">{{ t.label }}</button>
        </div>
      </div>
    </div>

    <div v-if="list.length" class="store-grid">
      <div v-for="item in list" :key="item.id" class="store-card">
        <div class="store-top">
          <h3>{{ item.name }}</h3>
          <span :class="['store-type', 'type-' + item.type]">{{ typeLabel(item.type) }}</span>
        </div>
        <p class="store-address">{{ areaText(item) }}</p>
        <div class="store-actions">
          <a v-if="item.phone" :href="'tel:' + item.phone" class="store-link">☎ {{ item.phone }}</a>
          <a v-if="item.lng && item.lat" :href="navUrl(item)" target="_blank" rel="noopener" class="store-link">导航前往</a>
        </div>
      </div>
    </div>
    <div v-else class="empty">{{ loading ? '加载中…' : '该地区暂无门店，可切换省份查看' }}</div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { getStoreProvinces, getStores } from '@/api/site'
import { setSeo } from '@/utils/seo'

const list = ref([])
const provinces = ref([])
const province = ref('')
const type = ref('')
const loading = ref(false)

const typeOptions = [
  { value: '1', label: '旗舰店' },
  { value: '2', label: '专卖店' },
  { value: '3', label: '服务网点' }
]

function typeLabel(value) {
  const hit = typeOptions.find(item => item.value === value)
  return hit ? hit.label : '门店'
}

function areaText(item) {
  const area = [item.province, item.city, item.district].filter(Boolean).join('')
  return area + (item.address || '')
}

function navUrl(item) {
  const name = encodeURIComponent(item.name || '门店')
  return 'https://uri.amap.com/marker?position=' + item.lng + ',' + item.lat + '&name=' + name
}

function loadStores() {
  loading.value = true
  getStores({ province: province.value || undefined, type: type.value || undefined }).then(data => {
    list.value = data || []
  }).catch(() => {
    list.value = []
  }).finally(() => {
    loading.value = false
  })
}

function loadProvinces() {
  getStoreProvinces().then(data => {
    provinces.value = data || []
  }).catch(() => {
    provinces.value = []
  })
}

watch([province, type], loadStores, { immediate: true })

onMounted(() => {
  loadProvinces()
  setSeo({
    title: '门店查询 - ' + (import.meta.env.VITE_SITE_NAME || 'site'),
    description: '查询品牌旗舰店、专卖店与服务网点，支持按省份与门店类型筛选。'
  })
})
</script>
