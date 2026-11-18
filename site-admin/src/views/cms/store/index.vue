<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="门店名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入门店名称" clearable style="width: 180px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="省份" prop="province">
        <el-input v-model="queryParams.province" placeholder="如 广东省" clearable style="width: 140px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="城市" prop="city">
        <el-input v-model="queryParams.city" placeholder="如 中山市" clearable style="width: 140px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="全部" clearable style="width: 140px">
          <el-option v-for="d in dict.type.cms_store_type" :key="d.value" :label="d.label" :value="d.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
          <el-option v-for="d in dict.type.sys_normal_disable" :key="d.value" :label="d.label" :value="d.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['cms:store:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="storeList" border>
      <el-table-column label="门店名称" prop="name" min-width="180" show-overflow-tooltip />
      <el-table-column label="类型" width="110" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="scope.row.type === '1' ? 'danger' : (scope.row.type === '3' ? 'info' : 'success')">
            {{ typeLabel(scope.row.type) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="省 / 市 / 区" width="220" align="center">
        <template slot-scope="scope">
          {{ [scope.row.province, scope.row.city, scope.row.district].filter(Boolean).join(' / ') || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="详细地址" prop="address" min-width="220" show-overflow-tooltip />
      <el-table-column label="电话" prop="phone" width="140" align="center" />
      <el-table-column label="排序" prop="sort" width="70" align="center" />
      <el-table-column label="状态" width="90" align="center">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['cms:store:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['cms:store:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum"
                :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="760px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="门店名称" prop="name">
              <el-input v-model="form.name" placeholder="如 中山旗舰店" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="门店类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%">
                <el-option v-for="d in dict.type.cms_store_type" :key="d.value" :label="d.label" :value="d.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="省份" prop="province">
              <el-input v-model="form.province" placeholder="如 广东省" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="城市" prop="city">
              <el-input v-model="form.city" placeholder="如 中山市" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="区县" prop="district">
              <el-input v-model="form.district" placeholder="如 小榄镇" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="form.address" placeholder="如 小榄镇菊城大道 88 号" />
        </el-form-item>
        <el-row>
          <el-col :span="8">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="如 0760-8888888" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="经度" prop="lng">
              <el-input v-model="form.lng" placeholder="如 113.253" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="纬度" prop="lat">
              <el-input v-model="form.lat" placeholder="如 22.671" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="form.sort" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="16">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="d in dict.type.sys_normal_disable" :key="d.value" :label="d.value">{{ d.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listStore, getStore, addStore, updateStore, delStore } from '@/api/cms/store'

export default {
  name: 'CmsStore',
  dicts: ['sys_normal_disable', 'cms_store_type'],
  data() {
    return {
      loading: true,
      showSearch: true,
      storeList: [],
      total: 0,
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        province: undefined,
        city: undefined,
        type: undefined,
        status: undefined
      },
      form: {},
      rules: {
        name: [{ required: true, message: '门店名称不能为空', trigger: 'blur' }],
        type: [{ required: true, message: '门店类型不能为空', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    typeLabel(type) {
      const hit = (this.dict.type.cms_store_type || []).find(item => item.value === type)
      return hit ? hit.label : '-'
    },
    getList() {
      this.loading = true
      listStore(this.queryParams).then(res => {
        this.storeList = res.rows || []
        this.total = res.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    reset() {
      this.form = {
        storeId: undefined,
        name: undefined,
        province: undefined,
        city: undefined,
        district: undefined,
        address: undefined,
        lng: undefined,
        lat: undefined,
        phone: undefined,
        type: '2',
        sort: 0,
        status: '0',
        remark: undefined
      }
      if (this.$refs.form) {
        this.resetForm('form')
      }
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        province: undefined,
        city: undefined,
        type: undefined,
        status: undefined
      }
      this.resetForm('queryForm')
      this.getList()
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增门店'
    },
    handleUpdate(row) {
      this.reset()
      getStore(row.storeId).then(res => {
        this.form = res.data
        this.open = true
        this.title = '修改门店'
      })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (!valid) {
          return
        }
        if (this.form.storeId) {
          updateStore(this.form).then(() => {
            this.$modal.msgSuccess('修改成功')
            this.open = false
            this.getList()
          })
        } else {
          addStore(this.form).then(() => {
            this.$modal.msgSuccess('新增成功')
            this.open = false
            this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('确认删除门店「' + row.name + '」？').then(() => {
        return delStore(row.storeId)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    }
  }
}
</script>
