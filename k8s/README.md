# KC SDET Automation - Kubernetes Execution

This folder provides a minimal, production-friendly pattern to execute **parallel smoke suites**:

- UI smoke: Linux + **Firefox** (remote via Selenium Grid)
- UI smoke: Linux + **Edge** (remote via Selenium Grid)
- API smoke

## 1) Build & push test-runner image

From repo root:

```bash
docker build -t <your-dockerhub-user>/kc-sdet-automation:1.0.0 .
docker push <your-dockerhub-user>/kc-sdet-automation:1.0.0
```

Then update `k8s/20-smoke-jobs.yaml` image fields.

## 2) Deploy Selenium Grid

```bash
kubectl apply -f k8s/00-namespace.yaml
kubectl apply -f k8s/10-selenium-grid.yaml
kubectl -n qa get pods
```

## 3) Run all 3 suites in parallel

```bash
kubectl apply -f k8s/20-smoke-jobs.yaml
kubectl -n qa get jobs
kubectl -n qa get pods -w
```

Logs:

```bash
kubectl -n qa logs job/ui-smoke-firefox
kubectl -n qa logs job/ui-smoke-edge
kubectl -n qa logs job/api-smoke
```

Wait for completion:

```bash
kubectl -n qa wait --for=condition=complete job/ui-smoke-firefox --timeout=30m
kubectl -n qa wait --for=condition=complete job/ui-smoke-edge --timeout=30m
kubectl -n qa wait --for=condition=complete job/api-smoke --timeout=30m
```
