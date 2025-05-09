import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '../views/Login.vue'
import register from '../views/register.vue'
import Platform from '../views/Platform.vue'
import Home from '../views/Home/index.vue'
//异步
const Tickets = () => import('../views/Tickets/Tickets.vue')
const Meal = () => import('../views/Meal/Meal.vue')
const Hotel = () => import('../views/Hotel/Hotel.vue')

const Notice = () => import('../views/Notice/Notice.vue')
const Usercenter = () => import('../views/Usercenter/index.vue')
const OrderList = () => import('../views/Usercenter/OrderList/OrderList.vue')
const CustomersManage = () => import('../views/Usercenter/CustomersManage/CustomersManage.vue')
const UserInformation = () => import('../views/Usercenter/UserInformation/UserInformation.vue')


Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'register',
    component: register
  },
  {
    path: '/platform',
    name: 'Platform',
    component: Platform,
    children: [
      {
        path: '/home',
        name: 'Home',
        component: Home
      }, {
        path: '/tickets',
        name: 'Tickets',
        component: Tickets
      }, {
        path: '/meal',
        name: 'Meal',
        component: Meal
      }, {
        path: '/hotel',
        name: 'Hotel',
        component: Hotel
      }, {
        path: '/usercenter',
        name: 'Usercenter',
        component: Usercenter,
        redirect: '/usercenter/userinformation',
        children: [
          {
            path: 'userinformation',
            component: UserInformation
          },
          {
            path: 'customersmanage',
            component: CustomersManage
          },
          {
            path: 'orderlist',
            component: OrderList
          }
        ]
      },
    ]
  }
]

const router = new VueRouter({
  routes
})

export default router
