# kc-automation Helm chart

This chart deploys:

- Selenium Grid (hub + browser nodes)
- Parallel smoke Jobs:
  - UI smoke on Linux + Chrome
  - UI smoke on Linux + Firefox
  - UI smoke on Linux + Edge
  - API smoke

## Prerequisites

1) Build & push your test-runner image (or make it available to the cluster):

```bash
docker build -t <your-repo>/kc-test-runner:1.0.0 .
docker push <your-repo>/kc-test-runner:1.0.0
```

2) Update `helm/kc-automation/values.yaml`:

- `runner.image`
- `jobs[*].tags` (for your repo: `@ui_smoke` and `@api_smoke`)
- `extraSystemProperties` (base URLs, tokens, etc.)

## Install / Upgrade

```bash
kubectl create namespace qa --dry-run=client -o yaml | kubectl apply -f -

helm upgrade --install kc-automation ./helm/kc-automation \
  -n qa
```

## Watch

```bash
kubectl -n qa get pods -w
kubectl -n qa get jobs
```

## Logs

```bash
kubectl -n qa logs job/ui-smoke-firefox
kubectl -n qa logs job/ui-smoke-edge
kubectl -n qa logs job/ui-smoke-chrome
kubectl -n qa logs job/api-smoke
```

## Re-run Jobs

Jobs are immutable once completed. Delete and re-install, or delete only Jobs:

```bash
kubectl -n qa delete job ui-smoke-chrome ui-smoke-firefox ui-smoke-edge api-smoke
helm upgrade --install kc-automation ./helm/kc-automation -n qa
```
