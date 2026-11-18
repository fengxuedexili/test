<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="栏目名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入栏目名称" clearable style="width: 180px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="全部" clearable style="width: 140px">
          <el-option v-for="d in dict.type.cms_category_type" :key="d.value" :label="d.label" :value="d.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd()" v-hasPermi="['cms:category:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="categoryList" row-key="categoryId" default-expand-all
              :tree-props="{ children: 'children' }" border>
      <el-table-column label="栏目名称" prop="name" min-width="200" />
      <el-table-column label="别名" prop="slug" width="160" />
      <el-table-column label="类型" width="100" align="center">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.cms_category_type" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column label="排序" prop="sort" width="80" align="center" />
      <el-table-column label="状态" width="90" align="center">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" align="center" />
      <el-table-column label="操作" width="230" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['cms:category:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-plus" @click="handleAdd(scope.row)"
                     v-hasPermi="['cms:category:add']">子栏目</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['cms:category:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="title" :visible.sync="open" width="720px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="上级栏目" prop="parentId">
              <el-select v-model="form.parentId" placeholder="顶级栏目" style="width: 100%">
                <el-option v-for="item in parentOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="栏目类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%">
                <el-option v-for="d in dict.type.cms_category_type" :key="d.value" :label="d.label" :value="d.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="栏目名称" prop="name">
              <el-input v-model="form.name" placeholder="如：吸油烟机" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="栏目别名" prop="slug">
              <el-input v-model="form.slug" placeholder="URL 别名，如 range-hood" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="sort">
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
        <el-form-item label="栏目封面" prop="cover">
          <image-upload v-model="form.cover" :limit="1" />
        </el-form-item>
        <el-form-item label="栏目简介" prop="summary">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="列表页展示的简介" />
        </el-form-item>
        <el-form-item label="SEO 标题" prop="seoTitle">
          <el-input v-model="form.seoTitle" placeholder="留空则使用栏目名称" />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="SEO 关键词" prop="seoKeywords">
              <el-input v-model="form.seoKeywords" placeholder="逗号分隔" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" placeholder="备注" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="SEO 描述" prop="seoDescription">
          <el-input v-model="form.seoDescription" type="textarea" :rows="2" placeholder="用于 meta description" />
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
import { listCategoryTree, getCategory, addCategory, updateCategory, delCategory } from '@/api/cms/category'

export default {
  name: 'CmsCategory',
  dicts: ['sys_normal_disable', 'cms_category_type'],
  data() {
    return {
      loading: true,
      showSearch: true,
      categoryList: [],
      title: '',
      open: false,
      queryParams: { name: undefined, type: undefined },
      form: {},
      parentOptions: [],
      rules: {
        name: [{ required: true, message: '栏目名称不能为空', trigger: 'blur' }],
        slug: [{ required: true, message: '栏目别名不能为空', trigger: 'blur' }],
        type: [{ required: true, message: '栏目类型不能为空', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listCategoryTree(this.queryParams.type).then(res => {
        this.categoryList = this.filterTree(res.data || [], this.queryParams.name)
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    filterTree(list, name) {
      if (!name) {
        return list
      }
      const result = []
      list.forEach(node => {
        const children = this.filterTree(node.children || [], name)
        if ((node.name || '').indexOf(name) > -1 || children.length > 0) {
          result.push(Object.assign({}, node, { children: children }))
        }
      })
      return result
    },
    reset() {
      this.form = {
        categoryId: undefined,
        parentId: 0,
        name: undefined,
        slug: undefined,
        type: '1',
        sort: 0,
        status: '0',
        cover: '',
        summary: undefined,
        seoTitle: undefined,
        seoKeywords: undefined,
        seoDescription: undefined,
        remark: undefined
      }
      this.parentOptions = [{ value: 0, label: '顶级栏目' }]
      if (this.$refs.form) {
        this.resetForm('form')
      }
    },
    buildParentOptions(excludeId) {
      listCategoryTree().then(res => {
        const tree = res.data || []
        let excluded = []
        const find = nodes => {
          for (const node of nodes) {
            if (node.categoryId === excludeId) {
              excluded = this.collectIds(node)
              return true
            }
            if (find(node.children || [])) {
              return true
            }
          }
          return false
        }
        if (excludeId) {
          find(tree)
        }
        const flat = [{ value: 0, label: '顶级栏目' }]
        const walk = (nodes, depth) => {
          (nodes || []).forEach(n => {
            if (excluded.indexOf(n.categoryId) === -1) {
              flat.push({ value: n.categoryId, label: '　'.repeat(depth) + n.name })
              walk(n.children, depth + 1)
            }
          })
        }
        walk(tree, 0)
        this.parentOptions = flat
      })
    },
    collectIds(node) {
      let ids = [node.categoryId]
      ;(node.children || []).forEach(child => {
        ids = ids.concat(this.collectIds(child))
      })
      return ids
    },
    handleQuery() {
      this.getList()
    },
    resetQuery() {
      this.queryParams = { name: undefined, type: undefined }
      this.resetForm('queryForm')
      this.getList()
    },
    handleAdd(row) {
      this.reset()
      if (row) {
        this.buildParentOptions(row.categoryId)
        this.form.parentId = row.categoryId
      } else {
        this.buildParentOptions()
      }
      this.open = true
      this.title = '新增栏目'
    },
    handleUpdate(row) {
      this.reset()
      getCategory(row.categoryId).then(res => {
        this.form = res.data
        this.buildParentOptions(this.form.categoryId)
        this.open = true
        this.title = '修改栏目'
      })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (!valid) {
          return
        }
        if (this.form.categoryId) {
          updateCategory(this.form).then(() => {
            this.$modal.msgSuccess('修改成功')
            this.open = false
            this.getList()
          })
        } else {
          addCategory(this.form).then(() => {
            this.$modal.msgSuccess('新增成功')
            this.open = false
            this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('确认删除栏目「' + row.name + '」？').then(() => {
        return delCategory(row.categoryId)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    }
  }
}
</script>

