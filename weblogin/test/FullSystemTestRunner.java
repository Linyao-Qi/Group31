import Admin.AdminAuthService;
import Admin.AdminDataManagement;
import Admin.AdminPostDomainService;
import Admin.AdminRecruitment;
import Admin.AdminRecruitmentReportService;
import Admin.AdminService;
import Admin.AdminWorkload;
import Admin.AdminWorkloadDomainService;
import TA.ResumeSuggestion;
import TA.ResumeSuggestionService;
import TA.TAApplicationService;
import TA.TAAuthService;
import TA.TAProfile;
import TA.TAProfileService;
import TaJob.JobRecommendation;
import TaJob.TaJobService;
import TaJob.JobRecommendationService;
import com.Application;
import com.AuthUtil;
import com.CsvFileUtil;
import com.Job;
import com.MoApplicantReviewService;
import com.MoService;
import com.SkillMatchUtil;
import com.TaCsvUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * End-to-end test runner for the final weblogin version.
 *
 * <p>The tests use an isolated copy of CSV data under build/test-data/data, so
 * running this class does not change the real data folder used by the web app.</p>
 */
public class FullSystemTestRunner {
    private static final Path PROJECT_ROOT = Paths.get("").toAbsolutePath();
    private static final Path TEST_DATA_DIR = PROJECT_ROOT.resolve("build").resolve("test-data").resolve("data");
    private static final List<TestResult> RESULTS = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        System.setProperty("tajobsystem.data.dir", TEST_DATA_DIR.toString());

        Map<String, TestCase> tests = new LinkedHashMap<>();
        tests.put("Authentication accepts only correct role credentials", FullSystemTestRunner::testAuthentication);
        tests.put("CSV utilities preserve quoted fields and ignore invalid rows", FullSystemTestRunner::testCsvUtilities);
        tests.put("MO can publish jobs and invalid credentials are rejected", FullSystemTestRunner::testMoPublishJob);
        tests.put("TA application flow prevents duplicates and supports withdrawal", FullSystemTestRunner::testTaApplicationFlow);
        tests.put("MO applicant decisions honor status and max-hire rules", FullSystemTestRunner::testMoApplicantDecisionFlow);
        tests.put("MO review and skill matching use job and TA profile data", FullSystemTestRunner::testMoReviewAndSkillMatching);
        tests.put("TA profile, job search, recommendations, and resume suggestions work", FullSystemTestRunner::testTaProfileRecommendationsAndResumeSuggestions);
        tests.put("Admin workload and post management rules work", FullSystemTestRunner::testAdminWorkloadAndPostManagement);
        tests.put("Admin sync converts cancelled workload back to application status", FullSystemTestRunner::testAdminAcceptedApplicationSyncAndCancellation);
        tests.put("Admin recruitment report summarizes and exports CSV data", FullSystemTestRunner::testAdminRecruitmentReport);
        tests.put("Web routes and JSP entry points for main features exist", FullSystemTestRunner::testWebRoutesAndPages);

        for (Map.Entry<String, TestCase> entry : tests.entrySet()) {
            resetDataAndServices();
            run(entry.getKey(), entry.getValue());
        }

