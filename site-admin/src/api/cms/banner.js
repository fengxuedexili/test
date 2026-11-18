import request from '@/utils/request'

// 查询 Banner 列表
export function listBanner(query) {
  return request({
    url: '/cms/banner/list',
    method: 'get',
    params: query
  })
}

// 查询 Banner 详细
export function getBanner(bannerId) {
  return request({
    url: '/cms/banner/' + bannerId,
    method: 'get'
  })
}

// 新增 Banner
export function addBanner(data) {
  return request({
    url: '/cms/banner',
    method: 'post',
    data: data
  })
}

// 修改 Banner
export function updateBanner(data) {
  return request({
    url: '/cms/banner',
    method: 'put',
    data: data
  })
}

// 删除 Banner
export function delBanner(bannerId) {
  return request({
    url: '/cms/banner/' + bannerId,
    method: 'delete'
  })
}
