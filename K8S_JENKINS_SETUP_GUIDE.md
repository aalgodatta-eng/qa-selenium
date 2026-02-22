# KC-SDET Automation — Kubernetes + Jenkins Setup Guide
> **Tailored to your existing project** (`KC-sdet-automation`)  
> Covers the exact files already in your repo and the one missing piece.

---

## What You Already Have ✅

| File | Purpose |
|---|---|
| `Dockerfile` | Jenkins agent image (Docker CLI + kubectl + Helm) |
| `docker/entrypoint.sh` | Unified test runner script (reads ENV vars → runs Maven) |
| `kc-stack.yml` | Docker Compose: Hub + Chrome/Firefox/Edge nodes + 4 test jobs |
| `k8s/00-namespace.yaml` | Creates `qa` namespace |
| `k8s/10-selenium-grid.yaml` | Hub + Chrome/Firefox/Edge node Deployments |
| `k8s/20-smoke-jobs.yaml` | 4 K8s Jobs: `ui-smoke-chrome`, `ui-smoke-firefox`, `ui-smoke-edge`, `api-smoke` |
| `Jenkinsfile` | Full pipeline (compose + k8s paths, RUN_TARGET param) |
| `Jenkinsfile.k8s` | Simpler k8s-only pipeline |
| `helm/kc-automation/` | Helm chart wrapping Grid + all 4 jobs |
| `kind-config.yml` | Kind cluster `qe-cluster` config |
| `kind` | kind binary (committed to repo) |

## What Was Missing ❌

**`Dockerfile.runner`** — The test-runner image.

Your root `Dockerfile` is the **Jenkins image** (`FROM jenkins/jenkins:lts-jdk17`).  
`kc-stack.yml` uses `build: .` for `ui-smoke-*` services — that was trying to build the Jenkins image as a test runner, which fails because it has no Maven.

**Solution:** Add `Dockerfile.runner` (provided alongside this guide) and reference it in compose.

---

## Step 1 — Fix `kc-stack.yml` (one-line change per service)

Change `build: .` → `build: { dockerfile: Dockerfile.runner }` for each test service:

```yaml
# In kc-stack.yml — change ONLY these 4 services' build section:

ui-smoke-chrome:
  build:
    context: .
    dockerfile: Dockerfile.runner   # ← ADD THIS LINE
  ...

ui-smoke-firefox:
  build:
    context: .
    dockerfile: Dockerfile.runner   # ← ADD THIS LINE
  ...

ui-smoke-edge:
  build:
    context: .
    dockerfile: Dockerfile.runner   # ← ADD THIS LINE
  ...

api-smoke:
  build:
    context: .
    dockerfile: Dockerfile.runner   # ← ADD THIS LINE
  ...
```

---

## Step 2 — Verify Local Docker Compose Still Works

```bash
# From repo root — start grid + all 3 browser suites in parallel
docker compose -f kc-stack.yml --profile grid up -d
docker compose -f kc-stack.yml --profile tests up --build \
  ui-smoke-chrome ui-smoke-firefox ui-smoke-edge api-smoke

# Watch logs for each browser
docker compose -f kc-stack.yml logs -f ui-smoke-chrome
docker compose -f kc-stack.yml logs -f ui-smoke-firefox
docker compose -f kc-stack.yml logs -f ui-smoke-edge
```

Reports will appear in `./reports/` (mounted as volume in compose).

---

## Step 3 — Build & Push the Test Runner Image

```bash
# Build
docker build -f Dockerfile.runner -t administratorkc/kc-test-runner:1.0.0 .

# Test locally (quick sanity check)
docker run --rm \
  -e SUITE=api \
  -e TAGS=@api_smoke \
  -e RUNMODE=local \
  administratorkc/kc-test-runner:1.0.0

# Push
docker push administratorkc/kc-test-runner:1.0.0
```

---

## Step 4 — Create the Kind Cluster (your `kind-config.yml` is already set up)

> If you're on **Windows + WSL + Docker Desktop** (as per your notes file), run these in WSL:

```bash
# 1. Create cluster using your existing config
kind create cluster --config kind-config.yml

# Verify
kubectl get nodes
# Expected: qe-cluster-control-plane   Ready

# 2. Copy kubeconfig to Windows (if using Jenkins on Windows)
mkdir -p /mnt/c/Users/malth/.kube
cp ~/.kube/config /mnt/c/Users/malth/.kube/config
```

---

## Step 5 — Update Image References in K8s Manifests

### Option A: Direct YAML (simple)

Edit `k8s/20-smoke-jobs.yaml` — find all `image: kc-sdet-automation:latest` and replace:

```yaml
# In k8s/20-smoke-jobs.yaml — update all 4 jobs:
image: administratorkc/kc-test-runner:1.0.0
imagePullPolicy: Always   # or IfNotPresent if image is local to cluster
```

