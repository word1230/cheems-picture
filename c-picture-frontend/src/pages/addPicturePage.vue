<script setup lang="ts">

import PictureUpload from '@/components/PictureUpload.vue'
import { onMounted, ref } from 'vue'
import { reactive } from 'vue';
import { editPictureUsingPost, listPictureTagCategoryUsingGet } from '@/api/pictureController.ts'
import router from '@/router'
import { message } from 'ant-design-vue'

const picture = ref<API.PictureVO>()
const onSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
  pictureForm.name =newPicture.name
}


const pictureForm = reactive<API.PictureEditRequest>({
})

const handlerSubmit = async (values: any) => {

  const pictureId = picture.value?.id;
  if (!pictureId) {
    message.error("请先上传图片");
    return;
  }
  const res = await editPictureUsingPost({
    id: pictureId,
    ...values,
  })
  if (res.data.code === 0 && res.data.data) {
    message.success("修改成功")
    await router.push(`/picture/${pictureId}`)
  } else {
    message.error("修改失败"+res.data.message)
  }
}

//todo 对于标签和分类的处理 学习

const categoryOptions = ref<string[]>([])
const tagOptions = ref<string[]>([])

// 获取标签和分类选项
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    // 转换成下拉选项组件接受的格式
    tagOptions.value = (res.data.data.tagList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
    categoryOptions.value = (res.data.data.categoryList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
  } else {
    message.error('加载选项失败，' + res.data.message)
  }
}

onMounted(() => {
  getTagCategoryOptions()
})


</script>

<template>
  <div id="picture-add">
    <h2> 上传图片</h2>
    <PictureUpload :picture="picture" :on-success="onSuccess"/>
    <a-form
      v-if="picture"
      :model="pictureForm"
      layout="vertical"
      @finish="handlerSubmit"
    >
      <a-form-item name="name" label="名称">
        <a-input v-model:value="pictureForm.name" placeholder="请输入名称" allowClear />
      </a-form-item>
      <a-form-item name="introduction" label="简介" >
        <a-textarea v-model:value="pictureForm.introduction" placeholder="请输入简介" :row="2" autoSize allowClear />
      </a-form-item>
      <a-form-item name="category" label="分类">
        <a-auto-complete
          v-model:value="pictureForm.category"
          :options="categoryOptions"
          />
      </a-form-item>
      <a-form-item name="tags" label="标签" >
        <a-select v-model:value="pictureForm.tags" mode="tags"
                  :options="tagOptions"
                    />
      </a-form-item>

        <a-button type="primary" html-type="submit" style="width: 100%">创建</a-button>
    </a-form>
  </div>

</template>

<style scoped>
#picture-add {
  max-width: 720px;
  margin: 0 auto;
}

</style>
