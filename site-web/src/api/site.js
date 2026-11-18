import request from './request'

// 站点信息
export function getSiteConfig() {
  return request({ url: '/api/site/config', method: 'get' })
}

// 栏目树（type: 1 产品 / 2 资讯）
export function getCategoryTree(type = 1) {
  return request({ url: '/api/site/category/tree', method: 'get', params: { type } })
}

// 产品分页
export function getProductPage(params) {
  return request({ url: '/api/site/product/page', method: 'get', params })
}

// 推荐产品
export function getRecommendProducts(limit = 6) {
  return request({ url: '/api/site/product/recommend', method: 'get', params: { limit } })
}

// 轮播（position: home / product / brand）
export function getBanners(position = 'home') {
  return request({ url: '/api/site/banner', method: 'get', params: { position } })
}

// 图形验证码（若依内置接口）
export function getCaptcha() {
  return request({ url: '/captchaImage', method: 'get' })
}

// 提交线索（预约/加盟/招聘/售后）
export function submitLead(data) {
  return request({ url: '/api/site/lead', method: 'post', data })
}

// 资讯分页
export function getArticlePage(params) {
  return request({ url: '/api/site/article/page', method: 'get', params })
}

// 资讯详情
export function getArticleDetail(articleId) {
  return request({ url: '/api/site/article/' + articleId, method: 'get' })
}

// 单页（品牌/隐私/服务说明）
export function getPage(slug) {
  return request({ url: '/api/site/page/' + slug, method: 'get' })
}

// 产品详情
export function getProductDetail(productId) {
  return request({ url: '/api/site/product/' + productId, method: 'get' })
}

// 门店列表（province / city / type 可选）
export function getStores(params) {
  return request({ url: '/api/site/store', method: 'get', params })
}

// 可筛选门店省份
export function getStoreProvinces() {
  return request({ url: '/api/site/store/provinces', method: 'get' })
}
