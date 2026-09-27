# Production Readiness Assessment — Trucking Manager

> Scope: a small trucking-business game with a Java 25 / Spring Boot backend
> and an Angular 22 frontend. This document records the initial (pre-change)
> assessment, classified by severity, together with the plan and, at the end,
> the final state after remediation.

## Repository overview

| Area | Technology | Target version (as found) |
| --- | --- | --- |
| Backend language | Java | 25 (LTS) |
| Backend framework | Spring Boot | 4.1.0 |
| Build (backend) | Maven (wrapper) | Maven wrapper 3.x |
| Frontend framework | Angular | 22.1.2 |
| Frontend language | TypeScript | 6.0.2 |
| Frontend test | Vitest (via `@angular/build:unit-test`) | 4.1.10 |
| Frontend runtime | Node.js | 22.12–24 (engines `>=22.12 <25`) |
| Build (frontend) | `@angular/build` application builder | 22.1.4 |
| CI | GitHub Actions | checkout@v5, setup-java@v5, setup-node@v5 |
| Dependency updates | Dependabot | weekly (maven, npm, github-actions) |
| Persistence | In-memory only (`Company` singleton) | n/a |
| Containerization | None (no Dockerfile) | n/a |

Baseline (pre-change):
- `./mvnw clean verify` → **BUILD SUCCESS**, 3 service tests pass.
- `npm ci && npm test && npm run build` → all pass (2 app tests).
- `npm audit` → **5 vulnerabilities** (1 high: `fast-uri`, 4 moderate: `@vitest/mocker`/`vitest`, `hono`, `qs`).

---

## Initial findings

### CRITICAL

None identified. There is no authentication surface, no persistence of
sensitive data, and no deserialization of untrusted types.

### HIGH

- **H1 — Frontend dependency vulnerabilities (`npm audit`).**
  - *Problem:* `fast-uri` (high) and `@vitest/mocker`/`hono`/`qs` (moderate)
    are pulled in by the frontend toolchain and are below patched versions.
  - *Impact:* Build/dev-tooling supply-chain risk. These are not present in the
    production static bundle, so runtime exposure is low, but the high-severity
    item should still be remediated.
  - *Proposed solution:* Bump `vitest` to a patched 4.x patch release and pin
    patched versions of the transitive dev-only deps via npm `overrides`.
  - *Implementation risk:* Low — dev-only; must re-validate `ng build`/`ng test`.
  - *Planned this mission:* **Yes.**

### MEDIUM

- **M1 — No API/controller tests.**
  - *Problem:* Only 3 unit tests exist, all on `GameService`. The four
    controllers (game, trucks, drivers, jobs) and the JSON contract have no
    tests.
  - *Impact:* Refactors or routing changes could silently break the API
    contract the frontend depends on.
  - *Proposed solution:* Add `MockMvc` controller tests plus additional
    `GameService` scenarios (full job lifecycle, cash math, error paths).
  - *Implementation risk:* Low. *Planned:* **Yes.**

- **M2 — Unsafe request-body handling in controllers.**
  - *Problem:* Controllers accept `Map<String, Object>` and cast values
    (`(Number) body.getOrDefault("salary", 200)`). A malformed body (e.g.
    `"salary": "abc"`) triggers a `ClassCastException` → HTTP 500 instead of a
    clean 400.
  - *Impact:* Poor error handling / availability; inconsistent contract.
  - *Proposed solution:* Introduce typed request DTOs with Bean Validation and
    a `@RestControllerAdvice` that maps `GameRuleException` and validation
    errors to 400.
  - *Implementation risk:* Low. *Planned:* **Yes.**

- **M3 — CORS origin hard-coded.**
  - *Problem:* `WebConfig` hard-codes `http://localhost:4200` as the only
    allowed origin.
  - *Impact:* Not deployable to a real origin without a code change.
  - *Proposed solution:* Make allowed origins configurable via
    `app.cors.allowed-origins` with the dev default preserved.
  - *Implementation risk:* Low. *Planned:* **Yes.**

- **M4 — Hard-coded backend URL in frontend `Api`.**
  - *Problem:* `Api` hard-codes `http://localhost:8080/api`.
  - *Impact:* Frontend cannot talk to a different backend host in production.
  - *Proposed solution:* Expose the base URL through an Angular `environment`
    value.
  - *Implementation risk:* Low. *Planned:* **Yes.**

- **M5 — Spring Boot patch outdated.**
  - *Problem:* Parent is 4.1.0 while 4.1.1 is available.
  - *Impact:* Misses a patch-level security/bug fix release.
  - *Proposed solution:* Upgrade to 4.1.1.
  - *Implementation risk:* Low. *Planned:* **Yes.**

### LOW

- **L1 — No coverage reporting in CI.**
  - *Proposed solution:* Add JaCoCo (backend) and report; wire coverage into
    the backend CI job. *Planned:* **Yes** (proportionate to repo size).
- **L2 — No `application.properties` / graceful-shutdown config.**
  - *Proposed solution:* Add a minimal `application.properties` with a
    configurable server port and `server.shutdown=graceful`. *Planned:* **Yes.**
- **L3 — No Dockerfile / no frontend error handling.**
  - *Note:* The app is a single-process, in-memory game with no deployment
    target defined. Adding containers/observability would be speculative
    infrastructure for this codebase, so these are documented, not
    implemented. Frontend HTTP calls currently ignore errors; a lightweight
    note is added. *Planned:* documented only.

---

## Plan

1. **LCM:** Spring Boot 4.1.0 → 4.1.1; `vitest` → patched 4.x; override the
   vulnerable dev-only transitive deps.
2. **Backend robustness:** typed request DTOs + Bean Validation,
   `@RestControllerAdvice`, configurable CORS, `application.properties`.
3. **Backend tests:** controller tests (all four) + expanded service tests.
4. **Frontend:** environment-based API base URL + tests for `Api` and the
   feature components.
5. **CI:** coverage reporting for the backend.
6. Update this document with the final state.

---

## Final report

_Updated at the end of the mission — see below._
