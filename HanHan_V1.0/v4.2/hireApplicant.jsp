<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>MO录用TA申请者-身份认证</title>
    <style>
        body {font-family: Arial, sans-serif; max-width: 600px; margin: 50px auto; padding: 0 20px;}
        .form-item {margin: 15px 0;}
        input {width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 5px; font-size: 14px;}
        button {width: 100%; padding: 12px; background: #2563eb; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px;}
        button:hover {background: #1d4ed8;}
        .msg {margin: 20px 0; padding: 10px; border-radius: 5px; text-align: center;}
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
    <h2 align="center">MO身份验证</h2>
    <form action="moApplicantList.jsp" method="post">
        <div class="form-item">
            <label>MO ID：</label>
            <input type="text" name="moId" required placeholder="请输入您的MO ID">
        </div>
        <div class="form-item">
            <label>MO 密码：</label>
            <input type="password" name="password" required placeholder="请输入密码">
        </div>
        <button type="submit">验证并查看我的申请者</button>
    </form>
    <%
        String msg = (String) request.getAttribute("msg");
        if (msg != null) {
            out.print("<div class='msg fail'>"+msg+"</div>");
        }
    %>
</body>
</html>