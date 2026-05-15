<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@ page import="Admin.AdminRecruitmentReportItem" %>
<%@ page import="Admin.AdminRecruitmentReportService" %>
<%
    AdminRecruitmentReportService.Summary summary =
            (AdminRecruitmentReportService.Summary) request.getAttribute("summary");
    List<AdminRecruitmentReportItem> reportItems =
            (List<AdminRecruitmentReportItem>) request.getAttribute("reportItems");
    List<AdminRecruitmentReportService.ApplicationStatusCount> applicationStatusCounts =
            (List<AdminRecruitmentReportService.ApplicationStatusCount>) request.getAttribute("applicationStatusCounts");
    List<AdminRecruitmentReportService.SuccessfulRecruitmentDetail> successfulRecruitmentDetails =
            (List<AdminRecruitmentReportService.SuccessfulRecruitmentDetail>) request.getAttribute("successfulRecruitmentDetails");
    AdminRecruitmentReportService.KeyFindings keyFindings =
            (AdminRecruitmentReportService.KeyFindings) request.getAttribute("keyFindings");
    String generatedAt = (String) request.getAttribute("generatedAt");
    String error = (String) request.getAttribute("error");

    if (reportItems == null) {
        reportItems = Collections.emptyList();
    }
    if (applicationStatusCounts == null) {
        applicationStatusCounts = Collections.emptyList();
    }
    if (successfulRecruitmentDetails == null) {
        successfulRecruitmentDetails = Collections.emptyList();
    }
    if (generatedAt == null) {
        generatedAt = "";
    }

    DecimalFormat hoursFormat = new DecimalFormat("0.##");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Recruitment Statistics Report</title>
    <style>
        * { box-sizing: border-box; }
        body { margin: 0; background: #f3f6fb; color: #1f2937; font-family: Arial, Helvetica, sans-serif; font-size: 14px; }
        .page { width: min(1180px, calc(100% - 48px)); margin: 0 auto; padding: 32px 0 40px; }
        .nav { display: flex; align-items: center; justify-content: space-between; gap: 16px; padding: 12px 14px; margin-bottom: 18px; background: #fff; border: 1px solid #e5e7eb; border-radius: 14px; box-shadow: 0 8px 22px rgba(15, 23, 42, 0.06); }
        .brand { font-weight: 800; color: #111827; white-space: nowrap; }
        .nav-links { display: flex; align-items: center; justify-content: flex-end; flex-wrap: wrap; gap: 8px; }
        .nav a, .nav button { display: inline-flex; align-items: center; justify-content: center; min-height: 34px; padding: 8px 11px; border-radius: 9px; border: 1px solid transparent; background: transparent; color: #374151; font: inherit; text-decoration: none; cursor: pointer; }
        .nav a:hover, .nav button:hover { background: #eff6ff; color: #1d4ed8; }
        .nav .active { background: #2563eb; color: #fff; }
        .nav .active:hover { background: #1d4ed8; color: #fff; }
        .header { display: flex; align-items: flex-start; justify-content: space-between; gap: 18px; margin-bottom: 18px; }
        h2 { margin: 0; font-size: 28px; color: #111827; }
        h3 { margin: 0 0 14px; font-size: 18px; color: #111827; }
        .subtitle { margin: 7px 0 0; color: #6b7280; }
        .card { background: #fff; border: 1px solid #e5e7eb; border-radius: 14px; box-shadow: 0 8px 22px rgba(15, 23, 42, 0.06); padding: 22px; margin-bottom: 18px; }
        .btn {
            display: inline-flex; align-items: center; justify-content: center; min-height: 38px; padding: 9px 14px;
            border-radius: 9px; border: 1px solid #2563eb; background: #2563eb; color: #fff;
            font: inherit; line-height: 1; text-decoration: none; cursor: pointer;
        }
        .btn:hover { background: #1d4ed8; border-color: #1d4ed8; }
        .btn-secondary { background: #fff; border-color: #d1d5db; color: #374151; }
        .btn-secondary:hover { background: #f9fafb; border-color: #9ca3af; }
        .stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(175px, 1fr)); gap: 12px; }
        .stat { border: 1px solid #e5e7eb; border-radius: 12px; background: #f8fbff; padding: 14px; }
        .stat-label { color: #6b7280; font-size: 13px; margin-bottom: 8px; }
        .stat-value { color: #111827; font-size: 24px; line-height: 1; font-weight: 800; }
        .table-wrap { width: 100%; overflow-x: auto; }
        table { width: 100%; border-collapse: separate; border-spacing: 0; font-size: 13px; }
        th, td { border-bottom: 1px solid #e5e7eb; padding: 11px 12px; text-align: left; vertical-align: top; }
        th { background: #eef4ff; color: #374151; font-weight: 700; white-space: nowrap; }
        tbody tr:hover { background: #f9fbff; }
        tbody tr:last-child td { border-bottom: 0; }
        .pill { display: inline-flex; border-radius: 999px; padding: 4px 9px; font-size: 12px; font-weight: 700; white-space: nowrap; }
        .open, .accepted, .successful, .hired { background: #dcfce7; color: #166534; }
        .closed { background: #e5e7eb; color: #374151; }
        .pending { background: #fef3c7; color: #92400e; }
        .rejected, .canceled, .cancelled { background: #fee2e2; color: #991b1b; }
        .summary-list { margin: 0; padding-left: 20px; }
        .summary-list li { margin: 8px 0; }
        .error { padding: 11px 13px; margin-bottom: 14px; border-radius: 10px; background: #fef2f2; border: 1px solid #fecaca; color: #991b1b; }
        .actions { margin-top: 12px; }
        .inline-form { display: inline-flex; margin: 0; }
        @media (max-width: 720px) { .page { width: calc(100% - 28px); padding-top: 22px; } .header, .nav { flex-direction: column; } }
    </style>
</head>
<body>
<div class="page">
    <div class="nav">
        <div class="brand">Admin System</div>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/admin/home">Home</a>
            <a href="${pageContext.request.contextPath}/admin/workloads">CheckWorkload</a>
            <a href="${pageContext.request.contextPath}/admin/posts">ClosePost</a>
            <a class="active" href="${pageContext.request.contextPath}/admin/report">Recruitment Report</a>
            <form class="inline-form" method="post" action="${pageContext.request.contextPath}/admin/logout">
                <button type="submit">Logout</button>
            </form>
        </div>
    </div>

    <div class="header">
        <div>
            <h2>Recruitment Statistics Report</h2>
            <p class="subtitle">Generated At: <strong><%= generatedAt %></strong></p>
        </div>
        <a class="btn" href="${pageContext.request.contextPath}/admin/report?export=csv">Export CSV</a>
    </div>

    <% if (error != null && !error.isEmpty()) { %>
        <div class="error"><%= error %></div>
    <% } else if (summary != null) { %>
        <div class="card">
            <h3>Overview Statistics</h3>
            <div class="stats">
                <div class="stat"><div class="stat-label">Total Jobs</div><div class="stat-value"><%= summary.getTotalJobs() %></div></div>
                <div class="stat"><div class="stat-label">Open Jobs</div><div class="stat-value"><%= summary.getOpenJobs() %></div></div>
                <div class="stat"><div class="stat-label">Closed Jobs</div><div class="stat-value"><%= summary.getClosedJobs() %></div></div>
                <div class="stat"><div class="stat-label">Total Applications</div><div class="stat-value"><%= summary.getTotalApplications() %></div></div>
                <div class="stat"><div class="stat-label">Pending Applications</div><div class="stat-value"><%= summary.getPendingApplications() %></div></div>
                <div class="stat"><div class="stat-label">Rejected Applications</div><div class="stat-value"><%= summary.getRejectedApplications() %></div></div>
                <div class="stat"><div class="stat-label">Canceled Applications</div><div class="stat-value"><%= summary.getCanceledApplications() %></div></div>
                <div class="stat"><div class="stat-label">Successful Applications</div><div class="stat-value"><%= summary.getSuccessfulApplications() %></div></div>
            </div>
        </div>

        <div class="card">
            <h3>Job Statistics</h3>
            <div class="table-wrap">
                <table>
                    <thead>
                    <tr>
                        <th>job ID</th>
                        <th>MO ID</th>
                        <th>Subject</th>
                        <th>Work Type</th>
                        <th>Hours/Week</th>
                        <th>Compensation</th>
                        <th>Status</th>
                        <th>Application Count</th>
                        <th>Pending Count</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% for (AdminRecruitmentReportItem item : reportItems) { %>
                        <tr>
                            <td><%= item.getJobId() %></td>
                            <td><%= item.getMoId() %></td>
                            <td><%= item.getSubject() %></td>
                            <td><%= item.getWorkType() %></td>
                            <td><%= hoursFormat.format(item.getHoursPerWeek()) %></td>
                            <td><%= item.getCompensation() %></td>
                            <td><span class="pill <%= "OPEN".equalsIgnoreCase(item.getStatus()) ? "open" : "closed" %>"><%= item.getStatus() %></span></td>
                            <td><%= item.getApplicationCount() %></td>
                            <td><%= item.getPendingCount() %></td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="card">
            <h3>Application Status Statistics</h3>
            <div class="table-wrap">
                <table>
                    <thead>
                    <tr>
                        <th>appStatus</th>
                        <th>Count</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% for (AdminRecruitmentReportService.ApplicationStatusCount statusCount : applicationStatusCounts) { %>
                        <tr>
                            <td><span class="pill <%= statusCount.getAppStatus().toLowerCase() %>"><%= statusCount.getAppStatus() %></span></td>
                            <td><%= statusCount.getCount() %></td>
                        </tr>
                    <% } %>
                    <% if (applicationStatusCounts.isEmpty()) { %>
                        <tr><td colspan="2">No application status data.</td></tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="card">
            <h3>Key Findings / Summary</h3>
            <% if (keyFindings != null) { %>
                <ul class="summary-list">
                    <li>Jobs with no applications: <strong><%= keyFindings.getJobsWithNoApplications() %></strong></li>
                    <li>Most applied job: <strong><%= keyFindings.getMostAppliedJob() %></strong>
                        with <strong><%= keyFindings.getMostAppliedJobApplicationCount() %></strong> applications</li>
                    <li>MO with most jobs: <strong><%= keyFindings.getMoIdWithMostJobs().isEmpty() ? "N/A" : keyFindings.getMoIdWithMostJobs() %></strong>
                        with <strong><%= keyFindings.getMoJobCount() %></strong> jobs</li>
                </ul>
            <% } %>
        </div>

        <div class="card">
            <div class="header">
                <div>
                    <h3>Successful Recruitment Details</h3>
                    <p class="subtitle">Applications with explicit successful statuses only.</p>
                </div>
                <a class="btn" href="${pageContext.request.contextPath}/admin/report?export=successful">Export Successful Recruitment CSV</a>
            </div>
            <% if (successfulRecruitmentDetails.isEmpty()) { %>
                <p class="subtitle">No successful recruitment records found in current dataset.</p>
            <% } else { %>
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>job ID</th>
                            <th>Subject</th>
                            <th>MO ID</th>
                            <th>appId</th>
                            <th>taId</th>
                            <th>name</th>
                            <th>email</th>
                            <th>appStatus</th>
                        </tr>
                        </thead>
                        <tbody>
                        <% for (AdminRecruitmentReportService.SuccessfulRecruitmentDetail detail : successfulRecruitmentDetails) { %>
                            <tr>
                                <td><%= detail.getJobId() %></td>
                                <td><%= detail.getSubject() %></td>
                                <td><%= detail.getMoId() %></td>
                                <td><%= detail.getAppId() %></td>
                                <td><%= detail.getTaId() %></td>
                                <td><%= detail.getName() %></td>
                                <td><%= detail.getEmail() %></td>
                                <td><span class="pill <%= detail.getAppStatus().toLowerCase() %>"><%= detail.getAppStatus() %></span></td>
                            </tr>
                        <% } %>
                        </tbody>
                    </table>
                </div>
            <% } %>
        </div>
    <% } else { %>
        <div class="error">No report data is available.</div>
    <% } %>

    <div class="actions">
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/home">Back</a>
    </div>
</div>
</body>
</html>
