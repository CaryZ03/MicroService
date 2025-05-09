<template>
  <div>
    <h2>订单列表</h2>
    <el-row :gutter="20" class="order-list">
      <el-col :span="8" v-for="(order, index) in orders" :key="order.oid" class="order-col">
        <el-card :body-style="{ padding: '0px' }" class="order-card">
          <div class="card-content">
            <h3>订单号: {{ order.oid }}</h3>
            <p>价格: ￥{{ formatPrice(order.price) }}</p>
            <p>添加时间: {{ order.addTime }}</p>
            <p>状态: {{ order.status }}</p>
            <p>订单类型: {{ order.type }}</p>
          </div>
          <div class="card-footer">
            <el-button type="text" class="button" @click="showDetails(order)">查看详情</el-button>
            <el-button type="text" class="button" @click="payOrder(order)"
              v-if="order.status === 'not paid'">付款</el-button>
            <el-button type="text" class="button" @click="cancelOrder(order)"
              v-if="order.status === 'not paid' || order.status === 'paid'">取消订单</el-button>
            <el-button type="text" class="button" @click="deleteOrder(order)"
              v-if="order.status === 'finished' || order.status === 'canceled'">删除订单</el-button>
            <el-button type="text" class="button" @click="rateHotel(order)"
              v-if="(order.status === 'finished' || order.status === 'paid') && order.type === 'hotelOrder' && order.comment === false">评价酒店</el-button>
            <el-button type="text" class="button" @click="updateRateHotel(order)"
              v-if="(order.status === 'finished' || order.status === 'paid') && order.type === 'hotelOrder' && order.comment === true">更新酒店评价</el-button>
            <el-button type="text" class="button" @click="cancelRateHotel(order)"
              v-if="(order.status === 'finished' || order.status === 'paid') && order.type === 'hotelOrder' && order.comment === true">取消酒店评价</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <!--订单信息对话框-->
    <el-dialog :visible.sync="dialogVisible" title="订单详情" width="50%">
      <div v-if="selectedOrder">
        <h3>订单号: {{ selectedOrder.oid }}</h3>
        <p>价格: ￥{{ formatPrice(selectedOrder.price) }}</p>
        <p>添加时间: {{ selectedOrder.addTime }}</p>
        <p>状态: {{ selectedOrder.status }}</p>
        <p>订单类型: {{ selectedOrder.type }}</p>

        <template v-if="selectedOrder.type === 'hotelOrder'">
          <h4>酒店订单</h4>
          <div v-for="(hotelCustomerOrderDemo, index) in selectedOrder.hotelCustomerOrderDemos" :key="index">
            <p>顾客姓名: {{ hotelCustomerOrderDemo.cname }}</p>
            <p>酒店名称: {{ hotelCustomerOrderDemo.hname }}</p>
            <p>房间号: {{ hotelCustomerOrderDemo.roomNumber }}</p>
            <p>入住时间: {{ hotelCustomerOrderDemo.fromTime }}</p>
            <p>退房时间: {{ hotelCustomerOrderDemo.toTime }}</p>
          </div>
        </template>

        <template v-if="selectedOrder.type === 'trainOrder'">
          <h4>火车订单</h4>
          <div v-for="(trainCustomerOrderDemo, index) in selectedOrder.trainCustomerOrderDemos" :key="index">
            <p>乘客姓名: {{ trainCustomerOrderDemo.cname }}</p>
            <p>列车编号：{{ trainCustomerOrderDemo.tid }}</p>
            <p>座位号: {{ trainCustomerOrderDemo.seatNumber }}</p>
            <p>座位类型: {{ trainCustomerOrderDemo.seatType }}</p>
            <p>出发时间: {{ trainCustomerOrderDemo.departureTime }}</p>
            <p>到达时间: {{ trainCustomerOrderDemo.arriveTime }}</p>
          </div>
        </template>

        <template v-if="selectedOrder.type === 'mealOrder'">
          <h4>餐饮订单</h4>
          <div v-for="(mealCustomerOrderDemo, index) in selectedOrder.mealCustomerOrderDemos" :key="index">
            <p>顾客名称：{{ mealCustomerOrderDemo.cname }}</p>
            <p>餐品名称: {{ mealCustomerOrderDemo.mname }}</p>
            <p>所属列车编号：{{ selectedOrder.tid }}</p>
          </div>
        </template>
      </div>
    </el-dialog>
    <!--酒店评价对话框-->
    <el-dialog style="width: 100%;height: 100%;" title="为酒店评价" :visible.sync="rateHotelDialog">
      <div class="dialog-body">
        <el-input v-model="comment" placeholder="给出评价"></el-input>
        <div class="block">
          <span class="demonstration">评价星级</span>
          <el-rate v-model="rate" :colors="colors"></el-rate>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="rateHotelCancel()">取 消</el-button>
        <el-button type="primary" @click="rateSubmit()">确 定</el-button>
      </div>
    </el-dialog>
    <!--确认评价框-->
    <el-dialog style="width: 100%;height: 100%;" title="确认提交吗？" :visible.sync="confirmRateDialog">
      <div class="dialog-body">
        <span>确认提交后将无法修改评价，并且删除订单将删除你的评论</span>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="confirmRateCancel()">取 消</el-button>
        <el-button type="primary" @click="confirmRateSubmit()">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { Message } from 'element-ui';

