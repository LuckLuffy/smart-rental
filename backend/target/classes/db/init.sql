-- ============================================
-- 智慧租房管理系统 - 数据库初始化脚本
-- 运行前请确保 smart_rental 数据库已创建
-- ============================================
SET NAMES utf8mb4;

-- ============================================
-- 1. 用户表
-- ============================================
DROP TABLE IF EXISTS user;
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(200) NOT NULL COMMENT '密码(MD5加密)',
    real_name VARCHAR(100) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    role VARCHAR(20) NOT NULL COMMENT '角色(TENANT-租客,LANDLORD-房东,ADMIN-管理员)',
    gender TINYINT COMMENT '性别(0-女,1-男)',
    enabled TINYINT DEFAULT 1 COMMENT '是否启用(0-禁用,1-启用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '用户表';

-- ============================================
-- 2. 房源表
-- ============================================
DROP TABLE IF EXISTS house;
CREATE TABLE house (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '房源标题',
    description VARCHAR(1000) COMMENT '房源描述',
    address VARCHAR(300) NOT NULL COMMENT '地址',
    area DECIMAL(10,2) NOT NULL COMMENT '面积(㎡)',
    price DECIMAL(10,2) NOT NULL COMMENT '月租(元)',
    type VARCHAR(50) COMMENT '户型',
    status VARCHAR(20) DEFAULT 'AVAILABLE' COMMENT '状态(AVAILABLE-待租,RENTED-已租,OFFLINE-下架)',
    landlord_id BIGINT NOT NULL COMMENT '房东ID',
    image_url VARCHAR(500) COMMENT '图片URL',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '房源表';

-- ============================================
-- 3. 订单表
-- ============================================
DROP TABLE IF EXISTS rental_order;
CREATE TABLE rental_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    house_id BIGINT NOT NULL COMMENT '房源ID',
    tenant_id BIGINT NOT NULL COMMENT '租客ID',
    landlord_id BIGINT NOT NULL COMMENT '房东ID',
    start_date DATE COMMENT '租赁开始日期',
    end_date DATE COMMENT '租赁结束日期',
    total_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '总金额',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态(PENDING-待确认,CONFIRMED-租赁中,CANCELLED-已取消,DONE-已退租)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) COMMENT '订单表';

