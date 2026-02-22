pipeline {
  agent any

  options {
    timestamps()
    ansiColor('xterm')
    disableConcurrentBuilds()
  }

  parameters {
    choice(name: 'ENV', choices: ['qa'], description: 'Test environment')
    string(name: 'TAGS', defaultValue: '@smoke', description: 'Cucumber tags to run')
    string(name: 'THREADS', defaultValue: '2', description: 'Parallel threads')
    booleanParam(name: 'RP_ENABLE', defaultValue: true, description: 'Enable ReportPortal reporting')
    string(name: 'RP_ENDPOINT', defaultValue: '', description: 'ReportPortal endpoint (optional, if not in reportportal.properties)')
    password(name: 'RP_UUID', defaultValue: '', description: 'ReportPortal API token (optional, if not in reportportal.properties)')
    string(name: 'RP_PROJECT', defaultValue: '', description: 'ReportPortal project name (optional, if not in reportportal.properties)')
  }

  environment {
    MAVEN_OPTS = "-Xmx1024m"
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Test') {
      steps {
        bat """
          mvn -U clean test ^
            -Denv=%ENV% ^
            -Dcucumber.filter.tags=%TAGS% ^
            -Dthreads=%THREADS% ^
            -Drp.enable=%RP_ENABLE% ^
            -Drp.endpoint=%RP_ENDPOINT% ^
            -Drp.uuid=%RP_UUID% ^
            -Drp.project=%RP_PROJECT%
        """
      }
    }
  }

  post {
    always {
      archiveArtifacts artifacts: 'tests/target/**/*.html, tests/target/**/*.json, tests/target/**/*.log, tests/target/surefire-reports/**, tests/target/extent-report/**', allowEmptyArchive: true
      junit 'tests/target/surefire-reports/*.xml'

      publishHTML(target: [
        allowMissing: true,
        alwaysLinkToLastBuild: true,
        keepAll: true,
        reportDir: 'tests/target/extent-report',
        reportFiles: 'ExtentSpark.html',
        reportName: 'Extent Report'
      ])
    }
  }
}
