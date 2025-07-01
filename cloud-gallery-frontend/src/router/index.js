import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import ImageDetail from '../components/ImageDetail.vue'
import ImageReview from '../views/ImageReview.vue'
import UserManagement from '../views/UserManagement.vue'

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/image/:id', name: 'ImageDetail', component: ImageDetail, props: true },
  { path: '/admin/image-review', name: 'ImageReview', component: ImageReview },
  { path: '/admin/user-management', name: 'UserManagement', component: UserManagement }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router