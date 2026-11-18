<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入标题" clearable style="width: 200px"
                  @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="分类" prop="categoryId">
        <el-select v-model="queryParams.categoryId" placeholder="全部" clearable style="width: 150px">
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
                   v-hasPermi="['cms:article:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="articleList" border>
      <el-table-column label="封面" width="100" align="center">
        <template slot-scope="scope">
          <el-image v-if="scope.row.cover" :src="scope.row.cover" :preview-src-list="[scope.row.cover]"
                    fit="cover" style="width: 80px; height: 46px" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="标题" min-width="260">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isTop === '1'" type="danger" size="mini" style="margin-right: 6px">置顶</el-tag>
          <span>{{ scope.row.title }}</span>
        </template>
      </el-table-column>
      <el-table-column label="分类" width="130" align="center">
        <template slot-scope="scope">{{ categoryLabel(scope.row.categoryId) }}</template>
      </el-table-column>
      <el-table-column label="发布时间" prop="publishTime" width="160" align="center" />
      <el-table-column label="浏览" prop="viewCount" width="80" align="center" />
      <el-table-column label="状态" width="90" align="center">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['cms:article:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['cms:article:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum"
                :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择资讯分类" clearable style="width: 100%">
                <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入文章标题" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="封面" prop="cover">
          <image-upload v-model="form.cover" :limit="1" />
        </el-form-item>
        <el-form-item label="摘要" prop="summary">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="列表页展示的摘要" />
        </el-form-item>
        <el-form-item label="正文" prop="content">
          <editor v-model="form.content" :height="320" />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="作者" prop="author">
              <el-input v-model="form.author" placeholder="作者" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源" prop="source">
              <el-input v-model="form.source" placeholder="来源" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="置顶" prop="isTop">
              <el-radio-group v-model="form.isTop">
                <el-radio label="1">是</el-radio>
                <el-radio label="0">否</el-radio>
              </el-radio-group>
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
        <el-form-item label="发布时间" prop="publishTime">
          <el-date-picker v-model="form.publishTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
                          placeholder="留空表示立即" style="width: 260px" />
        </el-form-item>
        <el-form-item label="SEO 标题" prop="seoTitle">
          <el-input v-model="form.seoTitle" placeholder="留空则使用文章标题" />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="SEO 关键词" prop="seoKeywords">
              <el-input v-model="form.seoKeywords" placeholder="逗号分隔" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="SEO 描述" prop="seoDescription">
              <el-input v-model="form.seoDescription" placeholder="meta description" />
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
import { listArticle, getArticle, addArticle, updateArticle, delArticle } from '@/api/cms/article'
import { listCategoryTree } from '@/api/cms/category'

export default {
  name: 'CmsArticle',
  dicts: ['sys_normal_disable'],
  data() {
    return {
      loading: true,
      showSearch: true,
      articleList: [],
      total: 0,
      title: '',
      open: false,
      categoryOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: undefined,
        categoryId: undefined,
        status: undefined
      },
      form: {},
      rules: {
        title: [{ required: true, message: '文章标题不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getCategoryOptions()
    this.getList()
  },
  methods: {
    getCategoryOptions() {
      listCategoryTree('2').then(res => {
        const flat = []
        const walk = (nodes, depth) => {
          (nodes || []).forEach(node => {
            flat.push({ value: node.categoryId, label: '　'.repeat(depth) + node.name })
            walk(node.children, depth + 1)
          })
        }
        walk(res.data || [], 0)
        this.categoryOptions = flat
      })
    },
    categoryLabel(categoryId) {
      const item = this.categoryOptions.find(option => option.value === categoryId)
      return item ? item.label.trim() : '-'
    },
    getList() {
      this.loading = true
      listArticle(this.queryParams).then(res => {
        this.articleList = res.rows || []
        this.total = res.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    reset() {
      this.form = {
        articleId: undefined,
        categoryId: undefined,
        title: undefined,
        cover: '',
        summary: undefined,
        content: undefined,
        author: undefined,
        source: undefined,
        isTop: '0',
        publishTime: undefined,
        status: '0',
        seoTitle: undefined,
        seoKeywords: undefined,
        seoDescription: undefined,
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
      this.resetForm('queryForm')
      this.queryParams = { pageNum: 1, pageSize: 10, title: undefined, categoryId: undefined, status: undefined }
      this.getList()
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增文章'
    },
    handleUpdate(row) {
      this.reset()
      getArticle(row.articleId).then(res => {
        this.form = res.data
        this.open = true
        this.title = '修改文章'
      })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (!valid) {
          return
        }
        if (this.form.articleId) {
          updateArticle(this.form).then(() => {
            this.$modal.msgSuccess('修改成功')
            this.open = false
            this.getList()
          })
        } else {
          addArticle(this.form).then(() => {
            this.$modal.msgSuccess('新增成功')
            this.open = false
            this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('确认删除文章「' + row.title + '」？').then(() => {
        return delArticle(row.articleId)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    }
  }
}
</script>
