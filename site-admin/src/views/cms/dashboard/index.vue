<template>
  <div class="app-container dashboard-page">
    <div class="dash-head">
      <div>
        <h2>运营看板</h2>
        <p>线索、内容与门店的实时概览（数据来自 CMS 业务表）。</p>
      </div>
      <el-button size="mini" icon="el-icon-refresh" :loading="loading" @click="load">刷新</el-button>
    </div>

    <el-row :gutter="16" class="stat-row" v-loading="loading">
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card tone-danger" @click="go('/cms/lead')">
          <div class="stat-label">待处理线索</div>
          <div class="stat-value">{{ summary.pendingLeadCount || 0 }}</div>
          <div class="stat-foot">今日新增 {{ summary.todayLeadCount || 0 }} 条</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card tone-primary" @click="go('/cms/product')">
          <div class="stat-label">产品总数</div>
          <div class="stat-value">{{ summary.productCount || 0 }}</div>
          <div class="stat-foot">含上架与停用</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card tone-success" @click="go('/cms/article')">
          <div class="stat-label">已发布文章</div>
          <div class="stat-value">{{ summary.articleCount || 0 }}</div>
          <div class="stat-foot">仅统计已发布</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card tone-warning" @click="go('/cms/store')">
          <div class="stat-label">启用门店</div>
          <div class="stat-value">{{ summary.storeCount || 0 }}</div>
          <div class="stat-foot">启用单页 {{ summary.pageCount || 0 }} 个</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :md="14">
        <el-card shadow="never" class="panel">
          <div slot="header" class="panel-head">
            <span>近 7 天线索</span>
            <span class="panel-sub">共 {{ trendTotal }} 条</span>
          </div>
          <div class="trend" v-if="summary.leadTrend && summary.leadTrend.length">
            <div class="trend-item" v-for="item in summary.leadTrend" :key="item.d">
              <span class="trend-num">{{ item.c }}</span>
              <div class="trend-bar" :style="{ height: barHeight(item.c) + '%' }"></div>
              <span class="trend-day">{{ item.d }}</span>
            </div>
          </div>
          <div v-else class="empty-line">暂无数据</div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="10">
        <el-card shadow="never" class="panel">
          <div slot="header" class="panel-head">
            <span>最新文章</span>
            <el-link type="primary" :underline="false" @click="go('/cms/article')">全部</el-link>
          </div>
          <ul class="article-list">
            <li v-for="item in summary.latestArticles" :key="item.articleId">
              <span class="dot" :class="{ top: item.isTop === '1' }"></span>
              <a class="article-title" @click="go('/cms/article')">{{ item.title }}</a>
              <span class="article-time">{{ item.publishTime }}</span>
            </li>
            <li v-if="!summary.latestArticles || !summary.latestArticles.length" class="empty-line">暂无已发布文章</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="panel">
      <div slot="header" class="panel-head">
        <span>最近线索</span>
        <el-link type="primary" :underline="false" @click="go('/cms/lead')">进入线索管理</el-link>
      </div>
      <el-table :data="summary.recentLeads || []" size="small" border>
        <el-table-column label="姓名" prop="name" width="110" />
        <el-table-column label="手机号" prop="phone" width="140" />
        <el-table-column label="类型" width="110" align="center">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.cms_lead_type" :value="scope.row.type" />
          </template>
        </el-table-column>
        <el-table-column label="地区" min-width="160">
          <template slot-scope="scope">
            {{ [scope.row.province, scope.row.city].filter(Boolean).join(' / ') || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.cms_lead_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="提交时间" prop="createTime" width="160" align="center" />
      </el-table>
      <div v-if="!summary.recentLeads || !summary.recentLeads.length" class="empty-line">暂无线索</div>
    </el-card>
  </div>
</template>

<script>
import { getDashboardSummary } from '@/api/cms/dashboard'

export default {
  name: 'CmsDashboard',
  dicts: ['cms_lead_type', 'cms_lead_status'],
  data() {
    return {
      loading: false,
      summary: {
        productCount: 0,
        articleCount: 0,
        pendingLeadCount: 0,
        todayLeadCount: 0,
        storeCount: 0,
        pageCount: 0,
        recentLeads: [],
        latestArticles: [],
        leadTrend: []
      }
    }
  },
  computed: {
    trendMax() {
      const list = this.summary.leadTrend || []
      return list.reduce((max, item) => Math.max(max, Number(item.c) || 0), 0)
    },
    trendTotal() {
      const list = this.summary.leadTrend || []
      return list.reduce((sum, item) => sum + (Number(item.c) || 0), 0)
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.loading = true
      getDashboardSummary().then(res => {
        this.summary = Object.assign({}, this.summary, res.data || {})
      }).catch(() => {}).finally(() => {
        this.loading = false
      })
    },
    barHeight(count) {
      if (!this.trendMax) {
        return 4
      }
      return Math.max(4, Math.round((Number(count) || 0) / this.trendMax * 100))
    },
    go(path) {
      this.$router.push(path)
    }
  }
}
</script>

<style scoped>
.dashboard-page .dash-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 16px;
}

.dashboard-page .dash-head h2 {
  margin: 0 0 4px;
  font-size: 20px;
}

.dashboard-page .dash-head p {
  margin: 0;
  color: #909399;
  font-size: 13px;
}

.stat-row {
  margin-bottom: 8px;
}

.stat-card {
  padding: 18px 20px;
  margin-bottom: 16px;
  border-radius: 6px;
  color: #fff;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.tone-danger {
  background: linear-gradient(135deg, #f56c6c, #c0392b);
}

.tone-primary {
  background: linear-gradient(135deg, #409eff, #2b6cb0);
}

.tone-success {
  background: linear-gradient(135deg, #67c23a, #2f855a);
}

.tone-warning {
  background: linear-gradient(135deg, #e6a23c, #b7791f);
}

.stat-label {
  font-size: 13px;
  opacity: 0.9;
}

.stat-value {
  margin: 6px 0 2px;
  font-size: 30px;
  font-weight: 600;
  line-height: 1.2;
}

.stat-foot {
  font-size: 12px;
  opacity: 0.85;
}

.panel {
  margin-bottom: 16px;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
}

.panel-sub {
  color: #909399;
  font-size: 12px;
  font-weight: 400;
}

.trend {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  height: 190px;
  padding: 8px 4px 0;
}

.trend-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-end;
  height: 100%;
}

.trend-num {
  margin-bottom: 4px;
  color: #606266;
  font-size: 12px;
}

.trend-bar {
  width: 60%;
  max-width: 42px;
  min-height: 4px;
  border-radius: 3px 3px 0 0;
  background: linear-gradient(180deg, #79bbff, #409eff);
  transition: height 0.3s;
}

.trend-day {
  margin-top: 6px;
  color: #909399;
  font-size: 12px;
}

.article-list {
  padding: 0;
  margin: 0;
  list-style: none;
}

.article-list li {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 9px 0;
  border-bottom: 1px dashed #ebeef5;
}

.article-list li:last-child {
  border-bottom: none;
}

.article-list .dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #c0c4cc;
  flex: 0 0 auto;
}

.article-list .dot.top {
  background: #f56c6c;
}

.article-title {
  flex: 1;
  overflow: hidden;
  color: #303133;
  cursor: pointer;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.article-title:hover {
  color: #409eff;
}

.article-time {
  color: #909399;
  font-size: 12px;
  flex: 0 0 auto;
}

.empty-line {
  padding: 12px 0;
  color: #909399;
  font-size: 13px;
  text-align: center;
}
</style>
