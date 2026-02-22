pipeline {
  agent any

  options {
    timestamps()
    ansiColor('xterm')
    disableConcurrentBuilds()
  }

  parameters {
    choice(name: 'ENV', choices: ['qa'], description: 'Test environment')
    string(name: 'THREADS', defaultValue: '2', description: 'Parallel threads inside each run')
    string(name: 'UI_TAGS', defaultValue: '@ui_smoke', description: 'Cucumber tags for UI suite')
    string(name: 'API_TAGS', defaultValue: '@api_smoke', description: 'Cucumber tags for API suite')
    booleanParam(name: 'RUN_CHROME', defaultValue: true, description: 'Run UI on Chrome')
    booleanParam(name: 'RUN_FIREFOX', defaultValue: true, description: 'Run UI on Firefox')
    booleanParam(name: 'RUN_EDGE', defaultValue: false, description: 'Run UI on Edge (heavy)')
    booleanParam(name: 'RUN_API', defaultValue: true, description: 'Run API suite')
    booleanParam(name: 'START_GRID', defaultValue: true, description: 'Start Selenium Grid inside pipeline')
    booleanParam(name: 'CLEANUP', defaultValue: true, description: 'Stop Grid after run')
  }

  environment {
    GRID_FILE = '.jenkins/selenium-grid.yml'
    // Grid URL visible from Jenkins container (host port 4444)
    GRID_URL = 'http://localhost:4444'
    // Run Maven via docker to avoid installing Java/Maven in Jenkins container
    MVN_IMG  = 'maven:3.9.8-eclipse-temurin-17'
    // Cache Maven repo between builds to reduce time + memory spikes
    MVN_CACHE = 'jenkins-m2'
  }

  stages {

    stage('Preflight') {
      steps {
        sh '''#!/usr/bin/env bash
          set -euo pipefail
          echo "[preflight] docker:"; docker --version
          echo "[preflight] docker compose:"; docker compose version
          mkdir -p .jenkins
        '''
      }
    }

    stage('Start Selenium Grid') {
      when { expression { return params.START_GRID } }
      steps {
        sh '''#!/usr/bin/env bash
          set -euo pipefail

          cat > "${GRID_FILE}" <<'YML'
services:
  selenium-hub:
    image: selenium/hub:4.21.0
    container_name: selenium-hub
    ports:
      - "4444:4444"
    environment:
      - JAVA_OPTS=-Xms128m -Xmx384m

  chrome:
    image: selenium/node-chrome:4.21.0
    shm_size: 1gb
    depends_on: [selenium-hub]
    environment:
      - SE_EVENT_BUS_HOST=selenium-hub
      - SE_EVENT_BUS_PUBLISH_PORT=4442
      - SE_EVENT_BUS_SUBSCRIBE_PORT=4443
      - SE_NODE_MAX_SESSIONS=1
      - SE_NODE_OVERRIDE_MAX_SESSIONS=true
      - JAVA_OPTS=-Xms128m -Xmx384m

  firefox:
    image: selenium/node-firefox:4.21.0
    shm_size: 1gb
    depends_on: [selenium-hub]
    environment:
      - SE_EVENT_BUS_HOST=selenium-hub
      - SE_EVENT_BUS_PUBLISH_PORT=4442
      - SE_EVENT_BUS_SUBSCRIBE_PORT=4443
      - SE_NODE_MAX_SESSIONS=1
      - SE_NODE_OVERRIDE_MAX_SESSIONS=true
      - JAVA_OPTS=-Xms128m -Xmx384m

  edge:
    image: selenium/node-edge:4.21.0
    shm_size: 1gb
    depends_on: [selenium-hub]
    environment:
      - SE_EVENT_BUS_HOST=selenium-hub
      - SE_EVENT_BUS_PUBLISH_PORT=4442
      - SE_EVENT_BUS_SUBSCRIBE_PORT=4443
      - SE_NODE_MAX_SESSIONS=1
      - SE_NODE_OVERRIDE_MAX_SESSIONS=true
      - JAVA_OPTS=-Xms128m -Xmx384m
YML

          echo "[grid] up"
          docker compose -f "${GRID_FILE}" up -d selenium-hub chrome firefox

          # start edge only if requested (saves memory)
          if [[ "${RUN_EDGE}" == "true" ]]; then
            docker compose -f "${GRID_FILE}" up -d edge
          fi

          echo "[grid] wait for ready"
          timeout 180 bash -c '
            until curl -sf http://localhost:4444/status \
              | python3 -c "import sys,json; d=json.load(sys.stdin); sys.exit(0 if d.get(\\"value\\",{}).get(\\"ready\\") else 1)"; do
              sleep 3
            done
          '
          echo "[grid] ready ✅"
        '''
      }
    }

    stage('Build & Test (Parallel)') {
      steps {
        script {
          def runs = [:]

          def mvnRun = { String name, String tags, String browser ->
            return {
              sh """#!/usr/bin/env bash
                set -euo pipefail
                echo "[run] ${name} tags=${tags} browser=${browser}"

                # Run Maven inside container (no local mvn needed)
                docker run --rm \\
                  -v "\$PWD:/work" -w /work \\
                  -v "${MVN_CACHE}:/root/.m2" \\
                  -e MAVEN_OPTS="-Xms128m -Xmx768m -XX:MaxMetaspaceSize=256m" \\
                  ${MVN_IMG} \\
                  mvn -U clean test -pl tests \\
                    -Denv=${ENV} \\
                    -Dthreads=${THREADS} \\
                    -Dcucumber.filter.tags="${tags}" \\
                    -DrunMode=grid \\
                    -DgridUrl=${GRID_URL} \\
                    -Dbrowser=${browser}

              """
            }
          }

          if (params.RUN_CHROME)  { runs['UI-Chrome']  = mvnRun('UI-Chrome',  params.UI_TAGS, 'chrome') }
          if (params.RUN_FIREFOX) { runs['UI-Firefox'] = mvnRun('UI-Firefox', params.UI_TAGS, 'firefox') }
          if (params.RUN_EDGE)    { runs['UI-Edge']    = mvnRun('UI-Edge',    params.UI_TAGS, 'edge') }

          if (params.RUN_API) {
            runs['API'] = {
              sh """#!/usr/bin/env bash
                set -euo pipefail
                echo "[run] API tags=${API_TAGS}"

                docker run --rm \\
                  -v "\$PWD:/work" -w /work \\
                  -v "${MVN_CACHE}:/root/.m2" \\
                  -e MAVEN_OPTS="-Xms128m -Xmx768m -XX:MaxMetaspaceSize=256m" \\
                  ${MVN_IMG} \\
                  mvn -U clean test -pl tests \\
                    -Denv=${ENV} \\
                    -Dthreads=${THREADS} \\
                    -Dcucumber.filter.tags="${API_TAGS}"
              """
            }
          }

          parallel runs
        }
      }
    }
  }

  post {
    always {
      // Test reports (if surefire/junit xml exists)
      junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

      // If your project outputs Extent/Allure, archive them
      archiveArtifacts artifacts: '**/target/**', allowEmptyArchive: true

      sh '''#!/usr/bin/env bash
        set +e
        if [[ "${CLEANUP}" == "true" && "${START_GRID}" == "true" ]]; then
          docker compose -f "${GRID_FILE}" down --remove-orphans
        fi
      '''
    }
  }
}