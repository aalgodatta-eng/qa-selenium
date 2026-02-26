pipeline {
  agent any

  options {
    timestamps()
    ansiColor('xterm')
    disableConcurrentBuilds()
    timeout(time: 90, unit: 'MINUTES')
  }

  parameters {
    choice(name: 'ENV',          choices: ['qa', 'dev', 'stage'],  description: 'Test environment')
    string(name: 'THREADS',      defaultValue: '2',                description: 'Parallel scenarios per runner')
    // Default tags used in this repo:
    //  - UI smoke: @ui_smoke (alias is also present as @ui and @smoke)
    //  - API smoke/regression: @api_smoke / @api_regression
    string(name: 'UI_TAGS',      defaultValue: '@ui_smoke',                 description: 'Cucumber tags for UI suite')
    string(name: 'API_TAGS',     defaultValue: '@api_smoke or @api_regression', description: 'Cucumber tags for API suite')
    booleanParam(name: 'RUN_CHROME',  defaultValue: true,  description: 'Run UI on Chrome')
    booleanParam(name: 'RUN_FIREFOX', defaultValue: true,  description: 'Run UI on Firefox')
    booleanParam(name: 'RUN_EDGE',    defaultValue: false, description: 'Run UI on Edge')
    booleanParam(name: 'RUN_API',     defaultValue: true,  description: 'Run API suite')
    booleanParam(name: 'START_GRID',  defaultValue: true,  description: 'Start Selenium Grid inside pipeline')
    booleanParam(name: 'CLEANUP',     defaultValue: true,  description: 'Stop grid and remove network after run')
  }

  environment {
    // ── Per-build isolated Docker network ─────────────────────────────────
    // Named per BUILD_NUMBER so concurrent builds never clash.
    // Created unconditionally in Preflight (BUG 4 FIX — network must exist
    // before Maven containers try to join it, even when START_GRID=false).
    GRID_NET  = "kc-grid-${BUILD_NUMBER}"
    GRID_FILE = '.jenkins/selenium-grid.yml'

    // Grid URL used by Maven containers — resolved via Docker DNS by service name.
    // NOT localhost (localhost inside a docker run = that container, not the Jenkins host).
    GRID_URL  = 'http://selenium-hub:4444/wd/hub'

    // Maven: run inside Docker so Jenkins agent needs no Java/Maven install
    MVN_IMG   = 'maven:3.9.8-eclipse-temurin-17'
    // Named volume caches ~/.m2 between builds — avoids re-downloading ~500 MB
    MVN_CACHE = 'jenkins-m2'

    // Allure HTML is generated via the Allure Maven plugin (no Docker image pull required)
  }

  stages {

    stage('Checkout') {
      steps {
        checkout scm
        sh '''#!/usr/bin/env bash
          set -euo pipefail
          echo "[checkout] pwd=$(pwd)"
          ls -la
          test -f pom.xml || { echo "[checkout] ERROR: root pom.xml not found"; exit 2; }
          test -d tests || { echo "[checkout] ERROR: tests module folder not found"; find . -maxdepth 3 -name pom.xml -print; exit 3; }
          echo "[checkout] ✅ root pom.xml + tests/ found"
        '''
      }
    }

    // ─────────────────────────────────────────────────────────────────────
    stage('Preflight') {
      steps {
        sh '''#!/usr/bin/env bash
          set -euo pipefail
          echo "[preflight] docker:"
          docker --version
          echo "[preflight] docker compose:"
          docker compose version

          mkdir -p .jenkins reports

          # BUG 4 FIX: Always create the per-build network so Maven containers can
          # join it regardless of whether START_GRID=true or false.
          # "--driver bridge" is the default; "--ignore" makes it idempotent.
          if docker network inspect "${GRID_NET}" >/dev/null 2>&1; then
            echo "[preflight] network ${GRID_NET} already exists — reusing"
          else
            docker network create --driver bridge "${GRID_NET}"
            echo "[preflight] ✅ created network ${GRID_NET}"
          fi
        '''
      }
    }

    // ─────────────────────────────────────────────────────────────────────
    // Writes the grid compose file and starts hub + browser nodes on the
    // per-build network.  No host ports are bound → zero port conflicts.
    // Maven containers join the same network and reach hub by DNS name.
    // ─────────────────────────────────────────────────────────────────────
    stage('Start Selenium Grid') {
      when { expression { return params.START_GRID } }
      steps {
        sh '''#!/usr/bin/env bash
set -euo pipefail

# ── Write the grid compose file from template ────────────────
GRID_TEMPLATE="${WORKSPACE}/docker/selenium-grid-template.yml"
if [[ ! -f "$GRID_TEMPLATE" ]]; then
  echo "[grid] ERROR: Template file $GRID_TEMPLATE not found!"
  exit 1
fi
envsubst < "$GRID_TEMPLATE" > "${GRID_FILE}"

# Check if compose file was written
if [[ ! -s "${GRID_FILE}" ]]; then
  echo "[grid] ERROR: Compose file is empty!"
  cat "${GRID_FILE}"
  exit 1
fi

# ── Start hub + chrome + firefox ──────────────────────────────
echo "[grid] starting hub + chrome + firefox on network ${GRID_NET}"
docker compose \
  --project-name "kc-${BUILD_NUMBER}" \
  -f "${GRID_FILE}" \
  up -d selenium-hub chrome firefox

# ── Start edge only when requested ────────────────────────────
if [[ "${RUN_EDGE:-false}" == "true" ]]; then
  echo "[grid] starting edge node"
  docker compose \
    --project-name "kc-${BUILD_NUMBER}" \
    -f "${GRID_FILE}" \
    up -d edge
fi

# ── Wait for hub to accept connections ───────────────────────
HUB=$(docker compose --project-name "kc-${BUILD_NUMBER}" -f "${GRID_FILE}" ps -q selenium-hub)
if [[ -z "$HUB" ]]; then
  echo "[grid] ERROR: Hub container not found!"
  docker compose --project-name "kc-${BUILD_NUMBER}" -f "${GRID_FILE}" ps
  exit 1
fi
echo "[grid] hub container: ${HUB}"
echo "[grid] waiting for /wd/hub/status (timeout 120s)..."

timeout 120 bash -c "
  until docker exec ${HUB} curl -sSf http://localhost:4444/wd/hub/status >/dev/null 2>&1; do
    sleep 2
  done
" || {
            echo "[grid] hub never became ready — dumping logs"
            docker compose --project-name "kc-${BUILD_NUMBER}" -f "${GRID_FILE}" logs --tail=100
            exit 1
          }

          echo "[grid] ✅ Selenium Grid is ready on network ${GRID_NET}"
          docker compose --project-name "kc-${BUILD_NUMBER}" -f "${GRID_FILE}" ps
        '''
      }
    }

    // ─────────────────────────────────────────────────────────────────────
    // Run all selected suites in parallel.  Each suite runs Maven inside
    // Docker, joined to GRID_NET so it can reach selenium-hub by name.
    // Reports land in ./reports/<suffix>/ via host volume mount.
    // ─────────────────────────────────────────────────────────────────────
    stage('Build & Test (Parallel)') {
      steps {
        script {

          // Runs Maven inside Docker and returns an exit code.
          // We DO NOT use "|| true" (it hides failures). Instead we capture the exit code,
          // mark the build as FAILURE, and still continue so report generation/publishing runs.
                    
              sh '''#!/usr/bin/env bash
                set -euo pipefail
                docker run --rm \
                  --network "${GRID_NET}" \
                  # ...existing code...
              '''

            

    // ─────────────────────────────────────────────────────────────────────
    // Generate Allure HTML reports using the official Docker image.
    // No local allure CLI install needed on the Jenkins agent.
    // ─────────────────────────────────────────────────────────────────────
    stage('Generate Allure Reports') {
      steps {
        sh '''#!/usr/bin/env bash
          set +e
          echo "[allure] pulling image (cached after first run)..."
          docker pull "${ALLURE_IMAGE}" --quiet || true

          GENERATED=0; SKIPPED=0

          for suffix in ui-chrome ui-firefox ui-edge api; do
            IN_DIR="${WORKSPACE}/reports/allure-results-${suffix}"
            OUT_DIR="${WORKSPACE}/reports/allure-report-${suffix}"

            COUNT=$(find "${IN_DIR}" -type f 2>/dev/null | wc -l)
            if [[ "${COUNT}" -eq 0 ]]; then
              echo "[allure] skip — 0 result files for: ${suffix}"
              (( SKIPPED++ )) || true; continue
            fi

            echo "[allure] generating allure-report-${suffix}  (${COUNT} files)..."

            # Generate Allure HTML using the Maven plugin inside the Maven container.
            docker run --rm \
              -v "${WORKSPACE}:/work" -w /work \
              -v "${MVN_CACHE}:/root/.m2" \
              ${MVN_IMG} \
              mvn -f pom.xml -pl tests -am -DskipTests \
                -Dallure.results.directory="/work/reports/allure-results-${suffix}" \
                -Dallure.report.directory="/work/reports/allure-report-${suffix}" \
                allure:report

            # Some plugin versions always write to tests/target/site/...; copy if needed
            if [[ ! -f "${OUT_DIR}/index.html" && -d "${WORKSPACE}/tests/target/site/allure-maven-plugin" ]]; then
              cp -r "${WORKSPACE}/tests/target/site/allure-maven-plugin/." "${OUT_DIR}/" || true
            fi

            [[ -f "${OUT_DIR}/index.html" ]] \
              && { echo "[allure] ✅ ${suffix} report ready"; (( GENERATED++ )) || true; } \
              || echo "[allure] ❌ generation failed for ${suffix}"
          done

          echo ""
          echo "[allure] ${GENERATED} generated, ${SKIPPED} skipped"
        '''
      }

                  # Check if compose file was written
                  if [[ ! -s "${GRID_FILE}" ]]; then
                    echo "[grid] ERROR: Compose file is empty!"
                    cat "${GRID_FILE}"
                    exit 1
                  fi

                  # ── Start hub + chrome + firefox ──────────────────────────────
                  echo "[grid] starting hub + chrome + firefox on network ${GRID_NET}"
                  docker compose \
                    --project-name "kc-${BUILD_NUMBER}" \
                    -f "${GRID_FILE}" \
                    up -d selenium-hub chrome firefox

                  # ── Start edge only when requested ────────────────────────────
                  if [[ "${RUN_EDGE:-false}" == "true" ]]; then
                    echo "[grid] starting edge node"
                    docker compose \
                      --project-name "kc-${BUILD_NUMBER}" \
                      -f "${GRID_FILE}" \
                      up -d edge
                  fi

                  # ── Wait for hub to accept connections ───────────────────────
                  HUB=$(docker compose --project-name "kc-${BUILD_NUMBER}" -f "${GRID_FILE}" ps -q selenium-hub)
                  if [[ -z "$HUB" ]]; then
                    echo "[grid] ERROR: Hub container not found!"
                    docker compose --project-name "kc-${BUILD_NUMBER}" -f "${GRID_FILE}" ps
                    exit 1
                  fi
                  echo "[grid] hub container: ${HUB}"
                  echo "[grid] waiting for /wd/hub/status (timeout 120s)..."

                  timeout 120 bash -c "
                    until docker exec ${HUB} curl -sSf http://localhost:4444/wd/hub/status >/dev/null 2>&1; do
                      sleep 2
                    done
                  " || {
    }

  } // end stages

  // ─────────────────────────────────────────────────────────────────────────
  post {
    always {
      // ── Stop grid containers and remove the per-build network ─────────
      sh '''#!/usr/bin/env bash
        set +e
        if [[ "${CLEANUP}" == "true" && "${START_GRID}" == "true" ]]; then
          echo "[post] stopping grid (project kc-${BUILD_NUMBER})"
          docker compose \
            --project-name "kc-${BUILD_NUMBER}" \
            -f "${GRID_FILE}" \
            down --remove-orphans 2>/dev/null || true
        fi

        if [[ "${CLEANUP}" == "true" ]]; then
          echo "[post] removing network ${GRID_NET}"
          docker network rm "${GRID_NET}" 2>/dev/null || true
        fi
      '''

      // ── JUnit XML results ─────────────────────────────────────────────
      junit allowEmptyResults: true,
            testResults: '**/target/surefire-reports/*.xml'

      // ── Archive raw artifacts ─────────────────────────────────────────
      archiveArtifacts artifacts: 'reports/**,**/target/cucumber/**',
                       allowEmptyArchive: true

      // ── Allure HTML reports — left-sidebar links in Jenkins ───────────
      publishHTML(target: [allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true,
        reportDir: 'reports/allure-report-ui-chrome',  reportFiles: 'index.html', reportName: 'Allure — Chrome'])
      publishHTML(target: [allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true,
        reportDir: 'reports/allure-report-ui-firefox', reportFiles: 'index.html', reportName: 'Allure — Firefox'])
      publishHTML(target: [allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true,
        reportDir: 'reports/allure-report-ui-edge',    reportFiles: 'index.html', reportName: 'Allure — Edge'])
      publishHTML(target: [allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true,
        reportDir: 'reports/allure-report-api',        reportFiles: 'index.html', reportName: 'Allure — API'])

      // ── Extent HTML reports ───────────────────────────────────────────
      publishHTML(target: [allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true,
        reportDir: 'reports/extent-report-ui-chrome',  reportFiles: 'ExtentSpark.html', reportName: 'Extent — Chrome'])
      publishHTML(target: [allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true,
        reportDir: 'reports/extent-report-ui-firefox', reportFiles: 'ExtentSpark.html', reportName: 'Extent — Firefox'])
      publishHTML(target: [allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true,
        reportDir: 'reports/extent-report-ui-edge',    reportFiles: 'ExtentSpark.html', reportName: 'Extent — Edge'])
      publishHTML(target: [allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true,
        reportDir: 'reports/extent-report-api',        reportFiles: 'ExtentSpark.html', reportName: 'Extent — API'])

      // ── Cucumber HTML reports ─────────────────────────────────────────
      publishHTML(target: [allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true,
        reportDir: 'reports/cucumber-report-ui-chrome',  reportFiles: 'index.html', reportName: 'Cucumber — Chrome'])
      publishHTML(target: [allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true,
        reportDir: 'reports/cucumber-report-ui-firefox', reportFiles: 'index.html', reportName: 'Cucumber — Firefox'])
      publishHTML(target: [allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true,
        reportDir: 'reports/cucumber-report-ui-edge',    reportFiles: 'index.html', reportName: 'Cucumber — Edge'])
      publishHTML(target: [allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true,
        reportDir: 'reports/cucumber-report-api',        reportFiles: 'index.html', reportName: 'Cucumber — API'])

      // ── ReportPortal link (documentation) ─────────────────────────────
      echo 'ReportPortal results are available at https://your.reportportal.server/ui/#kc-sdet-automation/launches/all'
    }

    success { echo '✅ All selected suites passed — Allure/Extent reports in sidebar' }
    failure { echo '❌ One or more suites failed — check stage logs above' }
  }
}
