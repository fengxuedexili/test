<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="别名" prop="slug">
        <el-input v-model="queryParams.slug" placeholder="如 privacy" clearable style="width: 160px"
                  @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="页面标题" clearable style="width: 180px"
                  @keyup.enter.native="handleQuery" />
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
                   v-hasPermi="['cms:page:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="pageList" border>
      <el-table-column label="别名" prop="slug" width="160" />
      <el-table-column label="标题" prop="title" min-width="200" />
      <el-table-column label="区块" width="70" align="center">
        <template slot-scope="scope">{{ blockCount(scope.row.blocks) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90" align="center">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="更新时间" prop="updateTime" width="160" align="center" />
      <el-table-column label="前台地址" width="150" align="center">
        <template slot-scope="scope">
          <el-link type="primary" :href="'http://localhost/p/' + scope.row.slug" target="_blank">/p/{{ scope.row.slug }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['cms:page:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['cms:page:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="title" :visible.sync="open" width="820px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="别名" prop="slug">
              <el-input v-model="form.slug" placeholder="如 privacy / about" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标题" prop="title">
              <el-input v-model="form.title" placeholder="页面标题" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="页面区块" prop="blocks">
          <div class="block-editor">
            <div class="block-toolbar">
              <el-button size="mini" type="primary" plain icon="el-icon-plus" @click="addBlock('richText')">正文区块</el-button>
              <el-button size="mini" type="primary" plain icon="el-icon-plus" @click="addBlock('imageText')">图文区块</el-button>
              <span class="block-tip">按顺序渲染，可上下移动；前台地址 /p/{{ form.slug || 'slug' }}</span>
            </div>

            <div v-if="!blockList.length" class="block-empty">暂无区块，点击上方按钮添加</div>

            <div v-for="(block, index) in blockList" :key="block._key" class="block-card">
              <div class="block-head">
                <span class="block-index">{{ index + 1 }}</span>
                <span class="block-type">{{ blockTypeLabel(block.type) }}</span>
                <div class="block-actions">
                  <el-button size="mini" type="text" icon="el-icon-top" :disabled="index === 0"
                             @click="moveBlock(index, -1)">上移</el-button>
                  <el-button size="mini" type="text" icon="el-icon-bottom" :disabled="index === blockList.length - 1"
                             @click="moveBlock(index, 1)">下移</el-button>
                  <el-button size="mini" type="text" icon="el-icon-delete" @click="removeBlock(index)">删除</el-button>
                </div>
              </div>

              <template v-if="block.type === 'richText'">
                <editor v-model="block.props.html" :min-height="180" />
              </template>

              <template v-else-if="block.type === 'imageText'">
                <el-row :gutter="16">
                  <el-col :span="10">
                    <image-upload v-model="block.props.image" :limit="1" />
                    <div class="block-tip">建议 800×600 以上的横向图片</div>
                  </el-col>
                  <el-col :span="14">
                    <div class="field-row">
                      <label>标题</label>
                      <el-input v-model="block.props.title" placeholder="图文标题（可空）" />
                    </div>
                    <div class="field-row">
                      <label>正文</label>
                      <el-input v-model="block.props.text" type="textarea" :rows="4" placeholder="图文描述（可空）" />
                    </div>
                    <div class="field-row">
                      <label>左右互换</label>
                      <el-switch v-model="block.props.reverse" />
                      <span class="block-tip" style="margin-left: 8px">开启后图片在右侧</span>
                    </div>
                  </el-col>
                </el-row>
              </template>

              <div v-else class="block-unsupported">
                暂不支持可视化编辑的区块类型「{{ block.type }}」，保存时会原样保留。
                <el-button size="mini" type="text" icon="el-icon-delete" @click="removeBlock(index)">删除该区块</el-button>
              </div>
            </div>

            <div class="block-footer" v-if="blockList.length">
              <el-link v-if="form.slug" type="primary" :href="'http://localhost/p/' + form.slug"
                       target="_blank" :underline="false">预览 /p/{{ form.slug }}</el-link>
              <span class="block-tip">保存后前台立即生效（服务端会清洗 HTML）</span>
            </div>
          </div>
          <div class="form-tip">
            支持 richText（正文）与 imageText（左右图文）；提交时自动序列化为 JSON，无需手写。
          </div>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="d in dict.type.sys_normal_disable" :key="d.value" :label="d.value">{{ d.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" placeholder="备注" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="SEO 标题" prop="seoTitle">
          <el-input v-model="form.seoTitle" placeholder="留空则使用页面标题" />
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
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPage, getPage, addPage, updatePage, delPage } from '@/api/cms/page'

export default {
  name: 'CmsPage',
  dicts: ['sys_normal_disable'],
  data() {
    return {
      loading: true,
      showSearch: true,
      pageList: [],
      title: '',
      open: false,
      queryParams: { slug: undefined, title: undefined, status: undefined },
      form: {},
      blockList: [],
      blockSeq: 0,
      rules: {
        slug: [{ required: true, message: '页面别名不能为空', trigger: 'blur' }],
        title: [{ required: true, message: '页面标题不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listPage(this.queryParams).then(res => {
        this.pageList = res.data || []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    reset() {
      this.form = {
        pageId: undefined,
        slug: undefined,
        title: undefined,
        blocks: '',
        status: '0',
        seoTitle: undefined,
        seoKeywords: undefined,
        seoDescription: undefined,
        remark: undefined
      }
      this.blockList = []
      this.blockSeq = 0
      if (this.$refs.form) {
        this.resetForm('form')
      }
    },
    handleQuery() {
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams = { slug: undefined, title: undefined, status: undefined }
      this.getList()
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增单页'
    },
    handleUpdate(row) {
      this.reset()
      getPage(row.pageId).then(res => {
        this.form = res.data
        this.blockList = this.parseBlocks(this.form.blocks)
        this.open = true
        this.title = '修改单页'
      })
    },
    parseBlocks(text) {
      if (!text) {
        return []
      }
      let value
      try {
        value = JSON.parse(text)
      } catch (e) {
        this.$modal.msgWarning('原有区块 JSON 解析失败，已按空处理，请重新编辑')
        return []
      }
      if (!Array.isArray(value)) {
        return []
      }
      return value.map(item => this.normalizeBlock(item))
    },
    normalizeBlock(block) {
      const item = block && typeof block === 'object' ? block : {}
      if (!item.type) {
        item.type = 'richText'
      }
      const props = item.props && typeof item.props === 'object' ? item.props : {}
      if (item.type === 'richText') {
        props.html = typeof props.html === 'string' ? props.html : ''
      } else if (item.type === 'imageText') {
        props.image = props.image || ''
        props.title = props.title || ''
        props.text = props.text || ''
        props.reverse = !!props.reverse
      }
      item.props = props
      this.blockSeq += 1
      item._key = 'blk_' + this.blockSeq
      return item
    },
    addBlock(type) {
      const props = type === 'imageText'
        ? { image: '', title: '', text: '', reverse: false }
        : { html: '' }
      this.blockList.push(this.normalizeBlock({ type: type, props: props }))
    },
    moveBlock(index, delta) {
      const target = index + delta
      if (target < 0 || target >= this.blockList.length) {
        return
      }
      const list = this.blockList
      const temp = list[index]
      this.$set(list, index, list[target])
      this.$set(list, target, temp)
    },
    removeBlock(index) {
      this.blockList.splice(index, 1)
    },
    serializeBlocks() {
      if (!this.blockList.length) {
        return ''
      }
      const data = JSON.parse(JSON.stringify(this.blockList))
      data.forEach(item => {
        delete item._key
      })
      return JSON.stringify(data)
    },
    isEmptyBlock(block) {
      if (!block || !block.props) {
        return true
      }
      if (block.type === 'richText') {
        const plain = String(block.props.html || '').replace(/<[^>]*>/g, '').replace(/&nbsp;/g, ' ').trim()
        return !plain
      }
      if (block.type === 'imageText') {
        return !(block.props.image || block.props.title || block.props.text)
      }
      return false
    },
    blockTypeLabel(type) {
      if (type === 'richText') {
        return '正文（富文本）'
      }
      if (type === 'imageText') {
        return '图文（左右排版）'
      }
      return type || '未知区块'
    },
    blockCount(text) {
      if (!text) {
        return 0
      }
      try {
        const value = JSON.parse(text)
        return Array.isArray(value) ? value.length : 0
      } catch (e) {
        return 0
      }
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (!valid) {
          return
        }
        const emptyIndex = this.blockList.findIndex(item => this.isEmptyBlock(item))
        if (emptyIndex > -1) {
          this.$modal.msgWarning('第 ' + (emptyIndex + 1) + ' 个区块内容为空，请补充或删除')
          return
        }
        this.form.blocks = this.serializeBlocks()
        if (this.form.pageId) {
          updatePage(this.form).then(() => {
            this.$modal.msgSuccess('修改成功')
            this.open = false
            this.getList()
          })
        } else {
          addPage(this.form).then(() => {
            this.$modal.msgSuccess('新增成功')
            this.open = false
            this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('确认删除单页「' + row.title + '」？').then(() => {
        return delPage(row.pageId)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.form-tip {
  margin-top: 6px;
  color: #909399;
  font-size: 12px;
  line-height: 1.7;
}

.block-editor {
  width: 100%;
}

.block-toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.block-tip {
  color: #909399;
  font-size: 12px;
}

.block-empty {
  padding: 18px;
  margin-top: 10px;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  color: #909399;
  font-size: 13px;
  text-align: center;
}

.block-card {
  padding: 12px 14px 4px;
  margin-top: 12px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  background: #fafafa;
}

.block-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.block-index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: #409eff;
  color: #fff;
  font-size: 12px;
}

.block-type {
  color: #303133;
  font-size: 13px;
  font-weight: 600;
}

.block-actions {
  margin-left: auto;
}

.field-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 12px;
}

.field-row > label {
  flex: 0 0 64px;
  padding-top: 6px;
  color: #606266;
  font-size: 13px;
}

.field-row > .el-input,
.field-row > .el-textarea {
  flex: 1;
}

.block-unsupported {
  padding: 10px 0 12px;
  color: #e6a23c;
  font-size: 13px;
}

.block-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 12px;
}
</style>
