import DOMPurify from 'dompurify'

/**
 * 富文本净化（前端兜底；后端仍应按需做服务端过滤）
 */
export function sanitizeHtml(html) {
  if (!html) {
    return ''
  }
  return DOMPurify.sanitize(String(html), { USE_PROFILES: { html: true } })
}
