drop database if exists traveldog; 
create database traveldog;

use traveldog;

create table trains(
t_id varchar(100),
t_type varchar(100),
price_rate double,
primary key(t_id)
);

create table meals(
m_id int,
m_name varchar(100),
m_type varchar(100),
m_price double,
m_detail varchar(500),
primary key(m_id)
);

create table train_meal(
t_id varchar(100),
m_id int,
m_count int,
primary key(t_id, m_id),
foreign key(t_id) references trains(t_id),
foreign key(m_id) references meals(m_id)
);

create table seat_price(
s_type varchar(100),
s_price double,
primary key(s_type)
);

create table train_seat(
t_id varchar(100),
s_id varchar(100),
s_type varchar(100),
bitmap bit(32),
primary key(t_id,s_id),
foreign key(t_id) references trains(t_id),
foreign key(s_type) references seat_price(s_type)
);



create table locations(
l_id int,
province varchar(100),
city varchar(100),
primary key(l_id)
);

create table train_arrivals(
t_id varchar(100),
l_id int,
arrivalTime datetime,
station_sequence int,
detail_location varchar(100),
primary key(t_id, l_id),
foreign key(t_id) references trains(t_id),
foreign key(l_id) references locations(l_id)
);

create table hotels(
h_id int,
h_name varchar(100),
l_id int,
price_rate double,
h_tel varchar(100),
detail_location varchar(500), #为了方便查询
h_description varchar(500),
primary key(h_id),
foreign key(l_id) references locations(l_id)
);

create table hotel_comment(
o_id int auto_increment,
h_id int,
rating int,
content varchar(500),
primary key(o_id),
foreign key(h_id) references hotels(h_id)
);

create table rooms(
r_id int,
r_name varchar(100),
r_type varchar(100),
r_detail varchar(500),
r_price int,
primary key(r_id)
);

create table hotel_room(
h_id int,
r_id int,
r_number varchar(100),
bitmap bit(32),
primary key(h_id, r_id,r_number),
foreign key(h_id) references hotels(h_id),
foreign key(r_id) references rooms(r_id)
);

create table users(
u_id varchar(100),
u_name varchar(100),
u_password varchar(100),
u_tel varchar(100),
u_money double,
u_status varchar(100),
primary key(u_id)
);

create table customers(
c_id int auto_increment,
c_name varchar(100),
c_tel varchar(100),
id_card varchar(100),
c_age int,
primary key(c_id)
);

create table users_customers(
u_id varchar(100),
c_id int,
primary key(u_id, c_id),
foreign key(u_id) references users(u_id),
foreign key(c_id) references customers(c_id)
);

create table orders(
o_id int auto_increment,
u_id varchar(100),
price double,
add_time datetime,
uo_status varchar(100),
service_type varchar(100),
is_comment boolean,
primary key(o_id),
foreign key(u_id) references users(u_id)
);

create table hotel_orders(
o_id int,
c_id int,
h_id int,
r_id int,
r_number varchar(100),
add_time datetime,
from_time date,
to_time date,
ho_price double,
primary key(o_id, c_id),
foreign key(o_id) references orders(o_id),
foreign key(c_id) references customers(c_id),
foreign key(h_id) references hotels(h_id),
foreign key(r_id) references rooms(r_id)
);

create table trains_orders(
o_id int,
c_id int,
t_id varchar(100),
seat_number varchar(100),
seat_type varchar(100),
add_time datetime,
departure_time datetime,
arrive_time datetime,
to_price double,
departure_location int,
arrive_location int,
primary key(o_id, c_id),
foreign key(o_id) references orders(o_id),
foreign key(c_id) references customers(c_id),
foreign key(t_id) references trains(t_id),
foreign key(departure_location) references locations(l_id),
foreign key(arrive_location) references locations(l_id)
);

create table meal_orders(
o_id int,
c_id int,
m_id int,
t_id varchar(100),
add_time datetime,
mo_price double,
primary key(o_id, c_id),
foreign key(o_id) references orders(o_id),
foreign key(c_id) references customers(c_id),
foreign key(m_id) references meals(m_id),
foreign key(t_id) references trains(t_id)
);

create table user_notice(
n_id int auto_increment,
u_id varchar(100),
n_time datetime,
n_content varchar(500),
n_status varchar(100),
primary key(n_id),
foreign key(u_id) references users(u_id)
);

insert into locations values
(1, "北京省","北京市"),
(2, "江苏省","南京市"),
(3, "贵州省","贵阳市"),
(4, "湖南省","长沙市"),
(5, "上海省","上海市"),
(6, "天津省","天津市"),
(7, "重庆省","重庆市"),
(8, "四川省","成都市"),
(9, "浙江省","杭州市");

