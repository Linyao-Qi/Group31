<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.Job" %>
<%@ page import="com.MoService" %>
<%@ page import="com.CsvFileUtil" %>
<html>
<head>
    <title>所有TA岗位列表</title>
    <style>
        body {font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 0 20px;}
        table {width: 100%; border-collapse: collapse; margin-top: 20px;}
        th, td {border: 1px solid #ddd; padding: 12px; text-align: center;}
        th {background: #f8fafc; color: #333;}
        .nav {margin-bottom: 30px; text-align: center;}
        .nav a {margin: 0 10px; color: #2563eb; text-decoration: none;}
        .nav a:hover {text-decoration: underline;}
        .empty {text-align: center; margin-top: 30px; color: #666;}
    </style>
</head>
<body>
    <div class="nav">
        <a href="publishJob.jsp">发布岗位</a>
        <a href="hireApplicant.jsp">录用申请者</a>
        <a href="jobList.jsp">查看所有岗位</a>
        <a href="appList.jsp">查看所有申请</a>
    </div>
    <h2 align="center">TA岗位列表</h2>
    <%
        MoService moService = new MoService();
        List<Job> jobList = moService.getAllJobs();
        if (jobList == null || jobList.size() == 0) {
            out.print("<div class='empty'>暂无岗位数据，请先发布岗位</div>");
        } else {
    %>
    <table>
        <tr>
            <th>岗位ID</th>
            <th>发布MO ID</th>
            <th>岗位名称</th>
            <th>岗位要求</th>
            <th>岗位状态</th>
        </tr>
        <%
            for (Job job : jobList) {
                out.print("<tr>");
                out.print("<td>" + job.getJobId() + "</td>");
                out.print("<td>" + job.getMoId() + "</td>");
                out.print("<td>" + job.getJobName() + "</td>");
                out.print("<td>" + job.getJobRequirements() + "</td>");
                out.print("<td>" + job.getJobStatus() + "</td>");
                out.print("</tr>");
            }
        %>
    </table>
    <%
        }
    %>
</body>
</html>