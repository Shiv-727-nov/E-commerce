import api from './api';

export const productService = {
  getAllProducts: async () => {
    const response = await api.get('/products');
    return response.data;
  },

  getProductById: async (id) => {
    const response = await api.get(`/products/${id}`);
    return response.data;
  },

  getProductsByGender: async (gender) => {
    const response = await api.get(`/products/gender/${gender}`);
    return response.data;
  },

  getProductsByCategory: async (category) => {
    const response = await api.get(`/products/category/${category}`);
    return response.data;
  },

  getProductsByGenderAndCategory: async (gender, category) => {
    const response = await api.get(`/products/gender/${gender}/category/${category}`);
    return response.data;
  },

  searchProducts: async (keyword) => {
    const response = await api.get(`/products/search?keyword=${encodeURIComponent(keyword)}`);
    return response.data;
  },

  createProduct: async (productData) => {
    const response = await api.post('/admin/products', productData);
    return response.data;
  },

  updateProduct: async (id, productData) => {
    const response = await api.put(`/admin/products/${id}`, productData);
    return response.data;
  },

  deleteProduct: async (id) => {
    const response = await api.delete(`/admin/products/${id}`);
    return response.data;
  },

  getLowStockProducts: async (threshold = 10) => {
    const response = await api.get(`/admin/products/low-stock?threshold=${threshold}`);
    return response.data;
  }
};

