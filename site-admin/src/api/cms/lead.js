import request from '@/utils/request'

// 查询线索列表
export function listLead(query) {
  return request({
    url: '/cms/lead/list',
    method: 'get',
    params: query
  })
}

// 查询线索详细
export function getLead(leadId) {
  return request({
    url: '/cms/lead/' + leadId,
    method: 'get'
  })
}

// 跟进（状态/处理人/备注）
export function updateLead(data) {
  return request({
    url: '/cms/lead',
    method: 'put',
    data: data
  })
}

// 删除线索
export function delLead(leadId) {
  return request({
    url: '/cms/lead/' + leadId,
    method: 'delete'
  })
}
