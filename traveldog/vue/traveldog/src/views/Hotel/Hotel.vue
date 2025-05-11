<template>
    <div class="hotels">
        <div class="searchHotel" v-show="!searchFlag">
          <div class="search-container">
            <h1>酒店预订</h1>
            <div class="search-area">
                <!-- 选择日期 -->
                <div class="block">
                    <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="预订入住日期" end-placeholder="预订退房日期" @blur="handleDateBlur"></el-date-picker>
                </div>
            </div>
            <!-- 搜索区域 -->
            <div class="header">
                <el-input v-model="province" placeholder="酒店所在省"></el-input>
                <el-input v-model="city" placeholder="酒店所在市"></el-input>
                <el-button type="primary" plain @click="searchHotels">查询</el-button>
                <el-button type="primary" plain @click="resetSearch">清空查询</el-button>
            </div>
           </div>
        </div>
          <div class="Hotel" v-show="searchFlag">
            <div class="container">
            <h1>酒店预订</h1>
            <div class="search-area">
                <!-- 选择日期 -->
                <div class="block">
                    <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="预订入住日期" end-placeholder="预订退房日期" @blur="handleDateBlur"></el-date-picker>
                </div>
            </div>
            <!-- 搜索区域 -->
            <div class="header">
                <el-input v-model="province" placeholder="酒店所在省"></el-input>
                <el-input v-model="city" placeholder="酒店所在市"></el-input>
                <el-button type="primary" plain @click="searchHotels">查询</el-button>
                <el-button type="primary" plain @click="resetSearch">清空查询</el-button>
            </div>
           </div>
        <!--表格区域展示视图数据-->
        <div class="wrapper">
            <el-table :data="hotelsPaginatedData" style="width: 100%" :default-sort="{prop: 'departure_time', order: 'descending'}">
                <el-table-column type="expand">
                    <template slot-scope="scope">
                        <el-form label-position="left" inline class="demo-table-expand">
                            <el-form-item label="酒店简介:">
                                <span>{{ scope.row.description }}</span>
                            </el-form-item>
                        </el-form>
                    </template>
                </el-table-column>
                <el-table-column  prop="name"  label="酒店名称"  width="250"></el-table-column>
                <el-table-column label="酒店评分"  width="250" sortable :sort-method="sortRate">
                    <template slot-scope="scope">{{ scope.row.rate }}</template>
                </el-table-column>
                <el-table-column  prop="phoneNumber"  label="酒店电话"  width="250"></el-table-column>
                <el-table-column  prop="detailLocation"  label="酒店地点"></el-table-column>
                <el-table-column  prop="lowestPrice"  label="最低价格" sortable :sort-method="sortPrice">
                    <template slot-scope="scope">{{ getLowestPrice(scope.row.rooms) }}</template>
                </el-table-column>
                <el-table-column label="操作">
                    <template slot-scope="scope">
                        <el-button size="small" @click="book(scope.row)">预订酒店</el-button>
                    </template>
                </el-table-column>
                <el-table-column label="评价">
                    <template slot-scope="scope">
                        <el-button size="small" @click="viewComments(scope.row)">查看评价</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <!--分页-->
        <Pagination :total='total' :pageSize='pageSize' @changePage="changePage"/>
        <!--选房对话框-->
        <el-dialog style="width: 100%;height: 1000px;" title="选择房间类型" :visible.sync="roomselectionDialog">
            <div style="display: flex; justify-content: space-around;">
                <el-card v-for="room in rooms" :key="room.value" class="room-card">
                    <h3>{{ room.type }}</h3>
                    <h4>价格：{{ room.price }}</h4>
                    <h4>数量：{{ room.count }}</h4>
                    <p>{{ room.details }}</p>
                    <el-button type="primary" @click="selectRoom(room)">选择</el-button>
                </el-card>
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button @click="roomselectionCancel()">取 消</el-button>
            </div>
        </el-dialog>
        <!--顾客选择对话框-->
        <el-dialog style="width: 100%;height: 100%;" title="选择为哪位顾客预订" :visible.sync="customersselectionDialog">
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
        <!--产看酒店评价对话框-->
        <el-dialog style="width: 100%;height: 100%;" title="选择为哪位顾客预订" :visible.sync="viewCommentsDialog">
            <div class="dialog-body">
                <el-table :data="selectedHotel.comments" border style="width: 100%">
                    <el-table-column label="编号" type="index" width="55"></el-table-column>
                    <el-table-column  label="评价"  style="width: 100%;">
                        <template slot-scope="scope">{{ scope.row }}</template>
                    </el-table-column>
                </el-table>
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="closeViewCommentsDialog()">关闭</el-button>
            </div>
        </el-dialog>
    </div>
