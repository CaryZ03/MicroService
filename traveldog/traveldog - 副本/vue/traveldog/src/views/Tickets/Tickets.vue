<!--代办-->
<!--不能订同一个座位-->

<template>
    <div class="Tickets">
        <div class="searchTickets" v-show="!searchFlag">
            <!--1、搜索区域-->
            <div class="container">
                <h1>火车票购买</h1>
                <el-form @submit.prevent="submitForm">
                    <el-form-item label="出发地">
                        <div class="inline-inputs">
                        <el-input v-model="departureProvince" placeholder="省" required></el-input>
                        <el-input v-model="departureCity" placeholder="市" required></el-input>
                        </div>
                    </el-form-item>
                    <el-form-item label="目的地">
                        <div class="inline-inputs">
                        <el-input v-model="destinationProvince" placeholder="省" required></el-input>
                        <el-input v-model="destinationCity" placeholder="市" required></el-input>
                        </div>
                    </el-form-item>
                    <el-form-item label="出发日期">
                        <el-date-picker v-model="date" type="datetime" placeholder="选择日期和时间" :picker-options="pickerOptions" @change="handleDateChange" required></el-date-picker>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="search">查询车票</el-button>
                        <el-button type="primary" @click="resetSearch">清空查询</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
        <div class="tickets" v-show="searchFlag">
            <!--1、搜索区域-->
            <div class="column">
                <el-form @submit.prevent="submitForm">
                    <el-form-item label="出发地">
                        <div class="inline-inputs2">
                        <el-input v-model="departureProvince" placeholder="省" required></el-input>
                        <el-input v-model="departureCity" placeholder="市" required></el-input>
                        </div>
                    </el-form-item>
                    <el-form-item label="目的地">
                        <div class="inline-inputs2">
                        <el-input v-model="destinationProvince" placeholder="省" required></el-input>
                        <el-input v-model="destinationCity" placeholder="市" required></el-input>
                        </div>
                    </el-form-item>
                    <el-form-item label="出发日期">
                        <el-date-picker v-model="date" type="datetime" placeholder="选择日期和时间" :picker-options="pickerOptions" @change="handleDateChange" required></el-date-picker>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="search">查询车票</el-button>
                        <el-button type="primary" @click="resetSearch">清空查询</el-button>
                    </el-form-item>
                </el-form>
            </div>
            <!--2、表格区域展示视图数据-->
            <div class="wrapper">
                <el-table :data="trainsPaginatedData" border style="width: 100%" :default-sort="{prop: 'departure_time', order: 'descending'}">
                    <el-table-column type="expand">
                        <template slot-scope="scope">
                            <el-table :data="scope.row.locations" style="width: 100%">
                                <el-table-column label="经停站" width="150">
                                    <template slot-scope="scope">{{ scope.row.province+scope.row.city }}</template>
                                </el-table-column>
                                <el-table-column label="到达时间" width="150">
                                    <template slot-scope="scope">{{ scope.row.arriveTime }}</template>
                                </el-table-column>
                            </el-table>
                        </template>
                    </el-table-column>
                    <el-table-column  label="出发时间"  width="180" sortable :sort-method="sortDepartureTime">
                        <template slot-scope="scope">{{ getDepartureTime(scope.row.locations) }}</template>
                    </el-table-column>
                    <el-table-column  prop="arrive_time"  label="到达时间"  width="180" sortable :sort-method="sortDestinationTime">
                        <template slot-scope="scope">{{ getDestinationTime(scope.row.locations) }}</template>
                    </el-table-column>
                    <el-table-column  prop="duration_time"  label="旅途时间"  width="180" sortable :sort-method="sortDurationTime">
                        <template slot-scope="scope">{{ getDurationTime(scope.row.locations) + 'h' }}</template>
                    </el-table-column>
                    <el-table-column  prop="id"  label="列车编号"  width="180">
                        <template slot-scope="scope">{{ scope.row.id }}</template>
                    </el-table-column>
                    <el-table-column  prop="type"  label="列车类型">
                        <template slot-scope="scope">{{ scope.row.type }}</template>
                    </el-table-column>
                    <el-table-column  prop="lowestPrice" label="最低价格" sortable :sort-method="sortPrice">
                        <template slot-scope="scope">{{ getLowestPrice(scope.row.seats) }}</template>
                    </el-table-column>
                    <el-table-column label="操作">
                        <template slot-scope="scope">
                            <el-button size="small" @click="buyTickets(scope.row)">购买车票</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
            <!--分页-->
            <Pagination :total='total' :pageSize='pageSize' @changePage="changePage"/>
            <!--选座对话框-->
            <el-dialog style="width: 100%;height: 1000px;" title="选择座位类型" :visible.sync="seatselectionDialog">
                <div style="display: flex; justify-content: space-around;">
                    <el-card v-for="seat in seats" :key="seat.value" class="seat-card">
                        <h3>{{ seat.type }}</h3>
                        <h4>价格：{{ formatPrice(seat.price) }}</h4>
                        <h4>数量：{{ seat.count }}</h4>
                        <p>{{ seat.details }}</p>
                        <el-button type="primary" @click="selectSeat(seat)">选择</el-button>
                    </el-card>
                </div>
                <div slot="footer" class="dialog-footer">
                    <el-button @click="seatselectionCancel()">取 消</el-button>
                </div>
            </el-dialog>
            <!--顾客选择对话框-->
            <el-dialog style="width: 100%;height: 1000px;" title="选择为哪位顾客购买车票" :visible.sync="customersselectionDialog">
                <div class="dialog-body">
                    <el-table :data="customersTableData" border style="width: 100%" @selection-change="handleSelectionChange">
                        <el-table-column type="selection" width="55"></el-table-column>
                        <el-table-column  prop="id"  label="顾客编号"  width="180"></el-table-column>
                        <el-table-column  prop="name"  label="顾客姓名"  width="180"></el-table-column>
                        <el-table-column  prop="phoneNumber"  label="顾客电话号码" width="180"></el-table-column>
                        <el-table-column  prop="idCard"  label="顾客身份证"  width="180"></el-table-column>
                        <el-table-column  prop="age"  label="顾客年龄" width="180"></el-table-column>
                    </el-table>
                </div>
                <div slot="footer" class="dialog-footer">
                    <el-button @click="customersselectionCancel()">取 消</el-button>
                    <el-button type="primary" @click="customersselectionSubmit()">确 定</el-button>
                </div>
            </el-dialog>
        </div>
    </div>
