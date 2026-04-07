# Lasttest-Projekt

## Voraussetzung
- Docker + Docker Compose

## Starten
```bash
docker compose up -d --build
```

## Lasttest ausführen
Beispiel: REST gegen Java-Client mit 50 VUs (k6 im Docker-Container):
```bash
bash k6/run_k6.sh rest java all docker 50
```

Weitere Beispiele:
```bash
# gRPC gegen Java
bash k6/run_k6.sh grpc java all docker 50

# REST gegen Node
bash k6/run_k6.sh rest node all docker 50

# gRPC gegen Node
bash k6/run_k6.sh grpc node all docker 50
```

## Ergebnisse
Die Ergebnisse liegen unter:
- `k6/results/*.json`

## Stoppen
```bash
docker compose down
```
