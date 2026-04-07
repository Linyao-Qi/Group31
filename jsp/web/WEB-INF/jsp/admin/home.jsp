<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Console (Web)</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .wrap { max-width: 640px; margin: 0 auto; text-align: center; }
        .btn {
            display: inline-block;
            padding: 12px 20px;
            margin: 8px;
            border: 1px solid #333;
            text-decoration: none;
            color: #111;
            border-radius: 6px;
        }
    </style>
</head>
<body>
<div class="wrap">
    <h2>Admin Console (JSP + Servlet)</h2>
    <p>Choose one module:</p>
    <a class="btn" href="${pageContext.request.contextPath}/admin/workloads">Open CheckWorkload</a>
    <a class="btn" href="${pageContext.request.contextPath}/admin/posts">Open ClosePost</a>
</div>
</body>
</html>
