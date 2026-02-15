# Performance Vergleich: gRPC vs REST

Dieses Projekt vergleicht die Performance von gRPC und REST APIs in verschiedenen Implementierungen (Spring Boot/Java und Node.js/Express).

## 📋 Übersicht

Das Projekt enthält:
- **REST APIs**: Spring Boot (Java) und Express (Node.js)
- **gRPC Services**: Spring Boot (Java) und Node.js
- **Lasttests**: K6-Skripte für verschiedene Payload-Größen
- **Test-Payloads**: Small (100B), Medium (50KB), Large (2MB)

## 🏗️ Projektstruktur

```
performance-grpc-rest/
├── rest-spring-boot/     # REST API (Spring Boot)
├── grpc-spring-boot/     # gRPC Service (Spring Boot)
├── rest-node/            # REST API (Node.js/Express)
├── grpc-node/            # gRPC Service (Node.js)
├── k6/                   # K6 Lasttest-Skripte
│   ├── rest_small.js
│   ├── rest_medium.js
│   ├── rest_large.js
│   ├── grpc_small.js
│   ├── grpc_medium.js
│   └── run_k6.sh        # Hauptskript zum Ausführen der Tests
├── payloads/             # Test-Daten
│   ├── small_100b.json
│   ├── medium_50kb.json
│   └── large_2mb.png
├── results/              # Testergebnisse (JSON)
└── docker-compose.yml    # Docker-Konfiguration
```

## 🚀 Schnellstart

### Voraussetzungen

**Für lokale Ausführung:**
- Node.js (v16+)
- Java 17+
- Maven 3.6+
- K6 Lasttesting Tool

**Für Docker-Ausführung:**
- Docker
- Docker Compose
- K6 Lasttesting Tool (auf dem Host-System)

### K6 Installation

**macOS:**
```bash
brew install k6
```

**Linux:**
```bash
# Debian/Ubuntu
sudo gpg -k
sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
sudo apt-get update
sudo apt-get install k6
```

**Windows:**
```powershell
choco install k6
```

Weitere Installationsoptionen: https://k6.io/docs/get-started/installation/

## 🐳 Ausführung mit Docker

### 1. Services starten

Alle Services gleichzeitig starten:

```bash
docker-compose up --build
```

Oder einzelne Services starten:

```bash
# Nur REST Spring Boot
docker-compose up rest-spring-boot

# Nur gRPC Node
docker-compose up grpc-node

# Mehrere Services
docker-compose up rest-spring-boot grpc-node
```

### 2. Services überprüfen

Die Services sind unter folgenden Ports erreichbar:

| Service | Port | URL |
|---------|------|-----|
| REST Spring Boot | 8080 | http://localhost:8080 |
| REST Node.js | 8081 | http://localhost:8081 |
| gRPC Spring Boot | 9091 | localhost:9091 |
| gRPC Node.js | 9090 | localhost:9090 |

**REST Endpoints testen:**
```bash
# Spring Boot
curl http://localhost:8080/api/payload/small

# Node.js
curl http://localhost:8081/api/payload/small
```

**gRPC Services testen (mit grpcurl):**
```bash
# Installation von grpcurl
brew install grpcurl  # macOS
# oder
go install github.com/fullstorydev/grpcurl/cmd/grpcurl@latest

# Test
grpcurl -plaintext localhost:9090 list
grpcurl -plaintext localhost:9090 bench.payload.PayloadService/GetSmall
```

### 3. Lasttests ausführen

Das `run_k6.sh` Skript führt die Tests gegen die laufenden Docker-Container aus:

```bash
# REST Tests gegen Spring Boot (Java)
./k6/run_k6.sh rest java

# REST Tests gegen Node.js
./k6/run_k6.sh rest node

# gRPC Tests gegen Spring Boot (Java)
./k6/run_k6.sh grpc java

# gRPC Tests gegen Node.js
./k6/run_k6.sh grpc node
```

### 4. Einzelne Tests ausführen

Einzelne K6-Skripte können auch direkt ausgeführt werden:

```bash
# REST Small Payload Test
k6 run -e BASE_URL="http://localhost:8080" k6/rest_small.js

# gRPC Medium Payload Test
k6 run -e GRPC_ADDR="localhost:9090" k6/grpc_medium.js
```

### 5. Services stoppen

```bash
# Stoppen
docker-compose down

# Stoppen und Volumes entfernen
docker-compose down -v

# Stoppen und Images entfernen
docker-compose down --rmi all
```

## 💻 Lokale Ausführung (ohne Docker)

### REST Node.js Service

```bash
cd rest-node
npm install
npm start
```

Der Service läuft auf Port 3000.

### gRPC Node.js Service

```bash
cd grpc-node
npm install
npm start
```

Der Service läuft auf Port 9090.

