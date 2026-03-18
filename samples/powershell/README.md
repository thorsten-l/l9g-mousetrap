# Mousetrap DNS-01 PowerShell Samples

Dieses Verzeichnis enthält PowerShell-Beispiele für die Interaktion mit der l9g-mousetrap API.

## Mousetrap-DnsHook.ps1

Dieses Skript implementiert die Logik zum Hinzufügen und Entfernen von DNS-TXT-Einträgen für die ACME DNS-01 Challenge.

### Voraussetzungen

- PowerShell 5.1 oder höher (PowerShell 7 empfohlen)
- Netzwerkzugriff auf die Mousetrap-API

### Verwendung

Das Skript kann direkt aufgerufen werden. Es benötigt Parameter für die Aktion (Add/Remove), die Domain, den Challenge-Token und die Authentifizierung.

```powershell
# Beispiel: Hinzufügen eines Challenge-Eintrags
.\Mousetrap-DnsHook.ps1 `
    -Action Add `
    -FullDomain "_acme-challenge.test.example.com" `
    -TxtValue "DEINE_CHALLENGE_TOKEN" `
    -Token "DEIN_MOUSETRAP_TOKEN" `
    -Zone "example.com." `
    -ApiUrl "http://mousetrap-server:8080/api/v1/micetro"
```

### Integration in Tools

Dieses Skript kann als "Pre-Request" und "Post-Request" Hook in Zertifikats-Management-Tools wie **WACS**, **Certify The Web** oder **Posh-ACME** verwendet werden.

#### Beispiel

- https://github.com/win-acme/win-acme

```
wacs.exe --test --validationmode dns-01 --validation script --dnscreatescript "C:\Pfad\zu\Mousetrap-DnsHook.ps1" --dnscreatescriptarguments "-Action Add -FullDomain {RecordName} -TxtValue {Token} -Zone example.com." --dnsdeletescript "C:\Pfad\zu\Mousetrap-DnsHook.ps1" --dnsdeletescriptarguments "-Action Remove -FullDomain {RecordName} -TxtValue {Token} -Zone example.com."
```
