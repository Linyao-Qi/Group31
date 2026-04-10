<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.Application" %>
<%@ page import="com.MoService" %>
<%@ page import="com.JsonFileUtil" %>
<html>
<head>
    <title>所有TA申请列表</title>
    <style>
        body {font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 0 20px;}
        table {width: 100%; border-collapse: collapse; margin-top: 20px;}
        th, td {border: 1px solid #ddd; padding: 12px; text-align: center;}
        th {background: #f8fafc; color: #333;}
        .nav {margin-bottom: 30px; text-align: center;}
        .nav a {margin: 0 10px; color: #2563eb; text-decoration: none;}
        .nav a:hover {text-decoration: underline;}
        .empty {text-align: center; margin-top: 30px; color: #666;}
        .accepted {color: #166534; font-weight: bold;}
        .pending {color: #f59e0b; font-weight: bold;}
    </style>
</head>
<body>
    <div class="nav">
        <a href="publishJob.jsp">发布岗位</a>
        <a href="hireApplicant.jsp">录用申请者</a>
        <a href="jobList.jsp">查看所有岗位</a>
        <a href="appList.jsp">查看所有申请</a>
    </div>
    <h2 align="center">TA申请列表（真实存储）</h2>
    <%
        MoService moService = new MoService();
        List<Application> appList = moService.getAllApps();
        if (appList == null || appList.size() == 0) {
            out.print("<div class='empty'>暂无申请数据，请先添加申请</div>");
        } else {
    %>
    <table>
        <tr>
            <th>申请ID</th>
            <th>关联岗位ID</th>
            <th>申请者TA ID</th>
            <th>申请状态</th>
        </tr>
        <%
            for (Application app : appList) {
                out.print("<tr>");
                out.print("<td>" + app.getAppId() + "</td>");
                out.print("<td>" + app.getJobId() + "</td>");
                out.print("<td>" + app.getTaId() + "</td>");
                if ("ACCEPTED".equals(app.getAppStatus())) {
                    out.print("<td class='accepted'>" + app.getAppStatus() + "</td>");
                } else {
                    out.print("<td class='pending'>" + app.getAppStatus() + "</td>");
                }
                out.print("</tr>");
            }
        %>
    </table>
    <%
        }
    %>
</body>
</html>