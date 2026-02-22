#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# KC-SDET — Generate Allure HTML reports from per-suite allure-results folders
#
# Usage (from project root):
#   ./scripts/generate_allure_reports.sh
#   REPORTS_DIR=/custom/path ./scripts/generate_allure_reports.sh
#
# Requires: Docker. No local allure CLI install needed.
# ─────────────────────────────────────────────────────────────────────────────
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPORTS_DIR="${REPORTS_DIR:-${SCRIPT_DIR}/../reports}"
REPORTS_DIR="$(realpath "${REPORTS_DIR}")"
ALLURE_IMAGE="allureframework/allure2:2.27.0"

echo "[allure] reports dir : ${REPORTS_DIR}"

if [[ ! -d "${REPORTS_DIR}" ]]; then
  echo "[allure] reports folder not found — nothing to do"
  exit 0
fi

echo "[allure] pulling image (no-op if cached)..."
docker pull "${ALLURE_IMAGE}" --quiet || true

declare -A SUITES=(
  ["allure-results-ui-chrome"]="allure-report-ui-chrome"
  ["allure-results-ui-firefox"]="allure-report-ui-firefox"
  ["allure-results-ui-edge"]="allure-report-ui-edge"
  ["allure-results-api"]="allure-report-api"
)

GENERATED=0; SKIPPED=0

for in_name in "${!SUITES[@]}"; do
  out_name="${SUITES[$in_name]}"
  in_dir="${REPORTS_DIR}/${in_name}"
  out_dir="${REPORTS_DIR}/${out_name}"

  if [[ ! -d "${in_dir}" ]]; then
    echo "[allure] skip — missing: ${in_name}"
    (( SKIPPED++ )) || true; continue
  fi

  COUNT=$(find "${in_dir}" -name "*.json" -o -name "*.xml" 2>/dev/null | wc -l)
  if [[ "${COUNT}" -eq 0 ]]; then
    echo "[allure] skip — no result files in: ${in_name}"
    (( SKIPPED++ )) || true; continue
  fi

  rm -rf "${out_dir}"; mkdir -p "${out_dir}"
  echo "[allure] generating ${out_name}  (${COUNT} files)..."

  docker run --rm \
    -v "${in_dir}:/allure-results:ro" \
    -v "${out_dir}:/allure-report" \
    "${ALLURE_IMAGE}" \
    allure generate /allure-results -o /allure-report --clean

  if [[ -f "${out_dir}/index.html" ]]; then
    echo "[allure] ✅ ${out_name}/index.html ready"
    (( GENERATED++ )) || true
  else
    echo "[allure] ❌ generation failed for ${out_name}"
  fi
done

echo ""
echo "[allure] done — ${GENERATED} generated, ${SKIPPED} skipped"
