import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import ImageDetail from '../components/ImageDetail.vue'
import ImageReview from '../views/ImageReview.vue'
import UserManagement from '../views/UserManagement.vue'
// 新增私人空间和AI功能路由
import PrivateSpace from '../views/PrivateSpace.vue'
import AITools from '../views/AITools.vue'

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/image/:id', name: 'ImageDetail', component: ImageDetail, props: true },
  { path: '/admin/image-review', name: 'ImageReview', component: ImageReview },
  { path: '/admin/user-management', name: 'UserManagement', component: UserManagement },
  // 新增路由
  { path: '/private-space', name: 'PrivateSpace', component: PrivateSpace },
  { path: '/ai-tools', name: 'AITools', component: AITools }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router