</div>
</template>

<script>
import { Message } from "element-ui";
import Pagination from "../../components/Pagination.vue"
export default {
    components:{
        Pagination
    },
    data(){
        return {
            input:'',
            hotelsTableData:[],
            total: 0,
            pageSize: 10,
            roomselectionDialog: false,
            uid: null,
            selectedRoom: '', // 选择的房间类型
            rooms: [],
            customersselectionDialog: false,
            customersTableData: {},
            selectedCustomers: [], // 选中的顾客
            province: '',
            city: '',
            dateRange: '', //日期范围？
            startTime: '',
            endtime: '',
            searchForm: {},
            hotelsPaginatedData: [],
            selectedHotel: '',
            customersAlreadyBookHotels: [],
            searchFlag: false,   //是否是查询 
            viewCommentsDialog: false  
        }
    },
    methods:{
        //分页页码
        changePage(num){
            this.http(num);
        },
        http(page){
            this.total = this.hotelsTableData.length;
            const start = (page - 1) * this.pageSize;
            const end = start + this.pageSize;
            console.log("2",this.hotelsTableData);
            this.hotelsPaginatedData = this.hotelsTableData.slice(start,end);
        },
        book(hotel){
            if (this.dateRange) {
                this.roomselectionDialog = true;
                this.rooms = hotel.rooms;
                this.selectedHotel = hotel;
                console.log("hotel",this.selectedHotel);
            } else {
                Message.error('请选择日期范围');
            }
        },
        viewComments(hotel){
            this.selectedHotel=hotel;
            this.viewCommentsDialog=true;
        },
        closeViewCommentsDialog(){
            this.viewCommentsDialog=false;
        },
        formatDate(date) {
            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            return `${year}-${month}-${day} 23:59:59`;
        },
        roomselectionCancel(){
            this.roomselectionDialog = false;
        },
        selectRoom(room) {
            this.selectedRoom = room;
            this.customersselectionDialog=true;
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
            console.log("handleSelectionChange",selectedCustomers);
        },
        async getCustomersAlreadyBookHotels(hid){
            try {
                let resp = await this.axios.get(`/api/customer/getCidByHid/${hid}`);
                let data = resp.data;
                console.log("data", data);
                this.customersAlreadyBookHotels = [...new Set(data.content)];
                console.log("customersAlreadyBookHotels", this.customersAlreadyBookHotels);
            } catch (error) {
                console.error("Error during getCustomersAlreadyBuyTickets:", error);
            }
        },
        async customersselectionSubmit(){
            //为selectedCustomers创建订单逻辑
            if(this.selectedCustomers == null || this.selectedCustomers.length == 0){
                Message.error("请选择顾客");
                return;
            }else{
                await this.getCustomersAlreadyBookHotels(this.selectedHotel.id);
                const submitForm={
                    uid: this.uid,
                    orderType: "hotelOrder",
                    tor:null,
                    mor:null,
                    hor:{
                        cids: [],
                        hid: this.selectedHotel.id,
                        rid: this.selectedRoom.rid,
                        fromTime: this.startTime,
                        toTime: this.endTime,
                        price:0.0
                    }
                };
                console.log("selectedCustomers",this.selectedCustomers);
                const errorCids = [];
                this.selectedCustomers.forEach(customer => {  
                    console.log("customer",customer);
                    if(this.customersAlreadyBookHotels.includes(customer.id)){
                        errorCids.push(customer.id);
                    }else{
                        submitForm.hor.cids.push(customer.id); // 假设customer对象有一个id属性  
                    }
                });
                console.log("submitForm",submitForm);
                if(errorCids.length>0){
                    Message.error("不可为顾客重复订购酒店");
                }else{
                    try {
                        let resp = await this.axios.post(`/api/user/addOrder`, submitForm);
                        let data = resp.data;
                        if (data.success) {
                            Message.success("预订成功");
                        } else {
                            Message.error("预订失败");
                        }  
                    } catch (error) {
                        console.error("Error during addOrder:", error);
                        Message.error("预订失败");
                    }
                }
            }
            this.customersselectionDialog=false;
            this.roomselectionDialog=false;
            if(this.searchFlag===false){
                await this.getAllHotels();
                console.log("getallhotelsUpdate");
            }else{
                await this.searchHotels();
                console.log("searchHotelsUpdate");
            }
        },
        async searchHotels(){
            this.searchFlag=true;
            this.searchForm = {
                startTime: '',
                endTime: '',
                location: {
                    id: '',
                    province: '',
                    city: ''
                }
            }
            this.searchForm.startTime=this.startTime;
            this.searchForm.endTime=this.endTime;
            this.searchForm.location={id:-1,province:this.province,city:this.city};
            console.log("searchForm",this.searchForm);
            if(!this.searchForm.startTime || !this.searchForm.endTime){
                Message.error("请选择日期范围");
                return;
            }
            if(!this.searchForm.location.province){
                Message.error("请选择省份");
                return;
            }
            console.log("searchForm",this.searchForm);
            //axios请求是异步，http()立即执行会导致执行顺序错误
            try {
                const resp = await this.axios.post(`/api/hotel/getByLocation`, this.searchForm);
                let data = resp.data;
                this.hotelsTableData = data.content;
                console.log("1", this.hotelsTableData);
                this.http(1); // 在获取数据后立即处理分页
            } catch (error) {
                console.error("Error fetching hotels:", error);
            }
        },
        handleDateBlur() {
        if (this.dateRange && this.dateRange.length === 2) {
            this.startTime = this.formatDate(new Date(this.dateRange[0]));
            this.endTime = this.formatDate(new Date(this.dateRange[1]));
            console.log("endTime",this.endTime);
            } else {
            this.startTime = '';
            this.endTime = '';
            }
        },
        resetSearch(){
            // this.getAllHotels();
            this.http(1);
            this.province='';
            this.city='';
            this.dateRange='';
            this.startTime='';
            this.endtime='';
        },
        getLowestPrice(rooms){
            let price = 999999;
            for(let i=0;i<rooms.length;i++){
                if(price>rooms[i].price){
                    price=rooms[i].price;
                }
            }
            return price.toFixed(2);
        },
        sortPrice(a, b) {
            const A = this.getLowestPrice(a.rooms);
            const B = this.getLowestPrice(b.rooms);
            return A - B;
        },
        sortRate(a,b){
            return a.rate-b.rate;
        }

    },
    //生命周期函数
    created() {
        this.uid=sessionStorage.getItem('uid');
    },
};
</script>

<style lang="less" scoped>


.searchHotel {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh; /* 让容器全屏 */
  background: url('./Hotel.jpg') no-repeat center center;
  background-size: cover; /* 确保图片覆盖整个容器 */
}

.search-container {
background: rgba(255, 255, 255, 0.8); /* 透明白色背景 */
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 100%;
}

.search-area {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: center;
  justify-content: center;
}

.header {
  display: flex;
  gap: 10px;
  align-items: center;
  justify-content: center;
}

.header el-input {
  width: 180px; /* 控制输入框宽度 */
}

.header el-button {
  min-width: 80px; /* 控制按钮最小宽度 */
}

.block {
  margin-bottom: 20px; /* 控制块之间的垂直间距 */
}

.el-date-picker {
  width: 300px; /* 控制日期选择器的宽度 */
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
.demo-table-expand {
    font-size: 0;
  }
  .demo-table-expand label {
    width: 90px;
    color: #99a9bf;
  }
  .demo-table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
    width: 50%;
  }
</style>