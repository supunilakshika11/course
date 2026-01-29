DROP DATABASE IF EXISTS gear_rent_pro;
CREATE DATABASE gear_rent_pro;
USE gear_rent_pro;

-- ---------- MASTER TABLES ----------
CREATE TABLE branch (
  branch_id INT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(20) UNIQUE NOT NULL,
  name VARCHAR(100) NOT NULL,
  address VARCHAR(255),
  contact VARCHAR(30)
);

CREATE TABLE category (
  category_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) UNIQUE NOT NULL,
  description VARCHAR(255),
  base_price_factor DECIMAL(6,2) NOT NULL DEFAULT 1.00,
  weekend_multiplier DECIMAL(6,2) NOT NULL DEFAULT 1.00,
  late_fee_per_day DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE membership_level (
  level ENUM('REGULAR','SILVER','GOLD') PRIMARY KEY,
  discount_percent DECIMAL(5,2)
);

CREATE TABLE customer (
  customer_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100),
  nic_passport VARCHAR(30) UNIQUE,
  contact VARCHAR(30),
  email VARCHAR(100),
  address VARCHAR(255),
  membership ENUM('REGULAR','SILVER','GOLD'),
  FOREIGN KEY (membership) REFERENCES membership_level(level)
);

CREATE TABLE equipment (
  equipment_id VARCHAR(30) PRIMARY KEY,
  category_id INT NOT NULL,
  branch_id INT NOT NULL,
  brand VARCHAR(50),
  model VARCHAR(50),
  purchase_year INT,
  base_daily_price DECIMAL(12,2),
  security_deposit DECIMAL(12,2),
  status ENUM('AVAILABLE','RESERVED','RENTED','MAINTENANCE') DEFAULT 'AVAILABLE',
  FOREIGN KEY (category_id) REFERENCES category(category_id),
  FOREIGN KEY (branch_id) REFERENCES branch(branch_id)
);

CREATE TABLE system_user (
  user_id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) UNIQUE,
  password_hash VARCHAR(255),
  role ENUM('ADMIN','BRANCH_MANAGER','STAFF'),
  branch_id INT,
  FOREIGN KEY (branch_id) REFERENCES branch(branch_id)
);

-- ---------- APP CONFIG (IMPORTANT) ----------
CREATE TABLE app_config (
  config_key VARCHAR(50) PRIMARY KEY,
  config_value VARCHAR(255) NOT NULL
);

-- ---------- RESERVATION (IMPORTANT) ----------
CREATE TABLE reservation (
  reservation_id INT PRIMARY KEY AUTO_INCREMENT,
  equipment_id VARCHAR(30) NOT NULL,
  customer_id INT NOT NULL,
  branch_id INT NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  status ENUM('ACTIVE','CANCELLED','EXPIRED') DEFAULT 'ACTIVE',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id),
  FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
  FOREIGN KEY (branch_id) REFERENCES branch(branch_id)
);

-- ---------- RENTAL ----------
CREATE TABLE rental (
  rental_id INT PRIMARY KEY AUTO_INCREMENT,
  reservation_id INT NULL,

  equipment_id VARCHAR(30) NOT NULL,
  customer_id INT NOT NULL,
  branch_id INT NOT NULL,

  start_date DATE NOT NULL,
  end_date DATE NOT NULL,

  rental_amount DECIMAL(12,2),
  security_deposit DECIMAL(12,2),
  membership_discount DECIMAL(12,2),
  long_rental_discount DECIMAL(12,2),
  final_payable DECIMAL(12,2),

  payment_status ENUM('PAID','PARTIALLY_PAID','UNPAID'),
  rental_status ENUM('ACTIVE','RETURNED','OVERDUE','CANCELLED'),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  FOREIGN KEY (reservation_id) REFERENCES reservation(reservation_id),
  FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id),
  FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
  FOREIGN KEY (branch_id) REFERENCES branch(branch_id)
);

CREATE TABLE rental_return (
  return_id INT PRIMARY KEY AUTO_INCREMENT,
  rental_id INT NOT NULL,
  actual_return_date DATE NOT NULL,
  damage_description VARCHAR(255),
  damage_charge DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  late_days INT NOT NULL DEFAULT 0,
  late_fee DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  total_charges DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  additional_pay DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  refund_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (rental_id) REFERENCES rental(rental_id)
);

-- ---------- SAMPLE DATA ----------
INSERT INTO branch(code,name,address,contact) VALUES
('PAN','Panadura Branch','Panadura','038-0000000'),
('GAL','Galle Branch','Galle','091-0000000'),
('COL','Colombo Branch','Colombo','011-0000000');

INSERT INTO category(name,description,base_price_factor,weekend_multiplier,late_fee_per_day) VALUES
('Camera','DSLR/Mirrorless',1.00,1.10,2500),
('Lens','Prime/Zoom',0.80,1.05,1500),
('Drone','Aerial',1.50,1.20,5000),
('Lighting','Lights & Kits',0.70,1.05,1200),
('Audio','Mics & Recorders',0.60,1.05,1000);

INSERT INTO membership_level VALUES
('REGULAR',0.00),('SILVER',3.00),('GOLD',5.00);

