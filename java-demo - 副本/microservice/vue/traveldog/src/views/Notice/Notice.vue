<template>
  <div>
    <button @click="getMessageFromDB()">更新消息</button>
    <div class="message-wrapper">
      <div class="message-column">
        <h2>未读消息</h2>
        <el-table :data="unreadMessages" style="width: 100%">
          <el-table-column prop="noticeContent" label=""></el-table-column>
          <el-table-column label="操作" width="100">
            <template slot-scope="scope">
              <el-button @click="markAsReadSingle(scope.row.noticeId)">已读</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button @click="markAllAsRead">一键已读</el-button>
      </div>
      <div class="message-column">
        <h2>已读消息</h2>
        <el-table :data="readMessages" style="width: 100%">
          <el-table-column prop="noticeContent" label=""></el-table-column>
          <el-table-column label="操作" width="100">
            <template slot-scope="scope">
              <!-- 添加删除按钮 -->
              <el-button type="danger" @click="deleteMessage(scope.row.noticeId)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="danger" @click="deleteAllReadMessages">一键删除</el-button>
      </div>
    </div>

  </div>
</template>
<script>
import { Message } from 'element-ui';
export default {
  props: {
    uid: {
      type: String,
      default: '' // 如果不传递 uid，则默认为空字符串
    }
  },
  data() {
    return {
      url: "ws://backend:8080/websocket",
      websocket: null, // WebSocket对象
      reconnectInterval: 3000, // 重连间隔时间（毫秒）
      heartbeatInterval: null, // 心跳定时器
      message:'',
      readMessages: [], // 已读消息数组
      unreadMessages: [], // 未读消息数组
    };
  },
  created() {
    console.log("created");
    //this.setupWebSocket(); // 创建WebSocket连接
    this.uid = sessionStorage.getItem('uid');
    this.getMessageFromDB();
  },
  methods: {
    deleteAllReadMessages() {
      this.readMessages.forEach(message => {
        const noticeId = `${message.noticeId}`;
        // this.sendMessage(noticeId);
        this.deleteMessage(noticeId);
      });
      this.readMessages = []; // 清空已读消息数组
    },
    deleteMessage(noticeId) {
      this.axios.post(`/user-service-api-8084/user/deleteNotice/${noticeId}`);
      //this.sendMessage(`c_${noticeId}`);
      this.readMessages = this.readMessages.filter(msg => msg.noticeId !== noticeId);
    },
    markAsReadSingle(noticeId) {
      this.axios.post(`/user-service-api-8084/user/updateNotice/${noticeId}`);
      //this.sendMessage(`b_${noticeId}`);
      const messageToMarkAsRead = this.unreadMessages.find(msg => msg.noticeId === noticeId);
      if (messageToMarkAsRead) {
        this.unreadMessages = this.unreadMessages.filter(msg => msg.noticeId !== noticeId);
        this.readMessages.push(messageToMarkAsRead);
      }
    },
    markAllAsRead() {
      this.unreadMessages.forEach(message => {
        // 为每条消息发送 WebSocket 消息
        const noticeId = `${message.noticeId}`;
        // this.sendMessage(noticeId);
        this.markAsReadSingle(noticeId);
      });
      this.readMessages = this.readMessages.concat(this.unreadMessages);
      this.unreadMessages = []; // 清空未读列表
    },
    setupWebSocket() {
      this.websocket = new WebSocket(this.url); // 创建WebSocket连接
      this.websocket.onopen = this.onWebSocketOpen; // WebSocket连接打开时的处理函数
      this.websocket.onmessage = this.onWebSocketMessage; // 收到WebSocket消息时的处理函数
      this.websocket.onclose = this.onWebSocketClose; // WebSocket连接关闭时的处理函数
    },
    closeWebSocket() {
      if (this.websocket) {
        this.websocket.close(); // 关闭WebSocket连接
      }
    },
    /**
     *  WebSocket连接打开后，启动心跳检测
     */
    onWebSocketOpen() {
      console.log("WebSocket connection is open");
      this.startHeartbeat();
      // 发送初始化消息
      this.sendMessage(`a_${this.uid}`);
    },
    // 处理从服务器接收的消息
    onWebSocketMessage(event) {
      if (event.data) {
        this.message=event.data;
        const messageData = JSON.parse(event.data);
        // 根据status字段的值将消息添加到对应的数组中
        if (messageData.noticeStatus === '已读') {
          this.readMessages.push(messageData);
        } else if (messageData.noticeStatus === '未读') {
          this.unreadMessages.push(messageData);
        }
        // 如果你想在控制台中看到每条接收到的消息，可以保留下面的代码
        console.log('Received message:', messageData);
      }
    },
    onWebSocketClose() {
      console.log("WebSocket connection is closed");
      this.stopHeartbeat(); // WebSocket连接关闭时，停止心跳检测
      setTimeout(this.setupWebSocket, this.reconnectInterval); // 在一定时间后重连WebSocket
    },
    sendMessage(message) {
      if (this.websocket && this.websocket.readyState === WebSocket.OPEN) {
        this.websocket.send(message); // 发送消息到WebSocket服务器
      }
    },
    startHeartbeat() {
      this.heartbeatInterval = setInterval(() => {

      }, 10000); // 每10秒发送一次心跳
    },
    stopHeartbeat() {
      if (this.heartbeatInterval) {
        clearInterval(this.heartbeatInterval); // 停止心跳检测定时器
      }
    },
    async getMessageFromDB(){
      console.log("getMessageFromDB");
      await this.axios.get(`/user-service-api-8084/user/getNoticeByUid/${this.uid}`).then((resp) => {
        let data = resp.data;
        console.log("SuccessgetMessageFromDB",data);
        if(data.success){
          this.readMessages = [];
          this.unreadMessages = []; // ��空未读列表
          for(let i=0;i<data.content.length;i++)
          {
            if (data.content[i].noticeStatus === '已读') {
              this.readMessages.push(data.content[i]);
            } else if (data.content[i].noticeStatus === '未读') {
              this.unreadMessages.push(data.content[i]);
            }
          }
        }else{
          Message.error("获取信息失败");
        }
      });
    }
  },
  beforeDestroy() {
    this.closeWebSocket(); // 在组件销毁前关闭WebSocket连接
  },

};
</script>
<style lang="less" scoped>
.message-wrapper {
  display: flex;
  justify-content: space-between; /* 根据需要调整 */
}

.message-column {
  flex: 1; /* 使两个列占用相等的空间 */

  margin: 10px; /* 可选，用于添加一些间距 */
}
.message-column h2 {
  margin-bottom: 5px; /* 调整标题与表格之间的距离 */
}

</style>
