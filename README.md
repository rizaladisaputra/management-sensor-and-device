## 🎯 Monitoring Device IoT untuk Pertanian
**Stack:** Java + Quarkus (Java 17, JDK 17, Hibernate ORM, Scheduler)
- Sensor suhu & kelembapan tanah
- Sensor curah hujan
- Sensor pH air
- Sensor intensitas cahaya matahari

### ⚙️ Fitur Utama

| Fitur | Deskripsi |
|-------|------------|
| **Device Management** | CRUD Device, termasuk `clientCallbackUrl` tempat data akan dikirim |
| **Sensor Management** | CRUD Sensor yang terhubung ke Device |
| **Data Delivery System** | Endpoint `/data/send/{deviceId}` untuk menerima data dari IoT device |
| **Retry Mechanism** | Scheduler Quarkus akan memproses data `PENDING` dan mencoba kirim ulang bila gagal |
| **Persistent Delivery Queue** | Semua status pengiriman (`PENDING`, `SENT`, `FAILED`) tersimpan di database |
| **Configurable Retry** | Jumlah percobaan dan interval retry diatur lewat `application.properties` |

---

### Arsitektur Sistem

- **IoT Device** mengirim data ke endpoint `/data/send/{deviceId}`
- **Backend (Quarkus)** menyimpan data ke tabel `DeliveryRecord`
- **Scheduler Worker** memproses data yang masih `PENDING` setiap 10 detik
- Jika client merespons sukses (HTTP 2xx) → status `SENT`
- Jika gagal → akan di-*retry* sampai `maxAttempts`, lalu `FAILED`

---
### Struktur Project
```
management-sensor-and-device/
├─ src/main/java/com/
│  ├─ config/
│  │  ├─ DeliveryConfig.java
│  ├─ controller/
│  │  ├─ DeviceController.java
│  │  ├─ SensorController.java
│  │  └─ DataDeliveryController.java
│  ├─ model/
│  │  ├─ Device.java
│  │  ├─ Sensor.java
│  │  └─ DeliveryRecord.java
│  ├─ service/
│  │  └─ DeliveryService.java
│  └─ Application.java
├─ src/main/resources/
│  └─ application.properties
├─ pom.xml
└─ README.md
```
### Cara Menjalankan
1. Clone repository
```
git clone https://github.com/<your-username>/quarkus-iot-backend.git
cd quarkus-iot-backend
```
2. Build project
```
./mvnw clean package
```
3. Jalankan dalam mode development
```
./mvnw quarkus:dev
```

Aplikasi akan berjalan di:
 http://localhost:8080

### API Endpoint
####  Device API 
| Method | Endpoint        | Deskripsi           |
| ------ | --------------- | ------------------- |
| GET    | `/devices`      | List semua device   |
| GET    | `/devices/{id}` | Ambil detail device |
| POST   | `/devices`      | Tambah device baru  |
| PUT    | `/devices/{id}` | Update data device  |
| DELETE | `/devices/{id}` | Hapus device        |
Contoh JSON :
```
{
  "name": "Soil Sensor A1",
  "clientCallbackUrl": "https://partner.com/api/data",
  "active": true
}
```
#### Sensor API
| Method | Endpoint        | Deskripsi         |
| ------ | --------------- | ----------------- |
| GET    | `/sensors`      | List semua sensor |
| POST   | `/sensors`      | Tambah sensor     |
| PUT    | `/sensors/{id}` | Update sensor     |
| DELETE | `/sensors/{id}` | Hapus sensor      |
Contoh JSON :
```
{
  "name": "Soil Moisture",
  "unit": "%",
  "device": { "id": 1 },
  "active": true
}
```
#### Data Monitoring API
| Method | Endpoint                | Deskripsi                                           |
| ------ | ----------------------- | --------------------------------------------------- |
| POST   | `/data/send/{deviceId}` | Simpan data monitoring & kirim ke clientCallbackUrl |
Contoh payload:
```
{
  "sensor": "humidity",
  "value": 68.4,
  "timestamp": "2025-11-03T13:10:00Z"
}
```
Response :
```
HTTP 202 Accepted
```
### Konfigurasi (application.properties)
```
quarkus.datasource.db-kind=h2
quarkus.datasource.username=sa
quarkus.datasource.password=
quarkus.datasource.jdbc.url=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1
quarkus.hibernate-orm.database.generation=update
quarkus.http.port=8080
# Delivery client timeout and settings
delivery.client-timeout-ms=5000
delivery.retry-max-attempts=5
```

### Studi Kasus Nyata

Contoh nyata penerapan sistem ini:
- IoT Sensor Pertanian mengirimkan data kelembapan tanah setiap 5 menit. 
- Server menyimpannya dan otomatis mengirim ke server dashboard milik klien. 
- Jika jaringan client sedang offline, sistem otomatis retry hingga 5x sebelum menandai data sebagai FAILED. 
- Admin bisa memonitor device & sensor dari backend dashboard.