-- ✅ customers 10+ (membership MUST be REGULAR/SILVER/GOLD)
INSERT INTO customer (name, nic_passport, contact, email, address, membership) VALUES
('Kamal Perera','901234567V','0771234567','kamal@gmail.com','Colombo','REGULAR'),
('Nimal Silva','911234568V','0771234568','nimal@gmail.com','Gampaha','REGULAR'),
('Sunil Fernando','921234569V','0771234569','sunil@gmail.com','Kalutara','SILVER'),
('Amal Jayasinghe','931234570V','0771234570','amal@gmail.com','Kandy','SILVER'),
('Saman Kumara','941234571V','0771234571','saman@gmail.com','Galle','REGULAR'),
('Chathura Perera','951234572V','0771234572','chathura@gmail.com','Matara','REGULAR'),
('Isuru Bandara','961234573V','0771234573','isuru@gmail.com','Kurunegala','SILVER'),
('Kasun Wijesinghe','971234574V','0771234574','kasun@gmail.com','Negombo','REGULAR'),
('Dinesh Rathnayake','981234575V','0771234575','dinesh@gmail.com','Anuradhapura','SILVER'),
('Tharindu Gunasekara','991234576V','0771234576','tharindu@gmail.com','Hambantota','REGULAR');

-- ✅ equipment 20 (equipment_id MUST be VARCHAR like CAM_001)
INSERT INTO equipment
(equipment_id, category_id, branch_id, brand, model, purchase_year, base_daily_price, security_deposit, status) VALUES
-- Cameras (1)
('CAM_001', 1, 1, 'Canon', 'EOS 90D', 2022, 3500, 20000, 'AVAILABLE'),
('CAM_002', 1, 1, 'Sony', 'A6400', 2021, 4000, 25000, 'AVAILABLE'),
('CAM_003', 1, 2, 'Panasonic', 'GH5', 2020, 4500, 30000, 'AVAILABLE'),
('CAM_004', 1, 3, 'DJI', 'Pocket 3', 2023, 3800, 18000, 'AVAILABLE'),
('CAM_005', 1, 2, 'GoPro', 'Hero 11', 2023, 3000, 15000, 'AVAILABLE'),

-- Lenses (2)
('LEN_001', 2, 1, 'Sigma', '35mm f/1.4', 2020, 2500, 15000, 'AVAILABLE'),
('LEN_002', 2, 1, 'Canon', '24-70mm', 2021, 2800, 18000, 'AVAILABLE'),
('LEN_003', 2, 3, 'Sony', '50mm f/1.8', 2022, 2200, 14000, 'AVAILABLE'),
('LEN_004', 2, 3, 'Nikon', '70-200mm', 2019, 3000, 20000, 'AVAILABLE'),

-- Drones (3)
('DRN_001', 3, 2, 'DJI', 'Mavic Air 2', 2021, 5000, 40000, 'AVAILABLE'),
('DRN_002', 3, 2, 'DJI', 'Mini 3 Pro', 2022, 4500, 35000, 'AVAILABLE'),
('DRN_003', 3, 3, 'Autel', 'Evo Lite+', 2023, 5200, 42000, 'AVAILABLE'),
('DRN_004', 3, 1, 'DJI', 'Air 3', 2023, 5500, 45000, 'AVAILABLE'),

-- Lighting (4)
('LGT_001', 4, 2, 'Godox', 'SL60W Kit', 2021, 1200, 8000, 'AVAILABLE'),
('LGT_002', 4, 3, 'Aputure', 'Amaran 100d', 2022, 1500, 9000, 'AVAILABLE'),
('LGT_003', 4, 1, 'Neewer', 'LED Panel', 2020, 1000, 6000, 'AVAILABLE'),
('LGT_004', 4, 1, 'Godox', 'Ring Light', 2021, 900, 5000, 'AVAILABLE'),

-- Audio (5)
('AUD_001', 5, 3, 'Shure', 'SM58', 2019, 1000, 4000, 'AVAILABLE'),
('AUD_002', 5, 3, 'Rode', 'Wireless GO II', 2022, 1500, 7000, 'AVAILABLE'),
('AUD_003', 5, 1, 'Sennheiser', 'MKE 600', 2021, 1800, 9000, 'AVAILABLE');

-- ✅ users
INSERT INTO system_user (username, password_hash, role, branch_id) VALUES
('admin', '1234', 'ADMIN', 1),
('pan_mgr', '1234', 'BRANCH_MANAGER', 1),
('gal_staff', '1234', 'STAFF', 2),
('col_staff', '1234', 'STAFF', 3);

-- ✅ configs (ADD DEPOSIT_LIMIT!)
INSERT INTO app_config (config_key, config_value) VALUES
('LONG_RENTAL_DAYS', '7'),
('LONG_RENTAL_DISCOUNT_PERCENT', '5'),
('TAX_PERCENT', '0'),
('DEPOSIT_LIMIT', '50000')
ON DUPLICATE KEY UPDATE config_value = VALUES(config_value);

INSERT INTO rental (
  reservation_id,
  equipment_id,
  customer_id,
  branch_id,
  start_date,
  end_date,
  rental_amount,
  security_deposit,
  membership_discount,
  long_rental_discount,
  final_payable,
  payment_status,
  rental_status
) VALUES (
  NULL,
  'CAM_001',
  1,
  1,
  '2026-01-10',
  '2026-01-12',
  7000,
  20000,
  0,
  0,
  27000,
  'PAID',
  'RETURNED'
);

INSERT INTO reservation (equipment_id, customer_id, branch_id, start_date, end_date, status)
VALUES
('CAM_002', 2, 1, '2026-02-01', '2026-02-03', 'ACTIVE'),
('LEN_001', 3, 1, '2026-02-05', '2026-02-06', 'ACTIVE');


