<template>
  <div id="poster">
    <el-form :model="ruleForm" status-icon :rules="rules" ref="ruleForm" label-width="0px" label-position="left"
      class="register_container">
      <h3 class="register_title">
        系统注册
        <el-button @click="toLogin()">去登录</el-button>
      </h3>

      <el-form-item label="" prop="u_id">
        <el-input v-model="ruleForm.u_id" type="text" placeholder="请输入账号" autocomplete="off"
          prefix-icon="el-icon-user-solid"></el-input>
      </el-form-item>

      <el-form-item label="" prop="u_password">
        <el-input type="password" v-model="ruleForm.u_password" autocomplete="off" placeholder="请输入密码"
          prefix-icon="el-icon-lock"></el-input>
      </el-form-item>

      <el-form-item label="" prop="checkPassword">
        <el-input type="password" v-model="ruleForm.checkPassword" autocomplete="off" placeholder="请确认密码"
          prefix-icon="el-icon-lock"></el-input>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" style="background:black;border:none" @click="submitForm('ruleForm')">注册</el-button>
        <el-button @click="resetForm('ruleForm')">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import HelloWorld from '@/components/HelloWorld.vue'
import Login from './Login.vue'
import { Message } from 'element-ui';

export default {
  name: 'register',
  data() {
    var validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      } else {
        if (this.ruleForm.checkPassword !== '') {
          this.$refs.ruleForm.validateField('checkPassword');
        }
        callback();
      }
    };
    var validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.ruleForm.u_password) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    return {
      ruleForm: {
        u_id: '',
        u_password: '',
        checkPassword: ''
      },
      registerForm: {
        uid: '',
        password: ''
      },
      rules: {
        u_id: [
          { required: true, message: "请输入你的名称", trigger: 'blur' },
          { min: 2, max: 9, message: "长度2~9个字符", trigger: 'blur' }
        ],
        u_password: [
          { validator: validatePass, trigger: 'blur' }
        ],
        checkPassword: [
          { validator: validatePass2, trigger: 'blur' }
        ]
      }
    };
  },
  methods: {
    submitForm(ruleForm) {
      this.$refs[ruleForm].validate((valid) => {
        if (valid) {
          this.registerForm.uid = this.ruleForm.u_id;
          this.registerForm.password = this.ruleForm.checkPassword;
          this.axios.post(`/user-service-api-8084/register`, this.registerForm).then((resp) => {
            let data = resp.data;
            if (data.success) {
              this.$message({
                message: '注册成功',
                type: 'success'
              });
              this.ruleForm = {};
              this.$router.push({ path: '/' });
            } else {
              Message.error("账号名称已被注册");
              this.ruleForm = {};
              this.registerForm = {};
            }
          });
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    toLogin() {
      this.$router.push({ path: '/' });
    }
  }
}
</script>

<style>
#poster {
  background-position: center;
  height: 100%;
  width: 100%;
  background-size: cover;
  position: fixed;
  margin: 0px;
  padding: 0px;
}

.register_container {
  border-radius: 15px;
  background-clip: padding-box;
  margin: 90px auto;
  width: 350px;
  padding: 35px 35px 15px 35px;
  background: white;
  border: 1px solid white;
  box-shadow: 0 0 25px gainsboro;
}

.register_title {
  margin: 0px auto 40px auto;
  text-align: center;
  color: black;
}
</style>