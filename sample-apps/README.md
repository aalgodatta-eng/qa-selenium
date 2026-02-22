# Sample Apps (Dummy UI + Dummy API)

## Dummy UI
- Path: `sample-apps/ui/index.html`
- Purpose: simple login form to practice Selenium (POM/PageFactory)

## Dummy API (WireMock)
- Stubs live under: `tests/src/test/resources/wiremock`
- Use docker-compose: `docker compose -f docker-compose-mocks.yml up -d`
- Base URL: `http://localhost:8089`
