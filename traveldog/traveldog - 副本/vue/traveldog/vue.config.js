const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true
})

module.exports = {  
  devServer: {  
    port: 8082,
    proxy: {  
      '/api': {  
        target: 'http://localhost:8082', // 代理的目标地址，比如你的后端服务器  
        ws: true, // 如果你的应用使用了 WebSocket  
        changeOrigin: true, // 开启跨域代理  
        pathRewrite: {  
          '^/api': '' // 如果后端服务没有`/api`前缀，你需要重写路径  
        }  
      },  
    }  
  }  
}


