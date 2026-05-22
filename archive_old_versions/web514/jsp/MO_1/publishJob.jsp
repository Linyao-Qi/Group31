<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Publish TA Job</title>
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
        <a href="${pageContext.request.contextPath}/jsp/MO_1/publishJob.jsp">Publish Job</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/hireApplicant.jsp">Hire Applicant</a>

        <a href="${pageContext.request.contextPath}/jsp/MO_1/appList.jsp">Application List</a>
    </div>
    <h2 align="center">Publish TA Job</h2>

    <form action="${pageContext.request.contextPath}/publishJob" method="post">
        <div class="form-item">
            <label>MO ID (e.g. mo001):</label>
            <input type="text" name="moId" required placeholder="Enter your MO ID">
        </div>

        <div class="form-item">
            <label>MO Password:</label>
            <input type="password" name="password" required placeholder="Enter your password">
        </div>

        <div class="form-item">
            <label>Subject / Course:</label>
            <input type="text" name="subject" required placeholder="e.g. Java, Python, Software Engineering">
        </div>

        <div class="form-item">
            <label>Work Type:</label>
            <input type="text" name="workType" required placeholder="e.g. Lab, Tutorial, Grading">
        </div>

        <div class="form-item">
            <label>Job Description:</label>
            <textarea name="description" rows="3" required placeholder="Describe the job duty"></textarea>
        </div>

        <div class="form-item">
            <label>Skill Requirement:</label>
            <textarea name="skillRequirement" rows="3" required placeholder="e.g. Java, Python, Excel"></textarea>
        </div>

        <div class="form-item">
            <label>Hours Per Week:</label>
            <input type="number" name="hoursPerWeek" required placeholder="e.g. 6, 8">
        </div>

        <div class="form-item">
            <label>Compensation:</label>
            <input type="text" name="compensation" required placeholder="e.g. $18/hour">
        </div>

        <div class="form-item">
            <label>Max Hire (Number of People):</label>
            <input type="number" name="maxHire" required min="1" placeholder="e.g. 1, 2, 3">
        </div>

        <button type="submit">Publish Job</button>
    </form>

    <%
        String msg = (String) request.getAttribute("msg");
        if (msg != null) {
            if (msg.contains("success") || msg.contains("Success")) {
                out.print("<div class='msg success'>" + msg + "</div>");
            } else {
                out.print("<div class='msg fail'>" + msg + "</div>");
            }
        }
    %>
</body>
</html>