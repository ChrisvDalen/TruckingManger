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

_Updated at the end of the mission._

### Executive summary

The repository started as a small, cleanly-structured two-module game that
**built and passed its 3 backend service tests and 2 frontend tests**, but had
no API/controller tests, no input validation on the write endpoints, a
hard-coded CORS origin, a hard-coded backend URL in the frontend, an outdated
Spring Boot patch, 5 known frontend dependency vulnerabilities, and no
security gate or coverage visibility in CI.

It is now in a substantially more production-ready state:

- **Backend**: 30 tests (from 3), 87% instruction coverage, typed validated
  request DTOs, a global `@RestControllerAdvice` error handler, configurable
  CORS, graceful shutdown config, and JaCoCo coverage wired into the build.
- **Frontend**: 12 tests (from 2), 0 npm-audit vulnerabilities, a relative
  `/api` base URL with a dev proxy (fixing the production deployability
  blocker), and a production build that stays under the size budgets.
- **CI**: a frontend dependency-vulnerability gate (`npm audit`) and Node
  aligned to the tested LTS.
- **Docs**: README documents the new run/test/proxy configuration.

All changes are on the `openhands/production-readiness` branch (3 commits on
top of `main`); nothing was merged to the default branch.

### Changes implemented

| # | Area | Change |
| --- | --- | --- |
| 1 | Backend LCM | Spring Boot parent 4.1.0 → 4.1.1 |
| 2 | Backend robustness | Typed request DTOs (`BuyTruckRequest`, `HireDriverRequest`, `AcceptJobRequest`) with Bean Validation replace raw `Map<String,Object>` + unsafe casts |
| 3 | Backend robustness | `@RestControllerAdvice` `GlobalExceptionHandler` maps `GameRuleException`, validation errors and malformed JSON to clean 400s instead of 500s |
| 4 | Backend config | `WebConfig` CORS origins now configurable via `app.cors.allowed-origins` |
| 5 | Backend config | `application.properties` with configurable port + `server.shutdown=graceful` |
| 6 | Backend tests | Controller tests for all 4 controllers (17 tests) + expanded `GameService` scenarios (13 tests) via standalone `RestTestClient` |
| 7 | Backend coverage | JaCoCo plugin (`0.8.15`) reporting on `verify` |
| 8 | Frontend LCM | `vitest` 4.1.10 → 4.1.11 (patch); moved to `devDependencies` |
| 9 | Frontend security | npm `overrides` for `fast-uri`, `hono`, `qs`; `@vitest/mocker` bumped → **0 vulnerabilities** |
| 10 | Frontend config | `Api` base URL `http://localhost:8080/api` → relative `/api` + `proxy.conf.json` dev proxy |
| 11 | Frontend tests | `Api` service endpoint/payload tests (6) + `Dashboard`/`Jobs` component tests (4) → 12 total |
| 12 | CI | Frontend `npm audit --audit-level=low` gate; Node 22 (LTS) |
| 13 | Docs | README updated for run/test/proxy configuration |

### Lifecycle management (LCM)

| Component | Previous | New | Notes |
| --- | --- | --- | --- |
| Spring Boot | 4.1.0 | 4.1.1 | Patch; re-validated full build + 30 tests |
| Vitest | 4.1.10 | 4.1.11 | Patch; re-validated `ng test` + `ng build` |
| `@vitest/mocker` | (transitive) | 4.1.11 | Via override; resolves the path-traversal advisory |
| `fast-uri` | 3.1.5 | 3.1.8 | Override; same major, satisfies ajv `^3.0.1` |
| `hono` | ≤4.13.4 | 4.13.9 | Override; test-only (vitest) |
| `qs` | ≤6.15.3 | 6.16.0 | Override; test-only |

**Migrations performed:** none required — all upgrades were patch-level or
additive overrides within the same major versions; no API/config changes were
needed beyond re-validating the build and tests.

**Remaining lifecycle risks:**
- `spring-boot-starter-webmvc`, `validation` and `test` are all managed by the
  4.1.1 BOM — no unpinned, vulnerable versions. Dependabot (weekly, maven + npm
  + github-actions) keeps these current.
- The `overrides` block in `package.json` should be re-evaluated when `vitest`
  moves to a new 4.x/5.x release; it is a deliberate, documented pin of
  dev-only transitive deps and can be removed once the upstream toolchain pulls
  in patched versions.

### Testing

**Original situation:** 3 backend tests (all `GameService`) + 2 frontend
smoke tests (app boot). No controller/API, no error-path, no payload-contract
tests. No coverage reporting.

**Tests added:**
- Backend: `TrucksControllerTest` (6), `JobsControllerTest` (5),
  `DriversControllerTest` (4), `GameControllerTest` (2), plus
  `GameServiceTest` expanded to 13 (job lifecycle, cash math, buy/hire
  validation, insufficient-funds and invalid-input error paths). Total **30**.
- Frontend: `Api` (6 — every endpoint's URL + request payload against the
  backend contract), `Dashboard` (2), `Jobs` (2). Total **12**.

