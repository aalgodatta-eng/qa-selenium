#!/usr/bin/env bash
set -euo pipefail

# KC-SDET Automation unified runner (UI/API)
# - Uses the existing tests module and its Maven properties.

SUITE="${SUITE:-ui}"            # ui | api
ENV_NAME="${ENV:-qa}"           # maps to tests/src/test/resources/env/*.properties
RUNMODE="${RUNMODE:-remote}"    # local | remote (your framework uses this)
BROWSER="${BROWSER:-firefox}"   # firefox | edge | chrome
HEADLESS="${HEADLESS:-true}"
GRIDURL="${GRIDURL:-http://selenium-hub:4444/wd/hub}"
TAGS="${TAGS:-@ui_smoke}"
THREADS="${THREADS:-2}"
RETRY="${RETRY:-2}"             # retry attempts for flaky API/network calls
GROUPS="${GROUPS:-}"

# Artifacts / reports
# Mount /work/artifacts from host (compose) or a volume (k8s) to collect results per job.
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
echo "  GROUPS     : ${GROUPS}"
echo "  ALLURE_DIR : ${ALLURE_DIR}"
echo "  EXTENT_HTML: ${EXTENT_HTML}"
echo "  RP_ENABLE  : ${RP_ENABLE}"
echo "============================="

# We scope execution to the existing Cucumber runners in tests module.
# Repo already uses: -DgridUrl, -Dbrowser, -DrunMode, -Denv, -Dthreads, -Dcucumber.filter.tags

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

# Optional base url overrides
if [[ -n "${BASE_URL:-}" ]]; then
  MVN_ARGS+=("-DbaseUrl=${BASE_URL}")
fi
if [[ -n "${API_BASE_URL:-}" ]]; then
  MVN_ARGS+=("-DapiBaseUrl=${API_BASE_URL}")
fi

# Optional TestNG groups, if used
if [[ -n "${GROUPS}" ]]; then
  MVN_ARGS+=("-Dgroups=${GROUPS}")
fi

# ReportPortal wiring via system properties (optional)
MVN_ARGS+=("-Drp.enable=${RP_ENABLE}")
if [[ -n "${RP_LAUNCH}" ]]; then
  MVN_ARGS+=("-Drp.launch=${RP_LAUNCH}")
else
  MVN_ARGS+=("-Drp.launch=kc-${REPORT_SUFFIX}")
fi
if [[ -n "${RP_ATTRIBUTES}" ]]; then
  MVN_ARGS+=("-Drp.attributes=${RP_ATTRIBUTES}")
fi

if [[ "${SUITE}" == "ui" ]]; then
  MVN_ARGS+=("-Dbrowser=${BROWSER}" "-DgridUrl=${GRIDURL}")
  # If your framework needs platformName/browserVersion, you can pass them too.
  exec mvn "${MVN_ARGS[@]}"

elif [[ "${SUITE}" == "api" ]]; then
  # API runner doesn’t need Selenium/grid, but keeping properties harmless.
  MVN_ARGS+=("-Dbrowser=api" "-DgridUrl=")
  exec mvn "${MVN_ARGS[@]}"

else
  echo "ERROR: Unknown SUITE='${SUITE}'. Use ui|api"
  exit 2
fi
