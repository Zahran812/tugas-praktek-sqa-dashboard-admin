# Tasklist Automation Testing & Handoff Engineer 2 (HADIR Web)

Dokumen ini merangkum seluruh pekerjaan automation testing Selenium Java (POM + TestNG) yang **sudah selesai (DONE)** serta **tasklist sisa untuk Engineer 2** pada modul **Import (18 skenario)** dan **Management (215 skenario)** sesuai dokumen acuan [DOKUMEN_SIT_MAGANG_DIKAHADIR_FIX.xlsx](file:///c:/Users/M%20Zahran%20A/Documents/SQA%20Bootcamp/day31/praktek-sqa/DOKUMEN_SIT_MAGANG_DIKAHADIR_FIX.xlsx).

---

## 1. Status Pekerjaan yang Sudah DONE (Engineer 1)

### A. Fondasi Proyek & Komponen Reusable
| Komponen | File | Status | Keterangan |
| :--- | :--- | :---: | :--- |
| **Driver & Config** | `DriverFactory.java`, `ConfigurationManager.java`, `config.properties` | **DONE** | ThreadLocal Chrome/Edge/Firefox, auto download dir (`target/downloads`), 18 URL rute Dashboard & Laporan. |
| **Base Page & Wait** | `BasePage.java`, `WaitUtils.java` | **DONE** | Wrapper klik/input/JS click/scroll/explicit wait untuk Next.js & MUI. |
| **Auth Reuse** | `BaseTest.java`, `BaseAuthenticatedTest.java` | **DONE** | `BaseAuthenticatedTest` memakai `@BeforeClass`/`@AfterClass` untuk *session reuse* per kelas test agar tidak terkena *rate-limit* login. |
| **Report Abstraction** | `BaseReportPage.java`, `GenericReportPage.java` | **DONE** | Abstraksi *reusable* untuk seluruh halaman laporan bertabel (search, datepicker, filter modal, pagination, header check, XSS guard). |

### B. Modul yang Sudah Diotomasi & Hasil Verifikasi
| Modul | Cakupan Skenario SIT FIX | File Test | Status Verifikasi |
| :--- | :--- | :--- | :--- |
| **Autentikasi** (`AUT`) | Login valid, password salah, validasi field kosong | `LoginTest.java` | **DONE — 3/3 Passed** |
| **Dashboard Menu** (`DSH`, `PND`) | **11 skenario** (`TC-ADM-DSH-001..003`, `TC-ADM-DSH-PND-001..008`) | `DashboardTest.java` | **DONE — 10/10 Test Methods Passed** |
| **Laporan Core & Spesifik** (`ALL`, `ACT`, `IZN`, `SKT`, `LMB`, `DWN`, `DW2`) | **26 skenario** (Semua, Kehadiran + Peta + XSS, Izin 5 kolom, Sakit + SKD preview, Lembur + Export Excel, Download Absen & V2) | `LaporanTest.java` | **DONE — 13/14 Passed** pada run penuh + **1/1 Passed** pada re-run `SKT` setelah perbaikan |
| **Laporan 9 Sub-Menu Standar** (`LAT`, `ERL`, `CUT`, `VAL`, `SHF`, `MTG`, `DNS`, `CRK`, `OFF`) | **20 skenario** (via `@DataProvider` + *negative cases* & validasi header spesifik) | `LaporanSubmenusTest.java` | **IMPLEMENTED (23 test methods)** — Catatan: run terakhir 23-test sempat terhenti karena restart server; silakan jalankan `mvn test "-Dtest=LaporanSubmenusTest"` untuk verifikasi final |
| **API Absen Masuk** (`example2@gmail.com`) | Eksplorasi & eksekusi langsung `POST /api/login` + `POST /api/activity/request-activity` | `scratch/ApiAbsenMasukRunner.java` | **DONE — 200 OK** (terverifikasi via `GET /api/activity/user-last-activity`) |

---

## 2. Tasklist Sisa untuk Engineer 2 — Modul Import (4 Sub-Menu | 18 Skenario)

> **Rekomendasi Arsitektur:** Buat `BaseImportPage.java` (untuk upload file `.xlsx/.csv`, download template, tombol Import/Submit, dan tabel/toast hasil validasi) + `ImportTest.java extends BaseAuthenticatedTest`.

| No | Sub-Menu | Rute URL Aktual | Total Kasus | Positive | Negative | Rentang ID SIT FIX | Checklist |
| :---: | :--- | :--- | :---: | :---: | :---: | :--- | :---: |
| 1 | **Import Absen** | `/imports/import-absen-hadir` | **3** | 2 | 1 | `TC-ADM-IMP-ABS-001` .. `003` | [ ] |
| 2 | **Import Status Aktif** | `/imports/import-status-aktif` | **4** | 2 | 2 | `TC-ADM-IMP-STS-001` .. `004` | [ ] |
| 3 | **Import Struktur User** | `/imports/import-struktur-users` | **5** | 3 | 2 | `TC-ADM-IMP-STR-001` .. `005` | [ ] |
| 4 | **Import Cuti** | `/imports/import-absen-cuti` | **6** | 3 | 3 | `TC-ADM-IMP-CUT-001` .. `006` | [ ] |
| | **TOTAL MODUL IMPORT** | **4 Sub-Menu** | **18** | **10** | **8** | | |

**Poin Kunci Pengujian Modul Import:**
- Siapkan file dummy uji di `src/test/resources/testdata/` (file Excel valid sesuai template, file format salah misal `.txt`/`.pdf`, file kosong/header rusak).
- Uji tombol **Download Template** (verifikasi file terunduh di `target/downloads`), upload file valid, dan verifikasi pesan error pada skenario negatif.

---

## 3. Tasklist Sisa untuk Engineer 2 — Modul Management (18 Sub-Menu | 215 Skenario)

> **Rekomendasi Arsitektur:** Sebagian besar halaman Management memiliki pola **CRUD + Search + Pagination + Modal Form**. Buat `BaseManagementPage.java` (abstraksi tabel, tombol Tambah/Edit/Hapus, dialog konfirmasi, search, dan toast) agar tidak menulis ulang kode berulang. Seluruh kelas test wajib `extends BaseAuthenticatedTest`.

### Batch A — User & Registrasi (3 Sub-Menu | 35 Skenario)
| No | Sub-Menu | Rute URL | Total | Pos | Neg | Rentang ID SIT FIX | Checklist |
| :---: | :--- | :--- | :---: | :---: | :---: | :--- | :---: |
| 1 | **User** | `/management/user` | **13** | 12 | 1 | `TC-ADM-MGT-USR-001` .. `013` | [ ] |
| 2 | **Pendaftaran User** | `/management/user-v2/register` | **11** | 3 | 8 | `TC-ADM-MGT-REG-001` .. `011` | [ ] |
| 3 | **Pendaftaran Sendiri** | `/management/self-registration` | **11** | 9 | 2 | `TC-ADM-MGT-SLF-001` .. `011` | [ ] |

### Batch B — Struktur Organisasi & Lokasi (7 Sub-Menu | 74 Skenario)
| No | Sub-Menu | Rute URL | Total | Pos | Neg | Rentang ID SIT FIX | Checklist |
| :---: | :--- | :--- | :---: | :---: | :---: | :--- | :---: |
| 4 | **Divisi** | `/management/divisi` | **10** | 7 | 3 | `TC-ADM-MGT-DIV-001` .. `010` | [ ] |
| 5 | **Unit** | `/management/departemens` | **9** | 7 | 2 | `TC-ADM-MGT-UNT-001` .. `009` | [ ] |
| 6 | **Unit Setting** | `/management/departements-settings` | **7** | 6 | 1 | `TC-ADM-MGT-UST-001` .. `007` | [ ] |
| 7 | **Posisi** | `/management/position` | **10** | 7 | 3 | `TC-ADM-MGT-POS-001` .. `010` | [ ] |
| 8 | **Client Upliner** | `/management/client-upliner` | **12** | 9 | 3 | `TC-ADM-MGT-CUP-001` .. `012` | [ ] |
| 9 | **Absen Point** | `/management/location-point` | **13** | 11 | 2 | `TC-ADM-MGT-APT-001` .. `013` | [ ] |
| 10 | **Jabatan** | `/management/job-level` | **13** | 11 | 2 | `TC-ADM-MGT-JAB-001` .. `013` | [ ] |

### Batch C — Jadwal, Cuti, Monitoring & Pengaturan Lainnya (8 Sub-Menu | 106 Skenario)
| No | Sub-Menu | Rute URL | Total | Pos | Neg | Rentang ID SIT FIX | Checklist |
| :---: | :--- | :--- | :---: | :---: | :---: | :--- | :---: |
| 11 | **Shifting** | `/management/shifting` *(cek aktual sidebar)* | **13** | 11 | 2 | `TC-ADM-MGT-SHF-001` .. `013` | [ ] |
| 12 | **Jadwal** | `/management/schedule` | **15** | 13 | 2 | `TC-ADM-MGT-JDW-001` .. `015` | [ ] |
| 13 | **Kalender** | `/management/calendar` | **13** | 11 | 2 | `TC-ADM-MGT-KAL-001` .. `013` | [ ] |
| 14 | **Aturan Cuti** | `/management/unit-leave` | **15** | 13 | 2 | `TC-ADM-MGT-CUT-001` .. `015` | [ ] |
| 15 | **Day Off** | `/management/day-off` | **5** | 5 | 0 | `TC-ADM-MGT-DOF-001` .. `005` | [ ] |
| 16 | **Monthly Schedule** | `/management/monthly-schedule` | **15** | 13 | 2 | `TC-ADM-MGT-MSC-001` .. `015` | [ ] |
| 17 | **User Monitoring** | *(cek rute aktual di sidebar)* | **15** | 12 | 3 | `TC-ADM-MGT-USM-001` .. `015` | [ ] |
| 18 | **Overtime Reason** | *(cek rute aktual di sidebar)* | **15** | 14 | 1 | `TC-ADM-MGT-OTR-001` .. `015` | [ ] |
| | **TOTAL MODUL MANAGEMENT** | **18 Sub-Menu** | **215** | **176** | **39** | | |

---

## 4. Ringkasan Beban Kerja & Panduan Eksekusi

| Kategori | Total Skenario SIT | Status |
| :--- | :---: | :--- |
| **Autentikasi + Dashboard + Laporan** | **67** | **DONE** (tinggal verifikasi ulang `LaporanSubmenusTest`) |
| **Import** | **18** | **TODO — Engineer 2** |
| **Management** | **215** | **TODO — Engineer 2** |
| **Total Keseluruhan (`SIT - Dashboard Admin`)** | **300** | |

### Langkah Awal untuk Engineer 2:
1. Baca [PANDUAN_AUTOMATION_TESTING_DAN_REUSE_LOGIN.md](file:///c:/Users/M%20Zahran%20A/Documents/SQA%20Bootcamp/day31/praktek-sqa/PANDUAN_AUTOMATION_TESTING_DAN_REUSE_LOGIN.md).
2. Jalankan verifikasi final `LaporanSubmenusTest`:
   ```powershell
   mvn test "-Dtest=LaporanSubmenusTest"
   ```
3. Tambahkan URL modul **Import** & **Management** ke `src/main/resources/config.properties` dan `ConfigurationManager.java`.
4. Gunakan `extends BaseAuthenticatedTest` pada seluruh kelas test baru agar langsung mewarisi sesi login otomatis.
5. Gunakan data uji ber-prefix unik (misal `AutoTest_<timestamp>`) pada skenario Create/Edit/Delete Management dan lakukan pembersihan data (*cleanup*) jika diperlukan.
