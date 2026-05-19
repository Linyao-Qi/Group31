<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.MoService" %>
<%@ page import="com.AuthUtil" %>
<%@ page import="com.Application" %>
<%@ page import="com.Job" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<html>
<head>
    <title>Applicant Management</title>
    <style>
        body {font-family: Arial, sans-serif; max-width: 1400px; margin: 50px auto; padding: 0 20px;}
        .nav {display: flex; justify-content: center; gap: 8px; flex-wrap: wrap; margin: 0 auto 30px; padding: 10px; background: #eff6ff; border: 1px solid #bfdbfe; border-radius: 8px; box-shadow: 0 6px 18px rgba(37, 99, 235, 0.10);}
        .nav a {padding: 9px 14px; color: #1d4ed8; text-decoration: none; font-weight: bold; border-radius: 6px; font-size: 16px;}
        .nav a:hover {background: #dbeafe; text-decoration: none;}
        table {width:100%; border-collapse:collapse; margin:20px 0; font-size: 14px;}
        th,td {border:1px solid #ddd; padding:10px; text-align:center;}
        th {background:#f8fafc; font-size: 14px;}
        .intro {text-align: left; max-width: 180px; word-break: break-word;}
        .btn-hire {padding:6px 12px; background:#16a34a; color:white; border:none; border-radius:4px; cursor:pointer; font-size:13px;}
        .btn-cancel {padding:6px 12px; background:#ef4444; color:white; border:none; border-radius:4px; cursor:pointer; font-size:13px;}
        .btn-reject {padding:6px 12px; background:#f97316; color:white; border:none; border-radius:4px; cursor:pointer; font-size:13px;}
        .btn-cancel-reject {padding:6px 12px; background:#64748b; color:white; border:none; border-radius:4px; cursor:pointer; font-size:13px;}
        .btn-hire:hover {background:#15803d;}
        .btn-cancel:hover {background:#dc2626;}
        .btn-reject:hover {background:#ea580c;}
        .btn-cancel-reject:hover {background:#475569;}
        .msg {
            margin:20px 0;
            padding:15px;
            border-radius: 6px;
            text-align:center;
            font-weight: bold;
            font-size: 16px;
        }
        .success {background:#dcfce7; color:#166534;}
        .fail {background:#fee2e2; color:#991b1b;}
        .empty {text-align:center; margin-top: 30px; color: #666; font-size: 16px;}
        .pending {color: #f59e0b; font-weight: bold;}
        .accepted {color: #16a34a; font-weight: bold;}
        .rejected {color: #dc2626; font-weight: bold;}
        .return-btn {
            width: 100%;
            padding: 16px;
            background: #2563eb;
            color: white;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 16px;
            margin-top: 20px;
        }
        .return-btn:hover {background: #1d4ed8;}
        .auth-box {
            max-width: 400px;
            margin: 50px auto;
            padding: 30px;
            border: 1px solid #ddd;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .auth-box div {margin: 15px 0;}
        .auth-box label {display: inline-block; width: 90px; font-size: 16px;}
        .auth-box input {
            width: 250px;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 16px;
        }
        .auth-box button {
            padding: 10px 30px;
            background: #2563eb;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        .auth-box button:hover {background: #1d4ed8;}
        .current-mo {margin: 0 auto 26px; padding: 14px; border-radius: 6px; background: #dbeafe; color: #1e3a8a; text-align: center; font-size: 20px;}
        .logout-form {margin: 28px auto 0; width: min(560px, 100%);}
        .logout-btn {width: 100%; padding: 12px; background: #64748b; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px;}
        .logout-btn:hover {background: #475569;}
    </style>
</head>
<body>
    <div class="nav">
        <a href="${pageContext.request.contextPath}/jsp/MO_1/publishJob.jsp">Publish Job</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/moApplicantList.jsp">Hire Applicant</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/appList.jsp">Application List</a>
        <a href="${pageContext.request.contextPath}/moApplicantReview">Skill Match Score</a>
    </div>

<%
    MoService.init(getServletContext());
    AuthUtil.init(getServletContext());

    request.setCharacterEncoding("UTF-8");
    String moId = (String) session.getAttribute("userId");
    if (!"MO".equals(session.getAttribute("userType")) || moId == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    MoService moService = new MoService();
    boolean authPass = true;
    List<Application> appList = null;
    String msg = null;
    boolean isFail = false;
    Map<String, Job> jobMap = new HashMap<>();

    String appId = request.getParameter("appId");
    String cancelAppId = request.getParameter("cancelAppId");
    String rejectAppId = request.getParameter("rejectAppId");
    String cancelRejectAppId = request.getParameter("cancelRejectAppId");

            if (appId != null) {
                Application result = moService.acceptApplicant(moId, appId);
                if (result != null) {
                    msg = "Hired successfully!";
                    isFail = false;
                } else {
                    msg = "Hire failed! Exceeded max hire limit.";
                    isFail = true;
                }
            }

            if (cancelAppId != null) {
                boolean result = moService.cancelApplicant(moId, cancelAppId);
                msg = result ? "Cancel hire successfully!" : "Cancel hire failed!";
                isFail = !result;
            }

            if (rejectAppId != null) {
                Application result = moService.rejectApplicant(moId, rejectAppId);
                msg = result != null ? "Rejected successfully!" : "Reject failed!";
                isFail = result == null;
            }

            if (cancelRejectAppId != null) {
                Application result = moService.cancelRejectApplicant(moId, cancelRejectAppId);
                msg = result != null ? "Cancel reject successfully!" : "Cancel reject failed!";
                isFail = result == null;
            }

            appList = moService.getAllAppsForMo(moId);
            List<Job> allJobs = moService.getAllJobs();
            for(Job j : allJobs) {
                jobMap.put(j.getJobId(), j);
            }
%>

    <h2 align="center">Applicant Management - MO: <%=moId != null ? moId : ""%></h2>
    <div class="current-mo">Current MO: <%= moId %></div>
    
    <% if (msg != null) { %>
        <div class="msg <%= isFail ? "fail" : "success" %>"><%= msg %></div>
    <% } %>

    <% if (!authPass) { %>
    <% } else { %>
        <% if (appList == null || appList.isEmpty()) { %>
            <div class="empty">No applicants yet</div>
        <% } else { %>
        <table>
            <tr>
                <th>App ID</th>
                <th>Name</th>
                <th>Job ID</th>
                <th>TA ID</th>
                <th>Major</th>
                <th>Intro</th>
                <th>Skills</th>
                <th>Email</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
            <%
                for (Application app : appList) {
                    Job job = jobMap.get(app.getJobId());
                    String status = app.getAppStatus();
                    String statusClass;
                    if ("ACCEPTED".equals(status)) {
                        statusClass = "accepted";
                    } else if ("REJECTED".equals(status)) {
                        statusClass = "rejected";
                    } else {
                        statusClass = "pending";
                    }
            %>
            <tr>
                <td><%= app.getAppId() %></td>
                <td><%= app.getName() %></td>
                <td><%= app.getJobId() %></td>
                <td><%= app.getTaId() %></td>
                <td><%= app.getMajor() %></td>
                <td class="intro"><%= app.getIntro() %></td>
                <td><%= app.getSkills() %></td>
                <td><%= app.getEmail() %></td>
                <td class="<%= statusClass %>"><%= status %></td>
                <td>
                    <% if ("PENDING".equals(status)) { %>
                        <form action="${pageContext.request.contextPath}/jsp/MO_1/moApplicantList.jsp" method="post" style="display:inline;margin:0 2px 0 0;">
                            <input type="hidden" name="appId" value="<%= app.getAppId() %>">
                            <button type="submit" class="btn-hire" onclick="return confirm('Hire this applicant?');">Hire</button>
                        </form>
                        <form action="${pageContext.request.contextPath}/jsp/MO_1/moApplicantList.jsp" method="post" style="display:inline;">
                            <input type="hidden" name="rejectAppId" value="<%= app.getAppId() %>">
                            <button type="submit" class="btn-reject" onclick="return confirm('Reject this applicant?');">Reject</button>
                        </form>
                    <% } else if ("ACCEPTED".equals(status)) { %>
                        <form action="${pageContext.request.contextPath}/jsp/MO_1/moApplicantList.jsp" method="post" style="margin:0;">
                            <input type="hidden" name="cancelAppId" value="<%= app.getAppId() %>">
                            <button type="submit" class="btn-cancel" onclick="return confirm('Cancel this hire?');">Cancel Hire</button>
                        </form>
                    <% } else if ("REJECTED".equals(status)) { %>
                        <form action="${pageContext.request.contextPath}/jsp/MO_1/moApplicantList.jsp" method="post" style="margin:0;">
                            <input type="hidden" name="cancelRejectAppId" value="<%= app.getAppId() %>">
                            <button type="submit" class="btn-cancel-reject" onclick="return confirm('Cancel this reject?');">Cancel Reject</button>
                        </form>
                    <% } %>
                </td>
            </tr>
            <% } %>
        </table>
        <% } %>
    <% } %>
    
    <form class="logout-form" method="post" action="${pageContext.request.contextPath}/mo/logout">
        <button type="submit" class="logout-btn">Logout</button>
    </form>
    
</body>
</html>
