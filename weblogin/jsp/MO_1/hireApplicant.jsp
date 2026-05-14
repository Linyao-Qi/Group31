<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>MO Authentication - Hire Applicant</title>
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
        <a href="${pageContext.request.contextPath}/jsp/MO_1/publishJob.jsp">Publish Job</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/hireApplicant.jsp">Hire Applicant</a>

        <a href="${pageContext.request.contextPath}/jsp/MO_1/appList.jsp">Application List</a>
    </div>
    <h2 align="center">MO Authentication</h2>

    <form action="${pageContext.request.contextPath}/jsp/MO_1/moApplicantList.jsp" method="post">
        <div class="form-item">
            <label>MO ID:</label>
            <input type="text" name="moId" required placeholder="Enter your MO ID">
        </div>
        <div class="form-item">
            <label>MO Password:</label>
            <input type="password" name="password" required placeholder="Enter your password">
        </div>
        <button type="submit">Authenticate & View My Applicants</button>
    </form>

    <%
        String error = request.getParameter("error");
        if (error != null && error.equals("1")) {
    %>
        <div class="msg fail">
            Invalid MO ID or password, please try again!
        </div>
    <%
        }
    %>

</body>
</html>