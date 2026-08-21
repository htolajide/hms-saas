import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8081/api/v1',
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  
  // ✅ CRITICAL: Only attach if token exists AND is not FormData
  if (token && !(config.data instanceof FormData)) {
    config.headers.Authorization = `Bearer ${token}`;
  } else if (token && config.data instanceof FormData) {
    // For FormData, still attach token but let browser set Content-Type
    config.headers.Authorization = `Bearer ${token}`;
  }
  
  return config;
});

export default api;