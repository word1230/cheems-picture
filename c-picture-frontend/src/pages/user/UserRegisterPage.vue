<template>
  <div id="login-page" style="display: flex; justify-content: center; align-items: flex-start; height: 100vh;">
    <a-form
      :model="formState"
      name="normal_login"
      class="login-form"
      @finish="handlerLogin"
      style="width: 300px;"
    >
      <!--      todo 请输入账号， 密码-->
      <a-form-item
        label="用户账号"
        name="userAccount"
        :rules="[{ required: true, message: '账号不能为空' }]"
      >
        <a-input v-model:value="formState.userAccount" style="width: 100%;">
          <template #prefix>
            <UserOutlined class="site-form-item-icon" />
          </template>
        </a-input>
      </a-form-item>

      <a-form-item
        label="用户密码"
        name="userPassword"
        :rules="[{ required: true, message: '密码不能为空' }]"
      >
        <a-input-password v-model:value="formState.userPassword" style="width: 100%;">
          <template #prefix>
            <LockOutlined class="site-form-item-icon" />
          </template>
        </a-input-password>
      </a-form-item>

      <a-form-item
        label="确认密码"
        name="checkPassword"
        :rules="[{ required: true, message: '确认密码不能为空' }]"
      >
        <a-input-password v-model:value="formState.checkPassword" style="width: 100%;">
          <template #prefix>
            <LockOutlined class="site-form-item-icon" />
          </template>
        </a-input-password>
      </a-form-item>

      <a-form-item style="text-align: center;">
        <a href="/user/login" >已有账号？立即登录</a>
      </a-form-item>

      <a-form-item style="text-align: center;">
        <a-button :disabled="disabled" type="primary" html-type="submit" class="login-form-button">
          登录
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup>
import { reactive, computed } from 'vue';


import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { userRegisterUsingPost } from '@/api/userController.js'

const router = useRouter();
const formState = reactive({
  userAccount: '',
  userPassword: '',
  checkPassword:'',
});


const handlerLogin = async () => {
  const res = await  userRegisterUsingPost(formState);
  if (res.data.code === 0) {
    router.push('/user/login');
  } else {
    message.error("注册失败，系统错误");
  }
};

</script>

<style scoped>
#components-form-demo-normal-login .login-form {
  max-width: 300px;
}
#components-form-demo-normal-login .login-form-forgot {
  float: right;
}
#components-form-demo-normal-login .login-form-button {
  width: 100%;
}
</style>
