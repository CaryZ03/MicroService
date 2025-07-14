<template>
    <div>
    <el-menu
      default-active="1"
      class="el-menu-vertical-demo"
      background-color="#545c64"
      text-color="#fff"
      active-text-color="#ffd04b"
      router
      :collapse="isCollapse"
      >
      <el-menu-item>
        <span slot="title">汪汪旅途</span>
      </el-menu-item>
      <el-menu-item index="/home">
        <i class="el-icon-discount"></i>
        <span slot="title">首页</span>
      </el-menu-item>
      <el-menu-item index="/tickets">
        <i class="el-icon-document"></i>
        <span slot="title">车票购买</span>
      </el-menu-item>
      <el-menu-item index="/meal">
        <i class="el-icon-burger"></i>
        <span slot="title">火车餐预订</span>
      </el-menu-item>
      <el-menu-item index="/hotel">
        <i class="el-icon-office-building"></i>
        <span slot="title">酒店预订</span>
      </el-menu-item>
      <!--下拉-->
      <el-submenu index="/usercenter">
        <template slot="title">
          <i class="el-icon-user"></i>
          <span>个人中心</span>
        </template>
        <el-menu-item index="/usercenter/userinformation"><i class="el-icon-search"></i>个人信息</el-menu-item>
        <el-menu-item index="/usercenter/customersmanage">  <i class="el-icon-goods"></i>顾客列表</el-menu-item>
        <el-menu-item index="/usercenter/orderlist">  <i class="el-icon-shopping-cart-2"></i>订单列表</el-menu-item>
      </el-submenu>

    </el-menu>
    </div>
</template>

<script>
export default {
    props:['isCollapse'],
    data(){
        return{
            //isCollapse:false
        
        }
    },
    methods: {  
  
  },  
};
</script>

<style lang="less" scoped>
.el-menu{
  border-right: 0;
  .is-active{
    background: #054476 !important;
    color: #fff !important;
  }
}
.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}


.el-icon-bell {  
  position: relative;  
  /* 其他样式 */  
}
</style>