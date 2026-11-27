# Hadir Web Automation Testing (Java + Selenium + TestNG)

Projek automation testing berbasis **Java 21**, **Selenium WebDriver 4**, dan **TestNG** dengan arsitektur **Page Object Model (POM)** berstandar industri SQA.

---

## 1. Lingkup Pengujian (Scope)
Sesuai kebutuhan, proyek ini mencakup skenario pengujian utama:
1. **Skenario Login (`LoginTest.java`)**:
   - `testLoginSuccessWithValidCredentials`: Verifikasi login admin berhasil menggunakan kredensial dari file `.env` (`ADMIN_EMAIL` dan `ADMIN_PASSWORD`), diarahkan ke dashboard.
   - `testLoginFailedWithInvalidPassword`: Verifikasi penolakan login saat password salah.
   - `testLoginValidationEmptyFields`: Verifikasi validasi input saat field disubmit kosong.
2. **Skenario Menu Download Absen (`DownloadAbsenTest.java`)**:
   - `testNavigateToDownloadAbsenPage`: Verifikasi navigasi ke menu Download Absen (`/laporan/daily-report`) via sidebar menu Laporan setelah login.
   - `testFilterDateAndDownloadAbsen`: **Skenario Utama**: Login -> Masuk menu Download Absen -> Memilih filter tanggal (format `dd/mm/yyyy`) -> Menekan tombol **Download**.
   - `testResetFilterDate`: Verifikasi tombol **Reset** mengembalikan filter tanggal ke nilai default.

---

## 2. Struktur Direktori Proyek (Industry Standard Layout)

```
praktek-sqa/
│   pom.xml                                  # Konfigurasi Maven dependencies & plugins
│   testng.xml                               # Suite runner TestNG
│   README.md                                # Dokumentasi panduan eksekusi
├── src/
│   ├── main/
│   │   ├── java/com/hadir/
│   │   │   ├── config/
│   │   │   │   └── ConfigurationManager.java # Loader konfigurasi & dynamic property reader
│   │   │   ├── drivers/
│   │   │   │   └── DriverFactory.java        # ThreadLocal WebDriver management & Chrome setup
│   │   │   ├── pages/                        # Page Object Model (POM)
│   │   │   │   ├── BasePage.java             # Wrapper aksi Selenium umum & explicit wait
│   │   │   │   ├── LoginPage.java            # Page object login (/authentication/login)
│   │   │   │   ├── DashboardPage.java        # Page object dashboard & sidebar menu
│   │   │   │   └── DownloadAbsenPage.java    # Page object download absen (/laporan/daily-report)
│   │   │   └── utils/
│   │   │       └── WaitUtils.java            # Helper Explicit & Fluent Wait
│   │   └── resources/
│   │       └── config.properties             # URL target, timeout, headless mode (tanpa kredensial)
│   └── test/
│       ├── java/com/hadir/
│       │   ├── base/
│       │   │   └── BaseTest.java             # Setup (@BeforeMethod) & Teardown (@AfterMethod)
│       │   └── tests/
│       │       ├── LoginTest.java            # Test suite autentikasi login
│       │       └── DownloadAbsenTest.java    # Test suite filter & download absen
│       └── resources/
│           └── testng.xml                    # Konfigurasi test suite TestNG
```

---

## 3. Prasyarat Sistem (Prerequisites)
- **Java Development Kit (JDK)**: Versi 21 LTS (`java -version`)
- **Apache Maven**: Versi 3.9+ (`mvn -version`)
- **Google Chrome**: Versi terbaru (ChromeDriver otomatis dikelola oleh WebDriverManager & SeleniumManager bawaan Selenium 4)

---

## 4. Konfigurasi Kredensial (.env)

Kredensial login **tidak disimpan di source code maupun `config.properties`**. Nilai email dan password dibaca dari file `.env` di root project:

```bash
# Salin template, lalu isi dengan kredensial yang valid
cp .env.example .env
```

Isi `.env`:
```dotenv
ADMIN_EMAIL=email.anda@example.com
ADMIN_PASSWORD=password.anda
```

Catatan:
- File `.env` sudah masuk `.gitignore`, jadi tidak akan ter-commit.
- Alternatif tanpa file `.env`: set environment variable `ADMIN_EMAIL` dan `ADMIN_PASSWORD`.
- Urutan prioritas konfigurasi: system property (`-D`) → environment variable → `.env` → `config.properties`.

---

## 5. Cara Menjalankan Pengujian (Execution)

### A. Menjalankan Seluruh Test Suite
Jalankan perintah berikut di root folder project:
```bash
mvn clean test
```

### B. Menjalankan Kelas Pengujian Spesifik
- **Hanya Login Test**:
  ```bash
  mvn test -Dtest=LoginTest
  ```
- **Hanya Download Absen Test**:
  ```bash
  mvn test -Dtest=DownloadAbsenTest
  ```

### C. Menjalankan dengan Browser Terlihat (Non-Headless)
Secara default pengujian berjalan secara `headless=true` agar cepat dan stabil di server/CI-CD. Untuk menampilkan browser GUI saat pengujian berlangsung:
```bash
mvn test -Dheadless=false
```

---

## 6. Laporan Hasil Pengujian (Test Reports)
Setelah eksekusi selesai, laporan TestNG otomatis tersedia di:
- `target/surefire-reports/index.html` (Laporan interaktif HTML TestNG)
- `target/surefire-reports/emailable-report.html` (Ringkasan laporan ringkas)
- `target/downloads/` (Direktori penyimpanan file Excel hasil download absen)
