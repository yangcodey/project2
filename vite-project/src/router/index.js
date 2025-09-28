import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/products',
    name: 'Products',
    component: () => import('../views/Products.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/products/:id',
    name: 'ProductDetail',
    component: () => import('../views/ProductDetail.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('../views/Orders.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/Admin.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/icon-test',
    name: 'IconTest',
    component: () => import('../views/IconTest.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/analytics',
    name: 'Analytics',
    component: () => import('../views/Analytics.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/seller-products',
    name: 'SellerProducts',
    component: () => import('../views/SellerProducts.vue'),
    meta: { requiresAuth: true, requiresSeller: true }
  },
  {
    path: '/feature-test',
    name: 'FeatureTest',
    component: () => import('../views/FeatureTest.vue'),
    meta: { requiresAuth: false }
  },
      {
        path: '/import-test',
        name: 'ImportTest',
        component: () => import('../views/ImportTest.vue'),
        meta: { requiresAuth: false }
      },
      {
        path: '/seller-dashboard',
        name: 'SellerDashboard',
        component: () => import('../views/SellerDashboard.vue'),
        meta: { requiresAuth: true, requiresSeller: true }
      },
      {
        path: '/buyer-dashboard',
        name: 'BuyerDashboard',
        component: () => import('../views/BuyerDashboard.vue'),
        meta: { requiresAuth: true, requiresBuyer: true }
      },
  {
    path: '/marketplace',
    name: 'Marketplace',
    component: () => import('../views/Marketplace.vue'),
    meta: { requiresAuth: true, requiresSeller: true }
  },
  {
    path: '/product-reviews/:productId',
    name: 'ProductReviews',
    component: () => import('../views/ProductReviews.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/address-management',
    name: 'AddressManagement',
    component: () => import('../views/AddressManagement.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('../views/Favorites.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/browse-history',
    name: 'BrowseHistory',
    component: () => import('../views/BrowseHistory.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/advanced-search',
    name: 'AdvancedSearch',
    component: () => import('../views/AdvancedSearch.vue'),
    meta: { requiresAuth: true }
  }
]

export default routes
