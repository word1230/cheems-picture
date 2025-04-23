<template>
  <div id="user-manager">


    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="账号">
        <a-input v-model:value="searchParams.userAccount" placeholder="输入账号" />
      </a-form-item>
      <a-form-item label="用户名">
        <a-input v-model:value="searchParams.userName" placeholder="输入用户名" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>


    <a-table :columns="columns" :data-source="dataList" :pagination="pagination" @change="doTableChange">
      <template #headerCell="{ column }">
        <template v-if="column.key === 'id'">
        <span>
          id
        </span>
        </template>
      </template>
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userAvatar'">
          <a-avatar :src="record.userAvatar" />
        </template>

        <template v-if="column.dataIndex === 'userRole'">
         <div v-if="record.userRole === 'admin'">
           管理员
         </div>
          <div v-else-if="record.userRole === 'vip'">
            会员
          </div>
          <div v-else>
            普通用户
          </div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-button danger @click="handlerDelete(record.id)">删除</a-button>
        </template>
      </template>
    </a-table>
  </div>

</template>
<script lang="ts" setup>

import { deleteUserUsingPost, listUserVoByPageUsingPost } from '@/api/userController.ts'
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
const columns = [
  {
    name: 'id',
    dataIndex: 'id',
    key:"id"
  },
  {
    title: '账号',
    dataIndex: 'userAccount',


  },
  {
    title: '用户名',
    dataIndex: 'userName',
    key:"userName"

  },
  {
    title: '头像',
    dataIndex: 'userAvatar',

  },
  {
    title: '简介',

    dataIndex: 'userProfile',
  },
  {
    title: '权限',

    dataIndex: 'userRole',
  },
  {
    title: '创建时间',

    dataIndex: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
  },
];

const dataList = ref([]);
const total =ref(0);
const searchParams = reactive<API.UserQueryRequest>({
  current: 1,
  pageSize:5,
})

/**
 * 获取数据
 */
const fetchData = async () => {
  const res = await listUserVoByPageUsingPost({
    ...searchParams
  })
  if (res.data.data){
    dataList.value = res.data.data.records ??[];
    total.value = res.data.data.total ?? 0;
  }else{
    message.error("获取数据失败"+res.data.message)
  }

}

onMounted(() => {
  fetchData();
})


// todo thinking  computed什么意思 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.current ?? 1,
    pageSize: searchParams.pageSize ?? 5,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total) => `共 ${total} 条`,
  }
})

// 表格变化处理
const doTableChange = (page: any) => {
  searchParams.current = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}


// 获取数据
const doSearch = () => {
  // 重置页码
  searchParams.current = 1
  fetchData()
}

const handlerDelete =async (id : number) => {
  if (!id) {
    message.error('id不能为空')
    return
  }
 const res = await deleteUserUsingPost( {id} );
  if (res.data.code === 0) {
    message.success('删除成功')
   fetchData()
  }else{
    message.error('删除失败'+res.data.message)
  }

}


</script>

