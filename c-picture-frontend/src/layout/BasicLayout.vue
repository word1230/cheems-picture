<script setup lang="ts">

import GlobalHeader from '@/components/GlobalHeader.vue'
</script>

<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header"><GlobalHeader/></a-layout-header>
      <a-layout-content class="content">
        <router-view></router-view>
      </a-layout-content>
      <a-layout-footer class="footer">
        <a href="https://github.com/word1230" target="_blank">
          cheems'github
        </a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<style scoped>
#basicLayout .header{
  background: white;
  color: unset;
  margin-bottom: 16px;
  padding-inline: 20px;
}

#basicLayout .footer {
  background: #efefef;
  padding: 16px;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
}

#basicLayout .content{
  padding: 20px;
  background: linear-gradient(to right,#fefefe,#fff);
  margin-bottom: 40px;  /* 防止底部显示不全， 加一段空白*/

}
</style>
