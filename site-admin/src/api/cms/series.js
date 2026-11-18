import request from '@/utils/request'

// 查询系列列表
export function listSeries(query) {
  return request({
    url: '/cms/series/list',
    method: 'get',
    params: query
  })
}

// 查询系列详细
export function getSeries(seriesId) {
  return request({
    url: '/cms/series/' + seriesId,
    method: 'get'
  })
}

// 新增系列
export function addSeries(data) {
  return request({
    url: '/cms/series',
    method: 'post',
    data: data
  })
}

// 修改系列
export function updateSeries(data) {
  return request({
    url: '/cms/series',
    method: 'put',
    data: data
  })
}

// 删除系列
export function delSeries(seriesId) {
  return request({
    url: '/cms/series/' + seriesId,
    method: 'delete'
  })
}
