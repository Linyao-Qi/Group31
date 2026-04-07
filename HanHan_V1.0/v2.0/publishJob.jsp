<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>MO发布TA岗位</title>
    <style>
        body {font-family: Arial, sans-serif; max-width: 600px; margin: 50px auto; padding: 0 20px;}
        .form-item {margin: 15px 0;}
        input, textarea {width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 5px; font-size: 14px;}
        button {width: 100%; padding: 12px; background: #2563eb; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px;}
        button:hover {background: #1d4ed8;}
        .msg {margin: 20px 0; padding: 10px; border-radius: 5px; text-align: center;}
        .success {background: #dcfce7; color: #166534;}
        .fail {background: #fee2e2; color: #991b1b;}
        .nav {margin-bottom: 30px; text-align: center;}
        .nav a {margin: 0 10px; color: #2563eb; text-decoration: none;}
        .nav a:hover {text-decoration: underline;}
    </style>
</head>
<body>
    <div class="nav">
        <a href="publishJob.jsp">发布岗位</a>
        <a href="hireApplicant.jsp">录用申请者</a>
        <a href="jobList.jsp">查看所有岗位</a>
        <a href="appList.jsp">查看所有申请</a>
    </div>
    <h2 align="center">MO发布TA岗位</h2>
    <!-- 表单提交到Servlet：/publishJob -->
    <form action="publishJob" method="post">
        <div class="form-item">
            <label>MO ID（如MO001）：</label>
            <input type="text" name="moId" required placeholder="请输入MO唯一ID">
        </div>
        <div class="form-item">
            <label>岗位名称：</label>
            <input type="text" name="jobName" required placeholder="如Java课程TA、监考助理">
        </div>
        <div class="form-item">
            <label>岗位要求：</label>
            <textarea name="jobRequirements" rows="4" required placeholder="如熟悉Java基础、有教学辅助经验"></textarea>
        </div>
        <button type="submit">发布岗位</button>
    </form>
    <!-- 展示后端返回的提示信息 -->
    <%
        String msg = (String) request.getAttribute("msg");
        if (msg != null) {
            if (msg.contains("成功")) {
                out.print("<div class='msg success'>" + msg + "</div>");
            } else {
                out.print("<div class='msg fail'>" + msg + "</div>");
            }
        }
    %>
</body>
</html>