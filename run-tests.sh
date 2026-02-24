#!/usr/bin/env bash
# run-tests.sh — Run all tests and write results to a timestamped target folder.
#
# Usage: ./run-tests.sh [extra mvn args]
#   e.g. ./run-tests.sh -Dthreads=2 -Pstage -Drp.enable=true
#
# Each execution writes directly to tests/target_YYYYMMDD_HHmmss/ so previous
# runs are never overwritten and all reports from every run are preserved.
#
# Normal "mvn clean verify" (without this script) still works and writes to
# the default tests/target/ folder.

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BUILD_DIR="target_${TIMESTAMP}"
RESULTS="tests/${BUILD_DIR}"

echo "========================================================"
echo "  KC SDET Automation — Test Run  $(date)"
echo "========================================================"
echo "  Output directory : ${RESULTS}"
echo "========================================================"
echo ""

# 1. Run full suite — pass timestamped directory as tests.build.dir
mvn clean verify -Dtests.build.dir="${BUILD_DIR}" "$@"
BUILD_EXIT=$?

# 2. Generate Allure HTML report into the same timestamped directory
if [ -d "${RESULTS}/allure-results" ]; then
  echo ""
  echo "Generating Allure HTML report..."
  mvn allure:report -pl tests -Dtests.build.dir="${BUILD_DIR}" -q 2>&1 | tail -3 || true
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
