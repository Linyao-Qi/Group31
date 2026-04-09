<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, model.Profile" %>
<html>
<head>
    <title>Applicant List</title>
    <style>
        .edit-btn {
            padding: 4px 10px;
            background: #4CAF50;
            color: white;
            text-decoration: none;
            border-radius: 4px;
        }
        .back-btn {
            margin-top: 20px;
            padding: 8px 16px;
            background: #2196F3;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            display: inline-block;
        }
    </style>
</head>
<body>
<h2>All Applicants</h2>

<table border="1" cellpadding="6" cellspacing="0">
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Email</th>
        <th>Skills</th>
        <th>CV</th>
        <th>Operation</th> <!-- 改为操作列 -->
    </tr>

    <%
        List<Profile> list = (List<Profile>) request.getAttribute("list");
        if (list != null && !list.isEmpty()) {
            for (Profile p : list) {
    %>
    <tr>
        <td><%= p.getId() %></td>
        <td><%= p.getName() %></td>
        <td><%= p.getEmail() %></td>
        <td><%= p.getSkills() %></td>

        <td>
            <a href="profile_uploads/<%= p.getCvPath() %>" target="_blank">View PDF</a>
        </td>

        <!-- 独立的 Edit 按钮 -->
        <td>
            <a href="profile?id=<%= p.getId() %>" class="edit-btn">Edit</a>
        </td>
    </tr>
    <%
            }
        }
    %>
</table>

<!-- 返回首页按钮 -->
<a href="index.jsp" class="back-btn">Return to Home</a>

</body>
</html>