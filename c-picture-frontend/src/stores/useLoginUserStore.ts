import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { getCurrentUserUsingGet } from '@/api/userController.ts'
import { useRouter } from 'vue-router'

/**
 * 存储登录登录用户的store
 */
export const useLoginUserStore = defineStore('loginUser', () => {


  const loginUser = ref<any>({
    userName: '未登录'
  })
  const router = useRouter()

  async function fetchLoginUser() {
      const res = await getCurrentUserUsingGet();
      if(res.data.code === 0){
        loginUser.value = res.data.data;
      }else{
        router.push('/user/login')
      }
  }

  function setLoginUser(newLoginUser: any) {
    loginUser.value = newLoginUser
  }


  return { loginUser, fetchLoginUser, setLoginUser }
})
//