insert into hotels values
(1, 'OS酒店', 1, 1.2, '13000000000', '北京省北京市','我是操作系统高手'),
(2, 'CO酒店', 2, 1.75, '13000000001', '江苏省南京市','我是硬件的狗'),
(3, 'OO酒店', 3, 2, '13000000002', '贵州省贵阳市','评价是Jvav不如++C'),
(4, 'AL酒店', 4, 3, '13000000003', '湖南省长沙市', '我有1000+个板子你怕不怕'),
(5, 'RG酒店', 1, 2.25, '13000000004', '北京省北京市','心中有座坟，埋着软工所有人');

insert into rooms values
(1, null, '单人间', '出差必选', 100),
(2, null, '双人间', '小情侣', 200),
(3, null, '豪华间', '发发发', 500);

insert into hotel_room values
(1, 1, 'a001', b'111111111111111111111111111111'),
(1, 2, 'a002', b'111111111111111111111111111111'),
(1, 3, 'a003', b'111111111111111111111111111111'),
(1, 1, 'a004', b'111111111111111111111111111111'),
(1, 2, 'a005', b'111111111111111111111111111111'),
(1, 3, 'a006', b'111111111111111111111111111111'),
(2, 1, 'b001', b'111111111111111111111111111111'),
(2, 2, 'b002', b'111111111111111111111111111111'),
(2, 3, 'b003', b'111111111111111111111111111111'),
(2, 1, 'b004', b'111111111111111111111111111111'),
(2, 2, 'b005', b'111111111111111111111111111111'),
(2, 3, 'b006', b'111111111111111111111111111111'),
(3, 1, 'c001', b'111111111111111111111111111111'),
(3, 2, 'c002', b'111111111111111111111111111111'),
(3, 3, 'c003', b'111111111111111111111111111111'),
(3, 1, 'c004', b'111111111111111111111111111111'),
(3, 2, 'c005', b'111111111111111111111111111111'),
(3, 3, 'c006', b'111111111111111111111111111111'),
(4, 1, 'd001', b'111111111111111111111111111111'),
(4, 2, 'd002', b'111111111111111111111111111111'),
(4, 3, 'd003', b'111111111111111111111111111111'),
(4, 1, 'd004', b'111111111111111111111111111111'),
(4, 2, 'd005', b'111111111111111111111111111111'),
(4, 3, 'd006', b'111111111111111111111111111111'),
(5, 1, 'e001', b'111111111111111111111111111111'),
(5, 2, 'e002', b'111111111111111111111111111111'),
(5, 3, 'e003', b'111111111111111111111111111111'),
(5, 1, 'e004', b'111111111111111111111111111111'),
(5, 2, 'e005', b'111111111111111111111111111111'),
(5, 3, 'e006', b'111111111111111111111111111111');



insert into trains values
('A01', '绿皮', 1.0),
('B02', '普快', 1.5),
('C03', '特快', 2.0),
('D04', '绿皮', 1.1),
('E05', '普快', 1.6),
('F06', '特快', 2.1),
('G07', '绿皮', 1.2),
('H08', '普快', 1.75),
('I09', '特快', 2.25),
('J10', '普快', 1.7),
('K11', '特快', 2.3);

insert into meals values
(1, "牛肉饭","午餐", 28.8, "贼好吃"),
(2, "麻婆豆腐饭", "午餐", 20.8, "好吃不贵"),
(3, "豌豆猪肉饭","午餐", 25.8, "性价比之王"),
(4, "培根炒蛋饭","午餐", 26.8, "洋鬼子"),
(5, "意大利面","午餐", 25.8, "不如李云龙"),
(6, "麻辣香锅","午餐", 30.8, "拉稀必备"),
(7, "牛肉饭","晚餐", 28.8, "贼好吃"),
(8, "麻婆豆腐饭", "晚餐", 20.8, "好吃不贵"),
(9, "豌豆猪肉饭","晚餐", 25.8, "性价比之王"),
(10, "培根炒蛋饭","晚餐", 26.8, "洋鬼子"),
(11, "意大利面","晚餐", 25.8, "不如李云龙"),
(12, "麻辣香锅","晚餐", 30.8, "拉稀必备");

