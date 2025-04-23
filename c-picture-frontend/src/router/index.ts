import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '@/pages/HomePage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomePage,
    },
    {
      path: '/user',
      name: 'user',
      children: [
        {
          path: 'login',
          name: 'login',
          component: () => import('@/pages/user/UserLoginPage.vue'),
        },
        {
          path: 'register',
          name: 'register',
          component: () => import('@/pages/user/UserRegisterPage.vue'),
        },
      ]
    },
    {
      path:'/admin',
      name:'admin',
      // isAdmin:true, todo 权限验证
      children: [
        {
          path: 'usermanager',
          name: 'usermanager',
          component: () => import('@/pages/admin/UserManagerPage.vue'),
        },
      ]
    },

    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue'),
    },
  ],
})

export default router
