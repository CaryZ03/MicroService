const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true
})

module.exports = {
  devServer: {
    port: 8080,
    proxy: {
      '/hotel-service-api-8080': {
        target: 'http://localhost:8080', // 代理的目标地址，比如你的后端服务器  
        ws: true, // 如果你的应用使用了 WebSocket  
        changeOrigin: true, // 开启跨域代理  
        pathRewrite: {
          '^/hotel-service-api-8080': '' // 如果后端服务没有`/api`前缀，你需要重写路径  
        }
      },
      '/location-service-api-8081': {
        target: 'http://localhost:8081',
        ws: true,
        changeOrigin: true,
        pathRewrite: { '^/location-service-api-8081': '' }
      },
      '/order-service-api-8082': {
        target: 'http://localhost:8082',
        ws: true,
        changeOrigin: true,
        pathRewrite: { '^/order-service-api-8082': '' }
      },
      '/train-service-api-8083': {
        target: 'http://localhost:8083',
        ws: true,
        changeOrigin: true,
        pathRewrite: { '^/train-service-api-8083': '' }
      },
      '/user-service-api-8084': {
        target: 'http://localhost:8084',
        ws: true,
        changeOrigin: true,
        pathRewrite: { '^/user-service-api-8084': '' }
      },

    }
  },

}


