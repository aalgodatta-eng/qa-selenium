#!/usr/bin/env bash
# run-tests.sh — Run all tests and write results to a timestamped target folder.
#
# Usage: ./run-tests.sh [--env <environment>] [extra mvn args]
#
#   --env / -e  Target environment (local|dev|qa|uat|stage|prod).
#               Translates to Maven profile -P<env> and system property -Denv=<env>.
#               Defaults to "qa" when omitted.
#
# Examples:
#   ./run-tests.sh                              # default: qa
#   ./run-tests.sh --env local                  # local environment
#   ./run-tests.sh --env uat                    # UAT environment
#   ./run-tests.sh --env prod                   # production environment
#   ./run-tests.sh --env stage -Dthreads=4      # stage + 4 parallel threads
#   ./run-tests.sh --env qa -Dcucumber.filter.tags="@smoke"
#   ./run-tests.sh -Drp.enable=true             # ReportPortal (uses default qa env)
#
# Each execution writes directly to tests/target_YYYYMMDD_HHmmss/ so previous
# runs are never overwritten and all reports from every run are preserved.
#
# Normal "mvn clean verify" (without this script) still works and writes to
# the default tests/target/ folder.

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BUILD_DIR="target_${TIMESTAMP}"
RESULTS="tests/${BUILD_DIR}"

# ── Argument parsing ──────────────────────────────────────────────────────────
ENV_FLAG=""
EXTRA_ARGS=()
while [[ $# -gt 0 ]]; do
  case $1 in
    -e|--env)
      ENV_FLAG="$2"
      shift 2
      ;;
    *)
      EXTRA_ARGS+=("$1")
      shift
      ;;
  esac
done

# Build env-related Maven flags: Maven profile (-P) + Surefire system property (-Denv)
ENV_MVN_ARGS=()
if [ -n "$ENV_FLAG" ]; then
  ENV_MVN_ARGS=("-P${ENV_FLAG}" "-Denv=${ENV_FLAG}")
fi

echo "========================================================"
echo "  KC SDET Automation — Test Run  $(date)"
echo "========================================================"
echo "  Output directory : ${RESULTS}"
if [ -n "$ENV_FLAG" ]; then
  echo "  Environment      : ${ENV_FLAG}"
else
  echo "  Environment      : qa (default)"
fi
echo "========================================================"
echo ""

# 1. Run full suite — pass timestamped directory as tests.build.dir
mvn clean verify -Dtests.build.dir="${BUILD_DIR}" \
  "${ENV_MVN_ARGS[@]}" "${EXTRA_ARGS[@]}"
BUILD_EXIT=$?

# 2. Generate Allure HTML report into the same timestamped directory
if [ -d "${RESULTS}/allure-results" ]; then
  echo ""
  echo "Generating Allure HTML report..."
  mvn allure:report -pl tests -Dtests.build.dir="${BUILD_DIR}" \
    "${ENV_MVN_ARGS[@]}" -q 2>&1 | tail -3 || true
fi

echo ""
echo "========================================================"
if [ ${BUILD_EXIT} -eq 0 ]; then
  echo "  BUILD SUCCESS"
else
  echo "  BUILD FAILED  (Maven exit code: ${BUILD_EXIT})"
fi
echo ""
echo "  Results → ${RESULTS}"
echo ""
echo "  Reports:"
echo "    Extent    → ${RESULTS}/extent-report/ExtentSpark.html"
echo "    Allure    → ${RESULTS}/site/allure-maven-plugin/index.html"
echo "    Cucumber  → ${RESULTS}/cucumber-html-reports/"
echo "    Surefire  → ${RESULTS}/surefire-reports/index.html"
echo "========================================================"

exit ${BUILD_EXIT}
