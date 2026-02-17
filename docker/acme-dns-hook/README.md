
### .env Beispiel
```
MICETRO_API_URL="http://mousetrap:8080/api/v1/micetro"
MICETRO_TOKEN="dein-token"
```

#### Zone hier passend zur Domainstruktur definieren
```
MICETRO_ZONE="example.de" 
APP_DOMAIN="test2.qa.example.de"
```

### Run
```
docker exec acme acme.sh --issue --server letsencrypt \
  -d "$APP_DOMAIN" \
  --dns dns_mousetrap
```
