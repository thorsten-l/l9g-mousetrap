<#
.SYNOPSIS
    PowerShell-Skript für die l9g-mousetrap DNS-01 Challenge.
    Implementiert die Funktionalität des acme.sh dns_mousetrap-Hooks.

.DESCRIPTION
    Dieses Skript ermöglicht das Hinzufügen und Entfernen von DNS-TXT-Einträgen
    über die Mousetrap-API, um die DNS-01-Validierung für SSL/TLS-Zertifikate
    (z.B. via Certify The Web oder eigene PS-Skripte) zu automatisieren.

.PARAMETER Action
    Die auszuführende Aktion: "Add" oder "Remove".

.PARAMETER FullDomain
    Der vollständige Domainname für den Challenge-Eintrag (z.B. _acme-challenge.example.com).

.PARAMETER TxtValue
    Der Wert des TXT-Records (Challenge-Token).

.PARAMETER ApiUrl
    Die URL der Mousetrap-API (Standard: http://localhost:8080/api/v1/micetro).

.PARAMETER Token
    Der Mousetrap-Authentifizierungs-Token.

.PARAMETER Zone
    Die DNS-Zone in Micetro, in der der Eintrag erstellt werden soll (z.B. example.com.).

.EXAMPLE
    .\Mousetrap-DnsHook.ps1 -Action Add -FullDomain "_acme-challenge.test.example.com" -TxtValue "token123" -Token "your-token" -Zone "example.com."
#>

Param(
    [Parameter(Mandatory=$true)]
    [ValidateSet("Add", "Remove")]
    [String]$Action,

    [Parameter(Mandatory=$true)]
    [String]$FullDomain,

    [Parameter(Mandatory=$true)]
    [String]$TxtValue,

    [String]$ApiUrl = "http://localhost:8080/api/v1/micetro",

    [String]$Token = "your-token-here",

    [String]$Zone = "example.com."
)

# Berechne den Namen des Eintrags durch Entfernen der Zone vom FQDN
# Äquivalent zu: name="${fulldomain%.$MOUSETRAP_ZONE}"
$ZoneWithDot = if ($Zone.EndsWith(".")) { $Zone } else { "$Zone." }
$ZoneSuffix  = "." + $ZoneWithDot.TrimEnd(".")

$NormalizedFqdn = $FullDomain.TrimEnd(".")
if ($NormalizedFqdn.EndsWith($ZoneSuffix, [System.StringComparison]::OrdinalIgnoreCase)) {
    $Name = $NormalizedFqdn.Substring(0, $NormalizedFqdn.Length - $ZoneSuffix.Length)
} else {
    $Name = $NormalizedFqdn
}

$Headers = @{
    "Authorization" = "Bearer $Token"
    "Content-Type"  = "application/json"
}

$Body = @{
    zone = $Zone
    name = $Name
    data = $TxtValue
} | ConvertTo-Json

Write-Host "Mousetrap: $Action record for $FullDomain (Name: $Name) in Zone $Zone" -ForegroundColor Cyan

try {
    if ($Action -eq "Add") {
        $Response = Invoke-RestMethod -Uri $ApiUrl -Method Post -Headers $Headers -Body $Body
        Write-Host "Erfolgreich hinzugefügt." -ForegroundColor Green
    }
    else {
        $Response = Invoke-RestMethod -Uri $ApiUrl -Method Delete -Headers $Headers -Body $Body
        Write-Host "Erfolgreich entfernt." -ForegroundColor Green
    }
}
catch {
    Write-Error "Mousetrap API Fehler: $_"
    exit 1
}
