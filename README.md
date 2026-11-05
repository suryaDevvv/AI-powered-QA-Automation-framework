# AI-Driven Web & API Testing Automation Framework

An automation framework that runs browser, API, and optional MySQL checks against a public ecommerce practice site. It records test results, attaches screenshots to failed UI tests, writes a structured bug report for every failure, and can ask an OpenAI model for an optional failure summary.

The default target is [Automation Exercise](https://automationexercise.com), whose published practice scenarios cover registration, product search and details, category browsing, cart quantity/removal, checkout, and account/product APIs. This repository tests that public practice site; it does not implement or host an ecommerce application.

## Tech stack and pinned versions

- Java 21
- Maven
- TestNG 7.12.0
- Selenium Java 4.52.0; Chrome driver is resolved by Selenium Manager
- REST Assured 6.3.0
- Postman collection, run in CI with Newman 6.2.3
- MySQL Connector/J 26.10.0
- Allure TestNG 2.35.5, Allure Maven plugin 3.1.0, Allure report runtime 3.17.0
- AspectJ Weaver 1.9.25.1 for Allure step annotations
- Jackson Databind 2.22.4
- Maven Compiler Plugin 3.14.1, Maven Surefire Plugin 3.5.4, Maven Dependency Plugin 3.11.0

The practice website creates unique temporary accounts for the tests. These are test records, not private credentials. No GitHub secrets are required for the normal CI runs.

## What is covered

- UI: valid/invalid login; account registration, duplicate email, and empty-password validation; product search, category filter, and details; add/remove/update cart quantity; checkout address review, successful checkout, and missing payment-field validation.
- API: product listing/search and independent account create/read/update/delete tests, plus update rejection with invalid credentials.
- Database: optional read-only MySQL connection check. The public practice site does not expose its application database, so this check only runs when a MySQL test database is supplied. Application data assertions need that application's schema and query details.
- Reporting: Allure steps/results and screenshots on UI failures; structured Markdown reports in target/bug-reports; optional AI triage.
- CI: smoke tests on every push; full regression tests on pull requests and manual runs. Both jobs also run the Postman collection and upload Allure results/report, Newman output, and bug reports.

## Configure

Base URLs, timeouts, and test values are in src/test/resources/config.properties. Override a value with its Java system property or environment variable. System properties take precedence over environment variables, which take precedence over the config file.

| Setting | System property | Environment variable |
| --- | --- | --- |
| Website URL | ecommerce.base-url | ECOMMERCE_BASE_URL |
| API URL | ecommerce.api-base-url | ECOMMERCE_API_BASE_URL |
| Test account password | test.account.password | TEST_ACCOUNT_PASSWORD |
| Headless Chrome | headless | HEADLESS |
| Explicit wait seconds | wait.timeout.seconds | WAIT_TIMEOUT_SECONDS |
| API connect timeout | api.connect-timeout-ms | API_CONNECT_TIMEOUT_MS |
| API response timeout | api.read-timeout-ms | API_READ_TIMEOUT_MS |
| API connection request timeout | api.connection-request-timeout-ms | API_CONNECTION_REQUEST_TIMEOUT_MS |
| MySQL URL | mysql.url | MYSQL_URL |
| MySQL user | mysql.username | MYSQL_USER |
| MySQL password | mysql.password | MYSQL_PASSWORD |
| MySQL health query | mysql.health-query | MYSQL_HEALTH_QUERY |

For optional AI triage, set OPENAI_ENABLED=true, OPENAI_API_KEY, and optionally OPENAI_MODEL. The code sends a redacted test failure summary and API response evidence to the configured OpenAI API; it does not send screenshots. Keep your API key in a local environment variable and do not commit it. CI does not use an AI key; it still creates a detailed structured failure report.

## Run checks locally

Run all TestNG groups, including the optional DB test (which skips if MYSQL_URL is unset):

    mvn clean test

Select a group:

    mvn test -Dgroups=smoke
    mvn test -Dgroups=regression
    mvn test -Dgroups=ui
    mvn test -Dgroups=api
    mvn test -Dgroups=db

Run UI tests in headless Chrome:

    mvn test -Dgroups=ui -Dheadless=true

Run the Postman collection locally (Node.js and npm required):

    npx --yes newman@6.2.3 run postman/collections/ecommerce-api.postman_collection.json

To use the same requests in the Postman desktop app, import the collection JSON and the ecommerce-local.example.json environment from postman/environments. Activate that environment before running the collection. The collection creates a unique account for the request flow and removes it at the end.

API tests retry once on failure. UI tests do not retry. REST Assured has explicit connect, response, and connection request timeouts. UI interactions use explicit waits; no fixed sleeps are used.

## Allure report and failure reports

After running tests, create and open a report:

    mvn allure:serve

Or generate a static report:

    mvn allure:report

Raw results are stored in target/allure-results; the static report is generated under target/site/allure-maven-plugin. A UI failure attaches a screenshot to Allure. Every test failure also creates a Markdown report under target/bug-reports/ with the test case ID, failure details, browser URL when available, API evidence when available, screenshot location, and AI triage status.

Test case IDs, steps, expected results, priorities, and TestNG groups are in [docs/test-cases.md](docs/test-cases.md).

## Project structure

    qa-automation-framework/
    ├── .github/workflows/ci.yml
    ├── docs/test-cases.md
    ├── postman/
    │   ├── collections/ecommerce-api.postman_collection.json
    │   └── environments/ecommerce-local.example.json
    ├── src/test/java/
    │   ├── pages/                  # Login, registration, products, details, cart, checkout
    │   └── tests/
    │       ├── api/                # REST Assured API checks
    │       ├── db/                 # Optional MySQL integration check
    │       ├── support/            # Config, browser, data, API, retry, and failure reporting
    │       └── ui/                 # Login, registration, products, cart, and checkout tests
    └── src/test/resources/
        ├── allure.properties
        └── config.properties

## External dependencies

All UI and API tests depend on automationexercise.com being reachable and behaving as documented. Site downtime, network issues, rate limits, changed selectors, and shared practice-site load can cause failures unrelated to this repository. API tests create unique accounts and remove them after each check. The MySQL check is skipped unless a test database is configured.
