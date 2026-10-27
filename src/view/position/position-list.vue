<template>
  <div>
    <!-- 列表页面 -->
    <div class="container" v-if="!showEdit">
      <div class="header">
        <div class="title" style=" text-align: left;">已发布的职位</div>
      </div>
      <!-- 表格 -->
      <el-table :data="tableData" style="width: 100%" :default-sort="{ prop: 'release_date', order: 'descending' }">
        <el-table-column prop="release_date" label="发布时间" sortable :formatter="dateFormat"></el-table-column>
        <el-table-column prop="title" label="职位名称"></el-table-column>
        <el-table-column prop="city" label="工作地点"></el-table-column>
        <el-table-column prop="quantity" label="招聘人数"></el-table-column>
        <el-table-column prop="state" label="是否审核" :formatter="stateFormat"></el-table-column>
        <el-table-column label="链上状态" width="120">
          <template slot-scope="scope">
            <el-tag :type="chainStatusType(scope.row.chain_status)" size="mini">
              {{ chainStatusFormat(scope.row.chain_status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260">
          <template slot-scope="scope">
            <el-button @click.prevent="handleEdit(scope.row)" type="primary" plain size="mini">编辑</el-button>
            <el-button @click.prevent="handleDelete(scope.row)" type="danger" plain size="mini">下架职位</el-button>
            <el-button @click.prevent="handleVerify(scope.row)" type="success" plain size="mini"
              v-if="scope.row.chain_status === 1">校验</el-button>
            <el-button @click.prevent="handleRetryChain(scope.row)" type="warning" plain size="mini"
              v-if="scope.row.chain_status === 2">重试上链</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination @current-change="handleCurrentChange" :background="true" :page-size="pageCount"
          :current-page="currentPage" v-if="refreshPagination" layout="prev, pager, next, jumper" :total="totalNums">
        </el-pagination>
      </div>
      <!-- 一致性校验结果弹窗 -->
      <el-dialog title="链上数据一致性校验" :visible.sync="verifyDialogVisible" width="500px">
        <div v-if="verifyResult">
          <el-alert :title="verifyResult.consistent ? '✅ 校验通过' : '❌ 校验失败'"
            :type="verifyResult.consistent ? 'success' : 'error'" :description="verifyResult.message" show-icon>
          </el-alert>
          <div style="margin-top: 16px; font-size: 13px; color: #666;">
            <p><strong>本地数据哈希：</strong></p>
            <p style="word-break: break-all; font-family: monospace;">{{ verifyResult.localHash }}</p>
            <p style="margin-top: 8px;"><strong>链上数据哈希：</strong></p>
            <p style="word-break: break-all; font-family: monospace;">{{ verifyResult.chainHash }}</p>
            <p style="margin-top: 8px;" v-if="verifyResult.txId"><strong>交易ID：</strong></p>
            <p style="word-break: break-all; font-family: monospace;">{{ verifyResult.txId }}</p>
          </div>
        </div>
      </el-dialog>
    </div>
    <!-- 编辑职位 -->
    <position-modify v-else @editClose="editClose" :editPositionID="editPositionID"></position-modify>
  </div>
</template>

<script>
import { Position } from '@/model/position'
import PositionModify from '@/view/position/position-modify'

export default {
  components: {
    PositionModify,
  },
  data() {
    return {
      loading: false,
      tableData: [],
      totalNums: 0,
      currentPage: 1,
      pageCount: 10,
      refreshPagination: true,
      showEdit: false,
      editPositionID: 0,
      verifyDialogVisible: false,
      verifyResult: null,
    }
  },
  async created() {
    this.loading = true
    await this.getByHrId()
    this.loading = false
  },
  methods: {
    async getByHrId() {
      try {
        const page = this.currentPage - 1
        const count = this.pageCount
        const res = await Position.getByHrId(this.$store.getters.user.id, page, count)
        this.tableData = res.items
        this.totalNums = res.total
      } catch (error) {
        this.tableData = []
      }
    },
    // 对发布时间列进行格式化
    dateFormat(row, column) {
      return row['release_date'].substring(0, 10)
    },
    // 对职位状态列进行格式化
    stateFormat(row, column) {
      if (row['state'] === 0) {
        return "未审核";
      } else if (row['state'] === 1) {
        return "审核通过";
      } else {
        return "审核不通过";
      }
    },
    // 链上状态格式化
    chainStatusFormat(status) {
      if (status === 0) return '未上链'
      if (status === 1) return '已上链'
      if (status === 2) return '上链失败'
      return '未知'
    },
    // 链上状态标签颜色
    chainStatusType(status) {
      if (status === 0) return 'info'
      if (status === 1) return 'success'
      if (status === 2) return 'danger'
      return 'info'
    },
    // 切换table页
    async handleCurrentChange(val) {
      this.currentPage = val
      this.loading = true
      this.getByHrId()
      this.loading = false
    },
    handleEdit(val) {
      this.showEdit = true
      this.editPositionID = val.id
    },
    handleDelete(val) {
      this.$confirm('是否下架该职位?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
        const res = await Position.delete(val.id)
        if (res.code < window.MAX_SUCCESS_CODE) {
          this.getByHrId()
          this.$message({
            type: 'success',
            message: `${res.message}`,
          })
        }
      })
    },
    // 一致性校验
    async handleVerify(val) {
      try {
        const res = await Position.verifyChainConsistency(val.id)
        this.verifyResult = res
        this.verifyDialogVisible = true
      } catch (error) {
        this.$message.error('校验请求失败')
      }
    },
    // 重新上链
    async handleRetryChain(val) {
      this.$confirm('是否重新上链该职位数据?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
        try {
          const res = await Position.retryChainUpload(val.id)
          if (res.code < window.MAX_SUCCESS_CODE) {
            this.$message.success('重新上链成功')
            this.getByHrId()
          }
        } catch (error) {
          this.$message.error('重新上链失败')
        }
      })
    },
    rowClick() { },
    editClose() {
      this.showEdit = false
      this.getByHrId()
    },
  },
}
</script>

<style lang="scss" scoped>
.container {
  padding: 0 30px;

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .title {
      text-align: left;
      height: 59px;
      line-height: 59px;
      color: $parent-title-color;
      font-size: 16px;
      font-weight: 500;
    }
  }

  .pagination {
    display: flex;
    justify-content: flex-end;
    margin: 20px;
  }
}
</style>
