pipeline {
  agent any
  options { timestamps(); ansiColor('xterm') }

  parameters {
    choice(name: 'ENV', choices: ['dev', 'qa', 'stage'], description: 'Environment config')
    choice(name: 'RUNMODE', choices: ['local', 'grid'], description: 'Execution mode')
    choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'], description: 'Browser')
    string(name: 'GRIDURL', defaultValue: 'http://localhost:4444/wd/hub', description: 'Grid URL')
    string(name: 'SUITEXML', defaultValue: 'testng/smoke.xml', description: 'TestNG suite XML')
    string(name: 'THREADS', defaultValue: '4', description: 'Parallel threads')
    choice(name: 'HEADLESS', choices: ['false', 'true'], description: 'Headless mode')
    string(name: 'RETRY', defaultValue: '0', description: 'Retry count')
  }

  environment { MVN = 'mvn -q' }

  stages {
    stage('Checkout') { steps { checkout scm } }

    stage('Start Selenium Grid') {
      when { expression { params.RUNMODE == 'grid' } }
      steps {
        sh '''
          docker compose up -d
          echo "Waiting for Grid..."
          for i in {1..30}; do
            curl -sf http://localhost:4444/status >/dev/null && break || true
            sleep 2
          done
          curl -s http://localhost:4444/status | head -c 500 || true
        '''
      }
    }

    stage('Build & Test') {
      steps {
        sh '''
          ${MVN} clean test -pl tests             -Denv=${ENV}             -DrunMode=${RUNMODE}             -Dbrowser=${BROWSER}             -Dheadless=${HEADLESS}             -DgridUrl=${GRIDURL}             -DsuiteXml=${SUITEXML}             -Dthreads=${THREADS}             -Dretry=${RETRY}
        '''
      }
    }

    stage('Allure Report') {
      steps {
        sh '''
          mvn -q -pl tests allure:report || true
          cp -f tests/src/test/resources/allure-categories.json tests/target/site/allure-maven-plugin/categories.json || true
        '''
      }
    }

    stage('Archive Artifacts') {
      steps {
        archiveArtifacts artifacts: '''
          **/tests/target/extent-report/**,
          **/tests/target/screenshots/**,
          **/tests/target/allure-results/**,
          **/tests/target/site/allure-maven-plugin/**,
          **/tests/target/surefire-reports/**
        ''', fingerprint: true
      }
    }
  }

  post {
    always {
      script {
        if (params.RUNMODE == 'grid') { sh 'docker compose down || true' }
      }
    }
  }
}
