# Mousetrap DNS-01 PowerShell Samples

PowerShell-Skripte zur Integration von l9g-mousetrap in Windows-ACME-Clients.

## Verzeichnisstruktur

```
.
├── Mousetrap-DnsHook.ps1   # DNS-Hook: TXT-Record per Mousetrap API hinzufügen/entfernen
├── FULLTEST_WACS.ps1       # Volltest mit Win-ACME (wacs.exe)
└── dot.env.sample          # Beispiel-Konfiguration
```

## Konfiguration

```powershell
Copy-Item dot.env.sample .env
# .env anpassen
```

| Variable             | Beschreibung                                          |
|----------------------|-------------------------------------------------------|
| `MOUSETRAP_API_URL`  | URL des Mousetrap-Service                             |
| `MOUSETRAP_TOKEN`    | Bearer Token (base64-kodiert)                         |
| `MOUSETRAP_ZONE`     | DNS-Zone mit abschließendem Punkt, z.B. `example.de.` |
| `APP_DOMAIN`         | Domain für das Zertifikat                             |

## Mousetrap-DnsHook.ps1

DNS-Hook-Skript für Windows-ACME-Clients. Implementiert `Add` und `Remove` von `_acme-challenge`-TXT-Records über die Mousetrap API.

**Voraussetzungen:** PowerShell 5.1+ (PowerShell 7 empfohlen), Netzwerkzugriff auf die Mousetrap-API.

### Direktaufruf

```powershell
# Record hinzufügen
.\Mousetrap-DnsHook.ps1 `
    -Action Add `
    -FullDomain "_acme-challenge.test.example.de" `
    -TxtValue "mein-challenge-token" `
    -ApiUrl "http://mousetrap-server:8080/api/v1/micetro" `
    -Token "DEIN_MOUSETRAP_TOKEN" `
    -Zone "example.de."

# Record entfernen
.\Mousetrap-DnsHook.ps1 `
    -Action Remove `
    -FullDomain "_acme-challenge.test.example.de" `
    -TxtValue "mein-challenge-token" `
    -ApiUrl "http://mousetrap-server:8080/api/v1/micetro" `
    -Token "DEIN_MOUSETRAP_TOKEN" `
    -Zone "example.de."
```

## FULLTEST_WACS.ps1

Volltest-Skript für [Win-ACME (wacs.exe)](https://github.com/win-acme/win-acme). Liest die Konfiguration aus `.env` und ruft `wacs.exe` mit den passenden `--dnscreatescript`/`--dnsdeletescript`-Argumenten auf.

**Voraussetzung:** `wacs.exe` muss im `PATH` verfügbar sein.

```powershell
.\FULLTEST_WACS.ps1
```

### Manuelle Win-ACME-Integration

```
wacs.exe --test `
  --validationmode dns-01 --validation script `
  --dnscreatescript "C:\Pfad\zu\Mousetrap-DnsHook.ps1" `
  --dnscreatescriptarguments "-Action Add -FullDomain {RecordName} -TxtValue {Token} -ApiUrl http://mousetrap:8080/api/v1/micetro -Token DEIN_TOKEN -Zone example.de." `
  --dnsdeletescript "C:\Pfad\zu\Mousetrap-DnsHook.ps1" `
  --dnsdeletescriptarguments "-Action Remove -FullDomain {RecordName} -TxtValue {Token} -ApiUrl http://mousetrap:8080/api/v1/micetro -Token DEIN_TOKEN -Zone example.de."
```

Das Skript funktioniert analog auch mit **Certify The Web** und **Posh-ACME** als Script-Hook.
