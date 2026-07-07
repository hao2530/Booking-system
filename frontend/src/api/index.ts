import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

export default api

export const userApi = {
  register: (data: any) => api.post('/users/register', data),
  login: (data: any) => api.post('/users/login', data),
}

export const serviceApi = {
  search: (params: any) => api.get('/services/search', { params }),
  byProvider: (id: number) => api.get(`/services/by-provider/${id}`),
  create: (data: any) => api.post('/services', data),
  delete: (id: number) => api.delete(`/services/${id}`),
}

export const slotApi = {
  getAvailable: (serviceId: number, date: string) =>
    api.get('/slots', { params: { serviceId, date } }),
  generate: (data: any) => api.post('/slots/generate', data),
}

export const orderApi = {
  create: (data: any) => api.post('/orders', data),
  cancel: (id: number) => api.put(`/orders/${id}/cancel`),
  my: (userId: number) => api.get('/orders/my', { params: { userId } }),
  providerBookings: (providerId: number) => api.get(`/orders/provider/${providerId}`),
}

export const providerApi = {
  register: (data: any) => api.post('/providers/register', data),
  byUser: (userId: number) => api.get(`/providers/by-user/${userId}`),
}

export const reviewApi = {
  submit: (data: any) => api.post('/reviews', data),
  byService: (serviceId: number) => api.get(`/reviews/by-service/${serviceId}`),
}

export const notificationApi = {
  my: (userId: number) => api.get('/notifications/my', { params: { userId } }),
  markRead: (id: number) => api.put(`/notifications/${id}/read`),
}
