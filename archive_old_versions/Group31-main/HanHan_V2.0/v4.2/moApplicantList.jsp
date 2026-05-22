<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.MoService" %>
<%@ page import="com.AuthUtil" %>
<%@ page import="com.Application" %>
<%@ page import="com.Job" %>
<%@ page import="com.CsvFileUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<html>
<head>
    <title>我的待审核申请者</title>
    <style>
        body {font-family: Arial, sans-serif; max-width: 1000px; margin: 50px auto; padding: 0 20px;}
        .nav {margin-bottom: 30px; text-align: center;}
        .nav a {margin: 0 10px; color: #2563eb; text-decoration: none; font-size: 16px;}
        .nav a:hover {text-decoration: underline;}
        table {width:100%; border-collapse:collapse; margin:20px 0; font-size: 16px;}
        th,td {border:1px solid #ddd; padding:15px; text-align:center;}
        th {background:#f8fafc; font-size: 17px;}
        .intro, .req {text-align: left; max-width: 220px; word-break: break-word;}
        .btn-hire {padding:8px 16px; background:#16a34a; color:white; border:none; border-radius:4px; cursor:pointer; font-size: 14px;}
        .btn-cancel {padding:8px 16px; background:#ef4444; color:white; border:none; border-radius:4px; cursor:pointer; font-size: 14px;}
        .btn-hire:hover {background:#15803d;}
        .btn-cancel:hover {background:#dc2626;}
        .msg {margin:20px 0; padding:10px; border-radius: 5px; text-align:center; font-weight: bold;}
        .success {background:#dcfce7; color:#166534;}
        .fail {background:#fee2e2; color:#991b1b;}
        .empty {text-align:center; margin-top: 30px; color: #666; font-size: 18px;}
        .pending {color: #f59e0b; font-weight: bold;}
        .accepted {color: #16a34a; font-weight: bold;}
        .auth-box {
            border: 1px solid #ddd;
            padding: 20px;
            width: 400px;
            margin: 0 auto 30px;
            border-radius: 8px;
        }
        .auth-box div {margin: 15px 0;}
        .auth-box label {
            display: inline-block;
            width: 120px;
            text-align: right;
            margin-right: 10px;
            font-weight: bold;
        }
        .auth-box input[type="text"], .auth-box input[type="password"] {
            width: 250px;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
        }
        .auth-box input[type="radio"] {
            margin: 0 5px 0 15px;
        }
        .auth-box button {
            width: 100%;
            padding: 12px;
            background: #2563eb;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
            margin-top: 10px;
        }
        .auth-box button:hover {background: #1d4ed8;}
        .return-btn {
            width: 100%;
            padding: 18px;
            background: #2563eb;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 18px;
            margin-top: 20px;
        }
        .return-btn:hover {background: #1d4ed8;}
    </style>
</head>
<body>
    <div class="nav">
        <a href="publishJob.jsp">发布岗位</a>
        <a href="hireApplicant.jsp">录用申请者</a>
        <a href="jobList.jsp">查看所有岗位</a>
        <a href="appList.jsp">查看所有申请</a>
    </div>

<%
    request.setCharacterEncoding("UTF-8");
    String moId = request.getParameter("moId");
    String password = request.getParameter("password");
    MoService moService = new MoService();
    boolean authPass = false;
    List<Application> appList = null;
    String msg = null;
    Map<String, Job> jobMap = new HashMap<>();

    // 取消录用 —— 完全调用你 MoService 里的方法，不直接访问路径！
    String cancelAppId = request.getParameter("cancelAppId");
    if(cancelAppId != null && moId != null && password != null) {
        boolean success = moService.cancelApplicant(moId, cancelAppId);
        if(success){
            msg = " 取消录用成功！";
        } else {
            msg = " 取消失败";
        }
    }

    if (moId != null && password != null) {
        if (AuthUtil.authenticateMO(moId, password)) {
            authPass = true;
            appList = moService.getAllApps(moId, password, false);
            List<Job> allJobs = moService.getAllJobs();
            for(Job j : allJobs) {
                jobMap.put(j.getJobId(), j);
            }
        } else {
            msg = " MO账号或密码错误";
        }
    }
%>

    <h2 align="center">MO【<%=moId != null ? moId : ""%>】- 申请者管理</h2>
    
    <% if (msg != null) { %>
        <div class="msg success"><%= msg %></div>
    <% } %>

    <% if (!authPass) { %>
        <div class="auth-box">
            <form action="moApplicantList.jsp" method="post">
                <div>
                    <label>MO ID：</label>
                    <input type="text" name="moId" required placeholder="请输入MO ID">
                </div>
                <div>
                    <label>密码：</label>
                    <input type="password" name="password" required placeholder="请输入密码">
                </div>
                <div style="text-align:center; margin-top:15px;">
                    <button type="submit">验证身份并查看</button>
                </div>
            </form>
        </div>
    <% } else { %>
        <% if (appList == null || appList.isEmpty()) { %>
            <div class="empty">暂无申请者数据</div>
        <% } else { %>
        <table>
            <tr>
                <th>申请ID</th>
                <th>岗位名称</th>
                <th>岗位需求</th>
                <th>TA介绍</th>
                <th>申请状态</th>
                <th>操作</th>
            </tr>
            <%
                for (Application app : appList) {
                    Job job = jobMap.get(app.getJobId());
                    String jobName = (job == null) ? "未知岗位" : job.getJobName();
                    String jobReq = (job == null) ? "无" : job.getJobRequirements();
                    String status = app.getAppStatus();
                    String statusClass = "ACCEPTED".equals(status) ? "accepted" : "pending";
            %>
            <tr>
                <td><%= app.getAppId() %></td>
                <td><%= jobName %></td>
                <td class="req"><%= jobReq %></td>
                <td class="intro"><%= app.getIntro() %></td>
                <td class="<%= statusClass %>">
                    <%= "ACCEPTED".equals(status) ? "已录用" : "未录用" %>
                </td>
                <td>
                    <% if ("ACCEPTED".equals(status)) { %>
                        <form action="moApplicantList.jsp" method="post" style="margin:0;">
                            <input type="hidden" name="cancelAppId" value="<%= app.getAppId() %>">
                            <input type="hidden" name="moId" value="<%= moId %>">
                            <input type="hidden" name="password" value="<%= password %>">
                            <button type="submit" class="btn-cancel" onclick="return confirm('确定取消录用？')">取消录用</button>
                        </form>
                    <% } else { %>
                        <form action="hireApplicant" method="post" style="margin:0;">
                            <input type="hidden" name="moId" value="<%= moId %>">
                            <input type="hidden" name="appId" value="<%= app.getAppId() %>">
                            <button type="submit" class="btn-hire" onclick="return confirm('确定录用？')">录用</button>
                        </form>
                    <% } %>
                </td>
            </tr>
            <% } %>
        </table>
        <% } %>
    <% } %>
    
    <button onclick="window.location.href='hireApplicant.jsp'" class="return-btn">返回验证</button>
    
</body>
</html>