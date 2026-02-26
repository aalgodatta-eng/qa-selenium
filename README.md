# KC SDET Automation Framework (BDD UI + API)

**Tech**: Java 17 • Selenium 4 • RestAssured • Cucumber 7 (TestNG runner)  
**Reports**: Extent (Spark) + ReportPortal.io  
**CI**: Jenkins • **Container**: Docker + Kubernetes

## Run all (UI + API)
```bash
mvn clean test -pl tests -Denv=qa -DrunMode=local -Dbrowser=chrome -Dheadless=true
```

## Run only UI / API
```bash
mvn clean test -pl tests -Dcucumber.filter.tags="@ui"
mvn clean test -pl tests -Dcucumber.filter.tags="@api"
```

## Selenium Grid
```bash
docker compose up -d
mvn clean test -pl tests -DrunMode=grid -DgridUrl=http://localhost:4444/wd/hub -Dcucumber.filter.tags="@ui"
docker compose down
```


# Reports
Extent Spark: `tests/target/extent/SparkReport/Spark.html`
Cucumber html/json: `tests/target/cucumber/`
ReportPortal: configure `tests/src/test/resources/reportportal.properties`.

## ReportPortal Usage
1. Configure your ReportPortal server, API token, and project in `tests/src/test/resources/reportportal.properties`.
2. Enable ReportPortal for your test run:
	- Add `-Drp.enable=true` to your Maven command.
	- Optionally set launch name and attributes:
	  - `-Drp.launch='KC-<env>-<suite>-<build>-<gitsha>'`
	  - `-Drp.attributes='env:<env>;suite:<suite>;build:<build>'`
3. After execution, view results at your ReportPortal dashboard:
	- `https://your.reportportal.server/ui/#kc-sdet-automation/launches/all`

## Run Single Test / Selected Tests
### Run a single scenario by tag:
```bash
mvn clean test -pl tests -Dcucumber.filter.tags="@your_test_tag"
```

### Run multiple selected scenarios by tags:
```bash
mvn clean test -pl tests -Dcucumber.filter.tags="@tag1 and @tag2"
```

### Run in different environments:
```bash
mvn clean test -pl tests -Denv=qa -Dcucumber.filter.tags="@ui"
mvn clean test -pl tests -Denv=dev -Dcucumber.filter.tags="@api"
mvn clean test -pl tests -Denv=stage -Dcucumber.filter.tags="@ui and @smoke"
```

### Run with custom browser and grid:
```bash
mvn clean test -pl tests -Dbrowser=firefox -DrunMode=grid -DgridUrl=http://localhost:4444/wd/hub -Dcucumber.filter.tags="@ui"
```

## Docker
```bash
docker build -t KC-bdd-tests .
docker run --rm -e TAGS="@api" KC-bdd-tests
```

## Kubernetes
`k8s/KC-bdd-tests-job.yaml`
## Scenario-level parallel execution
Parallel is enabled at Cucumber scenario level (TestNG DataProvider).
Control threads using `-Dthreads`:
```bash
mvn clean test -pl tests -Dthreads=6
```

## Jenkins pipeline split (API + UI)
Jenkinsfile runs API and UI as separate stages (in parallel) and can be toggled with `RUN_API` / `RUN_UI` parameters.

## ReportPortal dynamic launch naming
In Jenkins we inject:
- `-Drp.launch='KC-<env>-<suite>-<build>-<gitsha>'`
- `-Drp.attributes='env:<env>;suite:<suite>;build:<build>'`

## Kubernetes Selenium Grid
Deploy Grid:
```bash
kubectl apply -f k8s/selenium-grid.yaml
```
Run UI suite on Grid:
```bash
kubectl apply -f k8s/KC-bdd-tests-job-grid.yaml
```
---

# Selenium Grid parallel runs (same tests in parallel)
This project supports **scenario-level parallel execution** on **Selenium Grid** using:
- ThreadLocal WebDriver
- Cucumber TestNG DataProvider parallel
- Grid RemoteWebDriver

