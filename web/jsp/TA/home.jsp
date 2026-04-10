<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>TA Home</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 900px; margin: 50px auto; padding: 0 20px; }
        .nav { margin-bottom: 40px; text-align: center; }
        .nav a { margin: 0 12px; color: #2563eb; text-decoration: none; font-size: 16px; }
        .nav a:hover { text-decoration: underline; }
        .nav form { display: inline; }
        .nav button { background: none; border: none; color: #2563eb; font-size: 16px; cursor: pointer; padding: 0; margin: 0 12px; }
        .nav button:hover { text-decoration: underline; }
        h2 { text-align: center; color: #1e293b; margin-bottom: 40px; }
        .card-grid { display: flex; gap: 24px; justify-content: center; flex-wrap: wrap; }
        .card { background: white; border: 1px solid #ddd; border-radius: 8px; padding: 32px 36px; width: 200px; text-align: center; box-shadow: 0 2px 8px rgba(0,0,0,0.06); text-decoration: none; color: #1e293b; transition: box-shadow 0.2s; }
        .card:hover { box-shadow: 0 4px 16px rgba(37,99,235,0.15); border-color: #2563eb; }
        .card .icon { font-size: 36px; margin-bottom: 12px; }
        .card .label { font-size: 15px; font-weight: bold; color: #2563eb; }
        .welcome { text-align: center; color: #64748b; margin-bottom: 32px; font-size: 15px; }
    </style>
</head>
<body>
    <div class="nav">
        <a href="${pageContext.request.contextPath}/ta/home">Home</a>
        <a href="${pageContext.request.contextPath}/ta/jobs">Job List</a>
        <a href="${pageContext.request.contextPath}/ta/status">My Applications</a>
        <a href="${pageContext.request.contextPath}/ta/profile">Profile</a>
        <form method="post" action="${pageContext.request.contextPath}/ta/logout">
            <button type="submit">Logout</button>
        </form>
    </div>

    <h2>Welcome, <%= session.getAttribute("taId") %></h2>
    <p class="welcome">TA Job Application Portal</p>

    <div class="card-grid">
        <a href="${pageContext.request.contextPath}/ta/jobs" class="card">
            <div class="icon">&#128203;</div>
            <div class="label">Browse Jobs</div>
        </a>
        <a href="${pageContext.request.contextPath}/ta/status" class="card">
            <div class="icon">&#128196;</div>
            <div class="label">My Applications</div>
        </a>
        <a href="${pageContext.request.contextPath}/ta/profile" class="card">
            <div class="icon">&#128100;</div>
            <div class="label">My Profile</div>
        </a>
    </div>
</body>
</html>
