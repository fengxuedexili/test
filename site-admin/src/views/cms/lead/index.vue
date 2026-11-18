<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="全部" clearable style="width: 130px">
          <el-option v-for="d in dict.type.cms_lead_type" :key="d.value" :label="d.label" :value="d.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
          <el-option v-for="d in dict.type.cms_lead_status" :key="d.value" :label="d.label" :value="d.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable style="width: 160px"
                  @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="提交时间">
        <el-date-picker v-model="dateRange" type="daterange" value-format="yyyy-MM-dd"
                        range-separator="-" start-placeholder="开始" end-placeholder="结束"
                        style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
                   v-hasPermi="['cms:lead:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="leadList" border>
      <el-table-column label="ID" prop="leadId" width="70" align="center" />
      <el-table-column label="类型" width="110" align="center">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.cms_lead_type" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column label="姓名" prop="name" width="100" />
      <el-table-column label="手机号" prop="phone" width="130" />
      <el-table-column label="地区" width="190">
        <template slot-scope="scope">
          {{ (scope.row.province || '') + (scope.row.city || '') + (scope.row.district || '') }}
        </template>
      </el-table-column>
      <el-table-column label="留言" prop="content" min-width="180" show-overflow-tooltip />
      <el-table-column label="来源" prop="source" width="120" show-overflow-tooltip />
      <el-table-column label="状态" width="100" align="center">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.cms_lead_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="提交时间" prop="createTime" width="160" align="center" />
      <el-table-column label="操作" width="160" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)"
                     v-hasPermi="['cms:lead:list']">详情/跟进</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['cms:lead:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum"
                :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="线索详情与跟进" :visible.sync="open" width="720px" append-to-body>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="线索ID">{{ form.leadId }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ selectDictLabel(dict.type.cms_lead_type, form.type) }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ form.name }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ form.phone }}</el-descriptions-item>
        <el-descriptions-item label="地区">
          {{ (form.province || '') + (form.city || '') + (form.district || '') }}
        </el-descriptions-item>
        <el-descriptions-item label="关联产品ID">{{ form.productId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="来源">{{ form.source || '-' }}</el-descriptions-item>
        <el-descriptions-item label="UTM">{{ form.utm || '-' }}</el-descriptions-item>
        <el-descriptions-item label="提交IP">{{ form.ip || '-' }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ form.createTime }}</el-descriptions-item>
        <el-descriptions-item label="留言" :span="2">{{ form.content || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-form ref="form" :model="form" label-width="90px" style="margin-top: 18px">
        <el-form-item label="跟进状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择" style="width: 220px">
            <el-option v-for="d in dict.type.cms_lead_status" :key="d.value" :label="d.label" :value="d.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理人" prop="handler">
          <el-input v-model="form.handler" placeholder="处理人" style="width: 220px" />
        </el-form-item>
        <el-form-item label="跟进备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="沟通记录 / 后续计划" />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFollow" v-hasPermi="['cms:lead:edit']">保存跟进</el-button>
        <el-button @click="open = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listLead, getLead, updateLead, delLead } from '@/api/cms/lead'

export default {
  name: 'CmsLead',
  dicts: ['cms_lead_type', 'cms_lead_status'],
  data() {
    return {
      loading: true,
      showSearch: true,
      leadList: [],
      total: 0,
      dateRange: [],
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        type: undefined,
        status: undefined,
        phone: undefined
      },
      form: {}
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listLead(this.addDateRange(this.queryParams, this.dateRange)).then(res => {
        this.leadList = res.rows || []
        this.total = res.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.dateRange = []
      this.queryParams = { pageNum: 1, pageSize: 10, type: undefined, status: undefined, phone: undefined }
      this.getList()
    },
    handleView(row) {
      getLead(row.leadId).then(res => {
        this.form = res.data
        this.open = true
      })
    },
    submitFollow() {
      if (!this.form.leadId) {
        return
      }
      updateLead({
        leadId: this.form.leadId,
        status: this.form.status,
        handler: this.form.handler,
        remark: this.form.remark
      }).then(() => {
        this.$modal.msgSuccess('跟进已保存')
        this.open = false
        this.getList()
      })
    },
    handleDelete(row) {
      this.$modal.confirm('确认删除线索「' + (row.name || row.phone) + '」？').then(() => {
        return delLead(row.leadId)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handleExport() {
      this.download('cms/lead/export', {
        ...this.addDateRange(this.queryParams, this.dateRange)
      }, `lead_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
