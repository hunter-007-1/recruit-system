<!-- 首页的职位分类模块 -->
<template>
  <div class="Nav">
    <div class="nav-image">
      <el-carousel height="400px">
        <el-carousel-item v-for="(image, index) in imglist" :key="index">
          <img :src="image" />
        </el-carousel-item>
      </el-carousel>
    </div>
  </div>
</template>

<script>
import { Category } from '@/model/category'
export default {
  data() {
    return {
      name: 'hello',
      wName: [],
      overList: false,
      outList: true,
      imglist: ["https://job.zte.com.cn/content/dam/zte-job/home/2025banner.jpg",
        "https://career.huawei.com/reccampportal/servlet/portal/attachment?dlType=ImageAttachment&attachmentType=PortalPicAttachment&attachmentId=21698725",
        "https://campus.iflytek.com/official-pc/img/jobBannerNew2025.png",
        "https://portal-oss.zhiye.com/609105/image/ec740ce9-5237-4dc0-9384-7186c6f68eb5.png"]
    }
  },
  async mounted() {
    const res = await Category.show()
    this.wName = res
  },
}
</script>

<style lang="scss">
$nx-color: #449efe;
$all-padding: 0;
.Nav {
  width: 100%;
  height: 29rem;
  .nav-image {
    position: relative;
    width: 56.25rem;
    top: 1.4rem;
    margin: 0 auto;
    img {
      height: 100%;

    }
  }

}
</style>
