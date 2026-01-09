# ☕ FoodBevApp

<div align="center">

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)
![Midtrans](https://img.shields.io/badge/Midtrans-00D4AA?style=for-the-badge&logo=stripe&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

<br>

**🍔 Aplikasi Modern untuk Manajemen Pemesanan Makanan & Minuman**

*Solusi lengkap untuk bisnis F&B dengan integrasi pembayaran online*

[Fitur](#-fitur) • [Demo](#-demo) • [Instalasi](#-instalasi) • [Konfigurasi](#-konfigurasi) • [Dokumentasi](#-dokumentasi-api) • [Kontribusi](#-kontribusi)

---

![FoodBevApp Banner](https://via.placeholder.com/800x400/8B7355/FFFFFF?text=FoodBevApp+-+Food+%26+Beverage+Management)

</div>

## 📋 Tentang Project

**FoodBevApp** adalah aplikasi web full-stack untuk manajemen pemesanan makanan dan minuman yang dibangun dengan **Spring Boot** dan **Thymeleaf**. Aplikasi ini menyediakan pengalaman pemesanan yang seamless untuk pelanggan serta panel admin yang powerful untuk pemilik bisnis.

### 🎯 Mengapa FoodBevApp?

- ✅ **Siap Pakai** - Setup cepat dengan konfigurasi minimal
- ✅ **Responsif** - Tampilan modern yang mobile-friendly
- ✅ **Aman** - Integrasi Spring Security dengan role-based access
- ✅ **Pembayaran Online** - Integrasi Midtrans payment gateway
- ✅ **Skalabel** - Arsitektur bersih dan mudah dikembangkan

---

## ✨ Fitur

### 👤 Fitur Pelanggan

| Fitur | Deskripsi |
|-------|-----------|
| 🔐 **Autentikasi** | Registrasi & login dengan enkripsi password |
| 🍽️ **Katalog Produk** | Jelajahi menu Coffee, Food, dan Snacks |
| 🛒 **Keranjang Belanja** | Tambah, ubah jumlah, dan hapus item |
| 📝 **Pemesanan** | Dine-in atau Takeaway dengan detail lengkap |
| 💳 **Pembayaran Online** | Berbagai metode pembayaran via Midtrans |
| 📜 **Riwayat Pesanan** | Lacak semua pesanan sebelumnya |

### 👨‍💼 Fitur Admin

| Fitur | Deskripsi |
|-------|-----------|
| 📊 **Dashboard** | Overview metrik bisnis real-time |
| 📦 **Manajemen Produk** | CRUD produk dengan upload gambar |
| 👥 **Manajemen User** | Kelola akun pengguna terdaftar |
| 📋 **Manajemen Order** | Lihat dan update status pesanan |
| 💰 **Laporan Revenue** | Analitik pendapatan harian/total |
| 🏆 **Top Products** | Ranking produk terlaris |

### 🍕 Kategori Produk

```
┌─────────────────────────────────────────────────────────┐
│                    PRODUCT CATEGORIES                    │
├──────────────┬──────────────────┬───────────────────────┤
│   ☕ COFFEE   │     🍔 FOOD       │      🍿 SNACK         │
├──────────────┼──────────────────┼───────────────────────┤
│ • Espresso   │ • Main Course    │ • Chips               │
│ • Latte      │ • Appetizer      │ • Cookies             │
│ • Cappuccino │ • Dessert        │ • Crackers            │
│ • Americano  │ • Vegetarian     │ • Gluten-free options │
│ • Hot/Iced   │ • Various cuisine│ • Healthy snacks      │
│ • Size (S/M/L)│ • Prep time info │ • Calorie info       │
└──────────────┴──────────────────┴───────────────────────┘
```

---

## 🛠️ Tech Stack

<table>
<tr>
<td align="center" width="150">

**Backend**

</td>
<td align="center" width="150">

**Frontend**

</td>
<td align="center" width="150">

**Database**

</td>
<td align="center" width="150">

**Payment**

</td>
</tr>
<tr>
<td align="center">

![Java](https://skillicons.dev/icons?i=java)

Java 17

</td>
<td align="center">

![Bootstrap](https://skillicons.dev/icons?i=bootstrap)

Bootstrap 5

</td>
<td align="center">

![PostgreSQL](https://skillicons.dev/icons?i=postgres)

PostgreSQL

</td>
<td align="center">

💳

Midtrans

</td>
</tr>
<tr>
<td align="center">

![Spring](https://skillicons.dev/icons?i=spring)

Spring Boot 3.5

</td>
<td align="center">

🍃

Thymeleaf

</td>
<td align="center">

🔄

Hibernate JPA

</td>
<td align="center">

🔒

Snap Token

</td>
</tr>
</table>

### 📚 Dependencies Utama

```xml
• spring-boot-starter-web          → RESTful web services
• spring-boot-starter-data-jpa     → Database ORM
• spring-boot-starter-security     → Authentication & Authorization
• spring-boot-starter-thymeleaf    → Template engine
• spring-boot-starter-validation   → Input validation
• thymeleaf-extras-springsecurity6 → Security integration
• postgresql                       → Database driver
• lombok                           → Boilerplate reduction
```

---

## 📁 Struktur Project

```
FoodBevApp/
├── 📂 src/main/java/com/foodbev/FoodBevApp/
│   ├── 📂 config/                    # Konfigurasi aplikasi
│   │   ├── SecurityConfig.java       # Spring Security setup
│   │   ├── WebMvcConfig.java         # MVC configuration
│   │   └── RestTemplateConfig.java   # HTTP client config
│   │
│   ├── 📂 controller/                # MVC Controllers
│   │   ├── 📂 admin/                 # Admin controllers
│   │   │   ├── AdminController.java
│   │   │   ├── AdminProductController.java
│   │   │   ├── AdminOrderController.java
│   │   │   └── AdminUserController.java
│   │   ├── 📂 auth/                  # Authentication
│   │   ├── 📂 cart/                  # Shopping cart
│   │   ├── 📂 order/                 # Order management
│   │   ├── 📂 payment/               # Payment processing
│   │   └── 📂 home/                  # Public pages
│   │
│   ├── 📂 entity/                    # JPA Entities
│   │   ├── 📂 user/
│   │   │   └── User.java             # User entity
│   │   ├── 📂 product/
│   │   │   ├── Product.java          # Abstract product
│   │   │   ├── Coffee.java           # Coffee entity
│   │   │   ├── Food.java             # Food entity
│   │   │   └── Snack.java            # Snack entity
│   │   ├── 📂 cart/
│   │   │   └── CartItem.java
│   │   ├── 📂 order/
│   │   │   ├── Order.java
│   │   │   └── OrderItem.java
│   │   └── 📂 payment/
│   │       └── Payment.java
│   │
│   ├── 📂 repository/                # Data Access Layer
│   ├── 📂 service/                   # Business Logic
│   │   ├── 📂 cart/
│   │   ├── 📂 order/
│   │   ├── 📂 payment/               # Midtrans integration
│   │   ├── 📂 product/
│   │   ├── 📂 revenue/
│   │   └── 📂 user/
│   │
│   ├── 📂 dto/                       # Data Transfer Objects
│   ├── 📂 constants/                 # Application constants
│   ├── 📂 util/                      # Utility classes
│   │
│   ├── DataInitializer.java          # Default data seeder
│   └── FoodBevAppApplication.java    # Main class
│
├── 📂 src/main/resources/
│   ├── 📂 templates/                 # Thymeleaf templates
│   │   ├── home.html                 # Landing page
│   │   ├── 📂 admin/                 # Admin pages
│   │   ├── 📂 auth/                  # Login & Register
│   │   ├── 📂 order/                 # Order pages
│   │   ├── 📂 payment/               # Payment status
│   │   └── 📂 user/                  # User pages
│   ├── 📂 static/css/                # Stylesheets
│   └── application.properties        # App configuration
│
├── 📂 uploads/products/              # Product images
│   ├── 📂 coffee/
│   ├── 📂 food/
│   └── 📂 snack/
│
├── pom.xml                           # Maven dependencies
└── README.md
```

---

## 🚀 Instalasi

### Prerequisites

Pastikan Anda sudah menginstall:

- ☕ **Java 17** atau lebih tinggi
- 📦 **Maven 3.6+**
- 🐘 **PostgreSQL 12+**
- 💳 **Akun Midtrans Sandbox** (untuk testing pembayaran)

### Langkah Instalasi

#### 1️⃣ Clone Repository

```bash
git clone https://github.com/yourusername/FoodBevApp.git
cd FoodBevApp
```

#### 2️⃣ Buat Database PostgreSQL

```sql
CREATE DATABASE foodbevapp;
```

#### 3️⃣ Konfigurasi Environment Variables

Buat file `.env` di root project:

```properties
# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/foodbevapp
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# Server Configuration
SERVER_PORT=8080
APP_BASE_URL=http://localhost:8080

# Midtrans Configuration (Sandbox)
MIDTRANS_SERVER_KEY=your_midtrans_server_key
MIDTRANS_CLIENT_KEY=your_midtrans_client_key
```

#### 4️⃣ Build Project

```bash
# Windows
.\mvnw.cmd clean install

# Linux/Mac
./mvnw clean install
```

#### 5️⃣ Jalankan Aplikasi

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

#### 6️⃣ Akses Aplikasi

Buka browser dan navigasi ke:

| URL | Deskripsi |
|-----|-----------|
| `http://localhost:8080` | Home Page |
| `http://localhost:8080/auth/login` | Login Page |
| `http://localhost:8080/admin/dashboard` | Admin Dashboard |

---

## ⚙️ Konfigurasi

### Application Properties

| Property | Deskripsi | Default |
|----------|-----------|---------|
| `server.port` | Port server | 8080 |
| `spring.datasource.url` | Database URL | - |
| `app.upload.dir` | Direktori upload | uploads |
| `spring.servlet.multipart.max-file-size` | Max upload size | 5MB |
| `midtrans.is-production` | Mode Midtrans | false |

### Midtrans Setup

1. Daftar di [Midtrans Sandbox](https://dashboard.sandbox.midtrans.com/)
2. Ambil **Server Key** dan **Client Key** dari dashboard
3. Tambahkan ke file `.env`
4. Set callback URL di dashboard Midtrans: `http://your-domain/payment/notification`

---

## 📖 Dokumentasi API

### Authentication Endpoints

| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| `GET` | `/auth/login` | Halaman login |
| `GET` | `/auth/register` | Halaman registrasi |
| `POST` | `/auth/login` | Proses login |
| `POST` | `/auth/register` | Proses registrasi |
| `POST` | `/auth/logout` | Logout |

### User Endpoints

| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| `GET` | `/user/dashboard` | Dashboard user |
| `GET` | `/user/cart` | Halaman keranjang |
| `POST` | `/user/cart/add` | Tambah ke keranjang |
| `POST` | `/user/cart/update` | Update quantity |
| `POST` | `/user/cart/remove` | Hapus item |

### Order Endpoints

| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| `GET` | `/user/order/detail` | Detail pesanan |
| `POST` | `/user/order/place` | Place order |
| `GET` | `/user/order/confirmation/{id}` | Konfirmasi pesanan |
| `GET` | `/user/order/history` | Riwayat pesanan |

### Payment Endpoints

| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| `GET` | `/payment/{orderId}` | Halaman pembayaran |
| `POST` | `/payment/create/{orderId}` | Buat payment |
| `POST` | `/payment/notification` | Webhook Midtrans |

### Admin Endpoints

| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| `GET` | `/admin/dashboard` | Admin dashboard |
| `GET` | `/admin/products` | Daftar produk |
| `GET` | `/admin/products/add` | Form tambah produk |
| `POST` | `/admin/products/add/food` | Tambah food |
| `POST` | `/admin/products/add/coffee` | Tambah coffee |
| `POST` | `/admin/products/add/snack` | Tambah snack |
| `GET` | `/admin/revenue` | Laporan revenue |
| `GET` | `/admin/order-view` | Daftar semua order |

---

## 🔐 Keamanan

Aplikasi ini mengimplementasikan berbagai fitur keamanan:

- ✅ **Password Encryption** - BCrypt hashing
- ✅ **Role-Based Access Control** - Admin & User roles
- ✅ **CSRF Protection** - Cookie-based CSRF tokens
- ✅ **Session Management** - Secure session handling
- ✅ **Input Validation** - Server-side validation

### Default Accounts

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@foodbev.com | *(set via .env)* |
| User | user@foodbev.com | *(set via .env)* |

> ⚠️ **Penting**: Ubah password default sebelum deploy ke production!

---

## 📸 Screenshots

<details>
<summary>📱 Klik untuk melihat screenshots</summary>

### 🏠 Home Page
> Landing page dengan katalog produk

### 🛒 Shopping Cart
> Kelola item sebelum checkout

### 📋 Order Page
> Isi detail pemesanan (Dine-in/Takeaway)

### 💳 Payment Page
> Pembayaran via Midtrans Snap

### 📊 Admin Dashboard
> Overview bisnis dan top selling products

### 📦 Product Management
> CRUD produk dengan filter dan pagination

</details>

---

## 🧪 Testing

Jalankan unit tests:

```bash
# Windows
.\mvnw.cmd test

# Linux/Mac
./mvnw test
```

---

## 🤝 Kontribusi

Kontribusi sangat diterima! Ikuti langkah berikut:

1. **Fork** repository ini
2. **Clone** fork Anda
   ```bash
   git clone https://github.com/hidiyits/FoodBevApp.git
   ```
3. **Buat branch** baru
   ```bash
   git checkout -b feature/AmazingFeature
   ```
4. **Commit** perubahan
   ```bash
   git commit -m "Add: AmazingFeature"
   ```
5. **Push** ke branch
   ```bash
   git push origin feature/AmazingFeature
   ```
6. Buka **Pull Request**

### Panduan Kontribusi

- Ikuti konvensi penamaan yang ada
- Tulis unit test untuk fitur baru
- Update dokumentasi jika diperlukan
- Gunakan commit message yang deskriptif

---




