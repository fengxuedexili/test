<template>
  <router-link class="product-card" :to="'/product/d/' + product.id">
    <div class="thumb" @mouseenter="hovering = true" @mouseleave="hovering = false">
      <img :src="currentImage" :alt="product.name" loading="lazy" @error="handleError" />
    </div>
    <div class="body">
      <div class="model">{{ product.model }}</div>
      <div class="name">{{ product.name }}</div>
      <div class="tags" v-if="product.tags && product.tags.length">
        <span class="tag" v-for="tag in product.tags.slice(0, 3)" :key="tag">{{ tag }}</span>
      </div>
    </div>
  </router-link>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { PLACEHOLDER, resolveImageUrl } from '@/utils/image'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const hovering = ref(false)
const failIndex = ref(0)

// 候选图：主图 → 图集 → 占位图（去重、去空）
const candidates = computed(() => {
  const p = props.product || {}
  const list = [p.cover].concat(p.images || []).filter(Boolean).map(resolveImageUrl)
  const unique = []
  list.forEach(url => {
    if (unique.indexOf(url) === -1) {
      unique.push(url)
    }
  })
  return unique.length ? unique : [PLACEHOLDER]
})

const currentImage = computed(() => {
  const p = props.product || {}
  if (hovering.value && p.hoverImage) {
    return resolveImageUrl(p.hoverImage)
  }
  return candidates.value[failIndex.value] || PLACEHOLDER
})

watch(() => props.product && props.product.id, () => {
  failIndex.value = 0
})

function handleError(event) {
  if (failIndex.value < candidates.value.length - 1) {
    failIndex.value += 1
    return
  }
  const img = event && event.target
  if (img && img.src !== PLACEHOLDER) {
    img.src = PLACEHOLDER
    img.onerror = null
  }
}
</script>

