<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="产品名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入产品名称" clearable style="width: 180px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="型号" prop="model">
        <el-input v-model="queryParams.model" placeholder="请输入型号" clearable style="width: 160px" @keyup.enter.native="handleQuery" />
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['cms:product:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="productList" border>
      <el-table-column label="主图" align="center" width="90">
        <template slot-scope="scope">
          <el-image v-if="scope.row.cover" :src="scope.row.cover" :preview-src-list="[scope.row.cover]" fit="cover" style="width: 48px; height: 48px" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="型号" prop="model" width="150" />
      <el-table-column label="产品名称" prop="name" min-width="180" show-overflow-tooltip />
      <el-table-column label="所属栏目" prop="categoryName" width="120" align="center" />
      <el-table-column label="推荐" width="80" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isRecommend === '1'" type="success" size="mini">是</el-tag>
          <span v-else>否</span>
        </template>
      </el-table-column>
      <el-table-column label="排序" prop="sort" width="70" align="center" />
      <el-table-column label="发布时间" prop="publishTime" width="160" align="center" />
      <el-table-column label="浏览" prop="viewCount" width="70" align="center" />
      <el-table-column label="状态" width="90" align="center">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['cms:product:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['cms:product:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum"
                :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="所属栏目" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择栏目" style="width: 100%">
                <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产品型号" prop="model">
              <el-input v-model="form.model" placeholder="如 CXW-260-J6019HS" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="所属系列" prop="seriesId">
              <el-select v-model="form.seriesId" placeholder="可空，按栏目筛选" clearable filterable style="width: 100%">
                <el-option v-for="item in seriesOptions" :key="item.seriesId" :label="item.name" :value="item.seriesId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="产品名称" prop="name">
              <el-input v-model="form.name" placeholder="如 欧式顶吸烟机 J6019HS" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="一句话卖点" prop="subtitle">
              <el-input v-model="form.subtitle" placeholder="如 24m³/min 大风量" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="主图" prop="cover">
          <image-upload v-model="form.cover" :limit="1" />
        </el-form-item>
        <el-form-item label="图集">
          <image-upload v-model="imagesText" :limit="9" />
          <div class="form-tip">最多 9 张；第二张将作为列表悬停图</div>
        </el-form-item>
        <el-form-item label="产品看点">
          <div style="width: 100%">
            <image-upload :key="detailUploadKey" v-model="detailUploadText" :limit="20" />
            <div class="form-tip">可一次选多张，上传后自动追加到下方；建议使用 1200px 以上的详情长图（淘宝风格）</div>
            <div v-for="(item, index) in detailList" :key="'detail' + index" class="detail-row">
              <img v-if="item.url" :src="resolveDetailUrl(item.url)" class="detail-thumb" alt="看点图" />
              <el-input v-model="item.caption" placeholder="图片说明（可空）" style="width: 320px" />
              <el-button type="text" icon="el-icon-top" :disabled="index === 0"
                         @click="moveDetail(index, -1)">上移</el-button>
              <el-button type="text" icon="el-icon-bottom" :disabled="index === detailList.length - 1"
                         @click="moveDetail(index, 1)">下移</el-button>
              <el-button type="text" icon="el-icon-delete" @click="detailList.splice(index, 1)">删除</el-button>
            </div>
            <div v-if="!detailList.length" class="form-tip">暂无看点图，可批量上传</div>
          </div>
        </el-form-item>
        <el-form-item label="卖点">
          <div v-for="(point, index) in sellingPointList" :key="'p' + index" class="row-line">
            <el-input v-model="point.title" placeholder="卖点标题（如 24m³ 大风量）" style="width: 240px" />
            <el-input v-model="point.desc" placeholder="卖点描述（如 爆炒不跑烟）" style="width: 320px" />
            <el-button type="text" icon="el-icon-delete" @click="sellingPointList.splice(index, 1)">删除</el-button>
          </div>
          <el-button type="text" icon="el-icon-plus" @click="addSellingPoint">添加卖点</el-button>
        </el-form-item>
        <el-form-item label="参数分组">
          <div v-for="(group, gi) in paramGroupList" :key="'g' + gi" class="group-box">
            <div class="row-line">
              <el-input v-model="group.group" placeholder="分组名（如 主要参数）" style="width: 260px" />
              <el-button type="text" icon="el-icon-delete" @click="paramGroupList.splice(gi, 1)">删除分组</el-button>
            </div>
            <div v-for="(item, ii) in group.items" :key="'i' + ii" class="row-line">
              <el-input v-model="item.label" placeholder="参数名" style="width: 180px" />
              <el-input v-model="item.value" placeholder="参数值" style="width: 160px" />
              <el-input v-model="item.unit" placeholder="单位" style="width: 100px" />
              <el-button type="text" icon="el-icon-delete" @click="group.items.splice(ii, 1)">删除</el-button>
            </div>
            <el-button type="text" icon="el-icon-plus" @click="addParamItem(group)">添加参数</el-button>
          </div>
          <el-button type="text" icon="el-icon-plus" @click="addParamGroup">添加分组</el-button>
        </el-form-item>
        <el-form-item label="下载资料">
          <div v-for="(file, index) in downloadList" :key="'d' + index" class="row-line">
            <el-input v-model="file.name" placeholder="名称（如 产品说明书）" style="width: 200px" />
            <el-input v-model="file.url" placeholder="文件地址" style="width: 260px" />
            <el-input v-model="file.size" placeholder="大小（如 2.1MB）" style="width: 120px" />
            <el-button type="text" icon="el-icon-delete" @click="downloadList.splice(index, 1)">删除</el-button>
          </div>
          <el-button type="text" icon="el-icon-plus" @click="addDownload">添加资料</el-button>
        </el-form-item>
        <el-row>
          <el-col :span="8">
            <el-form-item label="标签" prop="tags">
              <el-input v-model="form.tags" placeholder="逗号分隔，如 顶吸,大吸力" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="form.sort" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="发布时间" prop="publishTime">
              <el-date-picker v-model="form.publishTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
                              placeholder="留空表示立即" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="是否推荐" prop="isRecommend">
              <el-radio-group v-model="form.isRecommend">
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
        <el-form-item label="SEO 标题" prop="seoTitle">
          <el-input v-model="form.seoTitle" placeholder="留空则使用产品名称" />
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
import { listProduct, getProduct, addProduct, updateProduct, delProduct } from '@/api/cms/product'
import { listCategoryTree } from '@/api/cms/category'
import { listSeries } from '@/api/cms/series'

export default {
  name: 'CmsProduct',
  dicts: ['sys_normal_disable'],
  data() {
    return {
      loading: true,
      showSearch: true,
      productList: [],
      total: 0,
      title: '',
      open: false,
      categoryOptions: [],
      seriesAll: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        model: undefined,
        categoryId: undefined,
        status: undefined
      },
      form: {},
      imagesText: '',
      detailList: [],
      detailUploadText: '',
      detailUploadKey: 0,
      sellingPointList: [],
      paramGroupList: [],
      downloadList: [],
      rules: {
        categoryId: [{ required: true, message: '所属栏目不能为空', trigger: 'change' }],
        model: [{ required: true, message: '产品型号不能为空', trigger: 'blur' }],
        name: [{ required: true, message: '产品名称不能为空', trigger: 'blur' }]
      }
    }
  },
  computed: {
    // 按当前栏目过滤可选系列
    seriesOptions() {
      if (!this.form.categoryId) {
        return this.seriesAll
      }
      return this.seriesAll.filter(item => item.categoryId === this.form.categoryId)
    }
  },
  watch: {
    // 批量上传：把 image-upload 的结果追加到看点列表
    detailUploadText(value) {
      if (!value) {
        return
      }
      const urls = String(value).split(',').map(item => item.trim()).filter(Boolean)
      urls.forEach(url => {
        const normalized = url.replace(/\\/g, '/')
        if (!this.detailList.some(item => item.url === normalized)) {
          this.detailList.push({ url: normalized, caption: '' })
        }
      })
      // 清空上传组件并重置其内部列表，避免删除后再上传把旧图带回来
      this.$nextTick(() => {
        this.detailUploadText = ''
        this.detailUploadKey += 1
      })
    }
  },
  created() {
    this.getCategoryOptions()
    this.getSeriesOptions()
    this.getList()
  },
  methods: {
    getSeriesOptions() {
      listSeries({}).then(res => {
        this.seriesAll = res.data || []
      }).catch(() => {})
    },
    getCategoryOptions() {
      listCategoryTree().then(res => {
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
    getList() {
      this.loading = true
      listProduct(this.queryParams).then(res => {
        this.productList = res.rows || []
        this.total = res.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    reset() {
      this.form = {
        productId: undefined,
        categoryId: undefined,
        seriesId: undefined,
        model: undefined,
        name: undefined,
        subtitle: undefined,
        cover: '',
        images: '',
        videoUrl: undefined,
        sellingPoints: '',
        paramsJson: '',
        downloads: '',
        tags: undefined,
        isRecommend: '0',
        sort: 0,
        publishTime: undefined,
        status: '0',
        seoTitle: undefined,
        seoKeywords: undefined,
        seoDescription: undefined,
        remark: undefined
      }
      this.imagesText = ''
      this.detailList = []
      this.detailUploadText = ''
      this.detailUploadKey += 1
      this.sellingPointList = []
      this.paramGroupList = []
      this.downloadList = []
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
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        model: undefined,
        categoryId: undefined,
        status: undefined
      }
      this.getList()
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增产品'
    },
    handleUpdate(row) {
      this.reset()
      getProduct(row.productId).then(res => {
        this.form = res.data
        this.imagesText = this.parseJsonArray(this.form.images).join(',')
        this.detailList = this.parseJsonArray(this.form.detailImages)
        this.sellingPointList = this.parseJsonArray(this.form.sellingPoints)
        this.paramGroupList = this.parseJsonArray(this.form.paramsJson)
        this.downloadList = this.parseJsonArray(this.form.downloads)
        this.open = true
        this.title = '修改产品'
      })
    },
    parseJsonArray(text) {
      if (!text) {
        return []
      }
      if (Array.isArray(text)) {
        return text
      }
      try {
        const value = JSON.parse(text)
        return Array.isArray(value) ? value : []
      } catch (e) {
        return []
      }
    },
    addSellingPoint() {
      this.sellingPointList.push({ title: '', desc: '' })
    },
    addParamGroup() {
      this.paramGroupList.push({ group: '', items: [] })
    },
    addParamItem(group) {
      if (!group.items) {
        this.$set(group, 'items', [])
      }
      group.items.push({ label: '', value: '', unit: '' })
    },
    addDownload() {
      this.downloadList.push({ name: '', url: '', size: '' })
    },
    resolveDetailUrl(url) {
      if (!url) {
        return ''
      }
      const normalized = String(url).replace(/\\/g, '/')
      if (normalized.indexOf('/profile') === 0) {
        return (process.env.VUE_APP_BASE_API || '') + normalized
      }
      return normalized
    },
    moveDetail(index, delta) {
      const target = index + delta
      if (target < 0 || target >= this.detailList.length) {
        return
      }
      const list = this.detailList
      const temp = list[index]
      this.$set(list, index, list[target])
      this.$set(list, target, temp)
    },
    buildJsonFields() {
      const images = this.imagesText ? this.imagesText.split(',').filter(Boolean) : []
      this.form.images = images.length ? JSON.stringify(images) : null

      const details = this.detailList
        .filter(item => item.url)
        .map(item => ({ url: item.url, caption: item.caption || '' }))
      this.form.detailImages = details.length ? JSON.stringify(details) : null

      const points = this.sellingPointList.filter(point => point.title || point.desc)
      this.form.sellingPoints = points.length ? JSON.stringify(points) : null

      const groups = this.paramGroupList
        .filter(group => group.group || (group.items && group.items.length))
        .map(group => ({
          group: group.group || '',
          items: (group.items || []).filter(item => item.label || item.value)
        }))
      this.form.paramsJson = groups.length ? JSON.stringify(groups) : null

      const files = this.downloadList.filter(file => file.name || file.url)
      this.form.downloads = files.length ? JSON.stringify(files) : null
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (!valid) {
          return
        }
        this.buildJsonFields()
        if (this.form.productId) {
          updateProduct(this.form).then(() => {
            this.$modal.msgSuccess('修改成功')
            this.open = false
            this.getList()
          })
        } else {
          addProduct(this.form).then(() => {
            this.$modal.msgSuccess('新增成功')
            this.open = false
            this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('确认删除产品「' + row.name + '」？').then(() => {
        return delProduct(row.productId)
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
  color: #909399;
  font-size: 12px;
  margin-left: 10px;
}

.row-line {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.group-box {
  width: 100%;
  margin-bottom: 10px;
  padding: 10px;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
}

.detail-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 8px 0;
  flex-wrap: wrap;
}

.detail-thumb {
  width: 72px;
  height: 54px;
  object-fit: cover;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  background: #f6f7f9;
}
</style>
