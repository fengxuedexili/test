<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="位置" prop="position">
        <el-select v-model="queryParams.position" placeholder="全部" clearable style="width: 140px">
          <el-option v-for="d in dict.type.cms_banner_position" :key="d.value" :label="d.label" :value="d.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入标题" clearable style="width: 180px"
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
                   v-hasPermi="['cms:banner:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="bannerList" border>
      <el-table-column label="位置" width="110" align="center">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.cms_banner_position" :value="scope.row.position" />
        </template>
      </el-table-column>
      <el-table-column label="封面" width="120" align="center">
        <template slot-scope="scope">
          <el-image v-if="scope.row.imageUrl" :src="scope.row.imageUrl" :preview-src-list="[scope.row.imageUrl]"
                    fit="cover" style="width: 88px; height: 50px" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="标题 / 副标题" min-width="200">
        <template slot-scope="scope">
          <div>{{ scope.row.title }}</div>
          <div style="color: #909399; font-size: 12px">{{ scope.row.subtitle }}</div>
        </template>
      </el-table-column>
      <el-table-column label="类型" width="80" align="center">
        <template slot-scope="scope">{{ scope.row.mediaType === '2' ? '视频' : '图片' }}</template>
      </el-table-column>
      <el-table-column label="按钮" width="110" align="center" prop="btnText" />
      <el-table-column label="投放时间" width="300" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.startTime || '不限' }} ~ {{ scope.row.endTime || '不限' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="排序" prop="sort" width="70" align="center" />
      <el-table-column label="状态" width="90" align="center">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['cms:banner:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['cms:banner:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum"
                :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="760px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="展示位置" prop="position">
              <el-select v-model="form.position" placeholder="请选择位置" style="width: 100%">
                <el-option v-for="d in dict.type.cms_banner_position" :key="d.value" :label="d.label" :value="d.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="媒体类型" prop="mediaType">
              <el-radio-group v-model="form.mediaType">
                <el-radio label="1">图片</el-radio>
                <el-radio label="2">视频</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item :label="form.mediaType === '2' ? '视频封面' : 'Banner 图片'" prop="imageUrl">
          <image-upload v-model="form.imageUrl" :limit="1" />
        </el-form-item>
        <el-form-item v-if="form.mediaType === '2'" label="视频地址" prop="videoUrl">
          <el-input v-model="form.videoUrl" placeholder="mp4 地址（OSS/CDN）" />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="主标题" prop="title">
              <el-input v-model="form.title" placeholder="如：让厨房更懂生活" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="副标题" prop="subtitle">
              <el-input v-model="form.subtitle" placeholder="如：2026 新品系列" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="按钮文案" prop="btnText">
              <el-input v-model="form.btnText" placeholder="如：了解更多" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="跳转链接" prop="linkUrl">
              <el-input v-model="form.linkUrl" placeholder="如：/product" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="投放时间" prop="dateRange">
          <el-date-picker v-model="dateRange" type="datetimerange" :picker-options="pickerOptions"
                          value-format="yyyy-MM-dd HH:mm:ss" range-separator="至"
                          start-placeholder="开始时间" end-placeholder="结束时间" style="width: 100%" />
        </el-form-item>
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
import { listBanner, getBanner, addBanner, updateBanner, delBanner } from '@/api/cms/banner'

export default {
  name: 'CmsBanner',
  dicts: ['cms_banner_position', 'sys_normal_disable'],
  data() {
    return {
      loading: true,
      showSearch: true,
      bannerList: [],
      total: 0,
      title: '',
      open: false,
      dateRange: [],
      pickerOptions: {
        shortcuts: [
          {
            text: '最近一周',
            onClick(picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
              picker.$emit('pick', [start, end])
            }
          }
        ]
      },
      queryParams: { pageNum: 1, pageSize: 10, position: undefined, title: undefined, status: undefined },
      form: {},
      rules: {
        position: [{ required: true, message: '展示位置不能为空', trigger: 'change' }],
        mediaType: [{ required: true, message: '媒体类型不能为空', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listBanner(this.queryParams).then(res => {
        this.bannerList = res.rows || []
        this.total = res.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    reset() {
      this.form = {
        bannerId: undefined,
        position: 'home',
        mediaType: '1',
        imageUrl: '',
        videoUrl: undefined,
        title: undefined,
        subtitle: undefined,
        btnText: undefined,
        linkUrl: undefined,
        sort: 0,
        status: '0',
        remark: undefined
      }
      this.dateRange = []
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
      this.queryParams = { pageNum: 1, pageSize: 10, position: undefined, title: undefined, status: undefined }
      this.getList()
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增 Banner'
    },
    handleUpdate(row) {
      this.reset()
      getBanner(row.bannerId).then(res => {
        this.form = res.data
        this.dateRange = (this.form.startTime || this.form.endTime)
          ? [this.form.startTime, this.form.endTime]
          : []
        this.open = true
        this.title = '修改 Banner'
      })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (!valid) {
          return
        }
        this.form.startTime = this.dateRange && this.dateRange.length ? this.dateRange[0] : null
        this.form.endTime = this.dateRange && this.dateRange.length ? this.dateRange[1] : null
        if (this.form.bannerId) {
          updateBanner(this.form).then(() => {
            this.$modal.msgSuccess('修改成功')
            this.open = false
            this.getList()
          })
        } else {
          addBanner(this.form).then(() => {
            this.$modal.msgSuccess('新增成功')
            this.open = false
            this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('确认删除 Banner「' + (row.title || row.bannerId) + '」？').then(() => {
        return delBanner(row.bannerId)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    }
  }
}
</script>