insert into train_meal values
('A01', 1, 10),
('A01', 2, 7),
('A01', 3, 3),
('A01', 4, 5),
('A01', 5, 8),
('A01', 6, 2),
('A01', 7, 10),
('A01', 8, 7),
('A01', 9, 3),
('A01', 10, 5),
('A01', 11, 8),
('A01', 12, 2),
('B02', 1, 10),
('B02', 4, 5),
('B02', 5, 8),
('B02', 7, 2),
('C03', 9, 3),
('C03', 11, 5),
('C03', 5, 8),
('C03', 6, 2),
('D04', 1, 10),
('D04', 12, 7),
('D04', 3, 3),
('D04', 9, 5),
('D04', 5, 8),
('D04', 7, 2),
('E05', 4, 5),
('E05', 5, 8),
('E05', 10, 2),
('F06', 3, 3),
('F06', 4, 5),
('F06', 12, 2),
('G07', 1, 10),
('G07', 8, 7),
('G07', 3, 3),
('G07', 11, 2),
('H08', 2, 7),
('H08', 3, 3),
('H08', 10, 5),
('H08', 5, 8),
('I09', 8, 10),
('I09', 6, 2),
('J10', 1, 20),
('J10', 4, 17),
('J10', 7, 23),
('J10', 10, 10),
('K11', 1, 20),
('K11', 4, 17),
('K11', 7, 23),
('K11', 10, 10);

insert into train_arrivals values
('A01', 1, '2024-06-11 18:00:00', 1, '北京省北京市'),
('A01', 2, '2024-06-11 20:00:00', 2, '江苏省南京市'),
('A01', 4, '2024-06-11 21:00:00', 3, '湖南省长沙市'),
('A01', 7, '2024-06-11 23:00:00', 4, '重庆省重庆市'),
('A01', 9, '2024-06-11 23:30:00', 5, '浙江省杭州市'),
('B02', 2, '2024-06-11 19:00:00', 1, '江苏省南京市'),
('B02', 3, '2024-06-11 21:30:00', 2, '贵州省贵阳市'),
('B02', 5, '2024-06-11 22:30:00', 3, '上海省上海市'),
('B02', 6, '2024-06-11 23:45:00', 4, '天津省天津市'),
('C03', 4, '2024-06-12 18:00:00', 1, '湖南省长沙市'),
('C03', 8, '2024-06-12 20:00:00', 2, '四川省成都市'),
('D04', 1, '2024-06-12 18:00:00', 1, '北京省北京市'),
('D04', 2, '2024-06-12 20:00:00', 2, '江苏省南京市'),
('D04', 7, '2024-06-12 23:00:00', 3, '重庆省重庆市'),
('D04', 9, '2024-06-12 23:30:00', 4, '浙江省杭州市'),
('E05', 2, '2024-06-12 07:00:00', 1, '江苏省南京市'),
('E05', 3, '2024-06-12 08:00:00', 2, '贵州省贵阳市'),
('E05', 4, '2024-06-12 09:00:00', 3, '湖南省长沙市'),
('F06', 7, '2024-06-13 02:00:00', 1, '重庆省重庆市'),
('F06', 9, '2024-06-13 10:30:00', 2, '浙江省杭州市'),
('F06', 1, '2024-06-13 21:00:00', 3, '北京省北京市'),
('G07', 8, '2024-06-13 20:00:00', 1, '四川省成都市'),
('G07', 5, '2024-06-13 21:00:00', 2, '上海省上海市'),
('H08', 7, '2024-06-14 23:00:00', 1, '重庆省重庆市'),
('H08', 9, '2024-06-14 23:30:00', 2, '浙江省杭州市'),
('H08', 4, '2024-06-14 18:00:00', 3, '湖南省长沙市'),
('H08', 5, '2024-06-14 20:00:00', 4, '上海省上海市'),
('H08', 6, '2024-06-14 21:00:00', 5, '天津省天津市'),
('I09', 6, '2024-06-15 23:00:00', 1, '天津省天津市'),
('I09', 2, '2024-06-15 23:30:00', 2, '江苏省南京市'),
('I09', 5, '2024-06-15 18:00:00', 3, '上海省上海市'),
('J10', 1, '2024-06-11 12:00:00', 1, '北京省北京市'),
('J10', 2, '2024-06-11 13:00:00', 2, '江苏省南京市'),
('J10', 3, '2024-06-11 14:00:00', 3, '贵州省贵阳市'),
('J10', 4, '2024-06-11 15:00:00', 4, '湖南省长沙市'),
('J10', 5, '2024-06-11 16:00:00', 5, '上海省上海市'),
('J10', 6, '2024-06-11 17:00:00', 6, '天津省天津市'),
('J10', 7, '2024-06-11 18:00:00', 7, '重庆省重庆市'),
('J10', 8, '2024-06-11 19:00:00', 8, '四川省成都市'),
('J10', 9, '2024-06-11 20:00:00', 9, '浙江省杭州市'),
('K11', 1, '2024-06-11 12:30:00', 1, '北京省北京市'),
('K11', 4, '2024-06-11 13:30:00', 2, '湖南省长沙市'),
('K11', 7, '2024-06-11 14:30:00', 3, '重庆省重庆市'),
('K11', 2, '2024-06-11 15:30:00', 4, '江苏省南京市'),
('K11', 5, '2024-06-11 16:30:00', 5, '上海省上海市'),
('K11', 8, '2024-06-11 17:30:00', 6, '四川省成都市'),
('K11', 3, '2024-06-11 18:30:00', 7, '贵州省贵阳市'),
('K11', 6, '2024-06-11 19:30:00', 8, '天津省天津市'),
('K11', 9, '2024-06-11 20:30:00', 9, '浙江省杭州市');


