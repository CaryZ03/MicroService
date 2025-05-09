<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>

export default {
  name: 'App',
  data() {
    return {
      gap_time: 0,
      beforeUnload_time: 0,
      loginForm: {
        uid: '',
        password: ''
      }
    };
  },
  methods: {
    // 关闭窗口之前执行
    beforeunloadHandler() {
      this.beforeUnload_time = new Date().getTime();
    },
    unloadHandler() {
      this.gap_time = new Date().getTime() - this.beforeUnload_time;
      //判断是窗口关闭还是刷新 毫秒数判断 网上大部分写的是5
      if (this.gap_time <= 10) {
        this.loginForm.uid = localStorage.getItem('uid')
        this.axios.post('/user-service-api-8084/logout', this.loginForm)
      }
    },
  },
  destroyed() {
    // 移除监听
    window.removeEventListener("beforeunload", () => this.beforeunloadHandler());
    window.removeEventListener("unload", () => this.unloadHandler());
  },
  mounted() {
    // 监听浏览器关闭
    window.addEventListener("beforeunload", () => this.beforeunloadHandler());
    window.addEventListener("unload", () => this.unloadHandler());
  }
};
</script>

<style lang="less">
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;

  a {
    font-weight: bold;
    color: #2c3e50;

    &.router-link-exact-active {
      color: #42b983;
    }
  }
}
</style>
