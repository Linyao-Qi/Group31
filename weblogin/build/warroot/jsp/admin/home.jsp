<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Console (Web)</title>
    <style>
        * { box-sizing: border-box; }
        body { margin: 0; min-height: 100vh; background: #f3f6fb; color: #1f2937; font-family: Arial, Helvetica, sans-serif; }
        .page { width: min(1180px, calc(100% - 48px)); margin: 0 auto; padding: 28px 0 40px; }
        .nav { display: flex; align-items: center; justify-content: space-between; gap: 16px; padding: 12px 14px; margin-bottom: 18px; background: #fff; border: 1px solid #e5e7eb; border-radius: 14px; box-shadow: 0 8px 22px rgba(15, 23, 42, 0.06); }
        .brand { font-weight: 800; color: #111827; white-space: nowrap; }
        .nav-links { display: flex; align-items: center; justify-content: flex-end; flex-wrap: wrap; gap: 8px; }
        .nav a, .nav button { display: inline-flex; align-items: center; justify-content: center; min-height: 34px; padding: 8px 11px; border-radius: 9px; border: 1px solid transparent; background: transparent; color: #374151; font: inherit; text-decoration: none; cursor: pointer; }
        .nav a:hover, .nav button:hover { background: #eff6ff; color: #1d4ed8; }
        .nav .active { background: #2563eb; color: #fff; }
        .nav .active:hover { background: #1d4ed8; color: #fff; }
        .card { background: #fff; border: 1px solid #e5e7eb; border-radius: 14px; box-shadow: 0 8px 22px rgba(15, 23, 42, 0.06); padding: 30px; }
        h2 { margin: 0; font-size: 30px; line-height: 1.2; color: #111827; }
        .subtitle { margin: 8px 0 22px; color: #6b7280; }
        .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 12px; margin-bottom: 18px; }
        .entry {
            display: block; padding: 16px; border: 1px solid #dbeafe; border-radius: 12px;
            background: #eff6ff; color: #1d4ed8; text-decoration: none; font-weight: 700;
        }
        .entry:hover { background: #dbeafe; }
        .actions { display: flex; justify-content: flex-end; border-top: 1px solid #eef2f7; padding-top: 18px; }
        form { margin: 0; }
        button {
            min-height: 38px; padding: 9px 14px; border-radius: 9px; border: 1px solid #d1d5db;
            background: #fff; color: #374151; font: inherit; cursor: pointer;
        }
        button:hover { background: #f9fafb; border-color: #9ca3af; }
    </style>
</head>
<body>
<div class="page">
    <div class="nav">
        <div class="brand">Admin System</div>
        <div class="nav-links">
            <a class="active" href="${pageContext.request.contextPath}/admin/home">Home</a>
            <a href="${pageContext.request.contextPath}/admin/workloads">CheckWorkload</a>
            <a href="${pageContext.request.contextPath}/admin/posts">ClosePost</a>
            <a href="${pageContext.request.contextPath}/admin/report">Recruitment Report</a>
            <form method="post" action="${pageContext.request.contextPath}/admin/logout">
                <button type="submit">Logout</button>
            </form>
        </div>
    </div>

    <div class="card">
        <h2>Admin Console</h2>
        <p class="subtitle">Choose an administration module.</p>

        <div class="grid">
            <a class="entry" href="${pageContext.request.contextPath}/admin/workloads">CheckWorkload</a>
            <a class="entry" href="${pageContext.request.contextPath}/admin/posts">ClosePost</a>
            <a class="entry" href="${pageContext.request.contextPath}/admin/report">Recruitment Report</a>
        </div>

    </div>
</div>
</body>
</html>
