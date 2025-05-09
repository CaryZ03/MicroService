<template>
    <div class="platform">
        <!--左侧导航区域-->
        <MyMenu class="menu" :isCollapse="isCollapse" />
        <!--右侧内容区域-->
        <Content class="content" :class="{ isActive: isCollapse }" @changeCollapse="changeCollapse"
            @changeDrawer="changeDrawer" :isCollapse="isCollapse" />
        <!-- 弹出框 -->
        <el-drawer title="我是标题" :visible.sync="drawer" :with-header="false">
            <Notice class="notice" />
        </el-drawer>
        <Notice class="notice" v-show="false" />
    </div>
</template>

<script>
import MyMenu from './Mymenu.vue'
import Content from './Content.vue'
import Notice from './Notice/Notice.vue'
export default {
    components: {
        MyMenu,
        Content,
        Notice
    },
    data() {
        return {
            isCollapse: false,
            drawer: false,
            gap_time: 0,
            beforeUnload_time: 0,
            uid: '',
            password: ''
        }
    },
    methods: {
        changeCollapse() {
            this.isCollapse = !this.isCollapse;
        },
        changeDrawer() {
            this.drawer = !this.drawer;
        },
        async beforeunloadHandler(e) {
            await this.clearLogin() // 退出登录接口
            // this._beforeUnload_time = new Date().getTime()
            // console.log('this._beforeUnload_time：', this._beforeUnload_time)
            window.close()
        },
        // 关闭窗口之后执行--暂时用不到
        unloadHandler() {
        },
        // 退出登录接口
        async clearLogin() {
            const submitForm = {
                uid: this.uid,
                password: this.password
            }
            await this.axios.post(`/user-service-api-8084/logout`, submitForm).then((resp) => {
            });
        }
    },
    mounted() {
        // 关闭浏览器执行退出接口--
        // onUnload方法是在关闭窗口之后执行
        // onbeforeUnload方法是在关闭窗口之前执行
        window.addEventListener('beforeunload', e => this.beforeunloadHandler(e))
        window.addEventListener('unload', e => this.unloadHandler(e))
    },
    destroyed() {
        // 关闭浏览器执行退出接口
        window.removeEventListener('beforeunload', e => this.beforeunloadHandler(e))
        window.removeEventListener('unload', e => this.unloadHandler(e))
    },
    created() {
        this.uid = sessionStorage.getItem('uid');
        this.password = sessionStorage.getItem('password');
    }
}
</script>

<style lang="less" scoped>
.platform {
    .menu {
        //width: 200px;
        //min-height: 500px;
        background: #666;
        position: fixed;
        top: 0;
        bottom: 0;
    }

    .content {
        margin-left: 200px;
        transition-duration: .5s;
    }

    .isActive {
        margin-left: 64px;
        transition-duration: .5s;
    }

    .notice {}
}
</style>