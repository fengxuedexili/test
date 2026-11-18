import request from '@/utils/request'

// 首页看板汇总
export function getDashboardSummary() {
  return request({
    url: '/cms/dashboard/summary',
    method: 'get'
  })
}
