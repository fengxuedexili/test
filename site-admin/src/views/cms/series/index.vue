<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="系列名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入系列名称" clearable style="width: 180px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="所属栏目" prop="categoryId">
        <el-select v-model="queryParams.categoryId" placeholder="全部" clearable style="width: 180px">
          <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['cms:series:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="seriesList" border>
      <el-table-column label="封面" align="center" width="90">
        <template slot-scope="scope">
          <el-image v-if="scope.row.cover" :src="scope.row.cover" :preview-src-list="[scope.row.cover]" fit="cover" style="width: 48px; height: 48px" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="系列名称" prop="name" min-width="180" show-overflow-tooltip />
      <el-table-column label="副标题" prop="subtitle" min-width="200" show-overflow-tooltip />
      <el-table-column label="所属栏目" width="140" align="center">
        <template slot-scope="scope">{{ categoryName(scope.row.categoryId) }}</template>
      </el-table-column>
      <el-table-column label="排序" prop="sort" width="70" align="center" />
      <el-table-column label="状态" width="90" align="center">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" align="center" />
      <el-table-column label="操作" width="150" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['cms:series:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['cms:series:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="title" :visible.sync="open" width="720px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="所属栏目" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择产品栏目" style="width: 100%">
                <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="系列名称" prop="name">
              <el-input v-model="form.name" placeholder="如 魔尔套系 Pro" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="副标题" prop="subtitle">
          <el-input v-model="form.subtitle" placeholder="如 一体化厨房美学" />
        </el-form-item>
        <el-form-item label="系列封面" prop="cover">
          <image-upload v-model="form.cover" :limit="1" />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="form.sort" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
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
import { listSeries, getSeries, addSeries, updateSeries, delSeries } from '@/api/cms/series'
import { listCategoryTree } from '@/api/cms/category'

export default {
  name: 'CmsSeries',
  dicts: ['sys_normal_disable'],
  data() {
    return {
      loading: true,
      showSearch: true,
      seriesList: [],
      categoryOptions: [],
      title: '',
      open: false,
      queryParams: { name: undefined, categoryId: undefined, status: undefined },
      form: {},
      rules: {
        categoryId: [{ required: true, message: '所属栏目不能为空', trigger: 'change' }],
        name: [{ required: true, message: '系列名称不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.loadCategories()
    this.getList()
  },
  methods: {
    loadCategories() {
      listCategoryTree('1').then(res => {
        const flat = []
        const walk = (nodes, depth) => {
          (nodes || []).forEach(n => {
            flat.push({ value: n.categoryId, label: '　'.repeat(depth) + n.name })
            walk(n.children, depth + 1)
          })
        }
        walk(res.data || [], 0)
        this.categoryOptions = flat
      })
    },
    categoryName(categoryId) {
      const hit = this.categoryOptions.find(item => item.value === categoryId)
      return hit ? hit.label.trim() : '-'
    },
    getList() {
      this.loading = true
      listSeries(this.queryParams).then(res => {
        this.seriesList = res.data || []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    reset() {
      this.form = {
        seriesId: undefined,
        categoryId: this.queryParams.categoryId || undefined,
        name: undefined,
        subtitle: undefined,
        cover: '',
        sort: 0,
        status: '0',
        remark: undefined
      }
      if (this.$refs.form) {
        this.resetForm('form')
      }
    },
    handleQuery() {
      this.getList()
    },
    resetQuery() {
      this.queryParams = { name: undefined, categoryId: undefined, status: undefined }
      this.resetForm('queryForm')
      this.getList()
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增系列'
    },
    handleUpdate(row) {
      this.reset()
      getSeries(row.seriesId).then(res => {
        this.form = res.data
        this.open = true
        this.title = '修改系列'
      })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (!valid) {
          return
        }
        if (this.form.seriesId) {
          updateSeries(this.form).then(() => {
            this.$modal.msgSuccess('修改成功')
            this.open = false
            this.getList()
          })
        } else {
          addSeries(this.form).then(() => {
            this.$modal.msgSuccess('新增成功')
            this.open = false
            this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('确认删除系列「' + row.name + '」？').then(() => {
        return delSeries(row.seriesId)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    }
  }
}
</script>
