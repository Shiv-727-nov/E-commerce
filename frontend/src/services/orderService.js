import api from './api';

export const orderService = {
  getUserOrders: async () => {
    const response = await api.get('/orders');
    return response.data;
  },

  getOrderById: async (orderId) => {
    const response = await api.get(`/orders/${orderId}`);
    return response.data;
  },

  createOrder: async (orderData) => {
    const response = await api.post('/orders', orderData);
    return response.data;
  },

  createPaymentOrder: async (orderId) => {
    const response = await api.post(`/orders/${orderId}/payment`);
    return response.data;
  },

  verifyPayment: async (orderId, paymentId, signature) => {
    const response = await api.post(`/orders/${orderId}/verify-payment`, null, {
      params: { paymentId, signature }
    });
    return response.data;
  },

  getAllOrders: async () => {
    const response = await api.get('/admin/orders');
    return response.data;
  },

  updateOrderStatus: async (orderId, status) => {
    const response = await api.put(`/admin/orders/${orderId}/status`, null, {
      params: { status }
    });
    return response.data;
  }
};

