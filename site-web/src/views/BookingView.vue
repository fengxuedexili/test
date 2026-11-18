<template>
  <div class="container booking-page">
    <div class="section-head" style="padding-top: 32px">
      <h2>预约体验</h2>
      <span class="en">BOOK A VISIT</span>
    </div>

    <div class="form-card">
      <p class="form-intro">留下联系方式，我们会尽快与您联系（信息仅用于本次沟通）。</p>

      <div class="form-row">
        <label>咨询类型</label>
        <select v-model="form.type">
          <option value="1">预约体验</option>
          <option value="2">招商加盟</option>
          <option value="3">招聘</option>
          <option value="4">售后服务</option>
        </select>
      </div>
      <div class="form-row">
        <label>姓名 <em>*</em></label>
        <input v-model.trim="form.name" maxlength="64" placeholder="请输入姓名" />
      </div>
      <div class="form-row">
        <label>手机号 <em>*</em></label>
        <input v-model.trim="form.phone" maxlength="20" placeholder="请输入手机号" />
      </div>
      <div class="form-row">
        <label>所在地区</label>
        <div class="region">
          <input v-model.trim="form.province" placeholder="省" />
          <input v-model.trim="form.city" placeholder="市" />
          <input v-model.trim="form.district" placeholder="区/县" />
        </div>
      </div>
      <div class="form-row">
        <label>需求描述</label>
        <textarea v-model.trim="form.content" rows="4" maxlength="1000" placeholder="如：想预约到店体验 J6019HS"></textarea>
      </div>

      <div class="form-row" v-if="captcha.enabled">
        <label>验证码 <em>*</em></label>
        <div class="captcha">
          <input v-model.trim="form.code" maxlength="8" placeholder="请输入右侧结果" />
          <img v-if="captcha.img" :src="'data:image/gif;base64,' + captcha.img" alt="验证码" title="点击刷新"
               @click="loadCaptcha" />
          <span v-else class="captcha-loading" @click="loadCaptcha">点击加载</span>
        </div>
      </div>

      <!-- 蜜罐字段：正常用户不可见 -->
      <input v-model="form.website" class="hp-field" tabindex="-1" autocomplete="off" aria-hidden="true" />

      <div class="form-actions">
        <button class="btn btn-primary" :disabled="submitting || cooldown > 0" @click="submit">
          {{ cooldown > 0 ? '请 ' + cooldown + 's 后再试' : (submitting ? '提交中…' : '提交预约') }}
        </button>
        <span v-if="error" class="error-text">{{ error }}</span>
        <span v-if="success" class="success-text">{{ success }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getCaptcha, submitLead } from '@/api/site'
import { setSeo } from '@/utils/seo'

const route = useRoute()

const form = reactive({
  type: '1',
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  content: '',
  code: '',
  website: ''
})

const captcha = reactive({ enabled: true, img: '', uuid: '' })
const submitting = ref(false)
const error = ref('')
const success = ref('')
const cooldown = ref(0)
let cooldownTimer = null

function newRequestId() {
  if (window.crypto && window.crypto.randomUUID) {
    return window.crypto.randomUUID()
  }
  return Date.now().toString(36) + Math.random().toString(36).slice(2, 10)
}

let requestId = newRequestId()

function collectUtm() {
  const params = []
  Object.keys(route.query).forEach(key => {
    if (key.indexOf('utm_') === 0) {
      params.push(key + '=' + route.query[key])
    }
  })
  return params.join('&')
}

function loadCaptcha() {
  getCaptcha().then(data => {
    captcha.enabled = data.captchaEnabled !== false
    captcha.img = data.img || ''
    captcha.uuid = data.uuid || ''
    form.code = ''
  }).catch(() => {
    captcha.img = ''
  })
}

function validate() {
  if (!form.name) {
    error.value = '请输入姓名'
    return false
  }
  if (!/^1[3-9]\d{9}$/.test(form.phone)) {
    error.value = '手机号格式不正确'
    return false
  }
  if (captcha.enabled && !form.code) {
    error.value = '请输入验证码'
    return false
  }
  return true
}

function startCooldown(seconds) {
  cooldown.value = seconds
  if (cooldownTimer) {
    clearInterval(cooldownTimer)
  }
  cooldownTimer = setInterval(() => {
    cooldown.value -= 1
    if (cooldown.value <= 0) {
      clearInterval(cooldownTimer)
      cooldownTimer = null
    }
  }, 1000)
}

function submit() {
  error.value = ''
  success.value = ''
  if (!validate()) {
    return
  }
  submitting.value = true
  submitLead({
    type: form.type,
    name: form.name,
    phone: form.phone,
    province: form.province,
    city: form.city,
    district: form.district,
    content: form.content,
    code: form.code,
    uuid: captcha.uuid,
    website: form.website,
    source: route.fullPath,
    utm: collectUtm(),
    requestId
  }).then(() => {
    success.value = '提交成功，我们会尽快联系您'
    form.name = ''
    form.phone = ''
    form.province = ''
    form.city = ''
    form.district = ''
    form.content = ''
    form.code = ''
    requestId = newRequestId()
    loadCaptcha()
  }).catch(err => {
    error.value = err.message || '提交失败，请稍后再试'
    if (err.code === 4003) {
      startCooldown(60)
    }
    if (err.code === 4001) {
      loadCaptcha()
    }
  }).finally(() => {
    submitting.value = false
  })
}

onMounted(() => {
  setSeo({ title: '预约体验 - ' + (import.meta.env.VITE_SITE_NAME || 'site'), description: '预约到店体验、招商加盟与售后服务' })
  loadCaptcha()
})
</script>