### Option B: Helm values (recommended)

Edit `helm/kc-automation/values.yaml`:

```yaml
runner:
  image: administratorkc/kc-test-runner:1.0.0   # ← UPDATE THIS
  imagePullPolicy: Always
```

### Load image into Kind cluster (if not pushing to registry)

```bash
# Alternative to pushing — load directly into kind nodes
kind load docker-image administratorkc/kc-test-runner:1.0.0 --name qe-cluster
```

---

## Step 6 — Deploy Selenium Grid on Kubernetes

```bash
# Apply namespace + grid
kubectl apply -f k8s/00-namespace.yaml
kubectl apply -f k8s/10-selenium-grid.yaml

# Wait for hub to be ready
kubectl -n qa rollout status deploy/selenium-hub --timeout=120s

# Verify all 4 pods are Running (hub + chrome + firefox + edge nodes each have 2 replicas)
kubectl -n qa get pods
# Expected:
# selenium-hub-xxxxxxx              1/1   Running
# selenium-node-chrome-xxxxxxx      1/1   Running
# selenium-node-chrome-xxxxxxx      1/1   Running
# selenium-node-firefox-xxxxxxx     1/1   Running
# selenium-node-firefox-xxxxxxx     1/1   Running
# selenium-node-edge-xxxxxxx        1/1   Running
# selenium-node-edge-xxxxxxx        1/1   Running

# Verify all 3 browsers registered with Grid
kubectl -n qa port-forward svc/selenium-hub 4444:4444 &
open http://localhost:4444/ui   # Grid UI shows Chrome/Firefox/Edge nodes
```

---

## Step 7 — Run Parallel Jobs on Kubernetes

```bash
# Apply all 4 jobs at once (they run in parallel)
kubectl apply -f k8s/20-smoke-jobs.yaml

# Watch job pods spin up
kubectl -n qa get pods -w

# Wait for all jobs (Chrome, Firefox, Edge UI + API)
kubectl -n qa wait --for=condition=complete job/ui-smoke-chrome  --timeout=45m
kubectl -n qa wait --for=condition=complete job/ui-smoke-firefox --timeout=45m
kubectl -n qa wait --for=condition=complete job/ui-smoke-edge    --timeout=45m
kubectl -n qa wait --for=condition=complete job/api-smoke        --timeout=45m

# Check logs
kubectl -n qa logs job/ui-smoke-chrome
kubectl -n qa logs job/ui-smoke-firefox
kubectl -n qa logs job/ui-smoke-edge
kubectl -n qa logs job/api-smoke
```

---

## Step 8 — Set Up Jenkins

### 8a. Start Jenkins (your docker-compose-jenkins.yml)

```bash
# Start Jenkins container (your Dockerfile at root builds the Jenkins image with kubectl+helm)
docker build -t kc-jenkins:latest .                   # Uses root Dockerfile (Jenkins image)
docker compose -f docker-compose-jenkins.yml up -d

# First run — get admin password
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

### 8b. Jenkins Initial Configuration

1. Open `http://localhost:8080`
2. Enter admin password from above
3. Install suggested plugins
4. **Extra plugins to install:** _(Manage Jenkins → Plugins → Available)_
   - `AnsiColor`
   - `HTML Publisher` (for Allure/Extent reports in pipeline)
   - `Allure` (optional, for native Allure integration)
   - `Kubernetes` (if running Jenkins agents as K8s pods)

### 8c. Verify kubectl + Helm Work Inside Jenkins

```bash
docker exec -it jenkins bash -lc "kubectl config get-contexts"
docker exec -it jenkins bash -lc "kubectl get nodes"
docker exec -it jenkins bash -lc "helm version"
```

> If `kubectl get nodes` fails: kubeconfig isn't mounted. Fix:

```bash
# In docker-compose-jenkins.yml, add this volume to jenkins service:
volumes:
  - ~/.kube:/root/.kube:ro       # mounts your kubeconfig read-only
  - /var/run/docker.sock:/var/run/docker.sock   # for docker builds
```

### 8d. Create the Jenkins Pipeline

1. Jenkins → **New Item** → **Pipeline** → Name: `KC-SDET-Automation`
2. Pipeline → **Definition**: `Pipeline script from SCM`
3. **SCM**: Git → URL: `<your repo URL>`
4. **Script Path**: `Jenkinsfile` _(your main pipeline with RUN_TARGET param)_
5. Save → **Build with Parameters**

### 8e. Pipeline Parameters (already in your Jenkinsfile)