### Start Grid (local)
```bash
docker compose up -d
```
### Run UI scenarios in parallel on Grid (example: 6 threads)
```bash
mvn clean test -pl tests -Denv=qa -DrunMode=grid -Dbrowser=chrome -DgridUrl=http://localhost:4444/wd/hub -Dthreads=6 -Dcucumber.filter.tags="@ui"
```
> Tip: increase node replicas / sessions to match `-Dthreads`.

---

# Dummy applications for practice (UI + API)

## A) Dummy UI app (static HTML)
A sample UI app is included at:
`sample-apps/ui/index.html`

### Serve it locally
From the repo root:
```bash
python -m http.server 8000
```
Open in browser:
`http://localhost:8000/sample-apps/ui/index.html`

### Run the UI demo scenario
```bash
mvn clean test -pl tests -Denv=qa -Dcucumber.filter.tags="@ui and @demo" -DdummyUiUrl=http://localhost:8000/sample-apps/ui/index.html
```

Credentials used in demo:
- username: `KC`
- password: `sdet123`

## B) Dummy API app using WireMock

### Option 1: Start WireMock via Docker Compose
```bash
docker compose -f docker-compose-mocks.yml up -d
```
WireMock will run on:
`http://localhost:8089`

Run demo API scenario:
```bash
mvn clean test -pl tests -Dcucumber.filter.tags="@api and @demo" -DapiBaseUrl=http://localhost:8089
```

### Option 2: Start WireMock automatically (in-process)
Tag `@mockapi` will start WireMock before the scenario and stop after:
```bash
mvn clean test -pl tests -Dcucumber.filter.tags="@mockapi"
```

Endpoints mocked:
- `POST /api/login` → `{ "token": "dummy-token-123" }`
- `GET /api/products/1` → returns sample product JSON

---

# Reports
- Extent Spark: `tests/target/extent/SparkReport/Spark.html`
- Cucumber: `tests/target/cucumber/`
- ReportPortal: configure `tests/src/test/resources/reportportal.properties` and enable with `-Drp.enable=true`
---

# Video recording (Selenoid) + VNC
Use Selenoid when you want **recorded videos** and **live VNC** access.

### Start Selenoid
```bash
docker compose -f docker-compose-selenoid.yml up -d
```
- Endpoint: `http://localhost:4445/wd/hub`
- UI: `http://localhost:8080`

### Run UI on Selenoid (parallel + video)
```bash
mvn clean test -pl tests -Denv=qa -DrunMode=grid -DgridUrl=http://localhost:4445/wd/hub -Dbrowser=chrome -Dthreads=6 -DenableVnc=true -DenableVideo=true -DvideoName=ui_run.mp4 -Dcucumber.filter.tags="@ui"
```
Videos stored at:
- `tests/target/videos/*.mp4`

> For CI: set a unique `videoName` per build (e.g. `${BUILD_NUMBER}.mp4`).

---

# Helm Chart (Kubernetes): Deploy Grid + Run Tests
A Helm chart is included at: `helm/KC-automation`

### Install (Grid + test job)
```bash
helm install KC-auto ./helm/KC-automation
```

### Override run params (example)
```bash
helm upgrade --install KC-auto ./helm/KC-automation   --set job.tags='@ui'   --set job.threads=6   --set job.gridUrl='http://selenium-hub:4444/wd/hub'
```

### Uninstall
```bash
helm uninstall KC-auto
```


## Jenkins (Docker) – Selenium Grid

This repo ships with a Linux-friendly `Jenkinsfile` that runs Selenium Grid and executes **ui-smoke** (Chrome/Firefox/Edge) + **api-smoke** in parallel using `kc-stack.yml`.

### Jenkins job
- Create a **Pipeline** job → *Pipeline script from SCM*
- Repo: this repository
- Script Path: `Jenkinsfile`

### Agent requirements
- Docker + Docker Compose v2 available on the Jenkins agent (Jenkins-in-Docker is fine, using the host docker socket)

### Parameters
- `ENV` (default `qa`)
- `BASE_URL` (your AUT URL)
- `API_BASE_URL` (default `https://jsonplaceholder.typicode.com`)
- `THREADS` and `RETRY`

### Reports
Build artifacts are stored under `reports/` and Jenkins publishes:
- Extent Spark (per-suite)
- Allure HTML (per-suite, generated automatically via `scripts/generate_allure_reports.sh`)
