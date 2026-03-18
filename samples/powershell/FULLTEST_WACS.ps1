<#
.SYNOPSIS
    Fulltest-Skript für Mousetrap mit Win-ACME (wacs.exe).
    Nutzt die Konfiguration aus .env.
#>

$EnvFile = Join-Path $PSScriptRoot ".env"
$HookScript = Join-Path $PSScriptRoot "Mousetrap-DnsHook.ps1"

if (-not (Test-Path $EnvFile)) {
    Write-Error ".env Datei nicht gefunden unter: $EnvFile"
    exit 1
}

# Lade .env Variablen (simuliert 'source .env')
Write-Host "Lade Konfiguration aus $EnvFile..." -ForegroundColor Gray
Get-Content $EnvFile | Where-Object { $_ -match "=" -and $_ -notmatch "^#" } | ForEach-Object {
    $name, $value = $_ -split '=', 2
    $value = $value.Trim('"').Trim("'")
    Set-Variable -Name "ENV_$name" -Value $value -Scope Script
}

# Überprüfe erforderliche Variablen
$RequiredVars = @("ENV_MICETRO_API_URL", "ENV_MICETRO_TOKEN", "ENV_MICETRO_ZONE", "ENV_APP_DOMAIN")
foreach ($var in $RequiredVars) {
    if (-not (Get-Variable $var -ErrorAction SilentlyContinue)) {
        Write-Error "Variable $var fehlt in der .env Datei!"
        exit 1
    }
}

Write-Host "Starte Win-ACME Test für: $ENV_APP_DOMAIN" -ForegroundColor Cyan

# Pfad zu wacs.exe (muss im PATH sein oder hier angepasst werden)
$WacsExe = "wacs.exe"

# Argumente für Win-ACME zusammenbauen
# Wir nutzen --test für LetsEncrypt Staging
# Wir nutzen --validation script für unsere Mousetrap-Integration
$WacsArgs = @(
    "--test",
    "--accepttos",
    "--email", "admin@$($ENV_MICETRO_ZONE.TrimEnd('.'))",
    "--host", "$ENV_APP_DOMAIN",
    "--validationmode", "dns-01",
    "--validation", "script",
    "--dnscreatescript", "$HookScript",
    "--dnscreatescriptarguments", "-Action Add -FullDomain {RecordName} -TxtValue {Token} -ApiUrl `"$ENV_MICETRO_API_URL`" -Token `"$ENV_MICETRO_TOKEN`" -Zone `"$ENV_MICETRO_ZONE`"",
    "--dnsdeletescript", "$HookScript",
    "--dnsdeletescriptarguments", "-Action Remove -FullDomain {RecordName} -TxtValue {Token} -ApiUrl `"$ENV_MICETRO_API_URL`" -Token `"$ENV_MICETRO_TOKEN`" -Zone `"$ENV_MICETRO_ZONE`""
)

Write-Host "Befehl: $WacsExe $($WacsArgs -join ' ')" -ForegroundColor DarkGray

# Ausführung
try {
    & $WacsExe @WacsArgs
}
catch {
    Write-Error "Fehler beim Ausführen von Win-ACME: $_"
    Write-Host "Hinweis: Stellen Sie sicher, dass wacs.exe installiert und im PATH verfügbar ist." -ForegroundColor Yellow
}