insert into seat_price values
('硬座',188.8),
('软卧', 588.8),
('站票', 88.8);

insert into train_seat values
('A01', 'sb001','硬座' ,b'11111111111'),
('A01', 'sb002','软卧' ,b'11111111111'),
('A01', 'sb003','站票' ,b'11111111111'),
('A01', 'sb004','硬座' ,b'11111111111'),
('A01', 'sb005','软卧' ,b'11111111111'),
('A01', 'sb006','站票' ,b'11111111111'),
('B02', 'sb001','硬座' ,b'11111111111'),
('B02', 'sb002','硬座' ,b'11111111111'),
('B02', 'sb003','硬座' ,b'11111111111'),
('B02', 'sb004','软卧' ,b'11111111111'),
('B02', 'sb005','软卧' ,b'11111111111'),
('B02', 'sb006','软卧' ,b'11111111111'),
('C03', 'sb001','软卧' ,b'11111111111'),
('C03', 'sb002','软卧' ,b'11111111111'),
('C03', 'sb003','站票' ,b'11111111111'),
('C03', 'sb004','站票' ,b'11111111111'),
('C03', 'sb005','站票' ,b'11111111111'),
('C03', 'sb006','站票' ,b'11111111111'),
('D04', 'sb001','硬座' ,b'11111111111'),
('D04', 'sb002','硬座' ,b'11111111111'),
('D04', 'sb003','硬座' ,b'11111111111'),
('D04', 'sb004','硬座' ,b'11111111111'),
('D04', 'sb005','硬座' ,b'11111111111'),
('D04', 'sb006','硬座' ,b'11111111111'),
('E05', 'sb001','软卧' ,b'11111111111'),
('E05', 'sb002','软卧' ,b'11111111111'),
('E05', 'sb003','软卧' ,b'11111111111'),
('E05', 'sb004','硬座' ,b'11111111111'),
('E05', 'sb005','硬座' ,b'11111111111'),
('E05', 'sb006','硬座' ,b'11111111111'),
('F06', 'sb001','软卧' ,b'11111111111'),
('F06', 'sb002','软卧' ,b'11111111111'),
('F06', 'sb003','软卧' ,b'11111111111'),
('F06', 'sb004','软卧' ,b'11111111111'),
('F06', 'sb005','软卧' ,b'11111111111'),
('F06', 'sb006','站票' ,b'11111111111'),
('G07', 'sb001','硬座' ,b'11111111111'),
('G07', 'sb002','硬座' ,b'11111111111'),
('G07', 'sb003','硬座' ,b'11111111111'),
('G07', 'sb004','硬座' ,b'11111111111'),
('G07', 'sb005','软卧' ,b'11111111111'),
('G07', 'sb006','软卧' ,b'11111111111'),
('H08', 'sb001','站票' ,b'11111111111'),
('H08', 'sb002','站票' ,b'11111111111'),
('H08', 'sb003','站票' ,b'11111111111'),
('H08', 'sb004','硬座' ,b'11111111111'),
('H08', 'sb005','硬座' ,b'11111111111'),
('H08', 'sb006','软卧' ,b'11111111111'),
('I09', 'sb001','软卧' ,b'11111111111'),
('I09', 'sb002','软卧' ,b'11111111111'),
('I09', 'sb003','站票' ,b'11111111111'),
('I09', 'sb004','站票' ,b'11111111111'),
('I09', 'sb005','站票' ,b'11111111111'),
('I09', 'sb006','站票' ,b'11111111111'),
('J10', 'sb001','硬座' ,b'11111111111'),
('J10', 'sb002','软卧' ,b'11111111111'),
('J10', 'sb003','站票' ,b'11111111111'),
('J10', 'sb004','硬座' ,b'11111111111'),
('J10', 'sb005','软卧' ,b'11111111111'),
('J10', 'sb006','站票' ,b'11111111111'),
('K11', 'sb001','硬座' ,b'11111111111'),
('K11', 'sb002','软卧' ,b'11111111111'),
('K11', 'sb003','站票' ,b'11111111111'),
('K11', 'sb004','硬座' ,b'11111111111'),
('K11', 'sb005','软卧' ,b'11111111111'),
('K11', 'sb006','站票' ,b'11111111111');