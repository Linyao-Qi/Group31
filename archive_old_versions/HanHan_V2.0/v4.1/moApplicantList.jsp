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
    <title>我的待审核申请者</title>
    <style>
        body {font-family: Arial, sans-serif; max-width: 600px; margin: 50px auto; padding: 0 20px;}
        .form-item {margin: 15px 0;}
        input {width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 5px; font-size: 14px;}
        button {width: 100%; padding: 12px; background: #2563eb; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px;}
        button:hover {background: #1d4ed8;}
        .msg {margin: 20px 0; padding: 10px; border-radius: 5px; text-align: center;}
        .success {background: #dcfce7; color: #166534;}
        .fail {background: #fee2e2; color: #991b1b;}
        .empty {background: #fef3c7; color: #92400e;}
        .nav {margin-bottom: 30px; text-align: center;}
        .nav a {margin: 0 10px; color: #2563eb; text-decoration: none;}
        .nav a:hover {text-decoration: underline;}
        table {width: 100%; border-collapse: collapse; margin: 20px 0;}
        th, td {border: 1px solid #ddd; padding: 10px; text-align: center; font-size: 14px;}
        th {background: #f8fafc;}
        .intro, .req {text-align: left; max-width: 180px; word-break: break-word;}
        .btn-hire {padding: 6px 10px; background: #16a34a; font-size: 14px; border-radius: 5px;}
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
    // 先获取变量 → 再使用
    request.setCharacterEncoding("UTF-8");
    String moId = request.getParameter("moId");
    String password = request.getParameter("password");
    MoService moService = new MoService();
    boolean authPass = false;
    List<Application> appList = null;
    String msg = null;
    // 新增：岗位ID映射岗位名称+需求，避免多次循环查询
    Map<String, Job> jobMap = new HashMap<>();

    if (moId == null || password == null || moId.isBlank() || password.isBlank()) {
        msg = "请输入账号密码";
    } else if (!AuthUtil.authenticateMO(moId, password)) {
        msg = "MO账号或密码错误";
    } else {
        authPass = true;
        List<Job> allJobs = moService.getAllJobs();
        List<Application> allApps = moService.getAllApps(moId, password, false);
        // 岗位ID做key，存整个岗位对象，方便快速取值
        for(Job job : allJobs){
            jobMap.put(job.getJobId(), job);
        }

        appList = new java.util.ArrayList<>();
        if (allApps != null && allJobs != null) {
            for (Application app : allApps) {
                if (!"PENDING".equals(app.getAppStatus())) continue;
                for (Job job : allJobs) {
                    if (app.getJobId().equals(job.getJobId()) && moId.equals(job.getMoId())) {
                        appList.add(app);
                        break;
                    }
                }
            }
        }
    }
%>

    <!-- 变量定义完了，现在可以用了 -->
    <h2 align="center">MO【<%=moId != null ? moId : ""%>】- 待录用申请者</h2>

<% if (msg != null) { %>
    <div class="msg fail"><%=msg%></div>
<% } %>

<% if (authPass) { %>
    <% if (appList == null || appList.isEmpty()) { %>
        <div class="msg empty">暂无待审核申请者</div>
    <% } else { %>
        <table>
            <tr>
                <th>申请ID</th>
                <th>岗位名称</th>
                <th>岗位需求</th>
                <th>TA介绍</th>
                <th>操作</th>
            </tr>
            <% for (Application app : appList) { %>
                <% 
                    // 根据申请的岗位ID获取岗位对象
                    Job job = jobMap.get(app.getJobId());
                    String jobName = job == null ? "未知岗位" : job.getJobName();
                    String jobReq = job == null ? "无" : job.getJobRequirements();
                %>
            <tr>
                <td><%=app.getAppId()%></td>
                <td><%=jobName%></td>
                <td class="req"><%=jobReq%></td>
                <td class="intro"><%=app.getIntro()%></td>
                <td>
                    <form action="hireApplicant" method="post" style="margin:0">
                        <input type="hidden" name="moId" value="<%=moId%>">
                        <input type="hidden" name="appId" value="<%=app.getAppId()%>">
                        <button type="submit" class="btn-hire">录用</button>
                    </form>
                </td>
            </tr>
            <% } %>
        </table>
    <% } %>
<% } %>

<button onclick="window.location.href='hireApplicant.jsp'" style="margin-top:10px;">返回验证</button>

<%
    String result = (String) request.getAttribute("msg");
    if (result != null) {
        String cls = result.contains("成功") ? "success" : "fail";
%>
    <div class="msg <%=cls%>"><%=result%></div>
<% } %>

</body>
</html>