        printSummary();
        if (RESULTS.stream().anyMatch(result -> !result.passed)) {
            System.exit(1);
        }
    }

    private static void testAuthentication() throws Exception {
        assertTrue(AuthUtil.authenticateMO("mo001", "Mo@123456"), "MO login should pass");
        assertFalse(AuthUtil.authenticateMO("mo001", "wrong"), "MO wrong password should fail");
        assertTrue(AuthUtil.authenticateTA("TA001", "Ta@123456"), "TA login should pass through AuthUtil");
        assertTrue(AuthUtil.authenticateAdmin("admin001", "Admin@123456"), "Admin login should pass through AuthUtil");
        assertFalse(AuthUtil.authenticateAdmin("TA001", "Ta@123456"), "TA account must not authenticate as admin");

        assertTrue(TAAuthService.authenticateTA("TA002", "Ta@234567"), "TAAuthService should read TA credentials");
        assertFalse(TAAuthService.authenticateTA("mo001", "Mo@123456"), "TAAuthService must reject non-TA users");

        AdminAuthService adminAuthService = new AdminAuthService();
        assertTrue(adminAuthService.validateCredentials("admin001", "Admin@123456"), "AdminAuthService should accept admin");
        assertFalse(adminAuthService.validateCredentials("admin001", "bad"), "AdminAuthService should reject bad password");
        assertFalse(adminAuthService.validateCredentials("", "Admin@123456"), "Blank username should fail");
    }

    private static void testCsvUtilities() throws Exception {
        Path jobFile = TEST_DATA_DIR.resolve("quoted-jobs.csv");
        List<Job> jobs = Arrays.asList(
                new Job("JOB_QUOTE", "mo001", "Software, Engineering", "Lab",
                        "Review \"team\" projects", "Java, Git", 6, "$18/hour", "OPEN", 2),
                new Job("JOB_SIMPLE", "mo002", "Database", "Remote",
                        "SQL support", "SQL", 4, "$16/hour", "CLOSED", 1)
        );
        CsvFileUtil.writeJobListToCsv(jobFile.toString(), jobs);
        List<Job> loadedJobs = CsvFileUtil.readJobListFromCsv(jobFile.toString());
        assertEquals(2, loadedJobs.size(), "Both jobs should reload");
        assertEquals("Software, Engineering", loadedJobs.get(0).getSubject(), "Quoted comma in subject should survive");
        assertEquals("Review team projects", loadedJobs.get(0).getDescription(), "Quoted text should be parsed safely");

        Path appFile = TEST_DATA_DIR.resolve("quoted-applications.csv");
        List<Application> apps = Arrays.asList(new Application(
                "APP_QUOTE", "Alice, Chen", "JOB_QUOTE", "mo001", "TA001",
                "Computer Science", "Enjoys \"teaching\"", "Java,Git",
                "alice@example.edu", "cvs/TA001/a.pdf", "PENDING"
        ));
        CsvFileUtil.writeAppListToCsv(appFile.toString(), apps);
        List<Application> loadedApps = CsvFileUtil.readAppListFromCsv(appFile.toString());
        assertEquals(1, loadedApps.size(), "Application should reload");
        assertEquals("Alice, Chen", loadedApps.get(0).getName(), "Quoted comma in applicant name should survive");

        Files.writeString(appFile,
                "appId,name,jobId,moId,taId,major,intro,skills,email,CVpath,appStatus\n"
                        + "TOO,SHORT\n"
                        + "APP_OK,Alice,JOB_OPEN_A,mo001,TA001,CS,intro,Java,a@b.edu,,PENDING\n",
                StandardCharsets.UTF_8);
        assertEquals(1, CsvFileUtil.readAppListFromCsv(appFile.toString()).size(),
                "Malformed application rows should be skipped");
    }

    private static void testMoPublishJob() {
        MoService service = new MoService();
        int before = service.getAllJobs().size();

        Job rejected = service.publishJob("mo001", "wrong", "Testing", "Lab",
                "Help students", "JUnit", 4, "$15/hour", 1);
        assertNull(rejected, "Invalid MO password should block publishing");

        Job missingSubject = service.publishJobForMo("mo001", "", "Lab",
                "Help students", "JUnit", 4, "$15/hour", 1);
        assertNull(missingSubject, "Blank subject should be rejected");

        Job created = service.publishJob("mo001", "Mo@123456", "Software Testing", "Lab",
                "Guide testing practice", "Java,JUnit", 4, "$15/hour", 2);
        assertNotNull(created, "Valid MO should publish a job");
        assertEquals("OPEN", created.getStatus(), "New job should be OPEN");
        assertEquals(before + 1, service.getAllJobs().size(), "Job CSV should contain the new job");
    }

    private static void testTaApplicationFlow() {
        String result = TAApplicationService.applyForJob(
                "TA004", "JOB_OPEN_A", "mo001", "David Zhang", "Data Science",
                "I can support Java labs", "Java,Git", "david@example.edu", "cvs/TA004/cv.pdf");
        assertEquals("SUCCESS", result, "First TA application should succeed");
        assertTrue(TAApplicationService.hasActiveApplication("TA004", "JOB_OPEN_A"),
                "New application should be active");
        assertEquals("PENDING", TAApplicationService.getActiveApplicationStatus("TA004", "JOB_OPEN_A"),
                "New application should be pending");

        String duplicate = TAApplicationService.applyForJob(
                "TA004", "JOB_OPEN_A", "mo001", "David Zhang", "Data Science",
                "Duplicate", "Java", "david@example.edu", "");
        assertEquals("DUPLICATE", duplicate, "Duplicate active application should be blocked");

        List<Application> taApps = TAApplicationService.getApplicationsByTA("TA004");
        Application created = taApps.stream()
                .filter(app -> "JOB_OPEN_A".equals(app.getJobId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Created application not found"));

        assertTrue(TAApplicationService.withdrawApplication("TA004", created.getAppId()),
                "TA should withdraw their pending application");
        assertFalse(TAApplicationService.hasActiveApplication("TA004", "JOB_OPEN_A"),
                "Withdrawn application should no longer be active");
        assertFalse(TAApplicationService.withdrawApplication("TA002", "APP_B"),
                "Accepted application should not be withdrawn");
        assertEquals("Java", TAApplicationService.getJobById("JOB_OPEN_A").getSubject(),
                "TA should be able to load job details by ID");
    }

    private static void testMoApplicantDecisionFlow() {
        MoService service = new MoService();

        Application accepted = service.acceptApplicant("mo001", "APP_A");
        assertNotNull(accepted, "MO should accept a pending application for own job");
        assertEquals("ACCEPTED", findApplication("APP_A").getAppStatus(), "Accepted application should persist");
        assertEquals("FILLED", findJob("JOB_OPEN_A").getStatus(), "Job should be filled when max hire is reached");

        assertNull(service.acceptApplicant("mo001", "APP_E"),
                "MO must not accept another applicant after max hire is reached");
        assertFalse(service.cancelApplicant("mo002", "APP_A"),
                "Different MO must not cancel another MO's applicant");
        assertTrue(service.cancelApplicant("mo001", "APP_A"),
                "MO should cancel an accepted applicant");
        assertEquals("PENDING", findApplication("APP_A").getAppStatus(), "Cancelled hire should return to pending");
        assertEquals("OPEN", findJob("JOB_OPEN_A").getStatus(), "Job should reopen after accepted count drops");

        assertNotNull(service.rejectApplicant("mo001", "APP_A"), "MO should reject a pending applicant");
        assertEquals("REJECTED", findApplication("APP_A").getAppStatus(), "Rejected status should persist");
        assertNotNull(service.cancelRejectApplicant("mo001", "APP_A"),
                "MO should restore a rejected applicant to pending");
        assertEquals("PENDING", findApplication("APP_A").getAppStatus(), "Restored application should be pending");
        assertEquals(4, service.getAllAppsForMo("mo001").size(), "MO review should list only this MO's applications");
    }

    private static void testMoReviewAndSkillMatching() {
        MoApplicantReviewService reviewService = new MoApplicantReviewService();
        List<Application> moApps = reviewService.getApplications("mo001");
        assertEquals(4, moApps.size(), "MO review should include applications for MO's jobs only");
        assertEquals("Java,Python,Git", reviewService.getSkillRequirement("JOB_OPEN_A"),
                "Review service should load job skill requirements");
        assertEquals("Java,Python,Git", reviewService.getTaSkill("TA001"),
                "Review service should prefer TA profile skills");

        SkillMatchUtil.MatchResult match = SkillMatchUtil.calculateMatchResult("Java,Python,Git", "java, git");
        assertEquals(67, match.getScore(), "Two of three skills should round to 67%");
        assertTrue(match.getMatchedSkills().contains("Java"), "Matched skills should be reported");
        assertTrue(match.getMissingSkills().contains("Python"), "Missing skills should be reported");

        List<Integer> scores = SkillMatchUtil.calculateMatchScores(moApps, reviewService);
        assertEquals(moApps.size(), scores.size(), "Skill scores should align with applications");
        assertTrue(scores.stream().anyMatch(score -> score == 100),
                "At least one applicant should fully match their job");

        Map<String, String> skillMap = TaCsvUtil.loadTaSkills(TEST_DATA_DIR.resolve("profiles.csv").toString());
        assertEquals("Algorithms,Java", skillMap.get("TA002"), "TA skill CSV helper should parse quoted skills");
        assertTrue(TaCsvUtil.getAllTaIds(TEST_DATA_DIR.resolve("profiles.csv").toString()).contains("TA001"),
                "TA CSV helper should list TA IDs");
    }

    private static void testTaProfileRecommendationsAndResumeSuggestions() {
        TAProfile original = TAProfileService.getProfileByTaId("TA001");
        assertNotNull(original, "Existing TA profile should load");
        assertEquals("Alice Chen", original.getName(), "TA profile name should load");

        TAProfile updated = new TAProfile("TA004", "David Zhang", "david@example.edu",
                "SQL, Excel, Python", "Data Science", "cvs/TA004/profile.pdf");
        TAProfileService.saveOrUpdateProfile(updated);
        TAProfile reloaded = TAProfileService.getProfileByTaId("TA004");
        assertEquals("SQL, Excel, Python", reloaded.getSkills(), "Updated profile skills should persist");

        TaJobService jobService = new TaJobService(TEST_DATA_DIR.resolve("job.csv").toString());
        assertEquals(3, jobService.getAllJobs().size(), "TA job service should load all jobs");
        assertEquals(2, jobService.searchJobs("", "", "OPEN", "").size(),
                "Status filter should return open jobs only");
        assertEquals(1, jobService.searchJobs("Java", "", "OPEN", "python").size(),
                "Combined subject, status, and keyword search should work");
        assertTrue(jobService.getAllSubjects().contains("Java"), "Subject options should include Java");
        assertTrue(jobService.getAllWorkTypes().contains("Lab"), "Work type options should include Lab");
        assertTrue(jobService.getAllStatuses().contains("FILLED"), "Status options should include FILLED");

        JobRecommendationService recommendationService = new JobRecommendationService(
                TEST_DATA_DIR.resolve("job.csv").toString(),
                TEST_DATA_DIR.resolve("profiles.csv").toString());
        List<JobRecommendation> recommendations = recommendationService.recommendJobs("TA001");
        assertEquals(2, recommendations.size(), "Only open jobs should be recommended");
        assertEquals("JOB_OPEN_A", recommendations.get(0).getJob().getJobId(),
                "Best matching Java job should rank first for TA001");
        assertTrue(recommendations.get(0).getScore() >= 80, "Top recommendation should have high score");
        assertNull(recommendationService.getProfile("UNKNOWN"), "Unknown TA profile should return null");

        ResumeSuggestion suggestion = new ResumeSuggestionService().generateSuggestion(
                "Java,Python,Git", "Java", "Java project experience");
        assertEquals(33, suggestion.getScore(), "Resume suggestion should reuse skill matching score");
        assertTrue(suggestion.getMissingSkills().contains("Python"), "Missing skill should be present");
        assertTrue(suggestion.getSuggestions().stream().anyMatch(text -> text.contains("GitHub")),
                "CV text without GitHub should trigger GitHub suggestion");
    }

    private static void testAdminWorkloadAndPostManagement() {
        AdminService adminService = new AdminService();
        List<AdminWorkload> workloads = adminService.getAllWorkloads();
        assertTrue(workloads.size() >= 4, "Admin should load base workloads");
        assertEquals(3, adminService.getTotalActiveTAs(workloads), "Distinct TA count should be computed");
        assertTrue(adminService.getOverloadedCount(workloads) >= 1, "Overloaded TA count should be computed");

        AdminWorkload ta001 = workloads.stream()
                .filter(workload -> "TA001".equals(workload.getTaId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("TA001 workload missing"));
        assertEquals(16.0, ta001.getTaTotalWorkHour(), "TA001 total hours should be normalized to 16");
        assertEquals("Overloaded", ta001.getStatus(), "TA001 should be overloaded above threshold");

        AdminWorkloadDomainService workloadDomainService = new AdminWorkloadDomainService();
        assertEquals(1, workloadDomainService.filterWorkloads(workloads, "CS101", "", "").size(),
                "Module filter should return matching workloads");
        assertEquals(1, workloadDomainService.filterAndSortWorkloads(
                workloads, "", "Normal", "mo002", "TA003", "taId", "asc").size(),
                "Combined filters should work");
        assertTrue(workloadDomainService.collectModuleCodes(workloads).contains("CS101"),
                "Module code collection should include CS101");
        assertTrue(workloadDomainService.collectStatuses(workloads).contains("Overloaded"),
                "Status collection should include Overloaded");
        assertTrue(workloadDomainService.collectMoIds(workloads).contains("mo001"),
                "MO collection should include mo001");
        assertTrue(workloadDomainService.collectTaIds(workloads).contains("TA001"),
                "TA collection should include TA001");
        assertTrue(workloadDomainService.markReassigning(workloads, "TA001", "CS101", "mo001"),
                "Overloaded workload should be markable for reassignment");
        assertEquals("Cancel", workloads.stream()
                .filter(workload -> "TA001".equals(workload.getTaId()) && "CS101".equals(workload.getModuleCode()))
                .findFirst()
                .orElseThrow()
                .getStatus(), "Marked workload should become Cancel");

        List<AdminRecruitment> posts = adminService.getAllPosts();
        assertEquals(2, adminService.countOpenPosts(posts), "Two base posts should be open");
        AdminPostDomainService postDomainService = new AdminPostDomainService();
        assertTrue(postDomainService.setOpenStatus(posts, "JOB_OPEN_C", false),
                "Open post should be closable");
        assertFalse(postDomainService.setOpenStatus(posts, "JOB_OPEN_C", false),
                "Closing an already closed post should report no change");
        assertTrue(adminService.saveAllPosts(posts), "Post changes should save");
        assertFalse(adminService.getAllPosts().stream()
                .filter(post -> "JOB_OPEN_C".equals(post.getJobId()))
                .findFirst()
                .orElseThrow()
                .isOpen(), "Closed post should persist");
    }

    private static void testAdminAcceptedApplicationSyncAndCancellation() {
        AdminService adminService = new AdminService();
        List<AdminWorkload> workloads = adminService.getAllWorkloads();
        assertTrue(workloads.stream().anyMatch(workload ->
                        "TA002".equals(workload.getTaId()) && "JOB_FILLED_B".equals(workload.getModuleCode())),
                "Accepted application should be appended to workloads");

        boolean marked = new AdminWorkloadDomainService()
                .markReassigning(workloads, "TA002", "JOB_FILLED_B", "mo001");
        assertTrue(marked, "Accepted appended workload should be markable when overloaded after normalization");
        assertTrue(adminService.saveAllWorkloads(workloads), "Cancelled workload should save");
        assertEquals("CANCELED", findApplication("APP_B").getAppStatus(),
                "Cancelled workload should update matching application status");
    }

    private static void testAdminRecruitmentReport() throws Exception {
        AdminRecruitmentReportService reportService = new AdminRecruitmentReportService();
        AdminRecruitmentReportService.ReportData report = reportService.buildReport();

        assertEquals(3, report.getSummary().getTotalJobs(), "Report should count all jobs");
        assertEquals(2, report.getSummary().getOpenJobs(), "Report should count open jobs");
        assertEquals(1, report.getSummary().getClosedJobs(), "Report should count non-open jobs as closed");
        assertEquals(5, report.getSummary().getTotalApplications(), "Report should count all applications");
        assertEquals(3, report.getSummary().getPendingApplications(), "Report should count pending applications");
        assertEquals(1, report.getSummary().getRejectedApplications(), "Report should count rejected applications");
        assertEquals(1, report.getSummary().getSuccessfulApplications(), "Report should count accepted applications");

        assertTrue(report.getItems().stream().anyMatch(item ->
                        "JOB_OPEN_A".equals(item.getJobId()) && item.getApplicationCount() == 2),
                "Per-job report should count applications");
        assertTrue(report.getSuccessfulRecruitmentDetails().stream().anyMatch(detail ->
                        "APP_B".equals(detail.getAppId()) && "TA002".equals(detail.getTaId())),
                "Successful recruitment details should include accepted applications");

        String statsCsv = reportService.buildCsv(report);
        assertContains(statsCsv, "Recruitment Statistics Report", "Stats CSV should include title");
        assertContains(statsCsv, "Remaining Vacancy", "Stats CSV should include vacancy column");

        String successCsv = reportService.buildSuccessfulRecruitmentCsv(report);
        assertContains(successCsv, "APP_B", "Successful recruitment CSV should include accepted app");
    }

    private static void testWebRoutesAndPages() throws Exception {
        String webXml = Files.readString(PROJECT_ROOT.resolve("WEB-INF").resolve("web.xml"), StandardCharsets.UTF_8);
        assertContains(webXml, "/admin/unified-login", "Admin login mapping should exist");
        assertContains(webXml, "/admin/home", "Admin home mapping should exist");
        assertContains(webXml, "/admin/workloads", "Admin workloads mapping should exist");
        assertContains(webXml, "/admin/posts", "Admin posts mapping should exist");
        assertContains(webXml, "/admin/report", "Admin report mapping should exist");

        assertSourceContains("src/TA/TALoginServlet.java", "@WebServlet(\"/ta/login\")");
        assertSourceContains("src/TA/TAHomeServlet.java", "@WebServlet(\"/ta/home\")");
        assertSourceContains("src/TA/TAJobListServlet.java", "@WebServlet(\"/ta/jobs\")");
        assertSourceContains("src/TA/TAApplyServlet.java", "@WebServlet(\"/ta/apply\")");
        assertSourceContains("src/TA/TAApplicationStatusServlet.java", "@WebServlet(\"/ta/status\")");
        assertSourceContains("src/TA/TAProfileServlet.java", "@WebServlet(\"/ta/profile\")");
        assertSourceContains("src/TA/ResumeSuggestionServlet.java", "@WebServlet(\"/ta/resumeSuggestion\")");
        assertSourceContains("src/TaJob/JobRecommendationServlet.java", "/ta/recommendations");
        assertSourceContains("src/MOpublish_apply/MoLoginServlet.java", "@WebServlet(\"/login\")");
        assertSourceContains("src/MOpublish_apply/PublishJobServlet.java", "/publishJob");
        assertSourceContains("src/MOpublish_apply/MoApplicantReviewServlet.java", "@WebServlet(\"/moApplicantReview\")");
        assertSourceContains("src/MOpublish_apply/GetAllAppsServlet.java", "/getAllApps");
        assertSourceContains("src/MOpublish_apply/HireApplicantServlet.java", "@WebServlet(\"/hireApplicant\")");
        assertSourceContains("src/MOpublish_apply/MoLogoutServlet.java", "@WebServlet(\"/mo/logout\")");

        List<Path> requiredPages = Arrays.asList(
                Paths.get("jsp/login/login.jsp"),
                Paths.get("jsp/admin/home.jsp"),
                Paths.get("jsp/admin/posts.jsp"),
                Paths.get("jsp/admin/workloads.jsp"),
                Paths.get("jsp/admin/report.jsp"),
                Paths.get("jsp/MO_1/publishJob.jsp"),
                Paths.get("jsp/MO_1/moApplicantList.jsp"),
                Paths.get("jsp/MO_1/applicantReview.jsp"),
                Paths.get("jsp/TA/login.jsp"),
                Paths.get("jsp/TA/home.jsp"),
                Paths.get("jsp/TA/jobList.jsp"),
                Paths.get("jsp/TA/applyJob.jsp"),
                Paths.get("jsp/TA/applicationStatus.jsp"),
                Paths.get("jsp/TA/profile.jsp"),
                Paths.get("jsp/TA/resumeSuggestion.jsp"),
                Paths.get("jsp/Ta_Job/recommendations.jsp")
        );
        for (Path page : requiredPages) {
            assertTrue(Files.exists(PROJECT_ROOT.resolve(page)), "JSP page should exist: " + page);
        }
    }

    private static void resetDataAndServices() throws Exception {
        Files.createDirectories(TEST_DATA_DIR);
        writeBaseCsvFiles();

        setStaticField(AuthUtil.class, "AUTH_FILE_PATH", TEST_DATA_DIR.resolve("auth.csv").toString());
        clearStaticMap(AuthUtil.class, "MO_USER_MAP");
        clearStaticMap(AuthUtil.class, "ADMIN_USER_MAP");
        clearStaticMap(AuthUtil.class, "TA_USER_MAP");
        Method loadAuthData = AuthUtil.class.getDeclaredMethod("loadAuthDataFromCsv");
        loadAuthData.setAccessible(true);
        loadAuthData.invoke(null);

        setStaticField(TAAuthService.class, "authFilePath", TEST_DATA_DIR.resolve("auth.csv").toString());
        setStaticField(TAApplicationService.class, "APP_FILE_PATH", TEST_DATA_DIR.resolve("application.csv").toString());
        setStaticField(TAApplicationService.class, "JOB_FILE_PATH", TEST_DATA_DIR.resolve("job.csv").toString());
        setStaticField(TAProfileService.class, "TA_PROFILE_PATH", TEST_DATA_DIR.resolve("profiles.csv").toString());
        setStaticField(TAProfileService.class, "LEGACY_TA_PROFILE_PATH", TEST_DATA_DIR.resolve("ta_profiles.csv").toString());

        MoService.JOB_FILE_PATH = TEST_DATA_DIR.resolve("job.csv").toString();
        MoService.APP_FILE_PATH = TEST_DATA_DIR.resolve("application.csv").toString();
        setStaticField(MoApplicantReviewService.class, "JOB_FILE_PATH", TEST_DATA_DIR.resolve("job.csv").toString());
        setStaticField(MoApplicantReviewService.class, "TA_CSV_PATH", TEST_DATA_DIR.resolve("profiles.csv").toString());
        setStaticField(MoApplicantReviewService.class, "APP_FILE_PATH", TEST_DATA_DIR.resolve("application.csv").toString());
        setStaticField(MoApplicantReviewService.class, "TA_SKILL_MAP",
                TaCsvUtil.loadTaSkills(TEST_DATA_DIR.resolve("profiles.csv").toString()));
    }

    private static void writeBaseCsvFiles() throws IOException {
        Files.writeString(TEST_DATA_DIR.resolve("auth.csv"),
                "userType,userId,password\n"
                        + "MO,mo001,Mo@123456\n"
                        + "MO,mo002,Mo@234567\n"
                        + "ADMIN,admin001,Admin@123456\n"
                        + "TA,TA001,Ta@123456\n"
                        + "TA,TA002,Ta@234567\n"
                        + "TA,TA003,Ta@345678\n"
                        + "TA,TA004,Ta@456789\n"
                        + "TA,TA005,Ta@567890\n",
                StandardCharsets.UTF_8);

        Files.writeString(TEST_DATA_DIR.resolve("job.csv"),
                "jobId,moId,subject,workType,description,skillRequirement,hoursPerWeek,compensation,status,maxHire\n"
                        + "JOB_OPEN_A,mo001,Java,Lab,\"Support Java labs and answer Python questions\",\"Java,Python,Git\",8,$18/hour,OPEN,1\n"
                        + "JOB_FILLED_B,mo001,Algorithms,Tutorial,\"Check algorithms assignments\",\"Algorithms,Java\",6,$20/hour,FILLED,1\n"
                        + "JOB_OPEN_C,mo002,Databases,Remote,\"Support SQL workshops\",\"SQL,Excel\",5,$15/hour,OPEN,1\n",
                StandardCharsets.UTF_8);

        Files.writeString(TEST_DATA_DIR.resolve("application.csv"),
                "appId,name,jobId,moId,taId,major,intro,skills,email,CVpath,appStatus\n"
                        + "APP_A,Alice Chen,JOB_OPEN_A,mo001,TA001,Computer Science,Experienced in Java teaching,\"Java,Python\",alice@example.edu,cvs/TA001/a.pdf,PENDING\n"
                        + "APP_B,Bob Li,JOB_FILLED_B,mo001,TA002,Computer Science,Good with algorithms,\"Algorithms,Java\",bob@example.edu,cvs/TA002/b.pdf,ACCEPTED\n"
                        + "APP_C,Carol Wang,JOB_FILLED_B,mo001,TA003,Physics,Can support labs,Java,carol@example.edu,,PENDING\n"
                        + "APP_D,David Zhang,JOB_OPEN_C,mo002,TA004,Data Science,SQL background,SQL,david@example.edu,,REJECTED\n"
                        + "APP_E,Eva Green,JOB_OPEN_A,mo001,TA005,Computer Science,Git helper,Git,eva@example.edu,,PENDING\n",
                StandardCharsets.UTF_8);

        Files.writeString(TEST_DATA_DIR.resolve("profiles.csv"),
                "taId,name,email,skills,major,cvPath\n"
                        + "TA001,Alice Chen,alice@example.edu,\"Java,Python,Git\",Computer Science,cvs/TA001/a.pdf\n"
                        + "TA002,Bob Li,bob@example.edu,\"Algorithms,Java\",Computer Science,cvs/TA002/b.pdf\n"
                        + "TA003,Carol Wang,carol@example.edu,Java,Physics,\n"
                        + "TA004,David Zhang,david@example.edu,SQL,Data Science,\n"
                        + "TA005,Eva Green,eva@example.edu,Git,Computer Science,\n",
                StandardCharsets.UTF_8);

        Files.writeString(TEST_DATA_DIR.resolve("workloads.csv"),
                "moName,moId,taId,taName,moduleName,moduleCode,courseWorkHour,taTotalWorkHour,status\n"
                        + "Amy,mo001,TA001,Alice,Data Structures,CS101,8.0,0.0,Normal\n"
                        + "Amy,mo001,TA001,Alice,Programming Basics,CS100,8.0,0.0,Normal\n"
                        + "Amy,mo001,TA002,Bob,Algorithms,CS102,10.0,0.0,Normal\n"
                        + "Brian,mo002,TA003,Carol,Databases,CS103,5.0,0.0,Normal\n",
                StandardCharsets.UTF_8);
    }

    private static Application findApplication(String appId) {
        return CsvFileUtil.readAppListFromCsv(TEST_DATA_DIR.resolve("application.csv").toString())
                .stream()
                .filter(app -> appId.equals(app.getAppId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Application not found: " + appId));
    }

    private static Job findJob(String jobId) {
        return CsvFileUtil.readJobListFromCsv(TEST_DATA_DIR.resolve("job.csv").toString())
                .stream()
                .filter(job -> jobId.equals(job.getJobId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Job not found: " + jobId));
    }

    private static void assertSourceContains(String relativePath, String expectedText) throws IOException {
        String text = Files.readString(PROJECT_ROOT.resolve(relativePath), StandardCharsets.UTF_8);
        assertContains(text, expectedText, "Source should contain " + expectedText + " in " + relativePath);
    }

    private static void setStaticField(Class<?> type, String fieldName, Object value) throws Exception {
        Field field = type.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    @SuppressWarnings("unchecked")
    private static void clearStaticMap(Class<?> type, String fieldName) throws Exception {
        Field field = type.getDeclaredField(fieldName);
        field.setAccessible(true);
        ((Map<?, ?>) field.get(null)).clear();
    }

    private static void run(String name, TestCase testCase) {
        long start = System.currentTimeMillis();
        try {
            testCase.run();
            RESULTS.add(new TestResult(name, true, System.currentTimeMillis() - start, ""));
            System.out.println("[PASS] " + name);
        } catch (Throwable error) {
            RESULTS.add(new TestResult(name, false, System.currentTimeMillis() - start, error.getMessage()));
            System.out.println("[FAIL] " + name);
            error.printStackTrace(System.out);
        }
    }

    private static void printSummary() {
        long passed = RESULTS.stream().filter(result -> result.passed).count();
        long failed = RESULTS.size() - passed;
        System.out.println();
        System.out.println("==== weblogin full system test summary ====");
        System.out.println("Total: " + RESULTS.size() + ", Passed: " + passed + ", Failed: " + failed);
        long duration = RESULTS.stream().mapToLong(result -> result.durationMs).sum();
        System.out.println("Duration: " + duration + " ms");

        if (failed > 0) {
            System.out.println();
            System.out.println("Failed tests:");
            RESULTS.stream()
                    .filter(result -> !result.passed)
                    .forEach(result -> System.out.println("- " + result.name + ": " + result.message));
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    private static void assertNull(Object actual, String message) {
        if (actual != null) {
            throw new AssertionError(message + " | expected null but was " + actual);
        }
    }

    private static void assertNotNull(Object actual, String message) {
        if (actual == null) {
            throw new AssertionError(message + " | expected non-null value");
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " | expected: " + expected + ", actual: " + actual);
        }
    }

    private static void assertContains(String text, String expected, String message) {
        if (text == null || !text.contains(expected)) {
            throw new AssertionError(message + " | missing: " + expected);
        }
    }

    private interface TestCase {
        void run() throws Exception;
    }

    private static class TestResult {
        private final String name;
        private final boolean passed;
        private final long durationMs;
        private final String message;

        private TestResult(String name, boolean passed, long durationMs, String message) {
            this.name = name;
            this.passed = passed;
            this.durationMs = durationMs;
            this.message = message;
        }
    }
}