### REST Spring Boot Service

```bash
cd rest-spring-boot
./mvnw clean install
./mvnw spring-boot:run
```

Der Service läuft auf Port 8080.

### gRPC Spring Boot Service

```bash
cd grpc-spring-boot
./mvnw clean install
./mvnw spring-boot:run
```

Der Service läuft auf Port 9090.

### Lasttests lokal ausführen

Nach dem Start der Services können die Tests wie oben beschrieben ausgeführt werden:

```bash
# Für lokale Node.js Services
./k6/run_k6.sh rest node
./k6/run_k6.sh grpc node

# Für lokale Java Services
./k6/run_k6.sh rest java
./k6/run_k6.sh grpc java
```

## 📊 Testszenarien

Alle Tests verwenden das gleiche Ramping-Muster:

1. **Warm-up**: 0 → 50 VUs über 20s
2. **Ramp-up 1**: 50 → 200 VUs über 20s
3. **Ramp-up 2**: 200 → 500 VUs über 20s
4. **Peak**: 500 → 1000 VUs über 20s
5. **Ramp-down**: 1000 → 0 VUs über 20s

**Gesamtdauer pro Test**: ~100 Sekunden

### Payload-Größen

- **Small**: ~100 Bytes JSON
- **Medium**: ~50 KB JSON
- **Large**: ~2 MB PNG Bild (Base64 encodiert in JSON Response)

## 📈 Testergebnisse

Die Testergebnisse werden automatisch im `results/` Verzeichnis gespeichert:

```
results/
├── rest_java_small_2025-12-31_12-00-16.json
├── rest_java_medium_2025-12-31_12-00-16.json
├── rest_java_large_2025-12-31_12-00-16.json
├── grpc_node_small_2025-12-31_12-00-30.json
└── ...
```

### Ergebnisse analysieren

Die JSON-Dateien enthalten detaillierte Metriken:
- Anzahl der Requests
- Response Times (min, max, avg, p90, p95, p99)
- Durchsatz (requests/sec)
- Fehlerrate
- Datenübertragung

**Beispiel-Analyse mit jq:**
```bash
# Durchschnittliche Response Time anzeigen
cat results/rest_java_small_*.json | jq '.metrics.http_req_duration.avg'

# Requests pro Sekunde
cat results/rest_java_small_*.json | jq '.metrics.http_reqs.rate'

# p95 Response Time
cat results/rest_java_small_*.json | jq '.metrics.http_req_duration.p95'
```

## 🔧 Konfiguration

### Test-Parameter anpassen

Die K6-Testskripte können über Umgebungsvariablen angepasst werden:

```bash
# Custom Base URL für REST
k6 run -e BASE_URL="http://example.com:8080" k6/rest_small.js

# Custom gRPC Address
k6 run -e GRPC_ADDR="example.com:9090" k6/grpc_small.js

# Custom Endpoint Path
k6 run -e PATH_SMALL="/custom/path" k6/rest_small.js
```

### Docker Resource Limits

Um realistische Performance-Tests durchzuführen, können Resource Limits gesetzt werden:

```yaml
# In docker-compose.yml
services:
  rest-spring-boot:
    # ... andere Konfiguration
    deploy:
      resources:
        limits:
          cpus: '2'
          memory: 2G
        reservations:
          cpus: '1'
          memory: 1G
```

## 🐛 Troubleshooting

### Port bereits in Verwendung

```bash
# Prüfen, welcher Prozess Port 8080 verwendet
lsof -i :8080

# Prozess beenden
kill -9 <PID>
```

### K6 findet Services nicht

Stelle sicher, dass die Services laufen:
```bash
docker-compose ps

# Oder für lokale Services
curl http://localhost:8080/api/payload/small
```

### Docker Build Fehler

Cache leeren und neu bauen:
```bash
docker-compose build --no-cache
docker-compose up
```

### gRPC Connection Refused

Prüfe, ob der gRPC-Service läuft und auf dem richtigen Port horcht:
```bash
# Mit grpcurl testen
grpcurl -plaintext localhost:9090 list

# Logs prüfen
docker-compose logs grpc-node
```

## 📝 Weitere Informationen

### API Endpoints (REST)

**Spring Boot (Port 8080) & Node.js (Port 8081):**

- `GET /api/payload/small` - Liefert kleine JSON-Daten (~100B)
- `GET /api/payload/medium` - Liefert mittlere JSON-Daten (~50KB)
- `GET /api/payload/large` - Liefert große Daten mit Base64-encodiertem Bild (~2MB)

### gRPC Services

**Service**: `bench.payload.PayloadService`

**Methods**:
- `GetSmall()` - Liefert kleine Daten
- `GetMedium()` - Liefert mittlere Daten
- `GetLarge()` - Liefert große Daten
