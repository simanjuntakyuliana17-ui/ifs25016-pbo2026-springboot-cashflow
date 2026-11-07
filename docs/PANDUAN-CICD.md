# Panduan CI/CD Delcom — Praktikum 4 PBO (ifs25016)

Proyek: `ifs25016-pbo2026-springboot-cashflow` (Java + Spring Boot + Vaadin)
Alur: **push ke GitHub → webhook → Jenkins build → deploy ke Delcom → web bisa diakses**

> Sumber: modul "Delcom CI/CD & Submission CI/CD". Nilai di bawah sudah disesuaikan untuk proyek ini.

---

## 0. Cek sebelum mulai

- [ ] `.\mvnw.cmd spring-boot:run` jalan di lokal dan web terbuka di http://localhost:8080
- [ ] `.\mvnw.cmd test` lulus (Jenkins biasanya menjalankan test juga)
- [ ] Nama repo dan nama job Jenkins **harus sesuai kriteria** di konten kursus *Jenkins Grading* (lihat bagian 6). Cek dulu sebelum membuat repo.
- [ ] File `.env` tidak ikut ter-push (sudah ada di `.gitignore`; yang ikut hanya `.env.example`)

## 1. GitHub

### 1.1 Buat repo dan push kode
Buat repo kosong di GitHub (tanpa README/gitignore) dengan nama `ifs25016-pbo2026-springboot-cashflow`, lalu di folder proyek:

```bash
git init
git add .
git commit -m "feat: cashflow clean architecture"
git branch -M main
git remote add origin https://github.com/<USERNAME-GITHUB>/ifs25016-pbo2026-springboot-cashflow.git
git push -u origin main
```

Branch default = `main` (cek lewat opsi Branch di GitHub). Nilai ini dipakai di Jenkins.

### 1.2 Webhook
Repo → **Settings → Webhooks → Add webhook**

| Kolom | Isi |
|---|---|
| Payload URL | `https://jenkins.delcom.org/github-webhook/` |
| Content type | `application/json` |
| SSL verification | Enable SSL verification |
| Event | Just the push event |
| Active | dicentang |

### 1.3 Personal Access Token (sekali saja, lewati jika sudah punya)
GitHub → foto profil → **Settings → Developer settings → Personal access tokens → Tokens (classic) → Generate new token (classic)**
- Note: `delcom`, Expiration: `No expiration`
- Scopes: centang `repo` dan `user`
- Klik **Generate token**, lalu **salin dan simpan token sekarang** (hanya tampil sekali)

## 2. Delcom

1. **External App → Github → Tambah**, isi *Github Auth Token* dengan token tadi, Simpan (sekali saja).
2. **Domain → Tambah**: nama domain **harus diawali** `ifs25016-pbo2026-springboot-cashflow` (batas prefix URL dari Delcom Grading, lihat bagian 7), pilih paket domain, Simpan.
3. **Website → Tambah**:
   - Domain: domain yang baru dibuat
   - Teknologi: `java-springboot`
   - Database: `none`
   - Sumber: `none` (untuk integrasi CI/CD)
   - Klik **Deploy**. Status `FAIL` di awal itu wajar.
4. **Aksi → Detail** pada website tersebut. **Salin konfigurasi deploy** (dipakai di langkah 3.4).
5. Tab **.env** pada halaman Detail: isi variabel yang dibutuhkan aplikasi, lalu Simpan. Aplikasi ini hanya memakai satu variabel:

   ```
   APP_PORT=<isi sesuai port yang tertera di konfigurasi Delcom>
   ```
   Kalau Delcom sudah menentukan port, ikuti port dari Delcom, bukan 8080.

## 3. Jenkins

Buka dari Delcom: **Redirect Website → Jenkins**.

### 3.1 Credentials
Foto profil → **Credentials → Add Credentials**
- Kind: `Username with password`
- Username: username GitHub kamu, centang *Treat username as secret*
- Password: token GitHub (bagian 1.3)
- ID: `github-<username-github-kamu>` (contoh: `github-auxtern`)

### 3.2 Item baru
Logo Jenkins → **New Item**
- Name: **sama persis dengan nama repo** → `ifs25016-pbo2026-springboot-cashflow`
- Type: **Pipeline** → OK

### 3.3 Konfigurasi item
Item → **Configure**:

| Bagian | Isi |
|---|---|
| Description | `CI/CD untuk praktikum 4 PBO terkait pengembangan aplikasi menggunakan Java SpringBoot` |
| Prepare an environment for the run | dicentang; *Properties Content* diisi konfigurasi deploy dari Delcom (2.4) |
| Triggers | `GitHub hook trigger for GITScm polling` |
| Definition | `Pipeline script from SCM` |
| SCM | `Git` |
| Repository URL | URL repo GitHub kamu |
| Credentials | credential dari 3.1 |
| Branch Specifier | `*/main` |
| Script Path | `Jenkinsfile` |
| Lightweight checkout | dicentang |

Klik **Save**.

