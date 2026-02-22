# Generates Allure HTML reports from per-suite allure-results folders under .\reports
# Requires Docker available on the Jenkins agent (Windows).

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$reports = Join-Path $root "reports"

if (-not (Test-Path $reports)) {
  Write-Host "[allure] reports folder not found: $reports"
  exit 0
}

$allureImage = "allureframework/allure2:2.27.0"

$pairs = @(
  @{ In = "allure-results-ui-chrome";  Out = "allure-report-ui-chrome" },
  @{ In = "allure-results-ui-firefox"; Out = "allure-report-ui-firefox" },
  @{ In = "allure-results-ui-edge";    Out = "allure-report-ui-edge" },
  @{ In = "allure-results-api";        Out = "allure-report-api" }
)

foreach ($p in $pairs) {
  $inDir  = Join-Path $reports $p.In
  $outDir = Join-Path $reports $p.Out

  if (-not (Test-Path $inDir)) {
    Write-Host "[allure] skip (missing): $inDir"
    continue
  }

  if (Test-Path $outDir) { Remove-Item -Recurse -Force $outDir }
  New-Item -ItemType Directory -Force -Path $outDir | Out-Null

  Write-Host "[allure] generating $($p.Out) from $($p.In)"

  docker run --rm `
    -v "${inDir}:/in" `
    -v "${outDir}:/out" `
    $allureImage `
    generate /in -o /out --clean
}

Write-Host "[allure] done"
