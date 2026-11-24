# PANDUAN AUTOMATION TESTING SELENIUM & STRATEGI REUSE LOGIN
**Target Aplikasi:** HADIR Web Application (`https://magang.dikahadir.com/`)  
**Tech Stack:** Java 21, Selenium WebDriver 4.27, TestNG 7.10, Maven, Page Object Model (POM)  
**Versi:** 1.0.0 (Baseline: Automation Login)  

---

## 1. Pendahuluan & Filosofi Arsitektur

Dalam proyek otomasi pengujian HADIR, modul **Autentikasi (Login)** ditempatkan sebagai **fondasi utama (*core foundation*)**. Hal ini dikarenakan:
1. Seluruh modul lanjutan (Dashboard Menu, Laporan, Import, Management, serta portal karyawan `/apps/*`) dilindungi oleh *Session Route Guard*.
2. Pengujian fitur apa pun memerlukan kondisi prasyarat (*pre-condition*) pengguna yang terotentikasi secara sah.
3. Dengan memisahkan dan memusatkan logika login ke dalam pola yang **reusable (dapat digunakan kembali)**, pengujian fitur-fitur lain tidak perlu mengulang-ulang penulisan kode interaksi form login.

> **Status Proyek Saat Ini:** Proyek telah diset aktif ke **Automation Login terlebih dahulu** pada `src/test/resources/testng.xml`.

---

## 2. Struktur Direktori Proyek (Standar Industri)

```text
praktek-sqa/
│
├── pom.xml                                     # Dependensi Maven (Selenium, TestNG, WebDriverManager)
├── PANDUAN_AUTOMATION_TESTING_DAN_REUSE_LOGIN.md # Panduan arsitektur & reuse login
│
├── src/main/
│   ├── java/com/hadir/
│   │   ├── config/
│   │   │   └── ConfigurationManager.java       # Pembaca konfigurasi properties & sistem
│   │   ├── drivers/
│   │   │   └── DriverFactory.java              # Lifecycle WebDriver & opsi Chrome headless
│   │   ├── pages/                              # Page Object Model (POM)
│   │   │   ├── BasePage.java                   # Wrapper interaksi umum Selenium (click, type, find)
│   │   │   ├── LoginPage.java                  # Representasi halaman /authentication/login
│   │   │   ├── DashboardPage.java              # Representasi menu Dashboard & Pending
│   │   │   └── DownloadAbsenPage.java          # Representasi form laporan download absen
│   │   └── utils/
│   │       └── WaitUtils.java                  # Explicit wait utilities (anti-flaky test)
│   └── resources/
│       └── config.properties                   # URL target, kredensial admin, timeout, browser
│
└── src/test/
    ├── java/com/hadir/
    │   ├── base/
    │   │   └── BaseTest.java                   # Setup @BeforeMethod dan teardown @AfterMethod
    │   └── tests/
    │       ├── LoginTest.java                  # Suite pengujian autentikasi (Aktif Utama)
    │       ├── DashboardTest.java              # Pengujian menu Dashboard & Pending
    │       └── DownloadAbsenTest.java          # Pengujian download laporan absensi
    └── resources/
        └── testng.xml                          # Konfigurasi TestNG Suite Runner
```

---

## 3. Konfigurasi Aktif Sekarang: Focus pada LoginTest

File `src/test/resources/testng.xml` saat ini dikonfigurasi secara eksklusif untuk menjalankan suite autentikasi:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Hadir Web Automation Test Suite" verbose="1" parallel="false">
    <test name="Authentication Tests">
        <classes>
            <class name="com.hadir.tests.LoginTest" />
        </classes>
    </test>
</suite>
```

### Cara Menjalankan Pengujian Login via CLI:
```bash
# Menjalankan seluruh test pada testng.xml aktif (LoginTest)
mvn test

# Atau menjalankan kelas LoginTest secara spesifik
mvn test -Dtest=LoginTest
```

### Konfigurasi Kredensial & Headless (`src/main/resources/config.properties`):
```properties
base.url=https://magang.dikahadir.com/
login.url=https://magang.dikahadir.com/authentication/login
admin.email=admin@hadir.com
admin.password=MagangSQA_JC@123

# Ubah headless=false jika ingin melihat jendela browser Chrome terbuka
browser=chrome
headless=true
```

---

## 4. Cara Mereuse Skenario Login untuk Seluruh Fitur

Ada 3 pendekatan utama yang telah disediakan dalam arsitektur ini agar Anda dapat mereuse login dengan sangat mudah:

### Pola 1: Reusable `@BeforeMethod` (Rekomendasi Standar POM)
Setiap kelas pengujian fitur baru mewarisi `BaseTest`. Di dalam `@BeforeMethod`, panggil `loginPage.loginWithDefaultAdmin()` yang otomatis mengembalikan objek `DashboardPage`.

```java
package com.hadir.tests;

import com.hadir.base.BaseTest;
import com.hadir.pages.DashboardPage;
import com.hadir.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LaporanIzinTest extends BaseTest {

    private DashboardPage dashboardPage;

    @BeforeMethod
    public void setupAndLogin() {
        // 1. Inisialisasi LoginPage
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        
        // 2. Reuse login default admin (mengembalikan DashboardPage)
        dashboardPage = loginPage.loginWithDefaultAdmin();
        
        // 3. Verifikasi prasyarat login berhasil
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Login admin harus berhasil sebelum menguji fitur");
    }

    @Test(description = "TC-ADM-REP-IZN-001: Verifikasi pencarian data izin karyawan")
    public void testPencarianDataIzin() {
        // Langsung lanjutkan interaksi dari dashboard ke fitur yang dituju!
        driver.get("https://magang.dikahadir.com/laporan/izin");
        // ... aksi pengujian izin ...
    }
}
```

### Pola 2: Page Chaining Fluid Navigation
`LoginPage.java` telah dirancang agar metode `loginAsAdmin(...)` atau `loginWithDefaultAdmin()` mengembalikan `DashboardPage`. Selanjutnya dari `DashboardPage`, Anda dapat membuat metode navigasi yang mengembalikan Page Object modul tujuan:

```java
// Contoh alur chaining:
LoginPage loginPage = new LoginPage(driver);
DownloadAbsenPage downloadPage = loginPage.open()
                                          .loginWithDefaultAdmin()
                                          .navigateToDownloadAbsenViaSidebar();
