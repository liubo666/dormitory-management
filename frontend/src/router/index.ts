import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: { title: '注册', requiresAuth: false }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/views/forgot-password/index.vue'),
    meta: { title: '忘记密码', requiresAuth: false }
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: () => import('@/views/reset-password/index.vue'),
    meta: { title: '重置密码', requiresAuth: false }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'dashboard-outline' }
      },
      {
        path: 'dormitory',
        name: 'Dormitory',
        component: () => import('@/views/dormitory/index.vue'),
        meta: { title: '宿舍与楼栋管理', icon: 'home-outline' }
      },
      {
        path: 'student',
        name: 'Student',
        component: () => import('@/views/student/index.vue'),
        meta: { title: '学生管理', icon: 'people-outline' }
      },
      {
        path: 'checkin',
        name: 'Checkin',
        component: () => import('@/views/checkin/index.vue'),
        meta: { title: '入住管理', icon: 'log-in-outline' }
      },
      {
        path: 'repair',
        name: 'Repair',
        component: () => import('@/views/repair/index.vue'),
        meta: { title: '报修管理', icon: 'build-outline' }
      },
      {
        path: 'inspection',
        name: 'Inspection',
        component: () => import('@/views/inspection/index.vue'),
        meta: { title: '卫生检查', icon: 'clipboard-outline' }
      },
      {
        path: 'visitor',
        name: 'Visitor',
        component: () => import('@/views/visitor/index.vue'),
        meta: { title: '访客登记', icon: 'person-add-outline' }
      },
      {
        path: 'fee',
        name: 'Fee',
        component: () => import('@/views/fee/index.vue'),
        meta: { title: '费用管理', icon: 'card-outline' }
      },
      {
        path: 'registration',
        name: 'Registration',
        component: () => import('@/views/admin/registration/index.vue'),
        meta: { title: '注册审批', icon: 'person-add-outline', requiresAdmin: true }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/settings/index.vue'),
        meta: { title: '个人设置', icon: 'settings-outline' }
      }
    ]
  },
  {
    path: '/registration/admin/approve/:token',
    name: 'AdminApproval',
    component: () => import('../views/admin/approval/index.vue'),
    meta: { title: '申请审批', requiresAuth: false }
  },
  {
    path: '/registration/admin/reject/:token',
    name: 'AdminReject',
    redirect: to => `/admin/approve/${to.params.token}`
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && userStore.isLoggedIn) {
    next('/')
  } else {
    next()
  }
})

export default router