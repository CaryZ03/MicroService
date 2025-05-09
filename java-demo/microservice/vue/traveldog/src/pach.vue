<template>
    <div>
      <Login @update-uid="updateUid" />  
      <Notice :uid="currentUid" />  
    </div>
  </template>
  
  <script>
  import Login from './views/Login.vue';  
  import Notice from './views/Notice/Notice.vue';  
  
  export default {
    components: {  
      Login,  
      Notice  
    },  
    data() {  
      return {  
        currentUid: '' // 用于存储从 Login 组件传递过来的 uid  
      };  
    },  
    methods: {  
      updateUid(uid) {  
        this.currentUid = uid;  
      }  
    }  
  };
  </script>