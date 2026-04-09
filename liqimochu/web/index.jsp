<%--
  Created by IntelliJ IDEA.
  User: 李玘墨初
  Date: 2026/4/9
  Time: 15:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Home</title>
    <style>
        /* 全局样式统一 */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
        }
        body {
            background-color: #ffffff;
            padding: 40px;
            text-align: center;
        }

        /* 标题 */
        h2 {
            font-size: 42px;
            font-weight: 700;
            margin-top: 60px;
            margin-bottom: 50px;
            color: #000;
        }

        .home-btn {
            display: block;
            width: 320px;
            margin: 20px auto;
            padding: 18px;
            background-color: #2563eb;
            color: white;
            font-size: 22px;
            font-weight: 600;
            text-decoration: none;
            border-radius: 12px;
            transition: background 0.2s;
        }
        .home-btn:hover {
            background-color: #1d4ed8;
        }
    </style>
</head>
<body>

<h2>Welcome</h2>

<a href="profile" class="home-btn">Create<br>a new personal profile</a>
<a href="list" class="home-btn">View Submitted Data</a>

</body>
</html>
