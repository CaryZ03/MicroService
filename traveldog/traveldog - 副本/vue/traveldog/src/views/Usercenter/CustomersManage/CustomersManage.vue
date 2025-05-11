<template>
    <div class="tickets">
        <!--搜索区域-->
        <div class="header">
            <el-input v-model="query" placeholder="顾客姓名" clearable @clear="resetSearch()" @blur="handleInputBlur"></el-input><!--blur设置一个监听-->
            <el-button type="primary" plain @click="searchCustomer">查询</el-button>
        </div>
        <!--添加区域-->
        <div class="addArea">
            <el-button class="add_btn" type="primary" @click="add()">新增</el-button>
        </div>
        <!--表格区域展示视图数据-->
        <div class="wrapper">
            <el-table :data="paginatedData" border style="width: 100%">
                <el-table-column  prop="id"  label="顾客编号"  width="180"></el-table-column>
                <el-table-column  prop="name"  label="顾客姓名"  width="180"></el-table-column>
                <el-table-column  prop="phoneNumber"  label="顾客电话号码" width="180"></el-table-column>
                <el-table-column  prop="idCard"  label="顾客身份证"  width="180"></el-table-column>
                <el-table-column  prop="age"  label="顾客年龄" width="180"></el-table-column>
                <el-table-column label="操作">
                    <template slot-scope="scope">
                        <el-button size="small" type="danger" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <!--分页-->
        <Pagination :total='total' :pageSize='pageSize' @changePage="changePage"/>
        <!--对话框-->
        <el-dialog style="width: 1000px;height: 1000px;" title="用户信息" :visible.sync="zdydialog">
            <el-form :model="form">
                <el-form-item label="顾客姓名" width="70px">
                    <el-input class="dialog_input" v-model="form.cName" ></el-input>
                </el-form-item>
                <el-form-item label="顾客身份证" width="70px">
                    <el-input class="dialog_input" v-model="form.idCard" ></el-input>
                </el-form-item>
                <el-form-item label="顾客年龄" width="70px">
                    <el-input class="dialog_input" v-model="form.cAge" ></el-input>
                </el-form-item>
                <el-form-item label="顾客电话号" width="70px">
                    <el-input class="dialog_input" v-model="form.cTel" ></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="cancel()">取 消</el-button>
                <el-button type="primary" @click="submitAdd()">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import Pagination from "../../../components/Pagination.vue";
import { Message } from "element-ui";
export default {
    components:{
        Pagination
    },
    data(){
        return {
            zdydialog: false,   //是否显示对话框
            form: {},           //对话框数据表格
            submitType: null,   //提交形式：add或者change
            uid: null,
            input:'',
            tableData:[],
            paginatedData: [],
            total:0,
            pageSize:10,
            currentPage: 1,
            query: '',
        }
    },
    methods:{
        changePage(page){
            this.currentPage=page;
            this.getPageinateData();
        },
        getPageinateData(){
            const start = (this.currentPage - 1) * this.pageSize;
            const end = start + this.pageSize;
            this.total = this.tableData.length;
            this.paginatedData = this.tableData.slice(start, end);
        },
        //列表获取
        http(uid){
            this.axios.get(`/api/customer/getByUid/${uid}`).then((resp) => {
                let data=resp.data;
                this.tableData = data.content;
                this.getPageinateData();
                console.log("success",this.tableData);
            });
        },
        add(){
            this.form = {
                uid: null,
                cid: null,
                cName: null,
                idCard: null,
                cTel: null,
                cAge: null
            }
            this.form.uid=this.uid;
            this.submitType = "add";
            this.zdydialog = true;
        },
        submitAdd(){
            if(!this.form.cName || !this.form.idCard || !this.form.cAge || !this.form.cTel){
                Message.error('请输入完整的信息');
                this.form = {};
                this.form.uid = this.uid;
            }else{
                if(this.submitType == "add"){
                    this.axios.post(`/api/user/addCustomer`,this.form).then((resp) => {
                        let data = resp.data;
                        if(data.success){
                            Message.success('添加成功');
                            this.zdydialog=false;
                            this.resetSearch();
                        }else{
                            Message.error("添加失败，已有顾客idCard重复");
                            this.add();
                        }
                    });
                }
            }
        },
        handleDelete(index,row){
            this.$confirm('此操作将永久删除该文件, 是否继续?','提示',{
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.form.uid=this.uid;
                this.form.cid = row.id;
                this.form.cName = row.name;
                this.form.idCard = row.idCard;  //
                this.form.cAge = row.age;
                this.form.cTel = null;
                console.log("deleterow",row);
                this.axios.post('/api/user/deleteCustomer',this.form).then((resp) => {
                    //如果删除成功
                    if(resp.data.success){
                        this.tableData.splice(index, 1);
                        this.getPageinateData();
                        Message.success('删除成功！');
                    }else{
                        Message.error('删除失败');
                    }
                });
            }).catch(() => {
                Message.info('已取消删除');
                console.log("取消删除",this.form);
            });
        },
        cancel() {
            this.zdydialog = false;
        },
        searchCustomer(){
            // 获取输入框中的 cname
            const cname = this.query.trim();
            let findFlag=false;
            let contentContainer=[];
            // 检查 cid 是否为空
            if (!cname) {
                Message.error('请输入顾客姓名');
                // 如果搜索框内容为空，调用 resetSearch 方法
                this.resetSearch();
                return;
            }else{
                for(let i=0;i<this.tableData.length;i++){
                    if(cname === this.tableData[i].name){
                        contentContainer.push(this.tableData[i]);
                        findFlag=true;
                    }
                }
                if(!findFlag){
                    Message.error('没有找到相关顾客');
                        return;
                }else{
                    this.paginatedData=contentContainer;
                    this.currentPage = 1;
                    this.total = this.paginatedData.length;
                }
            }
        },
        resetSearch(){
            this.http(this.uid);
            this.query='';
        },
        handleInputBlur() { //输入框失去焦点事件时
            // 如果输入框内容为空，恢复原始数据
            if (!this.query.trim()) {
                this.resetSearch();
            }
        },
    },
    //生命周期函数
    created() {
        this.uid=sessionStorage.getItem('uid');
        this.form.uid=this.uid;
        this.http(this.uid);
    }
};
</script>

<style lang="less" scoped>
.tickets{
    margin:20px;
}
.header{
    display: flex;
    
    button{
        margin-left: 20px;
    }
}

</style>