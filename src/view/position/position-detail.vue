<template>
  <div class="container">
    <div class="title">
      <span>职位详细信息</span>
      <span class="back" @click="back">
        <i class="iconfont icon-fanhui"></i> 返回
      </span>
    </div>
    <el-divider></el-divider>
    <div class="wrap">
      <el-row>
        <el-col :lg="16" :md="20" :sm="24" :xs="24">
          <el-form :model="form" status-icon ref="form" label-width="100px" v-loading="loading" @submit.native.prevent>
            <el-form-item label="职位名称" prop="title">
              <span>{{ form.title }}</span>
            </el-form-item>
            <el-form-item label="所属分类" prop="category_name">
              <span>{{ form.category_name }}</span>
            </el-form-item>
            <el-form-item label="职位浏览量" prop="hits">
              <span>{{ form.hits }}</span>
            </el-form-item>
            <el-form-item label="招聘人数" prop="quantity">
              <span>{{ form.quantity }}</span>
            </el-form-item>
            <el-form-item label="薪资" prop="salary_up">
              <span>{{ form.salary_down }}~{{ form.salary_up }}</span>
            </el-form-item>
            <el-form-item label="工作地点" prop="city">
              <span>{{ form.city }}</span>
            </el-form-item>
            <el-form-item label="职位要求" prop="requirement">
              <span>{{ form.requirement }}</span>
            </el-form-item>
            <el-form-item label="公司名称" prop="company_name">
              <span>{{ form.company_name }}</span>
            </el-form-item>
            <el-form-item label="公司简介" prop="company_desc">
              <span>{{ form.company_desc }}</span>
            </el-form-item>
            <!-- 区块链上链状态 -->
            <el-form-item label="链上状态">
              <el-tag :type="chainStatusType(form.chain_status)" size="small">
                {{ chainStatusFormat(form.chain_status) }}
              </el-tag>
            </el-form-item>
            <el-form-item label="数据哈希" v-if="form.chain_hash">
              <span style="font-family: monospace; font-size: 12px; word-break: break-all;">{{ form.chain_hash }}</span>
            </el-form-item>
            <el-form-item label="交易ID" v-if="form.chain_tx_id">
              <span style="font-family: monospace; font-size: 12px; word-break: break-all;">{{ form.chain_tx_id }}</span>
            </el-form-item>
            <el-form-item class="submit">
              <!--通过职位的状态属性state来控制通过按钮和不通过按钮是否显示-->
              <el-button type="primary" @click="handlePass('form')" v-if="form.state == 0 && flag">通 过</el-button>
              <el-button @click="handleFail('form')" v-if="form.state == 0 && flag">不通过</el-button>
              <el-button type="primary" @click="handleDelivery('form')" v-if="temp == 1">投递简历</el-button>
              <el-button @click="handleCollect('form')" v-if="temp == 1">收藏职位</el-button>
              <el-button type="success" @click="handleVerify('form')" v-if="form.chain_status === 1">链上校验</el-button>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
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
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { Position } from '@/model/position'
import { Application } from '@/model/application'
import { Resume } from '@/model/resume'
import { Favor } from '@/model/favor'
import router from '../../router'

export default {
  // 定义变量
  props: {
    // 子组件定义的变量通过父组件赋值
    detailPositionID: {
      type: Number,
    },
    flag: {
      type: Boolean,
      default: true,
    },
    temp: {
      type: Number,
      default: 0,
    },
  },
  data() {
    return {
      loading: false,
      form: {},
      list: {
        user_id: '',
        position_id: '',
        resume_id: '',
        hr_id: '',
        company_id: '',
      },
      collect: {},
      verifyDialogVisible: false,
      verifyResult: null,
    }
  },
  async mounted() {
    this.loading = true
    const res = await Position.getById(this.detailPositionID)
    // 更新职位浏览量
    await Position.updateHits(this.detailPositionID)
    this.form = res
    this.getInfo()
    this.loading = false
  },
  methods: {
    async handlePass(val) {
      const res = await Position.updateState(this.detailPositionID, 1)
      if (res.code < window.MAX_SUCCESS_CODE) {
        this.$message.success(`${res.message}`)
        this.$emit('detailClose')
      }
    },
    async handleFail(val) {
      const res = await Position.updateState(this.detailPositionID, 2)
      if (res.code < window.MAX_SUCCESS_CODE) {
        this.$message.success(`${res.message}`)
        this.$emit('detailClose')
      }
    },
    async handleDelivery(val) {
      if (this.$store.getters.user === null) {
        this.$message.error("请注册登录后投递简历")
        return
      }
      const flag = await Application.getByPositionIdAndUserId(this.detailPositionID, this.$store.getters.user.id)
      if (flag === false) {
        this.$message.error("你已投递过该职位，不可重复投递")
      } else {
        this.list.user_id = this.$store.getters.user.id
        this.list.position_id = this.detailPositionID
        const temp = await Resume.getByUserId(this.$store.getters.user.id)
        this.list.resume_id = temp.resume_id
        const result = await Position.getById(this.detailPositionID)
        this.list.hr_id = result.hr_id
        this.list.company_id = result.company_id
        const res = await Application.create(this.list)
        if (res.code < window.MAX_SUCCESS_CODE) {
          this.$message.success(`${res.message}`)
        }
      }
    },
    async getInfo() {
      this.list.user_id = this.$store.getters.user.id
      this.list.position_id = this.detailPositionID
      const temp = await Resume.getByUserId(this.$store.getters.user.id)
      this.list.resume_id = temp.id
      const result = await Position.getById(this.detailPositionID)
      this.list.hr_id = result.hr_id
      this.list.company_id = result.company_id
    },
    async handleCollect(val) {
      if (this.$store.getters.user === null) {
        this.$message.error("请注册登录后收藏职位")
        return
      }
      this.collect.user_id = this.$store.getters.user.id
      this.collect.position_id = this.detailPositionID
      const res = await Favor.create(this.collect)
      if (res.code < window.MAX_SUCCESS_CODE) {
        this.$message.success(`${res.message}`)
      }
    },
    // 链上状态格式化
    chainStatusFormat(status) {
      if (status === 0) return '未上链'
      if (status === 1) return '已上链'
      if (status === 2) return '上链失败'
      return '未知'
    },
    chainStatusType(status) {
      if (status === 0) return 'info'
      if (status === 1) return 'success'
      if (status === 2) return 'danger'
      return 'info'
    },
    // 链上一致性校验
    async handleVerify() {
      try {
        const res = await Position.verifyChainConsistency(this.detailPositionID)
        this.verifyResult = res
        this.verifyDialogVisible = true
      } catch (error) {
        this.$message.error('校验请求失败')
      }
    },
    back() {
      this.$emit('detailClose')
    },
  },
}
</script>

<style lang="scss" scoped>
.el-divider--horizontal {
  margin: 0;
}

.container {
  text-align: left;

  .title {
    height: 59px;
    line-height: 59px;
    color: $parent-title-color;
    font-size: 16px;
    font-weight: 500;
    text-indent: 40px;

    .back {
      float: right;
      margin-right: 40px;
      cursor: pointer;
    }
  }

  .wrap {
    padding: 20px;
  }

  .submit {
    float: left;
  }
}
</style>
