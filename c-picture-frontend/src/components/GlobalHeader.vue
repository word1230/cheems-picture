<template>
  <div id="globalHeader">
    <a-row :wrap="false">
      <a-col flex="200px">
        <router-link to="/">
          <div class="title-bar">
            <img class="logo" src="../assets/logo.jpg" alt="logo" />
            <div class="title">cheems图库云</div>
          </div>
        </router-link>
      </a-col>

      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="current"
          mode="horizontal"
          @click="onMenuClick"
          :items="items"
        />
      </a-col>
      <a-col flex="120px">
        <div class="user-login-status">
          <div v-if="loginUserStore.loginUser.id">
<!--    todo 1.下拉菜单与用户名离得有点远  2.补充头像-->
            <a-dropdown>
              <div  >
                {{loginUserStore.loginUser.userName ?? '无名'}}
                <DownOutlined />
              </div>
              <template #overlay>
                <a-menu>
                  <a-menu-item>
                    <a @click="handlerLogout">登出</a>
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>

          </div>
          <div v-else>
            <a-button type="primary" href="/user/login">登录</a-button>
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>
<script lang="ts" setup>
import { computed, h, ref } from 'vue'
import { HomeOutlined } from '@ant-design/icons-vue'
import { type MenuProps, message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { DownOutlined,UserOutlined } from '@ant-design/icons-vue';
import { userLogOutUsingGet } from '@/api/userController.ts'

const originitems = ref<MenuProps['items']>([
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: "/admin/usermanager",
    icon: () => h(UserOutlined),
    label: '用户管理',
    title: '用户管理',
  },


  {
    key: 'others',
    label: h('a', { href: 'https://github.com/word1230', target: '_blank' }, 'github'),
    title: 'github',
  },
])


const filterMenu = (menus = [] as MenuProps['items'])=> {

  return menus?.filter((menu) => {
    if(menu.key.startsWith('/admin')){
      const loginUser = loginUserStore.loginUser;
      if(!loginUser || loginUser.userRole !== 'admin'){
        return false;
      }
    }
    return true;
  })
}

const items =  computed<MenuProps['items']>(()=>{
  return filterMenu(originitems.value)
})
const router = useRouter()
const current = ref<string[]>([''])

const loginUserStore = useLoginUserStore();
loginUserStore.fetchLoginUser()




const onMenuClick = ({ key }) => {
  router.push({
    path: key,
  })
}

router.afterEach((to) => {
  current.value = [to.path]
})

const handlerLogout = async () => {
  /**
   * 1. 登出
   * 2. 清空用户信息
   */
 const res= await userLogOutUsingGet()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    });
    message.success("登出成功")
    await  router.push('/user/login')
  } else {
    message.error("登出失败")
  }

}


</script>

<style scoped>
.title-bar {
  display: flex;
  align-items: center;
}

.title {
  color: black;
  font-size: 18px;
  margin-left: 16px;
}

.logo {
  height: 48px;
}
</style>

