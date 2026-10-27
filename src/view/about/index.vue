<!-- 首页 -->
<template>
  <div id="app">
    <Top :isnow="1" />
    <Nav />
    <Search />
    <ScrollTop />
  </div>
</template>

<script>
import ScrollTop from '@/component/index/scrollTop.vue'
import Search from '@/component/index/Search.vue'
import Nav from '@/component/index/Nav.vue'
import Top from '@/component/index/index.vue'

export default {
  components: {
    Search,
    Nav,
    Top,
    ScrollTop,
  },


  methods: {

  },

}
</script>

<style lang="scss">
@import url("//at.alicdn.com/t/c/font_4675738_tkkf9xzx57.css");
$nx-color: #0470b8;
$all-padding: 0;
$nx-width: 76.25rem;

#app {
  min-width: $nx-width;

}

@media screen and (max-with: 76.25rem) {}
</style>