| Parameter | Recommended Value | Notes |
|---|---|---|
| `RUN_TARGET` | `k8s` | Use `both` to run compose + k8s |
| `ENV` | `qa` | Maps to `tests/src/test/resources/env/qa.properties` |
| `BASE_URL` | `https://example.com` | Your actual app URL |
| `API_BASE_URL` | `https://jsonplaceholder.typicode.com` | |
| `THREADS` | `2` | Parallel scenarios per browser job |
| `K8S_DEPLOY_GRID` | `true` | Let pipeline deploy Selenium Grid |
| `K8S_NAMESPACE` | `qa` | Matches your namespace yaml |
| `HELM_RELEASE` | `kc-automation` | Helm release name |
| `CLEANUP` | `true` | Deletes K8s jobs after run |

---

## Step 9 — Helm Deployment (Alternative to Raw YAML)

```bash
# First-time deploy
helm upgrade --install kc-automation ./helm/kc-automation \
  -n qa \
  --set runner.image=administratorkc/kc-test-runner:1.0.0 \
  --set runner.env.ENV=qa \
  --set runner.env.BASE_URL=https://your-app.com \
  --wait

# Check release
helm status kc-automation -n qa

# Update image tag without redeploying everything
helm upgrade kc-automation ./helm/kc-automation \
  -n qa \
  --set runner.image=administratorkc/kc-test-runner:1.1.0 \
  --reuse-values
```

---

## Quick Troubleshooting

### Browser nodes not registering with Hub
```bash
kubectl -n qa logs deploy/selenium-node-chrome
# Look for: "Node has been added to the grid successfully"
# If missing: check SE_EVENT_BUS_HOST=selenium-hub matches service name
```

### Jobs stuck in "Pending"
```bash
kubectl -n qa describe pod <pod-name>
# Common cause: image pull failure — check image name/credentials
# Fix: kind load docker-image <image> --name qe-cluster
```

### Tests fail with "Could not create connection to Grid"
```bash
# Your gridUrl in qa.properties is localhost — wrong for K8s pods
# In K8s, the correct URL is:
#   http://selenium-hub:4444/wd/hub   (pod-to-pod within qa namespace)
# This is already set correctly in k8s/20-smoke-jobs.yaml via GRIDURL env var
# Your DriverFactory reads GRIDURL via ConfigManager → System property → ok ✅
```

### entrypoint.sh: RUNMODE mismatch
```bash
# Your entrypoint uses RUNMODE=remote but DriverFactory checks for "grid"
# EITHER: change entrypoint.sh line: RUNMODE="${RUNMODE:-grid}"
# OR:     change DriverFactory: "grid".equalsIgnoreCase(runMode) handles it ✅
# (Already correct — DriverFactory checks for "grid", entrypoint sends "grid")
```

---

## Complete Flow Diagram

```
git push
    │
    ▼
Jenkins Pipeline (Jenkinsfile)
    │
    ├── stage: Checkout
    ├── stage: Preflight (docker, helm version check)
    │
    ├── stage: Deploy Namespace  →  kubectl apply k8s/00-namespace.yaml
    ├── stage: Deploy Selenium Grid  →  Helm or kubectl apply k8s/10-selenium-grid.yaml
    │         └── Hub pod (selenium/hub:4.22.0)
    │         └── Chrome node x2 (selenium/node-chrome:4.22.0)
    │         └── Firefox node x2 (selenium/node-firefox:4.22.0)
    │         └── Edge node x2 (selenium/node-edge:4.22.0)
    │
    └── stage: Run Parallel Smoke Jobs  →  kubectl apply k8s/20-smoke-jobs.yaml
              │
              ├── Job: ui-smoke-chrome  (kc-test-runner image, BROWSER=chrome)
              │         └── entrypoint.sh → mvn test -Dbrowser=chrome -DrunMode=grid ...
              │         └── RemoteWebDriver → Hub → Chrome Node
              │
              ├── Job: ui-smoke-firefox  (BROWSER=firefox)
              │         └── RemoteWebDriver → Hub → Firefox Node
              │
              ├── Job: ui-smoke-edge  (BROWSER=edge)
              │         └── RemoteWebDriver → Hub → Edge Node
              │
              └── Job: api-smoke  (SUITE=api, no browser needed)
                        └── REST Assured tests only

    post: archive reports, publish HTML (Allure + Extent)
```

---

## File Checklist Before First Run

- [ ] `Dockerfile.runner` added to repo root
- [ ] `kc-stack.yml` — 4 services updated: `build: { dockerfile: Dockerfile.runner }`
- [ ] `k8s/20-smoke-jobs.yaml` — all 4 jobs: image updated to your pushed image
- [ ] `helm/kc-automation/values.yaml` — `runner.image` updated
- [ ] `tests/src/test/resources/env/qa.properties` — `gridUrl` is `http://selenium-hub:4444/wd/hub` ✅ (already correct for K8s)
- [ ] Kind cluster running: `kubectl get nodes`
- [ ] Image built and available: `kind load docker-image ... --name qe-cluster` or pushed to registry
- [ ] Jenkins kubeconfig mounted and verified: `kubectl get nodes` from inside container