export default {
  data() {
    return {
      orders: [],
      dialogVisible: false,
      selectedOrder: null,
      rateHotelDialog: false,
      selectedHotelOrder: '',
      comment: '',
      rate: 3,
      colors: ['#99A9BF', '#F7BA2A', '#FF9900'],
      confirmRateDialog: false
    };
  },
  methods: {
    showDetails(order) {
      this.selectedOrder = order;
      this.dialogVisible = true;
    },
    async getOrders() {
      await this.axios.get(`/user-service-api-8084/user/getUserOrder/${this.uid}`).then((resp) => {
        this.orders = resp.data.content;
        console.log("orders", this.orders);
      });
    },
    payOrder(order) {
      const submitForm = {
        uid: this.uid,
        oid: order.oid
      }
      if (order.status === "not paid") {
        this.axios.post(`/order-service-api-8082/user/payOrder`, submitForm).then((resp) => {
          if (resp.data.success) {
            Message.success("付款成功");
          } else {
            Message.error("付款失败");
          }
          this.getOrders();
        });
      } else {
        Message.error("订单已取消或付款");
      }
    },
    cancelOrder(order) {
      const submitForm = {
        uid: this.uid,
        oid: order.oid
      };
      if (order.status == "canceled") {
        Message.error("订单已取消");
      } else {
        this.axios.post(`/order-service-api-8082/user/cancelOrder`, submitForm).then((resp) => {
          if (resp.data.success) {
            Message.success("取消订单成功");
            this.getOrders();
          } else {
            Message.error("取消订单失败");
          }
        });
      }
    },
    async deleteOrder(order) {
      const submitForm = {
        uid: this.uid,
        oid: order.oid
      }
      await this.axios.post(`/order-service-api-8082/user/deleteOrder`, submitForm).then(async (resp) => {
        let data = resp.data;
        if (data.success) {
          Message.success("删除订单成功");
          await this.getOrders();
        } else {
          Message.error("删除订单失败");
        }
      })
    },
    rateHotel(order) {
      this.rateHotelDialog = true;
      this.selectedHotelOrder = order;
    },
    async updateRateHotel(order) {
      this.rateHotelDialog = true;
      this.selectedHotelOrder = order;
      const submitForm = {
        uid: this.uid,
        oid: this.selectedHotelOrder.oid,
        rate: -1,
        comment: null
      }
      await this.axios.post(`/order-service-api-8082/user/getHotelComment`, submitForm).then(async (resp) => {
        let data = resp.data;
        if (data.success) {
          this.rate = data.content.rate;
          this.comment = data.content.comment;
          await this.getOrders();
        } else {
          Message.error("获取前一次评价失败");
        }
      });
    },
    async cancelRateHotel(order) {
      this.selectedHotelOrder = order;
      const submitForm = {
        uid: this.uid,
        oid: this.selectedHotelOrder.oid,
        rate: -1,
        comment: null
      }
      await this.axios.post(`/order-service-api-8082/user/deleteHotelComment`, submitForm).then(async (resp) => {
        let data = resp.data;
        if (data.success) {
          Message.success("删除评价成功");
          await this.getOrders();
        } else {
          Message.error("删除评价失败");
        }
      });
    },
    rateHotelCancel() {
      this.selectedHotelOrder = null;
      this.rateHotelDialog = false;
      this.rate = 3;
      this.comment = null;
    },
    rateSubmit() {
      this.confirmRateDialog = true;
    },
    async confirmRateSubmit() {
      const submitForm = {
        uid: this.uid,
        oid: this.selectedHotelOrder.oid,
        rate: this.rate,
        comment: this.comment
      }
      await this.axios.put(`/order-service-api-8082/user/rateHotelByOid`, submitForm).then((resp) => {
        let data = resp.data;
        if (data.success) {
          Message.success("提交评价成功");
          this.confirmRateDialog = false;
          this.rateHotelDialog = false;
        } else {
          Message.error("提交评价失败");
          this.confirmRateDialog = false;
          this.rateHotelDialog = false;
        }
      });
      this.rate = null;
      this.comment = null;
      await this.getOrders();
    },
    confirmRateCancel() {
      this.confirmRateDialog = false;
    },
    formatPrice(price) {
      return price.toFixed(2);
    },
  },
  //生命周期函数
  created() {
    this.uid = sessionStorage.getItem('uid');
    this.getOrders();
  }
};
</script>

<style>
.order-list {
  display: flex;
  flex-wrap: wrap;
}

.order-col {
  display: flex;
  flex: 0 0 33.3333%;
  /* Adjust this value if you want different sizing */
  max-width: 33.3333%;
  /* Adjust this value if you want different sizing */
  box-sizing: border-box;
}

.order-card {
  width: 100%;
}
</style>