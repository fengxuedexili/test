import { getSiteConfig } from '@/api/site'

const state = {
  info: {}
}

const mutations = {
  SET_INFO(state, info) {
    state.info = info || {}
  }
}

const actions = {
  load({ state, commit }) {
    if (state.info && state.info.name) {
      return Promise.resolve(state.info)
    }
    return getSiteConfig().then(data => {
      commit('SET_INFO', data)
      return data
    }).catch(() => {
      commit('SET_INFO', {})
      return {}
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
