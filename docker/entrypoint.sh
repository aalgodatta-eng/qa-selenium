#!/usr/bin/env bash
set -euo pipefail

# KC-SDET Automation — unified test runner (UI / API)
# All settings are driven by environment variables so the same image works
# for Docker Compose, Kubernetes Jobs, and local docker run.

SUITE="${SUITE:-ui}"             # ui | api
ENV_NAME="${ENV:-qa}"            # maps to tests/src/test/resources/env/<ENV>.properties
RUNMODE="${RUNMODE:-grid}"       # grid | local  (DriverFactory checks for "grid")
BROWSER="${BROWSER:-chrome}"    # chrome | firefox | edge
HEADLESS="${HEADLESS:-true}"
GRIDURL="${GRIDURL:-http://selenium-hub:4444/wd/hub}"
TAGS="${TAGS:-@ui_smoke}"
THREADS="${THREADS:-2}"
RETRY="${RETRY:-2}"
GROUPS="${GROUPS:-}"

# ── Artifact / report paths ────────────────────────────────────────────────
# Mount /work/artifacts from host (compose) or emptyDir (k8s) to collect results.
ARTIFACTS_ROOT="${ARTIFACTS_ROOT:-/work/artifacts}"
REPORT_SUFFIX="${REPORT_SUFFIX:-${SUITE}-${BROWSER}}"
ALLURE_DIR="${ALLURE_DIR:-${ARTIFACTS_ROOT}/allure-results-${REPORT_SUFFIX}}"
EXTENT_HTML="${EXTENT_HTML:-${ARTIFACTS_ROOT}/extent-report-${REPORT_SUFFIX}/ExtentSpark.html}"

mkdir -p "${ALLURE_DIR}" "$(dirname "${EXTENT_HTML}")"

RP_ENABLE="${RP_ENABLE:-false}"
RP_LAUNCH="${RP_LAUNCH:-}"
RP_ATTRIBUTES="${RP_ATTRIBUTES:-}"

echo "============================="
echo "KC Test Runner"
echo "  SUITE      : ${SUITE}"
echo "  ENV        : ${ENV_NAME}"
echo "  RUNMODE    : ${RUNMODE}"
echo "  BROWSER    : ${BROWSER}"
echo "  HEADLESS   : ${HEADLESS}"
echo "  GRIDURL    : ${GRIDURL}"
echo "  TAGS       : ${TAGS}"
echo "  THREADS    : ${THREADS}"
echo "  RETRY      : ${RETRY}"
echo "  ALLURE_DIR : ${ALLURE_DIR}"
echo "  EXTENT_HTML: ${EXTENT_HTML}"
echo "  RP_ENABLE  : ${RP_ENABLE}"
echo "============================="

MVN_ARGS=(
  -q
  -pl tests -am
  test
  "-Denv=${ENV_NAME}"
  "-DrunMode=${RUNMODE}"
  "-Dheadless=${HEADLESS}"
  "-Dthreads=${THREADS}"
  "-Dretry=${RETRY}"
  "-Dcucumber.filter.tags=${TAGS}"
  "-Dallure.results.directory=${ALLURE_DIR}"
  "-Dextent.reporter.spark.out=${EXTENT_HTML}"
)

# Optional URL overrides
[[ -n "${BASE_URL:-}"     ]] && MVN_ARGS+=("-DbaseUrl=${BASE_URL}")
[[ -n "${API_BASE_URL:-}" ]] && MVN_ARGS+=("-DapiBaseUrl=${API_BASE_URL}")
[[ -n "${GROUPS}"         ]] && MVN_ARGS+=("-Dgroups=${GROUPS}")

# ReportPortal (optional)
MVN_ARGS+=("-Drp.enable=${RP_ENABLE}")
if [[ -n "${RP_LAUNCH}" ]]; then
  MVN_ARGS+=("-Drp.launch=${RP_LAUNCH}")
else
  MVN_ARGS+=("-Drp.launch=kc-${REPORT_SUFFIX}")
fi
[[ -n "${RP_ATTRIBUTES}" ]] && MVN_ARGS+=("-Drp.attributes=${RP_ATTRIBUTES}")

if [[ "${SUITE}" == "ui" ]]; then
  MVN_ARGS+=("-Dbrowser=${BROWSER}" "-DgridUrl=${GRIDURL}")
  exec mvn "${MVN_ARGS[@]}"

elif [[ "${SUITE}" == "api" ]]; then
  MVN_ARGS+=("-Dbrowser=api" "-DgridUrl=")
  exec mvn "${MVN_ARGS[@]}"

else
  echo "ERROR: Unknown SUITE='${SUITE}'. Use ui | api"
  exit 2
fi
