import axios from 'axios'

// 用户相关API
export const userApi = {
  // 用户登录
  login: (data) => axios.post('/users/login', data),
  
  // 用户注册
  register: (data) => axios.post('/users/register', data),
  
  // 获取用户信息
  getUserById: (id) => axios.get(`/users/${id}`),
  
  // 获取所有用户
  getAllUsers: () => axios.get('/users'),
  
  // 根据角色获取用户
  getUsersByRole: (role) => axios.get(`/users/role/${role}`),
  
  // 更新用户信息
  updateUser: (id, data) => axios.put(`/users/${id}`, data),
  
  // 更新用户状态
  updateUserStatus: (id, status) => axios.put(`/users/${id}/status`, { status }),
  
  // 删除用户
  deleteUser: (id) => axios.delete(`/users/${id}`)
}

// 产品相关API
export const productApi = {
  // 获取所有产品
  getAllProducts: () => axios.get('/products'),
  
  // 获取产品列表（支持分页和筛选）
  getProducts: (params) => axios.get('/products', { params }),
  
  // 获取模拟产品数据（用于测试）
  getMockProducts: () => axios.get('/mock/products'),
  
  // 根据ID获取产品
  getProductById: (id) => axios.get(`/products/${id}`),
  
  // 根据销售方ID获取产品
  getProductsBySellerId: (sellerId) => axios.get(`/products/seller/${sellerId}`),
  
  // 搜索产品
  searchProducts: (name) => axios.get(`/products/search?name=${name}`),
  
  // 根据品种获取产品
  getProductsByVariety: (variety) => axios.get(`/products/variety/${variety}`),
  
  // 根据价格区间获取产品
  getProductsByPriceRange: (minPrice, maxPrice) => axios.get(`/products/price?minPrice=${minPrice}&maxPrice=${maxPrice}`),
  
  // 添加产品
  addProduct: (data) => axios.post('/products', data),
  
  // 更新产品
  updateProduct: (id, data) => axios.put(`/products/${id}`, data),
  
  // 更新库存
  updateStock: (id, quantity) => axios.put(`/products/${id}/stock`, { quantity }),
  
  // 更新产品状态
  updateProductStatus: (id, status) => axios.put(`/products/${id}/status`, { status }),
  
  // 删除产品
  deleteProduct: (id) => axios.delete(`/products/${id}`),
  
  // 高级搜索
  advancedSearch: (params) => axios.get('/products/advanced-search', { params }),
  
  // 搜索建议
  getSearchSuggestions: (keyword) => axios.get(`/products/search-suggestions?keyword=${keyword}`),
  
  // 热门搜索词
  getHotKeywords: () => axios.get('/products/hot-keywords')
}

// 订单相关API
export const orderApi = {
  // 获取所有订单
  getAllOrders: () => axios.get('/orders'),
  
  // 根据ID获取订单
  getOrderById: (id) => axios.get(`/orders/${id}`),
  
  // 根据订单号获取订单
  getOrderByOrderNo: (orderNo) => axios.get(`/orders/orderNo/${orderNo}`),
  
  // 根据购买方ID获取订单
  getOrdersByBuyerId: (buyerId) => axios.get(`/orders/buyer/${buyerId}`),
  
  // 根据产品ID获取订单
  getOrdersByProductId: (productId) => axios.get(`/orders/product/${productId}`),
  
  // 根据订单状态获取订单
  getOrdersByStatus: (status) => axios.get(`/orders/status/${status}`),
  
  // 创建订单
  createOrder: (data) => axios.post('/orders', data),
  
  // 更新订单
  updateOrder: (id, data) => axios.put(`/orders/${id}`, data),
  
  // 更新订单状态
  updateOrderStatus: (id, status) => axios.put(`/orders/${id}/status`, { status }),
  
  // 支付订单
  payOrder: (id, paymentMethod) => axios.post(`/orders/${id}/pay`, { paymentMethod }),
  
  // 取消订单
  cancelOrder: (id) => axios.post(`/orders/${id}/cancel`),
  
  // 删除订单
  deleteOrder: (id) => axios.delete(`/orders/${id}`)
}

// 统计相关API
export const statisticsApi = {
  // 获取概览统计
  getOverview: () => axios.get('/statistics/overview'),

  // 获取销售统计
  getSales: (startDate, endDate) => axios.get('/statistics/sales', {
    params: { startDate, endDate }
  }),

  // 获取用户分析
  getUsers: () => axios.get('/statistics/users'),

  // 获取产品分析
  getProducts: () => axios.get('/statistics/products'),

  // 获取订单分析
  getOrders: () => axios.get('/statistics/orders'),

  // 获取养殖计划分析
  getBreedingPlans: () => axios.get('/statistics/breeding-plans'),

  // 获取物流分析
  getLogistics: () => axios.get('/statistics/logistics')
}

