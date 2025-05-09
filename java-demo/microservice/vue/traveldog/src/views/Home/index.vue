<template>  
<div>
<div class="swiper">
        <div class="swiper-content">
          <div class="swiper-item" v-for="(item, index) in imageurl" :key="index">
            <img :src="item" width="100%" />
          </div>
        </div>
        <div class="page">
          <span></span>
          <span></span>
        </div>
      </div>

  <div class="main-box">  
    <div class="header">  
      <span class="blue-text">人气景点</span>  
    </div>  
    <div class="spots-container">  
      <!-- 第一个景点 -->  
      <div class="spot-box">  
        <img src="./3.jpg" class="spot-image" />  
        <div class="spot-details">  
          <span class="spot-name">漓江</span>  
          <!-- <spen class="spot-price">￥100</spen>   -->
        </div>  
      </div>  
      <!-- 第二个景点 -->  
      <div class="spot-box">  
        <img src="./4.png" class="spot-image" />  
        <div class="spot-details">  
          <span class="spot-name">华山</span>  
          <!-- <spen class="spot-price">￥200</spen>   -->
        </div>  
      </div>  
      <!-- 第三个景点 -->  
      <div class="spot-box">  
        <img src="./5.jpg" class="spot-image" />  
        <div class="spot-details">  
          <span class="spot-name">颐和园</span>  
          <!-- <spen class="spot-price">￥100</spen>   -->
        </div>  
      </div>  
      <div class="spot-box">  
        <img src="./6.jpg" class="spot-image" />  
        <div class="spot-details">  
          <span class="spot-name">桂林</span>  
          <!-- <spen class="spot-price">￥200</spen>   -->
        </div>  
      </div>  
    </div>  
  </div> 
  <div class="main-btx">  
    <div class="jeader">  
      <span class="jlue-text">地方美食</span>  
    </div>  
    <div class="sptts-container">  
      <!-- 第一个 -->  
      <div class="sptt-box">  
        <img src="./7.jpg" class="sptt-image" />  
        <div class="sptt-details">  
          <span class="sptt-name">东坡肉</span>  
          <!-- <spen class="sptt-price">￥30</spen>   -->
        </div>  
      </div>  
      <!-- 第二个 -->  
      <div class="sptt-box">  
        <img src="./10.jpg" class="sptt-image" />  
        <div class="spot-details">  
          <span class="sptt-name">美味中餐</span>  
          <!-- <spen class="sptt-price">￥200</spen>   -->
        </div>  
      </div>  
      <!-- 第三个 -->  
      <div class="sptt-box">  
        <img src="./8.jpg" class="sptt-image" />  
        <div class="sptt-details">  
          <span class="sptt-name">红烧系列</span>  
          <!-- <spen class="sptt-price">￥100</spen>   -->
        </div>  
      </div>  
      <div class="sptt-box">  
        <img src="./9.jpg" class="sptt-image" />  
        <div class="sptt-details">  
          <span class="sptt-name">特色美食</span>  
          <!-- <spen class="sptt-price">￥60</spen>   -->
        </div>  
      </div>  
    </div>  
  </div> 
  <footer class="footer">  
      <p class="footer-text">&copy; 2024 汪汪旅途 - 版权所有</p>  
    </footer>
 
</div>
</template>  

<script>  
import { Swiper } from "../../unit/swiper";
export default {  
data() {  
  return {  
    imageurl: [
            require("./1.jpg"),
            require("./2.jpg"),
            require("./1.jpg"),
          ],

  };  
}  ,
methods: {
        swiper() {
          new Swiper({
            content: ".swiper-content",
            swiperItem: ".swiper-item",
            page: ".page",
          });
        },
      },
    
      mounted() {
        this.swiper();
      },


};  
</script>  

