$ErrorActionPreference = "Stop"

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..\..")).Path
$sourceRoot = Join-Path $repoRoot "docs\src"
$outputRoot = Join-Path $repoRoot "docs"
$edgeCandidates = @(
    "C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe",
    "C:\Program Files\Microsoft\Edge\Application\msedge.exe"
)
$edge = $edgeCandidates | Where-Object { Test-Path -LiteralPath $_ } | Select-Object -First 1
if (-not $edge) {
    throw "Microsoft Edge no esta instalado en una ubicacion compatible."
}

$documents = @(
    "GUIA_INSTALACION_APK",
    "MANUAL_USUARIO_NEXO",
    "MANUAL_APK_EJECUCION_GITHUB",
    "ADOPCION_DE_APERTURA"
)
$profile = Join-Path $env:TEMP "nexo-pdf-$PID"

try {
    New-Item -ItemType Directory -Path $profile -Force | Out-Null
    foreach ($name in $documents) {
        $source = (Resolve-Path (Join-Path $sourceRoot "$name.html")).Path
        $output = Join-Path $outputRoot "$name.pdf"
        $temporaryOutput = Join-Path $env:TEMP "$name-$PID.pdf"
        $sourceUri = [Uri]::new($source).AbsoluteUri
        $arguments = @(
            "--headless",
            "--disable-gpu",
            "--no-pdf-header-footer",
            "--user-data-dir=$profile",
            "--print-to-pdf=$temporaryOutput",
            $sourceUri
        )
        $process = Start-Process -FilePath $edge -ArgumentList $arguments -Wait -PassThru
        if ($process.ExitCode -ne 0 -or -not (Test-Path -LiteralPath $temporaryOutput)) {
            throw "No se pudo generar $name.pdf"
        }
        Move-Item -LiteralPath $temporaryOutput -Destination $output -Force
    }
} finally {
    Remove-Item -LiteralPath $profile -Recurse -Force -ErrorAction SilentlyContinue
}
