export const PLACEHOLDER =
  'data:image/svg+xml;charset=utf-8,' +
  encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" width="480" height="360">' +
      '<rect width="100%" height="100%" fill="#f4f5f7"/>' +
      '<text x="50%" y="50%" fill="#b9bec7" font-size="18" text-anchor="middle" dominant-baseline="middle">site</text>' +
      '</svg>'
  )

const API_BASE = (import.meta.env.VITE_API_BASE || '').replace(/\/$/, '')

/**
 * 统一图片地址：
 * 1) 反斜杠转正斜杠（Windows 上传路径）
 * 2) /profile/** 是后端资源，开发环境拼上 API_BASE，生产走 Nginx 同域代理
 */
export function resolveImageUrl(url) {
  if (!url) {
    return PLACEHOLDER
  }
  let result = String(url).replace(/\\/g, '/')
  if (result.indexOf('/profile') === 0 && API_BASE) {
    result = API_BASE + result
  }
  return result
}

export function onImgError(event) {
  const img = event && event.target
  if (img && img.src !== PLACEHOLDER) {
    img.src = PLACEHOLDER
    img.onerror = null
  }
}