<style scoped>  
/*
.main-box {  
border: 1px solid #ccc;  
padding: 10px;  
width: 1200px; 
margin-left: 120px; 
margin-top: 100px; 
height: 300px
}  
.main-btx {  
border: 1px solid #ccc;  
padding: 10px;  
width: 1200px;  
margin-left: 120px; 
margin-top: 80px; 
height: 300px
}  
.header {  
font-size: 18px;  
margin-bottom: 10px;  
}  
.jeader {  
font-size: 18px;  
margin-bottom: 10px;  
}  
.blue-text {  
color: rgb(0, 119, 255);  
font-family: '微软雅黑', sans-serif; 
font-weight: bold; 
}  
.jlue-text {  
color: rgb(0, 119, 255);  
font-family: '微软雅黑', sans-serif; 
font-weight: bold; 
}  
.spots-container {  
display: flex;  
flex-wrap: nowwrap;  
justify-content: space-between;  
}  
.sptts-container {  
display: flex;  
flex-wrap: nowwrap;  
justify-content: space-between;  
}  

.spot-box {  
width: calc(50% - 10px); 
border: 1px solid #fff;  
padding: 10px;  
box-sizing: border-box;  
margin-bottom: 10px;  
}  
.sptt-box {  
width: calc(50% - 10px); 
border: 1px solid #fff;  
padding: 10px;  
box-sizing: border-box;  
margin-bottom: 10px;  
}  
.spot-image {  
width: 100%;  
height: 80%;  
margin-bottom: 5px;  
}  
.sptt-image {  
width: 100%;  
height: 60%;  
margin-bottom: 5px;  
}  
.spot-details {  
display: flex;  
justify-content: space-between;  
flex-direction: column; 
}  
.sptt-details {  
display: flex;  
justify-content: space-between;  
flex-direction: column; 
}  
.spot-name{
font-weight: bold; 

}
.sptt-name{
font-weight: bold; 

}
.spot-price {  
font-size: 14px;  
}  
.sptt-price {  
font-size: 14px;  
}  

.spot-price { 
color: red; 
} 
.sptt-price { 
color: red; 
} 
*/
.main-box, .main-btx {  
  border: 1px solid #ccc;  
  padding: 10px;  
  /* 宽度使用百分比 */  
  width: 80%; /* 根据需要调整 */  
  margin-left: auto; /* 居中 */  
  margin-right: auto; /* 居中 */  
  margin-top: 2%; /* 使用百分比 */  
  height: auto; /* 根据内容自适应 */  
}  
  
/* 移除冗余的样式块，并合并相似的类 */  
.header, .jeader {  
  font-size: 1.2rem; /* 使用相对单位rem，便于根据根元素字体大小缩放 */  
  margin-bottom: 1rem;  
}  
  
.blue-text, .jlue-text {  
  color: rgb(0, 119, 255);  
  font-family: '微软雅黑', sans-serif;  
  font-weight: bold;  
}  
  
/* 修正flex容器的wrap属性 */  
.spots-container, .sptts-container {  
  display: flex;  
  flex-wrap: wrap; /* 允许换行 */  
  justify-content: space-between;  
  margin-left: -10px; /* 修正外边距重叠 */  
  margin-right: -10px; /* 修正外边距重叠 */  
}  
  
.spot-box, .sptt-box {  
  width: calc(50% - 20px); /* 考虑外边距 */  
  border: 1px solid #fff;  
  padding: 10px;  
  box-sizing: border-box;  
  margin-bottom: 20px; /* 加大间距 */  
  margin-left: 10px; /* 加上外边距 */  
  margin-right: 10px; /* 加上外边距 */  
}  
  
/* 响应式图片 */  
.spot-image, .sptt-image {  
  width: 100%;  
  height: auto; /* 保持图片宽高比 */  
  margin-bottom: 10px;  
}  
  
/* 其他细节样式保持不变 */  
.spot-details, .sptt-details {  
  display: flex;  
  justify-content: space-between;  
  flex-direction: column;  
}  
  
.spot-name, .sptt-name {  
  font-weight: bold;  
}  
  
.spot-price, .sptt-price {  
  font-size: 1rem; /* 使用相对单位 */  
  color: red;  
}  

.swiper {
      height: 350px;
      width: 1400px;
      position: relative;
      overflow: hidden;
      margin: 10px ;
      border: 10px solid royalblue;
      border-radius: 20px;
      box-shadow: 15px 15px 20px 4px rgba(5, 131, 148, 0.5);
    }
    .swiper-content {
      position: absolute;
      left: 0;
      display: flex;
    }
    
    .swiper-item {
      height: 350px;
      width: 1400px;
    }
    
    img {
      height: 100%;
      width: 100%;
    }
    
    span {
  display: inline-block;
  height: 12px;
  width: 12px;
  background-color: orange;
  margin-left: 50px;
  border-radius: 200px;
  cursor: pointer;
}
    .page {
      position: absolute;
      left: 50%;
      transform: translate(-50%, -50%);
      bottom: 5px;
    }
    .footer-text {
  margin: 0; 
  padding: 0;
}

</style>