## 4. Tambahkan Jenkinsfile (bagian ini harus kamu kerjakan sendiri)

`Jenkinsfile` **tidak dibuat di sini**: unduh dari konten kursus *Jenkins Grading* di Delcom. Jika di sana juga ada `sonar-project.properties`, pakai juga.

1. Letakkan di **root proyek**, sejajar dengan `pom.xml`.
2. Push:
   ```bash
   git add Jenkinsfile sonar-project.properties
   git commit -m "ci: add Jenkinsfile"
   git push
   ```
3. Pastikan file terlihat di repo GitHub.

## 5. Cek hasil

1. Jenkins → item repo kamu. Karena ada webhook, job baru otomatis jalan setelah push. Manual: **Build Now**.
2. Job → **Pipeline Overview** untuk melihat tiap stage.
3. Delcom → **Aksi → Akses** pada website untuk membuka aplikasinya.

## 6. Submission (Jenkins Grading)

1. Buka konten kursus **Jenkins Grading** → baca kriteria, terutama **nama job Jenkins dan nama repo**.
2. Jika nama di kriteria berbeda dari yang kamu buat, sesuaikan (rename repo dan/atau job + Repository URL di Jenkins).
3. Pastikan pipeline di 5 sudah **hijau**.
4. Klik **Sinkronisasi Hasil** → nilai akan tampil.

---

## 7. Delcom Grading — Audit Web App (penilaian akhir)

Ketentuan dari halaman Delcom Grading:

| Ketentuan | Nilai |
|---|---|
| Mode audit | Mobile & Desktop (keduanya diaudit) |
| Attempt | Tanpa batas |
| Deadline | **10 November 2026 pukul 23.59** |
| Batas prefix URL | `https://ifs25016-pbo2026-springboot-cashflow` |
| Localhost | **Tidak diizinkan** (harus URL publik dari Delcom) |

Target nilai (masing-masing 20, total 100): Lighthouse Performance, Lighthouse Accessibility, Lighthouse Best Practices, Lighthouse SEO, dan Axe Core Accessibility.

**Cara kerja:** deploy sukses → buka Delcom Grading → Kursus Saya → isi URL web app → klik **Audit Web App**.

### Yang sudah disiapkan di kode
- **SEO:** `<title>` deskriptif, meta description, `lang="id"`, `robots.txt`, favicon.
- **Accessibility / Axe:** kontras warna teks ≥ 4.5:1 (kartu dan tombol), label pada kolom pencarian, `aria-label` pada grid dan tombol hapus, landmark `main`, target sentuh ≥ 44px.
- **Mobile:** kartu dan toolbar otomatis turun baris di layar sempit.
- **Performance:** kompresi respons aktif di `application.yaml`.
- **Best Practices:** favicon tersedia (tidak ada error 404 di console).

### Syarat penting: build harus mode production
Lighthouse jauh lebih baik pada build production (tanpa dev-server Vaadin). Pastikan Jenkins membangun JAR dengan `-Pproduction`:
```
mvn clean package -Pproduction
```
Cek perintah di `Jenkinsfile`. Jika tidak ada `-Pproduction`, nilai Best Practices dan Performance bisa turun karena muncul error console dari mode development.

### Uji sendiri sebelum submit (Chrome DevTools)
1. Jalankan build production dan JAR di lokal:
   ```
   mvn clean package -Pproduction
   java -jar target\ifs25016-pbo2026-springboot-cashflow-0.0.1-SNAPSHOT.jar
   ```
2. Buka http://localhost:8080 → F12 → tab **Lighthouse** → centang Performance, Accessibility, Best practices, SEO → jalankan untuk **Mobile** lalu **Desktop** (pakai jendela Incognito agar ekstensi tidak mengganggu).
3. Untuk Axe: pasang ekstensi **axe DevTools**, atau lihat bagian Accessibility di Lighthouse.
4. Localhost hanya untuk uji lokal; submit tetap memakai URL Delcom.

## Troubleshooting

| Gejala | Kemungkinan penyebab |
|---|---|
| Push tidak memicu job | Webhook salah URL/tidak Active; atau trigger *GitHub hook* belum dicentang. Cek **Recent Deliveries** di halaman webhook (harus 200) |
| Gagal checkout | Credential salah, token tidak punya scope `repo`, atau Branch Specifier tidak `*/main` |
| `Jenkinsfile not found` | File belum ter-push atau tidak di root; Script Path salah |
| Build gagal di tahap test | Jalankan `.\mvnw.cmd test` di lokal dulu dan perbaiki |
| Build gagal di tahap Vaadin/frontend (npm, node) | Build jar harus pakai profile `production` (`-Pproduction`); lihat perintah di Jenkinsfile |
| Web tidak terbuka / port salah | `APP_PORT` di tab .env Delcom tidak sesuai konfigurasi deploy |
| Status website tetap `FAIL` | Lihat log job Jenkins terakhir; deploy hanya berhasil setelah pipeline sukses |
