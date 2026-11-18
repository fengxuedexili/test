const SITE_NAME = import.meta.env.VITE_SITE_NAME || 'site'

export function setSeo(options = {}) {
  const title = options.title || SITE_NAME
  document.title = title

  setMeta('description', options.description || '')
  setMeta('keywords', options.keywords || '')
  setMeta('robots', options.robots || 'index,follow')

  const canonical = options.canonical || (window.location.origin + window.location.pathname)
  setLink('canonical', canonical)

  setProperty('og:title', title)
  if (options.description) {
    setProperty('og:description', options.description)
  }
  if (options.ogImage) {
    setProperty('og:image', absoluteUrl(options.ogImage))
  }
  setProperty('og:url', canonical)
}

/** 注入 JSON-LD（同 id 会覆盖） */
export function setJsonLd(id, data) {
  const elementId = 'ld-json-' + id
  let element = document.getElementById(elementId)
  if (!element) {
    element = document.createElement('script')
    element.type = 'application/ld+json'
    element.id = elementId
    document.head.appendChild(element)
  }
  element.textContent = JSON.stringify(data)
}

export function absoluteUrl(path) {
  if (!path) {
    return ''
  }
  if (/^https?:\/\//i.test(path)) {
    return path
  }
  return new URL(path, window.location.origin).href
}

function setMeta(name, content) {
  if (!content) {
    return
  }
  let meta = document.querySelector('meta[name="' + name + '"]')
  if (!meta) {
    meta = document.createElement('meta')
    meta.setAttribute('name', name)
    document.head.appendChild(meta)
  }
  meta.setAttribute('content', content)
}

function setProperty(property, content) {
  if (!content) {
    return
  }
  let meta = document.querySelector('meta[property="' + property + '"]')
  if (!meta) {
    meta = document.createElement('meta')
    meta.setAttribute('property', property)
    document.head.appendChild(meta)
  }
  meta.setAttribute('content', content)
}

function setLink(rel, href) {
  if (!href) {
    return
  }
  let link = document.querySelector('link[rel="' + rel + '"]')
  if (!link) {
    link = document.createElement('link')
    link.setAttribute('rel', rel)
    document.head.appendChild(link)
  }
  link.setAttribute('href', href)
}

