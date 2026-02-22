#!/usr/bin/env bash
set -euo pipefail

# Generates Allure HTML reports from per-suite allure-results folders under ./reports
# Requires Docker available on the Jenkins agent (or local machine).

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
REPORTS_DIR="$ROOT_DIR/reports"

if [[ ! -d "$REPORTS_DIR" ]]; then
  echo "[allure] reports folder not found: $REPORTS_DIR"
  exit 0
fi

ALLURE_IMAGE="allureframework/allure2:2.27.0"

# Map: results folder -> output folder
pairs=(
  "allure-results-ui-chrome:allure-report-ui-chrome"
  "allure-results-ui-firefox:allure-report-ui-firefox"
  "allure-results-ui-edge:allure-report-ui-edge"
  "allure-results-api:allure-report-api"
)

for p in "${pairs[@]}"; do
  in_name="${p%%:*}"
  out_name="${p##*:}"

  in_dir="$REPORTS_DIR/$in_name"
  out_dir="$REPORTS_DIR/$out_name"

  if [[ ! -d "$in_dir" ]]; then
    echo "[allure] skip (missing): $in_dir"
    continue
  fi

  rm -rf "$out_dir"
  mkdir -p "$out_dir"

  echo "[allure] generating $out_name from $in_name"
  docker run --rm \
    -u "$(id -u):$(id -g)" \
    -v "$in_dir:/in" \
    -v "$out_dir:/out" \
    "$ALLURE_IMAGE" \
    generate /in -o /out --clean || {
      echo "[allure] generation failed for $in_name"
      exit 1
    }

done

echo "[allure] done"
