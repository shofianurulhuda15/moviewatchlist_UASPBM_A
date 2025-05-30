# MovieWatchlist

Selamat datang di repositori resmi **MovieWatchlist**!  
MovieWatchlist adalah aplikasi mobile berbasis Android yang memungkinkan pengguna untuk melacak daftar film yang ingin ditonton atau telah ditonton. Dibangun sebagai proyek UAS Pemrograman Berbasis Mobile, aplikasi ini menggunakan **Java** dan **Supabase** untuk menyediakan pengalaman pengelolaan watchlist yang efisien dengan antarmuka modern bertema hitam dan merah.

---

## 🎥 Tentang Aplikasi

**MovieWatchlist** adalah platform digital untuk penggemar film, dengan fitur berikut:
- Menambahkan, mengedit, dan menghapus film dari watchlist.
- Melihat detail film di halaman terpisah.
- Menampilkan daftar film dengan poster (atau placeholder jika poster tidak tersedia).
- Mengelola data film secara aman menggunakan Supabase sebagai backend.

Aplikasi ini menawarkan desain responsif dan intuitif, cocok untuk pengguna yang ingin mengatur koleksi film mereka dengan mudah.

---

## 🛠️ Tech Stack

- **Bahasa Pemrograman**: Java
- **UI & Layout**: XML, AndroidX, Material Design Components
- **Database & Backend**: Supabase (REST API dan autentikasi)
- **API Client**: Retrofit, Gson
- **Image Loading**: Glide
- **Navigasi**: Fragment, DialogFragment, Activity
- **Desain**: Tema kustom dengan warna hitam (#121212) dan merah (#B71C1C)

---

## ✨ Fitur

### 📋 Fitur Utama
- **Daftar Film**: Tampilan daftar film dalam RecyclerView dengan kartu yang menampilkan poster (atau placeholder jika tidak ada URL), judul, genre, status (Watched/Want to Watch), dan rating.
- **Detail Film**: Lihat detail lengkap film (judul, genre, deskripsi, status, rating, poster) di `MovieDetailActivity`.
- **Tambah Film**: Form dialog (`AddMovieFragment`) untuk menambahkan film baru dengan input untuk judul, genre, deskripsi, status, rating, dan poster URL opsional.
- **Edit Film**: Ubah detail film melalui dialog (`EditMovieFragment`) dengan validasi input.
- **Hapus Film**: Hapus film dari watchlist dengan tombol hapus pada kartu film.
- **Profile Pengguna**: Lihat email pengguna dan logout dari `ProfileFragment`.
- **Autentikasi**: Login aman menggunakan Supabase Auth untuk akses data pribadi.

### 🎨 Desain
- Tema gelap dengan palet warna hitam (#121212) dan aksen merah (#B71C1C).
- Komponen UI modern seperti `CardView`, `RatingBar`, `Spinner`, dan `ConstraintLayout`.
- Dialog responsif untuk menambah/mengedit film.
- Poster film menggunakan Glide, dengan placeholder (`ic_movie_placeholder`) jika `poster_url` kosong.

---

## ⚙️ Prasyarat

Sebelum menjalankan proyek, pastikan Anda memiliki:
- **Android Studio**: Versi terbaru (misalnya, Iguana 2023.3.1 atau lebih baru).
- **JDK**: Versi 17 atau lebih baru.
- **Android SDK**: API Level 21 (Lollipop) atau lebih tinggi.
- **Akun Supabase**: Untuk konfigurasi backend dan autentikasi.
- **Emulator Android**: Atau perangkat fisik dengan USB debugging aktif.
- **Git**: Untuk mengkloning repositori.
- **Koneksi Internet**: Untuk komunikasi dengan Supabase.

---

## 🚀 Instalasi

### 1. Kloning Repositori
```bash
git clone https://github.com/[your-username]/MovieWatchlist-UAS.git
cd MovieWatchlist-UAS
```

### 2. Buka Proyek
1. Buka Android Studio.
2. Pilih **Open an existing project**.
3. Arahkan ke folder `MovieWatchlist-UAS`.

### 3. Konfigurasi Supabase
1. Buat proyek di [Supabase](https://supabase.com).
2. Dapatkan **URL API** (e.g., `https://<your-project-id>.supabase.co/rest/v1/`) dan **Anon Key** dari **Settings > API** di dashboard Supabase.
3. Buat file `Constants.java` di `app/src/main/java/com/moviewatchlist/utils/`:
   ```java
   package com.moviewatchlist.utils;

   public class Constants {
       public static final String SUPABASE_URL = "https://<your-project-id>.supabase.co/rest/v1/";
       public static final String SUPABASE_ANON_KEY = "<your-anon-key>";
   }
   ```
   Ganti `<your-project-id>` dan `<your-anon-key>` dengan nilai dari Supabase.

4. Buat tabel `movies` di Supabase dengan skema berikut:
   ```sql
   CREATE TABLE movies (
       id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
       title TEXT NOT NULL,
       genre TEXT NOT NULL,
       description TEXT,
       status TEXT NOT NULL,
       user_id UUID REFERENCES auth.users(id),
       rating REAL DEFAULT 0,
       poster_url TEXT
   );
   ```

5. Aktifkan Row-Level Security (RLS) dan tambahkan kebijakan:
   ```sql
   -- Allow authenticated users to insert movies
   CREATE POLICY "Allow insert for authenticated users" ON public.movies
       FOR INSERT TO authenticated
       WITH CHECK (user_id = auth.uid());

   -- Allow authenticated users to select their movies
   CREATE POLICY "Allow select for authenticated users" ON public.movies
       FOR SELECT TO authenticated
       USING (user_id = auth.uid());

   -- Allow authenticated users to update their movies
   CREATE POLICY "Allow update for authenticated users" ON public.movies
       FOR UPDATE TO authenticated
       USING (user_id = auth.uid());

   -- Allow authenticated users to delete their movies
   CREATE POLICY "Allow delete for authenticated users" ON public.movies
       FOR DELETE TO authenticated
       USING (user_id = auth.uid());
   ```

### 4. Impor Dump Database (Opsional)
- File dump database (`movies_dump.sql`) tersedia di folder `database/`.
- Untuk mengimpor ke Supabase:
  ```bash
  psql -U postgres -d your_database -f database/movies_dump.sql
  ```
- Pastikan Anda memiliki akses ke database Supabase lokal atau server.

### 5. Instal Dependensi
1. Buka `app/build.gradle` dan pastikan dependensi berikut ada:
   ```gradle
   implementation 'androidx.appcompat:appcompat:1.6.1'
   implementation 'com.google.android.material:material:1.9.0'
   implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
   implementation 'androidx.recyclerview:recyclerview:1.3.2'
   implementation 'androidx.cardview:cardview:1.0.0'
   implementation 'com.github.bumptech.glide:glide:4.12.0'
   implementation 'com.squareup.retrofit2:retrofit:2.9.0'
   implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
   implementation 'com.squareup.okhttp3:okhttp:4.9.3'
   implementation 'com.squareup.okhttp3:logging-interceptor:4.9.3'
   implementation 'com.google.code.gson:gson:2.8.9'
   ```
2. Klik **Sync Project with Gradle Files** di Android Studio.

### 6. Jalankan Aplikasi
1. Hubungkan perangkat Android atau jalankan emulator (e.g., Pixel 6 API 33).
2. Klik tombol **Run** (ikon segitiga hijau) atau jalankan perintah:
   ```bash
   ./gradlew installDebug
   ```
3. Aplikasi akan memulai di `MainActivity` dengan `HomeFragment` sebagai layar utama.

---

## ▶️ Cara Penggunaan
1. **Login**: Masuk dengan kredensial Supabase melalui `LoginActivity`.
2. **Lihat Daftar Film**: Buka `HomeFragment` untuk melihat watchlist dalam RecyclerView.
3. **Tambah Film**: Klik tombol "Add" untuk membuka `AddMovieFragment`, lalu masukkan detail film (judul, genre, deskripsi, status, rating, poster URL opsional).
4. **Lihat Detail Film**: Klik kartu film untuk membuka `MovieDetailActivity` dengan detail lengkap.
5. **Edit Film**: Klik ikon edit pada kartu film untuk membuka `EditMovieFragment` dan perbarui detail.
6. **Hapus Film**: Klik ikon hapus pada kartu film untuk menghapus dari watchlist.
7. **Profile**: Buka `ProfileFragment` untuk melihat email pengguna atau logout.
8. **Navigasi**: Gunakan tombol kembali atau tutup untuk berpindah antar layar.

---

## 🛠️ Troubleshooting

- **Poster URL Tidak Tampil**:
  - **Periksa URL**: Pastikan `poster_url` di Supabase berisi URL gambar yang valid (e.g., `https://example.com/image.jpg`).
  - **Cek Glide**: Verifikasi konfigurasi Glide di `MovieAdapter`. Tambahkan logging untuk debugging:
    ```java
    Glide.with(context)
        .load(movie.getPosterUrl())
        .placeholder(R.drawable.ic_movie_placeholder)
        .error(R.drawable.ic_movie_placeholder)
        .listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Log.e("Glide", "Failed to load image: " + e.getMessage());
                return false;
            }
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        })
        .into(holder.posterImageView);
    ```
  - **Jaringan**: Pastikan perangkat terhubung ke internet.
  - **Supabase Data**: Cek tabel `movies` untuk memastikan `poster_url` terisi dengan benar.

- **Gagal Koneksi ke Supabase**:
  - Periksa `SUPABASE_URL` dan `SUPABASE_ANON_KEY` di `Constants.java`.
  - Pastikan internet aktif dan Supabase server berjalan.
  - Cek log API di Logcat (filter: `tag:AddMovieFragment` atau `tag:EditMovieFragment`).

- **Gagal Menambah Film**:
  - Verifikasi skema tabel `movies` di Supabase (termasuk kolom `poster_url` nullable).
  - Pastikan RLS policy diatur untuk `INSERT`, `SELECT`, `UPDATE`, dan `DELETE`.
  - Cek kode respons dan pesan error di Logcat (e.g., 400: schema mismatch, 401: invalid token, 403: permission denied).

- **Error Resource Linking**:
  - Pastikan semua layout XML (e.g., `fragment_add_movie.xml`) menggunakan atribut yang valid (e.g., `app:layout_constraintHorizontal_weight`).
  - Clean dan rebuild proyek: **Build > Clean Project**, lalu **Build > Rebuild Project**.

- **Masalah UI**:
  - Uji aplikasi di berbagai ukuran layar untuk memastikan responsivitas.
  - Pastikan drawable seperti `ic_movie_placeholder` ada di `res/drawable`.

---

## 🤝 Kontribusi
Kami menyambut kontribusi dari komunitas! Untuk berkontribusi:
1. Fork repositori ini.
2. Buat branch baru:
   ```bash
   git checkout -b fitur-baru
   ```
3. Lakukan perubahan dan commit:
   ```bash
   git commit -m 'Menambahkan fitur baru'
   ```
4. Push ke branch Anda:
   ```bash
   git push origin fitur-baru
   ```
5. Buat Pull Request ke repositori ini.

Silakan buka issue untuk melaporkan bug atau mengusulkan fitur baru.

---

## 📄 Lisensi
Proyek ini dilisensikan di bawah **MIT License**. Lihat file `LICENSE` untuk detail lengkap.

---

## 👨‍💻 Tim Pengembang
- **Sofi** ([sofi@email.com](mailto:sofi@email.com))
- **[Nama Anggota 2]** ([email2@email.com](mailto:email2@email.com))

---

## 📝 Catatan Pengembangan

### Isu Saat Ini
- **Poster URL Tidak Tampil**: Gambar poster tidak muncul meskipun URL diinput. Perlu debugging Glide dan validasi URL di `MovieAdapter`.
- **Placeholder**: Berfungsi dengan baik, menampilkan `ic_movie_placeholder` jika `poster_url` kosong.

### Pengembangan Selanjutnya
- **Perbaikan Poster URL**:
  - Validasi URL di `AddMovieFragment` dan `EditMovieFragment` untuk memastikan format benar (e.g., mulai dengan `http://` atau `https://`).
  - Tambahkan pratinjau gambar saat memasukkan URL di form.
- **Filter Status**:
  - Tambahkan `Spinner` atau toggle di `HomeFragment` untuk memfilter film berdasarkan status (`watched`/`want_to_watch`).
- **Pencarian**:
  - Integrasikan `SearchView` di `HomeFragment` untuk mencari film berdasarkan judul atau genre.
- **Sortir Rating**:
  - Tambahkan opsi sortir (e.g., tombol atau menu) untuk mengurutkan film berdasarkan rating (ascending/descending).
- **Fitur Tambahan**:
  - Dukungan offline untuk melihat daftar film.
  - Notifikasi untuk mengingatkan film di watchlist.
  - Integrasi API eksternal (e.g., TMDB) untuk auto-fill detail film.

---

Terima kasih telah menggunakan **MovieWatchlist**! 🎬  
Untuk pertanyaan atau dukungan, hubungi [sofi@email.com](mailto:sofi@email.com).
