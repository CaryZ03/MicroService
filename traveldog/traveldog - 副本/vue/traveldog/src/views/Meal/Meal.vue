<template>
    <div class="meals">
        <div class="searchMeal" v-show="!searchFlag">
            <div class="search-container">
            <h1 style="color:skyblue ">火车餐购买</h1>
            <div class="search-area">
        <!--搜索区域-->
        <div class="header">
            <el-input v-model="searchTid" placeholder="所要查询火车餐的列车编号" clearable @clear="resetSearch()" @blur="handleInputBlur"></el-input>
            <el-button type="primary" plain @click="searchMeals">查询</el-button>
            <el-button type="primary" plain @click="resetSearch">清空查询</el-button>
        </div>
        <!--表格区域展示视图数据-->
        <div class="wrapper">
            <el-table :data="mealsPaginatedData" style="width: 100%" :default-sort="{prop: 'departure_time', order: 'descending'}">
                <el-table-column  prop="tid" label="列车编号"  width="250"></el-table-column>
                <el-table-column  prop="name"  label="火车餐名称"  width="250"></el-table-column>
                <el-table-column  prop="count"  label="火车餐数量"  width="250"></el-table-column>
                <el-table-column  prop="price"  label="火车餐价格"  width="250" :formatter="formatPrice" sortable></el-table-column>
                <el-table-column  prop="type"  label="火车餐类型"></el-table-column>
                <el-table-column  prop="detail"  label="火车餐描述"></el-table-column>
                <el-table-column label="操作">
                    <template slot-scope="scope" v-if="searchTidInTidListFlag">
                        <el-button size="small" @click="buyMeal(scope.row)">购买火车餐</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
    </div>
    </div>
        </div>
        <div class="Meal" v-show="searchFlag">
            <h1>火车餐购买</h1>
        <!--搜索区域-->
        <div class="header">
            <el-input v-model="searchTid" placeholder="所要查询火车餐的列车编号" clearable @clear="resetSearch()" @blur="handleInputBlur"></el-input>
            <el-button type="primary" plain @click="searchMeals">查询</el-button>
            <el-button type="primary" plain @click="resetSearch">清空查询</el-button>
        </div>
        <!--表格区域展示视图数据-->
        <div class="wrapper">
            <el-table :data="mealsPaginatedData" style="width: 100%" :default-sort="{prop: 'departure_time', order: 'descending'}">
                <el-table-column  prop="tid" label="列车编号"  width="250"></el-table-column>
                <el-table-column  prop="name"  label="火车餐名称"  width="250"></el-table-column>
                <el-table-column  prop="count"  label="火车餐数量"  width="250"></el-table-column>
                <el-table-column  prop="price"  label="火车餐价格"  width="250" :formatter="formatPrice" sortable></el-table-column>
                <el-table-column  prop="type"  label="火车餐类型"></el-table-column>
                <el-table-column  prop="detail"  label="火车餐描述"></el-table-column>
                <el-table-column label="操作">
                    <template slot-scope="scope" v-if="searchTidInTidListFlag">
                        <el-button size="small" @click="buyMeal(scope.row)">购买火车餐</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <!--分页-->
        <Pagination :total='total' :pageSize='pageSize' @changePage="changePage"/>
        <!--顾客选择对话框-->
        <!-- <el-dialog style="width: 100%;height: 1000px;" title="选择为哪位顾客预订" :visible.sync="customersselectionDialog">
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
        </el-dialog> -->
    </div>
    <div>
        <el-dialog style="width: 100%;height: 1000px;" title="选择为哪位顾客预订" :visible.sync="customersselectionDialog">
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
import { Message } from "element-ui";
import Pagination from "../../components/Pagination.vue"
export default {
    components:{
        Pagination
    },
    data(){
        return {
            input:'',
            mealsTableData:[],
            total: 0,
            pageSize: 10,
            uid: null,
            tid: '',    //所选择火车餐得tid
            searchTid: '',  //需要搜索的tid
            searchTidInTidListFlag: '', //搜索列车号是否在tidList中
            customersselectionDialog: false,
            customersTableData: {},
            selectedCustomers: [], // 选中的顾客
            mealsPaginatedData: [],
            selectedMeal: '',
            tidList: [], //从订单获取的tid数组
            searchFlag: false
        }
    },
    methods:{
        //分页页码
        changePage(num){
            this.http(num);
        },
        // 根据订单得到所有订单所在tid？
        async getTidList(){
            try {
                const resp = await this.axios.get(`/api/train/getTidByUid/${this.uid}`);
                let data = resp.data;
                console.log("data.content", data.content);
                this.tidList = [...new Set(data.content)];
            } catch (error) {
                console.error("Error fetching tid list:", error);
            }
        },
        //根据tid查询火车餐
        async getMeals(){
            this.searchTidInTidListFlag=true;
            this.searchFlag= false;
            const submitForm = {
                uid: this.uid,
                tid: null,
                mid: -1
            };
            await this.getTidList();
            let allContent = []; // 用于收集所有的content
            let promises = [];
            for(let i=0;i<this.tidList.length;i++){
                submitForm.tid = this.tidList[i];
                let promise = this.axios.post(`/api/meal/getTrainMeals`, submitForm).then((resp) => {
                    let data = resp.data;
                    // 合并content数组
                    allContent = allContent.concat(data.content);
                });
                promises.push(promise);
            }
            await Promise.all(promises);
            this.mealsTableData = allContent;
            this.http(1);
        },
        http(page){
            this.total = this.mealsTableData.length;
            const start = (page - 1) * this.pageSize;
            const end = start + this.pageSize;
            this.mealsPaginatedData = this.mealsTableData.slice(start,end);
        },
        formatPrice(row, column, cellValue) {
            return cellValue.toFixed(2);
        },
        async buyMeal(meal){
            this.customersselectionDialog = true;
            this.selectedMeal = meal;
            await this.axios.post(`/api/customer/getCidByTidStrict/${meal.tid}`).then(async (resp) => {
                let data=resp.data;
                let allContent = []; // 用于收集所有的content
                for(let i=0;i<data.content.length;i++)
                {
                    await this.axios.get(`/api/customer/getByCid/${data.content[i]}`).then((resp) => {
                        let data = resp.data;
                        console.log("data",data.content);
                        allContent = allContent.concat(data.content);
                    });
                }
                console.log("allContent",allContent);
                this.customersTableData = allContent;
            });
        },
        customersselectionCancel(){
            this.customersselectionDialog = false;
        },
        handleSelectionChange(selectedCustomers) {
            this.selectedCustomers = selectedCustomers;
        },
        async customersselectionSubmit(){
            //为selectedCustomers创建订单逻辑
            if(this.selectedCustomers == null || this.selectedCustomers.length == 0){
                Message.error("请选择顾客");
                return;
            }else{
                const submitForm={
                    uid: this.uid,
                    orderType: "mealOrder",
                    tor:null,
                    mor:{
                        cids: [],
                        tid: this.selectedMeal.tid,
                        mid: this.selectedMeal.mid
                    },
                    hor:null
                };
                console.log("selectedCustomers",this.selectedCustomers);
                this.selectedCustomers.forEach(customer => {  
                    console.log("customer",customer);
                    submitForm.mor.cids.push(customer.id); // 假设customer对象有一个id属性  
                });
                console.log("submitForm",submitForm);
                await this.axios.post(`/api/user/addOrder`,submitForm).then((resp) => {
                    if(resp.data.success){
                        Message.success("预订成功");
                    }else{
                        Message.error("此餐品已被卖完，购买失败");
                    }
                 });
            }
            this.customersselectionDialog=false;
            if(this.searchFlag==false){ //不是搜索时直接获取
                await this.getMeals();
            }else{                      //是搜索时按搜索逻辑获取
                await this.searchMeals();
            }
        },
        async searchMeals(){
            this.searchFlag = true;
            const submitForm = {
                uid: this.uid,
                tid: this.searchTid,
                mid: -1
            };
            if(this.tidList.includes(this.searchTid)){
                this.searchTidInTidListFlag = true;
            }else{
                this.searchTidInTidListFlag = false;
            }
            await this.axios.post(`/api/meal/getTrainMeals`,submitForm).then((resp) => {
                //成功获取
                let data = resp.data;
                this.mealsTableData = data.content;
                console.log("mealsTableData",this.mealsTableData);
                this.http(1);
            });
        },
        resetSearch(){
            this.searchTid='';
            this.getMeals();
        },
        handleInputBlur() { //输入框失去焦点事件时
            // 如果输入框内容为空，恢复原始数据
            if (!this.searchTid.trim()) {
                this.resetSearch();
            }
        },
    },
    //生命周期函数
    created() {
        this.uid=sessionStorage.getItem('uid');
        this.getMeals();
    },
};
</script>

<style lang="less" scoped>
.searchMeal {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh; /* 让容器全屏 */
  background: url('./10.jpg') no-repeat center center;
  background-size: cover; /* 确保图片覆盖整个容器 */
}

.hotels{
    margin:20px;
}

.search-container {
    
  padding: 20px;
  border-radius: 8px;
  
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
</style>