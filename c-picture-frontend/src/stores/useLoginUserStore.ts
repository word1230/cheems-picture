import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

/**
 * 存储登录登录用户的store
 */
export const useLoginUserStore = defineStore('loginUser', () => {


  const loginUser = ref<any>({
    userName: '未登录'
  })

  async function fetchLoginUser() {
    //todo 由于后端还没提供接口，暂时注释
    setTimeout(()=>{
        loginUser.value = { userName:"测试用户", id:1}
    },3000)
  }

  function setLoginUser(newLoginUser: any) {
    loginUser.value = newLoginUser
  }


  return { loginUser, fetchLoginUser, setLoginUser }
})
//