-- ============================================
-- 4. 验证码表
-- ============================================
DROP TABLE IF EXISTS verification_code;
CREATE TABLE verification_code (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    code VARCHAR(6) NOT NULL COMMENT '验证码',
    type VARCHAR(20) NOT NULL DEFAULT 'REGISTER' COMMENT '类型',
    expires_at DATETIME NOT NULL COMMENT '过期时间',
    used TINYINT DEFAULT 0 COMMENT '是否已使用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '邮箱验证码表';

-- ============================================
-- 初始化测试数据
-- 密码加密方式: MD5(明文密码 + salt)  salt = smartrental2026
-- admin123 → 06466fb910012860fc8b91f8a037dead
-- 123456   → f06efe0a1f7c68844677fd45d1ced81f
-- ============================================

-- 初始化用户 (5个)
INSERT INTO user (username, password, real_name, phone, email, role, gender, enabled) VALUES
('admin',     '06466fb910012860fc8b91f8a037dead', '系统管理员', '13800000000', 'admin@smartrental.com', 'ADMIN',    1, 1),
('landlord1', 'f06efe0a1f7c68844677fd45d1ced81f', '张房东',     '13800000001', 'landlord1@example.com', 'LANDLORD', 1, 1),
('landlord2', 'f06efe0a1f7c68844677fd45d1ced81f', '李房东',     '13800000002', 'landlord2@example.com', 'LANDLORD', 0, 1),
('tenant1',   'f06efe0a1f7c68844677fd45d1ced81f', '王租客',     '13800000003', 'tenant1@example.com',   'TENANT',   1, 1),
('tenant2',   'f06efe0a1f7c68844677fd45d1ced81f', '赵租客',     '13800000004', 'tenant2@example.com',   'TENANT',   0, 1);

-- 初始化房源 (12条: landlord1=8条, landlord2=4条)
INSERT INTO house (title, description, address, area, price, type, status, landlord_id) VALUES
-- landlord1 的房源
('阳光花园精装两室', '精装修，南北通透，采光极好，家电齐全拎包入住', '朝阳区建国路88号阳光花园3栋502', 85.00, 4500, '两室一厅', 'AVAILABLE', 2),
('地铁口青年公寓', '紧邻地铁站步行5分钟，适合上班族，周边配套成熟', '海淀区中关村大街15号青年公寓A1201', 35.00, 2800, '一室一卫', 'AVAILABLE', 2),
('翠湖世家高端三室', '湖景房，豪华装修，全屋智能家居，带地下车位', '滨江区翠湖路66号翠湖世家8栋1501', 130.00, 8500, '三室两厅', 'AVAILABLE', 2),
('温馨小窝一室户', '独立厨卫，安静舒适，小区环境优美，适合单身人士', '朝阳区望京街道花家地小区2栋101', 25.00, 1800, '一室一卫', 'AVAILABLE', 2),
('城市花园两室一厅', '小区绿化率40%，人车分流，带电梯，物业好', '东城区东直门外大街城市花园5栋802', 78.00, 5200, '两室一厅', 'RENTED', 2),
('科技园公寓', '近科技园，全智能门锁，健身房游泳池配套', '海淀区上地科技园南路3号402', 40.00, 3200, '一室一卫', 'RENTED', 2),
('老城区便宜单间', '老小区无电梯，但价格实惠交通便利', '西城区新街口北大街胡同12号', 18.00, 1200, '一室一卫', 'OFFLINE', 2),
('月租特价公寓', '短期特价出租，家具齐全，适合过渡居住', '通州区梨园镇云景东路20号301', 30.00, 1500, '一室一卫', 'OFFLINE', 2),

-- landlord2 的房源
('静安小区三室两厅', '学区房，靠近重点中小学，环境安静适合家庭', '静安区南京西路1888号静安小区12栋301', 110.00, 6800, '三室两厅', 'AVAILABLE', 3),
('大学城合租单间', '靠近大学城，适合学生合租，公共区域宽敞', '松江区大学城文汇路600弄5号', 22.00, 1000, '一室一卫', 'AVAILABLE', 3),
('拎包入住精装公寓', '全配家电，24小时热水，安全门禁，物业服务好', '浦东新区张江高科技园区碧波路328号', 50.00, 3800, '两室一厅', 'RENTED', 3),
('远郊独栋小院', '独立小院带花园，适合喜欢安静的租客，距离市区较远', '崇明区陈家镇东滩花园88号', 150.00, 5500, '三室两厅', 'OFFLINE', 3);

-- 初始化订单 (8条)
INSERT INTO rental_order (house_id, tenant_id, landlord_id, start_date, end_date, total_amount, status) VALUES
-- tenant1 的订单
(5, 4, 2, '2026-07-01', '2027-06-30', 62400, 'CONFIRMED'),
(6, 4, 2, '2026-06-01', '2026-09-01', 9600, 'CONFIRMED'),
(11, 4, 3, '2026-06-15', '2027-06-15', 45600, 'DONE'),
(1, 4, 2, '2026-08-01', '2027-08-01', 54000, 'PENDING'),

-- tenant2 的订单
(7, 5, 2, '2026-05-01', '2026-08-01', 3600, 'CANCELLED'),
(10, 5, 3, '2026-07-15', '2027-01-15', 6000, 'PENDING'),
(2, 5, 2, '2026-04-01', '2026-10-01', 16800, 'DONE'),
(3, 5, 2, '2026-07-01', '2026-12-01', 51000, 'CONFIRMED');