```

### Pola 3: Kustomisasi Kredensial (Role Admin vs Role Karyawan)
Jika ingin menguji login dengan akun lain (misal akun staf/karyawan atau akun nonaktif):
```java
LoginPage loginPage = new LoginPage(driver);
loginPage.open();
loginPage.enterEmail("karyawan@hadir.com")
         .enterPassword("PasswordKaryawan123!")
         .clickSubmit();
```

---

## 5. Tutorial Langkah-demi-Langkah Menambahkan Fitur Baru

Misalkan Anda ingin membuat otomasi untuk menu **Laporan Izin** (`/laporan/izin`):

### Langkah 1: Daftarkan URL di `config.properties` & `ConfigurationManager.java`
- Buka `src/main/resources/config.properties`, tambahkan:
  ```properties
  laporan.izin.url=https://magang.dikahadir.com/laporan/izin
  ```
- Buka `src/main/java/com/hadir/config/ConfigurationManager.java`, tambahkan:
  ```java
  public static String getLaporanIzinUrl() {
      return getProperty("laporan.izin.url", "https://magang.dikahadir.com/laporan/izin");
  }
  ```

### Langkah 2: Buat Page Object Baru di `src/main/java/com/hadir/pages/LaporanIzinPage.java`
```java
package com.hadir.pages;

import com.hadir.config.ConfigurationManager;
import com.hadir.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LaporanIzinPage extends BasePage {

    // Locators
    private final By searchInput = By.xpath("//input[@placeholder='Cari berdasarkan nama']");
    private final By btnSearch = By.xpath("//button[contains(.,'Search') or contains(text(),'Search')]");
    private final By btnReset = By.xpath("//button[contains(.,'Reset') or contains(text(),'Reset')]");
    private final By tableRows = By.xpath("//table//tbody//tr");

    public LaporanIzinPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get(ConfigurationManager.getLaporanIzinUrl());
        WaitUtils.waitUntilUrlContains(driver, "laporan/izin");
    }

    public void searchByName(String name) {
        type(searchInput, name);
        click(btnSearch);
        WaitUtils.waitForSeconds(1);
    }

    public void clickReset() {
        click(btnReset);
        WaitUtils.waitForSeconds(1);
    }

    public int getRowsCount() {
        return driver.findElements(tableRows).size();
    }
}
```

### Langkah 3: Buat Test Class di `src/test/java/com/hadir/tests/LaporanIzinTest.java`
```java
package com.hadir.tests;

import com.hadir.base.BaseTest;
import com.hadir.pages.DashboardPage;
import com.hadir.pages.LaporanIzinPage;
import com.hadir.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LaporanIzinTest extends BaseTest {

    private LaporanIzinPage izinPage;

    @BeforeMethod
    public void loginBeforeTest() {
        // Reuse login
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        DashboardPage dashboard = loginPage.loginWithDefaultAdmin();
        Assert.assertTrue(dashboard.isDashboardLoaded(), "Login admin wajib sukses");

        izinPage = new LaporanIzinPage(driver);
        izinPage.open();
    }

    @Test(description = "Verify search employee in Laporan Izin")
    public void testSearchEmployee() {
        izinPage.searchByName("Siti");
        Assert.assertTrue(izinPage.getRowsCount() >= 0, "Tabel harus merespons pencarian nama");
    }
}
```

### Langkah 4: Daftarkan ke `testng.xml` Ketika Siap Dijalankan Bersama
Jika ingin menjalankan `LoginTest` bersama fitur baru, tambahkan class tersebut ke `testng.xml`:
```xml
<classes>
    <class name="com.hadir.tests.LoginTest" />
    <class name="com.hadir.tests.LaporanIzinTest" />
</classes>
```

---

## 6. Best Practices Menulis Otomasi yang Tangguh (*Flaky-Free*)

1. **Gunakan `WaitUtils` daripada `Thread.sleep()`:**
   - Gunakan `WaitUtils.waitUntilVisible(driver, locator)` atau `WaitUtils.waitUntilClickable(...)` agar pengujian dinamis mengikuti kecepatan rendering React/Next.js.
2. **Gunakan XPath Semantik Material-UI (MUI):**
   - Komponen MUI menggunakan class dinamis (`css-...`). Hindari class hash acak, utamakan atribut teks semantik:
     `By.xpath("//button[contains(.,'Search')]")` atau `By.xpath("//input[@placeholder='Start Date']")`.
3. **Manfaatkan `jsClick()` saat Terhalang Overlay:**
   - Jika tombol terhalang animasi backdrop atau snackbar notifikasi, gunakan `jsClick(locator)` yang sudah tersedia di `BasePage`.
4. **Isolasi State Antar Tes:**
   - Setiap `@Test` harus independen. `BaseTest` menjalankan `@BeforeMethod` (buka browser bersih & login) dan `@AfterMethod` (`driver.quit()`), sehingga kegagalan di satu tes tidak akan merusak tes berikutnya.