**Coverage achieved (backend, JaCoCo, instruction):** 87.3% overall.
Controllers: `TrucksController`, `JobsController`, `DriversController` and
`GlobalExceptionHandler` at **100%**; `GameService` at **97%**; models
`Truck`/`Job`/`Driver` at **100%**. Uncovered remainder is `WebConfig` (a thin
CORS pass-through of a property into Spring's own tested machinery — see
"Remaining risks"), the `main` method, and a few defensive branches in
`Company`. This exceeds the 80% target with risk-focused placement rather than
padded numbers.

**Important scenarios now protected:** API routing and JSON contract for all
write/read endpoints; rejection of malformed and out-of-range input (clean 400
instead of 500); full job lifecycle and cash accounting; the frontend's exact
request URLs and payload shapes.

### Security

**Findings → resolved:**
- **H1 (high) frontend dependency vulnerabilities** → resolved to **0** via a
  patch bump + `overrides` (all dev-only; none ship in the production bundle).
- **M2** unsafe request-body casting → typed validated DTOs + `GlobalExceptionHandler`.
- **M3** hard-coded CORS origin → configurable `app.cors.allowed-origins`.
- **M4** hard-coded backend URL → relative `/api` (no host pinned in the bundle).

**Remaining / documented:**
- No secrets are stored or committed; no credentials in the codebase.
- The app is a single-process, in-memory game with **no authentication
  surface and no persistence**, so authn/authz, secrets-at-rest and
  DB-configuration concerns do not apply. This is by design for the app's scope.
- `WebConfig`'s CORS pass-through is not directly unit-tested (Spring Boot 4
  removed `@WebMvcTest` and `CorsRegistry` internals are protected). Its
  behavior is a one-line delegation to Spring's well-tested CORS machinery;
  the risk is a typo in the property name, which is covered by the dev default
  and the README. Acceptable residual risk.

### Architecture

No risky refactors were required; the codebase is small and already well
structured. Notable decisions:
- Introduced a `web`/`controller`/`service`/`model`/`dto` split with DTOs at the
  boundary so domain models are not exposed to raw untrusted input.
- Added a small `reset()` test seam on `Company`/`GameService` (production-
  harmless) to keep controller tests isolated without a real DB.
- **Considered and deliberately not done:** adding Jackson `@JsonCreator` to
  the model classes to make them round-trippable. The app only *serializes*
  these to output (input always arrives via DTOs), so coupling the domain to
  the serializer for a need that doesn't exist was rejected as a design smell;
  tests read the live bean instead of round-tripping JSON. Documented in
  `ApiTestBase`.

**Remaining technical debt:** model `id`s come from a static `AtomicLong`
counter (fine for a single in-memory process; would need a real ID strategy if
persistence were added).

### CI/CD

- Frontend job now runs `npm audit --audit-level=low` (fails on any known
  vulnerability) and uses Node 22 LTS, matching the engines range and the
  runtime the frontend was validated on.
- Backend job already runs `./mvnw verify`, which now includes the JaCoCo
  coverage report.
- `permissions: contents: read` and Dependabot (weekly) were already present
  and remain.

### Validation

Final clean results (all run after all changes):

| Command | Result |
| --- | --- |
| `cd backend && ./mvnw clean verify` | **BUILD SUCCESS** — Tests run: 30, Failures: 0, Errors: 0; JaCoCo report generated |
| `cd frontend/trucking-manager-frontend && npm ci` | install OK |
| `npm audit --audit-level=low` | **found 0 vulnerabilities** |
| `npm test` | **Test Files 4 passed, Tests 12 passed** |
| `npm run build` | production build OK, initial 294 kB (79 kB gz) — under budget |

No secrets were introduced (verified by inspection; no new files containing
credentials). README and this document reflect the resulting state.

### Remaining risks

1. **No containerization / deployment target** — no Dockerfile or deployment
   pipeline. This is out of scope for a single-process in-memory game with no
   defined deploy target; adding it would be speculative infrastructure. If a
   real deployment is planned, a multi-stage Dockerfile (JRE base image for the
   backend, static server for the frontend behind a reverse proxy that routes
   `/api`) is the natural next step.
2. **Frontend HTTP error handling** — component `subscribe()` callbacks have no
   `error` handlers; a failed call is silent. Low user-impact for this app but
   worth an error-toast if the product grows.
3. **`WebConfig` CORS pass-through not directly unit-tested** (see Security).
4. **`overrides` block** is a deliberate pin that should be revisited on the
   next vitest major.

### Recommended next actions (prioritized)

1. If deployment is imminent: add a multi-stage Dockerfile + a reverse-proxy
   config (Caddy/nginx) that serves the frontend and forwards `/api`.
2. Add a lightweight frontend global HTTP error handler (interceptor) so API
   failures surface to the user.
3. Add a JaCoCo `<check>` rule in the backend build to enforce the ≥80%
   threshold in CI (currently reported, not enforced).
4. Revisit the frontend `overrides` on the next `vitest` upgrade.
5. Add an `application.properties` for a production profile (e.g. a real
   `app.cors.allowed-origins` value and log level) if a non-localhost origin
   is introduced.
