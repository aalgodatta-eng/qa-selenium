# Tesco SDET Automation Framework (Selenium + API + DB + WireMock + Extent + Allure)

## Quick start
```bash
mvn clean test -pl tests -Denv=qa -DrunMode=local -Dbrowser=chrome -Dheadless=true -DsuiteXml=testng/smoke.xml -Dthreads=4
```

## Selenium Grid
```bash
docker compose up -d
mvn clean test -pl tests -Denv=qa -DrunMode=grid -Dbrowser=edge -DgridUrl=http://localhost:4444/wd/hub -DsuiteXml=testng/smoke.xml -Dthreads=4
docker compose down
```

## Reports
- Extent: `tests/target/extent-report/index.html`
- Allure results: `tests/target/allure-results/`
Generate Allure report:
```bash
mvn -pl tests allure:report
```
