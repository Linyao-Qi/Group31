# weblogin Final Test Program

This folder contains the test program for the final integrated version of the project: `weblogin`.

The purpose of this test program is to support the final coursework submission requirement for **Test programs**. It verifies the main business logic, CSV persistence, cross-role workflows, and important web entry points of the TA recruitment system.

## Files

- `FullSystemTestRunner.java`: main test runner. It uses simple built-in assertions and does not require JUnit.
- `run-tests.ps1`: PowerShell script that compiles the project source code and runs the test runner.
- `README.md`: this test guide.

## How To Run

Open PowerShell in the `weblogin` directory and run:

```powershell
.\test\run-tests.ps1
```

The script will:

1. Compile the Java source files and the test runner.
2. Create isolated test CSV data under `build/test-data/data`.
3. Run all test cases.
4. Print `[PASS]` or `[FAIL]` for each test group.
5. Delete temporary test build files after the run.

The test program does **not** modify the real application data in `weblogin/data`.

## Expected Output

A successful run should end with:

```text
==== weblogin full system test summary ====
Total: 12, Passed: 12, Failed: 0
```

If any test fails, the script exits with code `1` and prints the failing test name and error message.

## Coverage

### 1. Authentication

Checks role-based login logic for:

- Admin users
- Module Organizer users
- Teaching Assistant users
- wrong passwords
- blank input
- cross-role login rejection

### 2. TA Registration

Checks:

- successful TA account registration
- duplicate TA ID rejection
- password confirmation validation
- non-TA registration rejection
- authentication with the newly registered TA account
- registration writes only to isolated test `auth.csv`

### 3. CSV Data Handling

Checks:

- job CSV read/write
- application CSV read/write
- quoted fields containing commas
- malformed CSV rows
- UTF-8 file handling

### 4. MO Functions

Checks:

- publishing a job
- rejecting invalid publish input
- rejecting invalid MO credentials
- accepting applicants
- rejecting applicants
- cancelling accepted applicants
- cancelling rejected applicants
- enforcing `maxHire`
- changing job status between `OPEN` and `FILLED`

### 5. TA Functions

Checks:

- applying for a job
- duplicate application prevention
- withdrawing pending applications
- blocking withdrawal of accepted applications
- reading job details
- reading and updating TA profiles
- searching jobs by subject, status, and keyword
- generating job recommendations
- generating resume improvement suggestions

### 6. Admin Functions

Checks:

- admin credential validation
- workload loading
- workload total-hour recalculation
- overload detection
- workload filtering and sorting
- marking overloaded workloads as `Cancel`
- closing recruitment posts
- persisting recruitment post status
- syncing cancelled workload status back to applications
- generating recruitment report summaries
- exporting recruitment report CSV text

### 7. Web Entry Points

Checks that important routes and JSP pages exist, including:

- Admin pages: login, home, workloads, posts, report
- MO pages: login, publish job, applicant list, applicant review
- TA pages: login, home, jobs, apply, status, profile, resume suggestions, recommendations

## Test Data

The test runner writes its own temporary data files:

- `auth.csv`
- `job.csv`
- `application.csv`
- `profiles.csv`
- `workloads.csv`

These files are created under:

```text
build/test-data/data
```

The temporary data is designed to include:

- valid users for all three roles
- open and filled jobs
- pending, accepted, and rejected applications
- TA profiles with different skills
- normal and overloaded workload records

## Limitations

This is mainly a service-level and integration-level test program. It does not start Tomcat or run browser automation.

The following items should still be verified manually during demonstration or acceptance testing:

- real browser navigation
- session timeout behavior
- file upload through the browser
- visual layout of JSP pages
- JavaScript validation on forms
- full Tomcat deployment behavior

## Notes For Final Report

In the final report, this test program can be described as covering:

- unit-level checks for utility and service classes
- integration checks across Admin, MO, and TA workflows
- CSV persistence checks
- acceptance-style checks for the main user stories

Manual testing should be mentioned separately for UI interaction, deployment, screenshots, and live demonstration tasks.
