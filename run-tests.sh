#!/usr/bin/env bash
# run-tests.sh — Run all tests and archive results into a timestamped target folder.
#
# Usage: ./run-tests.sh [extra mvn args]
#   e.g. ./run-tests.sh -Dthreads=2 -Pstage -Drp.enable=true
#
# After each run the tests/target directory is renamed to tests/target_YYYYMMDD_HHmmss
# so results from every execution are preserved side by side.

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
ARCHIVE="tests/target_${TIMESTAMP}"

echo "========================================================"
echo "  KC SDET Automation — Test Run  $(date)"
echo "========================================================"
echo "  Archive destination: ${ARCHIVE}"
echo "========================================================"
echo ""

# 1. Run the full suite
mvn clean verify "$@"
BUILD_EXIT=$?

# 2. Generate Allure HTML report (needs allure-results produced by step 1)
if [ -d "tests/target/allure-results" ]; then
  echo ""
  echo "Generating Allure HTML report..."
  mvn allure:report -pl tests -q 2>&1 | tail -3 || true
fi

# 3. Archive tests/target → tests/target_TIMESTAMP (regardless of pass/fail)
if [ -d "tests/target" ]; then
  mv "tests/target" "${ARCHIVE}"
  echo ""
  echo "========================================================"
  if [ ${BUILD_EXIT} -eq 0 ]; then
    echo "  BUILD SUCCESS"
  else
    echo "  BUILD FAILED  (Maven exit code: ${BUILD_EXIT})"
  fi
  echo ""
  echo "  Results archived → ${ARCHIVE}"
  echo ""
  echo "  Open a report:"
  echo "    Extent    → ${ARCHIVE}/extent-report/ExtentSpark.html"
  echo "    Allure    → ${ARCHIVE}/site/allure-maven-plugin/index.html"
  echo "    Cucumber  → ${ARCHIVE}/cucumber-html-reports/"
  echo "    Surefire  → ${ARCHIVE}/surefire-reports/index.html"
  echo "========================================================"
fi

exit ${BUILD_EXIT}