</template>

<script>
import Pagination from "../../components/Pagination.vue"
import { Message } from "element-ui"
export default {
    components:{
        Pagination
    },
    data(){
        return {
            uid: '',
            departureProvince: '',
            departureCity: '',
            destinationProvince: '',
            destinationCity: '',
            temp:{
                departureProvince: '',
                departureCity: '',
                destinationProvince: '',
                destinationCity: '',
            },
            date: '',
            pickerOptions: {
                disabledDate(time) {
                    return time.getTime() < Date.now() - 8.64e7; // Disable past dates
                }
            },
            tableData:[],
            total: 0,
            pageSize: 10,
            customersselectionDialog: false,
            seatselectionDialog: false,
            seats: [],
            customersTableData: [],
            selectedSeat: {},
            selectedTrain: {},
            selectedCustomers: [],
            trainsPaginatedData: [],
            customersAlreadyBuyTickets: [],  //已购买此列火车的顾客，不可重复购买
            searchFlag: false
        }
    },
    methods:{
        formatDate(date) {
            date = new Date(date);
            const year = date.getFullYear();
            const month = (date.getMonth() + 1).toString().padStart(2, '0');
            const day = date.getDate().toString().padStart(2, '0');
            const hours = date.getHours().toString().padStart(2, '0');
            const minutes = date.getMinutes().toString().padStart(2, '0');
            const seconds = date.getSeconds().toString().padStart(2, '0');
            return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
        },
        changePage(num){
            this.http(num);
        },

        http(page){
            this.total = this.tableData.length;
            const start = (page - 1) * this.pageSize;
            const end = start + this.pageSize;
            this.trainsPaginatedData = this.tableData.slice(start,end);
        },
        //购买车票
        buyTickets(train){
            this.seatselectionDialog = true;
            this.selectedtrain = train;
            this.seats = this.selectedtrain.seats;
        },
        async search() {
            this.searchFlag=true;
            //获取符合查询条件的车辆信息
            const submitForm={
                curTime: this.formatDate(this.date),
                startLocation:{
                    id: -1,
                    province: this.departureProvince,
                    city: this.departureCity
                },
                endLocation:{
                    id: -1,
                    province: this.destinationProvince,
                    city: this.destinationCity
                }
            };
            this.temp={
                departureProvince: this.departureProvince,
                departureCity: this.departureCity,
                destinationProvince: this.destinationProvince,
                destinationCity: this.destinationCity
            }
            // console.log("submitForm",submitForm);
            try {
                let resp = await this.axios.post(`/api/train/getByTimeAndLocation`, submitForm);
                let data = resp.data;
                this.tableData = data.content;
                this.http(1);
            } catch (error) {
                console.error("Error during search:", error);
            }
        },
        sortDepartureTime(a, b) {
            const timeA = this.getDepartureTime(a.locations);
            const timeB = this.getDepartureTime(b.locations);
            return new Date(timeA) - new Date(timeB);
        },
        sortDestinationTime(a, b) {
            const timeA = this.getDestinationTime(a.locations);
            const timeB = this.getDestinationTime(b.locations);
            return new Date(timeA) - new Date(timeB);
        },
        sortDurationTime(a, b) {
            const timeA = this.getDurationTime(a.locations);
            const timeB = this.getDurationTime(b.locations);
            console.log("timeA",timeA);
            console.log("timeB",timeB);
            console.log("new Date(timeA) - new Date(timeB)",new Date(timeA) - new Date(timeB));
            return timeA - timeB;
        },
        sortPrice(a, b) {
            const A = this.getLowestPrice(a.seats);
            const B = this.getLowestPrice(b.seats);
            return A - B;
        },
        resetSearch(){
            this.departureProvince = '';
            this.departureCity = '';
            this.destinationProvince = '';
            this.destinationCity = '';
            this.date = '';
            this.tableData=[];
            this.http(1);
        },
        seatselectionCancel(){
            this.seatselectionDialog=false;
        },
        selectSeat(seat){
            this.customersselectionDialog=true;
            this.selectedSeat=seat;
            this.axios.get(`/api/customer/getByUid/${this.uid}`).then((resp) => {
                let data=resp.data;
                this.customersTableData = data.content;
            });
        },
        customersselectionCancel(){
            this.customersselectionDialog=false;
        },
        handleSelectionChange(selectedCustomers) {
            this.selectedCustomers = selectedCustomers;
            console.log("selectedCustomers",selectedCustomers);
        },
        handleDateChange() {
            this.date = this.formatDate(this.date);
            //console.log("date",this.date);
        },
        async getCustomersAlreadyBuyTickets(tid){
            try {
                let resp = await this.axios.post(`/api/customer/getCidByTid/${tid}`);
                let data = resp.data;
                console.log("data", data);
                this.customersAlreadyBuyTickets = [...new Set(data.content)];
                console.log("customersAlreadyBuyTickets", this.customersAlreadyBuyTickets);
            } catch (error) {
                console.error("Error during getCustomersAlreadyBuyTickets:", error);
            }
        },
        async customersselectionSubmit(){
            //为selectedCustomers创建订单逻辑
            console.log("selectedCustomers",this.selectedCustomers);
            if(this.selectedCustomers == null || this.selectedCustomers.length == 0){
                Message.error("请选择顾客");
                return;
            }else{
                await this.getCustomersAlreadyBuyTickets(this.selectedtrain.id);
                const submitForm={
                    uid: this.uid,
                    orderType: "trainOrder",
                    tor:{
                        cids: [],
                        tid: this.selectedtrain.id,
                        seatType: this.selectedSeat.type,
                        beginLocation: {
                            id: -1,
                            province: this.departureProvince,
                            city: this.departureCity
                        },
                        endLocation: {
                            id: -1,
                            province: this.destinationProvince,
                            city: this.destinationCity
                        }
                    },
                    mor:null,
                    hor:null
                };
                console.log("selectedCustomers",this.selectedCustomers);
                const errorCids = [];
                this.selectedCustomers.forEach(customer => {  
                    console.log("customer",customer);
                    if(this.customersAlreadyBuyTickets.includes(customer.id)){
                        errorCids.push(customer.id);
                    }
                    else{
                        submitForm.tor.cids.push(customer.id); // 假设customer对象有一个id属性  
                    }
                });
                console.log("submitForm",submitForm);
                if (errorCids.length > 0) {
                    Message.error("不可为顾客重复订购一趟列车，预定失败");
                } else {
                    try {
                        let resp = await this.axios.post(`/api/user/addOrder`, submitForm);
                        let data = resp.data;
                        if (data.success) {
                            Message.success("预订成功");
                        } else {
                            Message.error("车票已售完，预订失败");
                        }  
                    } catch (error) {
                        console.error("Error during addOrder:", error);
                        Message.error("预订失败");
                    }
                }
            }
            this.customersselectionDialog=false;
            this.seatselectionDialog=false;
            await this.search();
        },
        getDepartureTime(locations){
            // console.log("locationsDepartTime",locations);
            for(let i = 0;i<locations.length;i++){
                const location = locations[i];
                if(location.province === this.temp.departureProvince && location.city === this.temp.departureCity){
                    return location.arriveTime;
                }
            }
        },
        getDestinationTime(locations){
            // console.log("locationsDestTime",locations);
            for(let i = 0;i<locations.length;i++){
                const location = locations[i];
                // console.log("location",location);
                if(location.province === this.temp.destinationProvince && location.city === this.temp.destinationCity){
                    // console.log("arriveTime",location.arriveTime);
                    return location.arriveTime;
                }
            }
        },
        getDurationTime(locations){
            let timeA =  new Date(this.getDestinationTime(locations));
            let timeB =  new Date(this.getDepartureTime(locations))
            let durationTime = timeA - timeB;
            console.log("durationTime",durationTime);
            return durationTime/1000/60/60;
        },
        getLowestPrice(seats){
            let price = 999999;
            for(let i=0;i<seats.length;i++){
                if(price>seats[i].price && seats[i].count > 0){
                    price=seats[i].price;
                }
            }
            return price.toFixed(2);
        },
        formatPrice(price) {
            return price.toFixed(2);
        },
    },
    //生命周期函数
    created() {
        this.uid = sessionStorage.getItem('uid');
    }
};
</script>

