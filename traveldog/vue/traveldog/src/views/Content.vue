<template>
    <div>
        <div class="header">
            <i v-if="!isCollapse" @click="changeMenu" class="el-icon-s-fold"></i>
            <i v-else @click="changeMenu" class="el-icon-s-unfold"></i>
            <i @click="changeDrawer" class="el-icon-message-solid"></i>
        </div>
            
        <!--内容区域---路由出口-->
        <div class="content">
            <router-view/>
        </div>
    </div>
</template>

<script>
export default {
    props:['isCollapse','drawer'],
    methods:{
        changeMenu(){
            //点击切换按钮时，修改父组件platform的数据isCollapse
            this.$emit('changeCollapse')
        },
        changeDrawer(){
            console.log("changDrawer active");
            this.$emit('changeDrawer');
        }
    }
}
</script>

<style lang="less" scoped>
.header{
    height: 50px;
    line-height: 50px;
    color: #fff;
    background: skyblue;
    text-align: left;
    .el-icon-s-fold{   
        font-size: 24px;
    }
    .el-icon-s-unfold{
        font-size: 24px;
    }
    .el-icon-message-solid{
        font-size: 30px;
        margin-top: 10px;
        float: right;
    }
}

</style>