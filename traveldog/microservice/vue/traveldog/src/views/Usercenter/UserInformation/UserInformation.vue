<template>
    <div>

        <div class="usercenter">
            <h1>用户中心</h1>
        </div>

        <div class="container">

            <div class="avatar-container">
                <div class="avatar-title">
                    <h2>用户头像</h2>
                </div>
                <div class="avatar">
                    <img src="./touxiang.jpg" alt="用户头像">
                </div>
                <div class="logout-btn">
                    <el-button type="danger" @click="logout()">退出登录</el-button>
                </div>
            </div>

            <div class="white-box">
                <div class="white-title">
                    <h2>用户信息</h2>
                </div>
                <div class="profile-info">
                    <span>账号: <span class="info-value">{{ uid }}</span></span><br>
                    <span>密码: <span class="info-value">******</span></span><br>
                    <span>昵称: <span class="info-value">{{ name }}</span></span><br>
                    <span>电话号码: <span class="info-value">{{ phoneNumber }}</span></span><br>
                    <span>余额: <span class="info-value">{{ formatPrice(money) }}</span></span>
                </div>
            </div>

            <div class="white2-box">
                <div class="white2-title">
                    <h2>修改信息</h2>
                </div>

                <div class="input-container">
                    <span class="label">密码：</span>
                    <el-input type="text" v-model="passwordChange" autocomplete="off" placeholder="请输入新密码"></el-input>
                </div>


                <div class="input-container">
                    <span class="label">昵称：</span>
                    <el-input type="text" v-model="nameChange" autocomplete="off" placeholder="修改用户名"></el-input>
                </div>


                <div class="input-container">
                    <span class="label">电话号码：</span>
                    <el-input type="text" v-model="phoneNumberChange" autocomplete="off"
                        placeholder="修改电话号码"></el-input>
                </div>

                <!-- 余额输入框 -->
                <div class="input-container">
                    <span class="label">余额：</span>
                    <el-input type="text" v-model="moneyChange" autocomplete="off" placeholder="修改余额"></el-input>
                </div>
                <el-button type="primary" @click="submitChanges()">提交修改</el-button>
                <el-button type="primary" @click="cancelChanges()">取消修改</el-button>



            </div>



        </div>

    </div>

</template>

<script>
import { Message } from "element-ui";
export default {
    data() {
        return {
            uid: null,
            password: null,
            name: null,
            phoneNumber: null,
            money: null,
            passwordChange: null,
            nameChange: null,
            phoneNumberChange: null,
            moneyChange: null,
            temp: {
                uid: null,
                password: null,
                phoneNumber: null,
                name: null,
                money: null
            }
        }
    },
    created() {
        this.password = sessionStorage.getItem('password');
        this.uid = sessionStorage.getItem('uid');
        this.temp.uid = this.uid;
        if (this.uid) {
            this.getUserInformation(this.uid);
        }

    },
    methods: {

        async getUserInformation(uid) {
            await this.axios.get(`/user-service-api-8084/user/getByUid/${uid}`).then((resp) => {
                let dataContent = resp.data.content;
                this.name = dataContent.name;
                this.phoneNumber = dataContent.phoneNumber;
                this.money = dataContent.money;
            });
        },
        beforeRouteEnter(to, from, next) {
            next(vm => {
                vm.uid = sessionStorage.getItem('uid');
                if (vm.uid) {
                    vm.getUserInformation(vm.uid);
                }
            });
        },
        async submitChanges() {
            this.temp.password = this.passwordChange;
            this.temp.phoneNumber = this.phoneNumberChange;
            this.temp.name = this.nameChange;
            this.temp.money = this.moneyChange;
            if (this.temp.money > 10000) {
                Message.error("安全性原因，钱包数额不可大于10000");
                this.moneyChange = null;
                this.temp.money = null;
                return;
            }
            if (!this.passwordChange) {
                this.temp.password = this.password;
            }
            if (!this.phoneNumberChange) {
                this.temp.phoneNumber = this.phoneNumber;
            }
            if (!this.nameChange) {
                this.temp.name = this.name;
            }
            if (!this.moneyChange) {
                this.temp.money = this.money;
            }
            await this.axios.put(`/user-service-api-8084/user/update/`, this.temp).then((resp) => {
                let data = resp.data;
                if (data.success) {
                    this.password = this.temp.password;
                    sessionStorage.setItem('password', this.temp.password);
                    this.phoneNumber = this.temp.phoneNumber;
                    this.name = this.temp.name;
                    this.money = this.temp.money;
                    Message.success("修改成功");
                } else {
                    Message.error("修改失败");
                }
                this.passwordChange = null;
                this.phoneNumberChange = null;
                this.nameChange = null;
                this.moneyChange = null;
            });
            await this.getUserInformation(this.uid);
        },
        cancelChanges() {
            this.passwordChange = null;
            this.phoneNumberChange = null;
            this.nameChange = null;
            this.moneyChange = null;
        },
        formatPrice(price) {
            return price.toFixed(2);
        },
        logout() {
            const submitForm = {
                uid: this.uid,
                password: this.password
            }
            this.axios.post(`/user-service-api-8084/logout`, submitForm).then((resp) => {
                let data = resp.data;
                if (data.success) {
                    this.$router.push({ path: '/' });
                }
            });
        }
    }

}
</script>

<style lang="less" scoped>
.usercenter {
    font-size: 13px;
}

.container {
    display: flex;
    align-items: flex-start;
    margin-top: 40px;
    margin-left: 30px;
}

.avatar-container {
    padding: 20px;
    background-color: #fff;
    border: 1px solid #ccc;
    width: 200px;
    height: 400px;
    margin-right: 10px;
    text-align: center;
}

.avatar-title {
    font-size: 14px;
    margin-top: 20px;
}

.info-value {
    display: inline-block;
    border: 1px solid #ccc;
    padding: 3px 5px;
    margin-bottom: 0px;
    /* 减小行间距 */
    min-width: 150px;
    /* 设置最小宽度 */
    /* 如果你想要固定宽度，可以将 min-width 替换为 width: 150px; */
    background-color: #f5f5f5;
    /* 还可以添加 box-sizing: border-box; 以确保边框和内边距包含在宽度内 */
    box-sizing: border-box;
}

.avatar img {

    width: 200px;
    height: 200px;
    object-fit: cover;
    border: 2px solid #ccc;
    margin-top: 20px;
}

.change-avatar-btn {
    margin-top: 30px;
}

.logout-btn {
    margin-top: 30px;
}

.white-box {
    border: 1px solid #ccc;
    padding: 10px;
    width: 400px;
    height: 420px;
    margin-left: 30px;
}

.white-title {
    font-size: 14px;
    margin-top: 28px;
}

.white2-title {
    font-size: 14px;
    margin-top: 28px;
}

.profile-info {
    margin-top: 40px;
    list-style-type: none;
    padding: 0;


    display: flex;
    flex-direction: column;
    align-items: flex-start;
}

.profile-info span {


    font-size: 16px;
    margin-bottom: 5px;
}


.profile-info span br {
    display: block;
    margin: 0;
}

.white2-box {
    border: 1px solid #ccc;
    padding: 10px;
    width: 400px;
    height: 420px;
    margin-left: 30px;
}

.el-input {
    margin-bottom: 10px;
    width: 300px;
}

.button-group {
    margin-top: 20px;
    display: flex;
    justify-content: space-between;
}
</style>