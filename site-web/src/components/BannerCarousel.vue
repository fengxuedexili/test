<template>
  <section v-if="items.length" class="hero-carousel"
           @mouseenter="paused = true" @mouseleave="paused = false">
    <div v-for="(item, index) in items" :key="item.id" v-show="index === current" class="slide">
      <video v-if="item.mediaType === '2' && item.videoUrl" :src="item.videoUrl" :poster="item.imageUrl"
             muted loop autoplay playsinline></video>
      <img v-else :src="item.imageUrl || PLACEHOLDER" :alt="item.title" @error="onImgError" />
      <div class="mask"></div>
      <div class="container caption">
        <h1>{{ item.title }}</h1>
        <p v-if="item.subtitle">{{ item.subtitle }}</p>
        <router-link v-if="item.linkUrl" class="btn btn-primary" :to="item.linkUrl">
          {{ item.btnText || '了解更多' }}
        </router-link>
      </div>
    </div>

    <template v-if="items.length > 1">
      <button class="arrow prev" type="button" @click="prev">‹</button>
      <button class="arrow next" type="button" @click="next">›</button>
      <div class="dots">
        <span v-for="(item, index) in items" :key="item.id"
              :class="{ active: index === current }" @click="go(index)"></span>
      </div>
    </template>
  </section>
</template>

<script setup>
import { onMounted, onUnmounted, ref, watch } from 'vue'
import { PLACEHOLDER, onImgError } from '@/utils/image'

const props = defineProps({
  items: {
    type: Array,
    default: () => []
  }
})

const current = ref(0)
const paused = ref(false)
let timer = null

function next() {
  if (!props.items.length) {
    return
  }
  current.value = (current.value + 1) % props.items.length
}

function prev() {
  if (!props.items.length) {
    return
  }
  current.value = (current.value - 1 + props.items.length) % props.items.length
}

function go(index) {
  current.value = index
}

onMounted(() => {
  timer = setInterval(() => {
    if (!paused.value) {
      next()
    }
  }, 6000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})

watch(() => props.items.length, () => {
  current.value = 0
})
</script>
