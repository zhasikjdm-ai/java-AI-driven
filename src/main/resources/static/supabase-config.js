// Supabase Configuration
// Note: Use environment variables in production instead of hardcoding secrets

const SUPABASE_CONFIG = {
    url: process.env.SUPABASE_URL || 'https://your-supabase-url.supabase.co',
    key: process.env.SUPABASE_ANON_KEY || 'your-supabase-anon-key'
};

// Supabase client initialization (if using Supabase JS client)
// import { createClient } from '@supabase/supabase-js'
// const supabase = createClient(SUPABASE_CONFIG.url, SUPABASE_CONFIG.key)

// API Configuration
const API_CONFIG = {
    baseURL: 'http://localhost:8081/api',
    timeout: 10000
};

// Auth Helper Functions
const AuthHelper = {
    
    getToken() {
        return localStorage.getItem('token');
    },
    
    setToken(token) {
        localStorage.setItem('token', token);
    },
    
    clearToken() {
        localStorage.removeItem('token');
    },
    
    isAuthenticated() {
        return !!this.getToken();
    },
    
    getAuthHeader() {
        const token = this.getToken();
        return token ? { 'Authorization': `Bearer ${token}` } : {};
    }
};

// API Helper Functions
const ApiHelper = {
    
    async request(method, endpoint, data = null) {
        try {
            const url = `${API_CONFIG.baseURL}${endpoint}`;
            const options = {
                method,
                headers: {
                    'Content-Type': 'application/json',
                    ...AuthHelper.getAuthHeader()
                },
                timeout: API_CONFIG.timeout
            };
            
            if (data && (method === 'POST' || method === 'PUT' || method === 'PATCH')) {
                options.body = JSON.stringify(data);
            }
            
            const response = await fetch(url, options);
            
            if (response.status === 401) {
                AuthHelper.clearToken();
                window.location.href = '/login.html';
                throw new Error('Unauthorized');
            }
            
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            
            return await response.json();
            
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    },
    
    get(endpoint) {
        return this.request('GET', endpoint);
    },
    
    post(endpoint, data) {
        return this.request('POST', endpoint, data);
    },
    
    put(endpoint, data) {
        return this.request('PUT', endpoint, data);
    },
    
    delete(endpoint) {
        return this.request('DELETE', endpoint);
    }
};

// Utility Functions
const Utils = {
    
    formatDate(date) {
        return new Date(date).toLocaleDateString('ru-RU');
    },
    
    formatTime(date) {
        return new Date(date).toLocaleTimeString('ru-RU');
    },
    
    debounce(func, delay) {
        let timeoutId;
        return function(...args) {
            clearTimeout(timeoutId);
            timeoutId = setTimeout(() => func.apply(this, args), delay);
        };
    },
    
    throttle(func, limit) {
        let inThrottle;
        return function(...args) {
            if (!inThrottle) {
                func.apply(this, args);
                inThrottle = true;
                setTimeout(() => inThrottle = false, limit);
            }
        };
    }
};

// Make global for use in HTML scripts
window.SUPABASE_CONFIG = SUPABASE_CONFIG;
window.API_CONFIG = API_CONFIG;
window.AuthHelper = AuthHelper;
window.ApiHelper = ApiHelper;
window.Utils = Utils;
