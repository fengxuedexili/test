import request from '@/utils/request'

// 查询栏目列表
export function listCategory(query) {
  return request({
    url: '/cms/category/list',
    method: 'get',
    params: query
  })
}

// 查询栏目树
export function listCategoryTree(type) {
  return request({
    url: '/cms/category/tree',
    method: 'get',
    params: { type: type }
  })
}

// 查询栏目详细
export function getCategory(categoryId) {
  return request({
    url: '/cms/category/' + categoryId,
    method: 'get'
  })
}

// 新增栏目
export function addCategory(data) {
  return request({
    url: '/cms/category',
    method: 'post',
    data: data
  })
}

// 修改栏目
export function updateCategory(data) {
  return request({
    url: '/cms/category',
    method: 'put',
    data: data
  })
}

// 删除栏目
export function delCategory(categoryId) {
  return request({
    url: '/cms/category/' + categoryId,
    method: 'delete'
  })
}
