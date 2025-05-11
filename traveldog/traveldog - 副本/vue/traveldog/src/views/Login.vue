<template>
    <body id="poster">
        <el-form class="login-container" label-position="left" label-width="0px">
            <h3 class="login_title">
                系统登录
                <el-button @click="toregister()">点我注册</el-button>
            </h3>
            <el-form-item label="">
                <el-input type="text" v-model="loginForm.uid" autocomplete="off" placeholder="账号"></el-input>
            </el-form-item>
            <el-form-item label="">
                <el-input type="password" v-model="loginForm.password" autocomplete="off" placeholder="密码"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" style="width:100%; background: #505458;border: none;" @click="Login()">登录</el-button>
            </el-form-item>
        </el-form>
    </body>
  </template>
  
  <script>
  export default {
    name: 'Login',
    data() {
      return {
        loginForm: {
          uid: '',
          password: ''
        }
      }
    },
    methods: {
      Login() {
        this.$emit('update-uid', this.loginForm.u_id);  
        this.axios.post('/api/login',this.loginForm).then((resp) =>{
            let data = resp.data;
            if(data.success){   //成功
                this.$message({
                    showClose: true,
                    message: '成功登录',
                    type: 'success'
                });
                sessionStorage.setItem('uid', data.content.id);
                sessionStorage.setItem('password',this.loginForm.password);
                this.$router.push({path:'/home'})
                this.loginForm = {};    //置空
            }else{
                this.loginForm = {};    //置空
                this.$message({
                    showClose: true,
                    message: data.message,
                    type: 'error'
                });
            }
        })
      } ,
      toregister(){
        this.$router.push({path:'/register'})
      }
    }
  }
  </script>
  
<style>
    #poster{
        background-position: center;
        height:100%;
        width: 100%;
        background-size: cover;
        position: fixed;
    }
    body{
        margin: 0px;
        padding:0px;
    }
    .login-container{
        border-radius: 15px;
        background-clip: padding-box;
        margin: 90px auto;
        width: 350px;
        padding: 35px 35px 15px 15px;
        background: #fff;
        border: 1px solid #eaeaea;
        box-shadow: 0 0 25px #cac6c6;
    }
    .login_title{
        margin: 0px auto 40px auto;
        text-align:center;
        color: #505458;
    }
</style>