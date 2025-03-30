<script setup lang="ts">
import BasicLayout from '@/layout/BasicLayout.vue'
import { healthUsingGet } from '@/api/mainController.ts'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'

healthUsingGet().then((res =>{
  console.log (res);
}))


</script>

<template>
 <div id="app">
   <BasicLayout></BasicLayout>
 </div>
</template>

<style scoped>

</style>