// 商城相关API
export const marketplaceApi = {
  // 获取产品供应商列表
  getProductSuppliers: (productId) => axios.get(`/marketplace/suppliers/${productId}`),

  // 获取商城产品列表
  getMarketplaceProducts: (params) => axios.get('/marketplace/products', { params }),

  // 创建采购订单
  createPurchaseOrder: (data) => axios.post('/marketplace/purchase-orders', data),

  // 获取采购方的采购订单
  getBuyerPurchaseOrders: (buyerId) => axios.get(`/marketplace/purchase-orders/buyer/${buyerId}`),

  // 获取供应商的供应订单
  getSupplierPurchaseOrders: (supplierId) => axios.get(`/marketplace/purchase-orders/supplier/${supplierId}`),

  // 更新采购订单状态
  updatePurchaseOrderStatus: (id, status) => axios.put(`/marketplace/purchase-orders/${id}/status`, { status }),

  // 获取销售方库存
  getSellerInventory: (sellerId) => axios.get(`/marketplace/inventory/${sellerId}`),

  // 更新库存
  updateInventory: (id, data) => axios.put(`/marketplace/inventory/${id}`, data),

  // 获取商城统计数据
  getMarketplaceStats: () => axios.get('/marketplace/stats')
}

// 评价相关API
export const reviewApi = {
  // 获取产品评价
  getProductReviews: (productId) => axios.get(`/api/reviews/product/${productId}`),
  
  // 创建评价
  createReview: (data) => axios.post('/api/reviews', data),
  
  // 更新评价
  updateReview: (id, data) => axios.put(`/api/reviews/${id}`, data),
  
  // 删除评价
  deleteReview: (id) => axios.delete(`/api/reviews/${id}`),
  
  // 点赞评价
  likeReview: (id) => axios.post(`/api/reviews/${id}/like`),
  
  // 获取用户评价
  getUserReviews: (userId) => axios.get(`/api/reviews/user/${userId}`),
  
  // 获取评价统计
  getReviewStats: (productId) => axios.get(`/api/reviews/stats/${productId}`)
}

// 地址管理API
export const addressApi = {
  // 获取用户地址列表
  getAddresses: () => axios.get('/api/addresses'),
  
  // 创建地址
  createAddress: (data) => axios.post('/api/addresses', data),
  
  // 更新地址
  updateAddress: (id, data) => axios.put(`/api/addresses/${id}`, data),
  
  // 删除地址
  deleteAddress: (id) => axios.delete(`/api/addresses/${id}`),
  
  // 设置默认地址
  setDefaultAddress: (id) => axios.put(`/api/addresses/${id}/default`)
}

// 收藏夹API
export const favoriteApi = {
  // 获取商品收藏
  getProductFavorites: () => axios.get('/api/favorites/products'),
  
  // 获取店铺收藏
  getSellerFavorites: () => axios.get('/api/favorites/sellers'),
  
  // 添加商品收藏
  addProductFavorite: (productId) => axios.post('/api/favorites/products', { productId }),
  
  // 添加店铺收藏
  addSellerFavorite: (sellerId) => axios.post('/api/favorites/sellers', { sellerId }),
  
  // 移除收藏
  removeFavorite: (id) => axios.delete(`/api/favorites/${id}`),
  
  // 清空所有收藏
  clearAllFavorites: () => axios.delete('/api/favorites/clear')
}

// 浏览历史API
export const historyApi = {
  // 获取浏览历史
  getBrowseHistory: (params) => axios.get('/api/history', { params }),
  
  // 记录浏览历史
  recordBrowseHistory: (productId) => axios.post('/api/history', { productId }),
  
  // 删除浏览记录
  removeFromHistory: (id) => axios.delete(`/api/history/${id}`),
  
  // 清空浏览历史
  clearBrowseHistory: () => axios.delete('/api/history/clear')
}

// 通知API
export const notificationApi = {
  // 获取用户通知列表
  getUserNotifications: (params) => axios.get('/api/notifications', { params }),
  
  // 获取未读通知数量
  getUnreadCount: (userId) => axios.get('/api/notifications/unread-count', { params: { userId } }),
  
  // 创建通知
  createNotification: (data) => axios.post('/api/notifications', data),
  
  // 标记通知为已读
  markAsRead: (id) => axios.put(`/api/notifications/${id}/read`),
  
  // 标记所有通知为已读
  markAllAsRead: (userId) => axios.put('/api/notifications/mark-all-read', null, { params: { userId } }),
  
  // 删除通知
  deleteNotification: (id) => axios.delete(`/api/notifications/${id}`),
  
  // 获取通知统计
  getNotificationStats: (userId) => axios.get('/api/notifications/stats', { params: { userId } }),
  
  // 创建订单通知
  createOrderNotification: (userId, orderNo, status) => axios.post('/api/notifications/order', null, {
    params: { userId, orderNo, status }
  }),
  
  // 创建库存预警通知
  createStockNotification: (userId, productName, stock) => axios.post('/api/notifications/stock', null, {
    params: { userId, productName, stock }
  })
}