<style lang="less" scoped>

.header{
    display: flex;

    button{
        margin-left: 30px;
    }
align-items: center;

}
.wrapper{
    margin:10px 0;
}
.dialog-body {
  max-height: 400px; /* Adjust height as needed */
  overflow-y: auto;
}
.dialog-footer {
  text-align: right;
}

.searchTickets {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh; /* 让容器全屏 */
  background: url('./Tickets.jpg') no-repeat center center;
  background-size: cover; /* 确保图片覆盖整个容器 */
}

.container {
  background: rgba(255, 255, 255, 0.8); /* 透明白色背景 */
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 100%;
}
.inline-inputs {
  display: flex;
  gap: 10px; /* 设定输入框之间的间距 */
}
.column {
  background: rgba(255, 255, 255, 0.8); /* 透明白色背景 */
  position: sticky;
  top: 0;
  background-color: #fff; /* 背景颜色 */
  z-index: 100; /* 设置 z-index 确保置顶显示 */
  max-width: 100%; /* 控制列的最大宽度 */
  margin: 0 auto; /* 居中显示 */
}
.inline-inputs2 {
  display: flex;
  gap: 10px; /* 控制输入框之间的间距 */
  align-items: center; /* 垂直居中输入框 */
}
.el-form-item {
  margin-bottom: 20px; /* 设置表单项之间的垂直间距 */
}
.el-button {
  margin-right: 10px; /* 设置按钮之间的水平间距 */
}
h1 {
  font-size: 24px;
  margin-bottom: 20px;
  text-align: center;
}
.el-input {
  flex: 1;
}
</style>