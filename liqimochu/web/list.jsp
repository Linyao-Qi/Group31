<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, model.Profile" %>
<html>
<head>
    <title>Applicant List</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
        }
        body {
            background-color: #ffffff;
            padding: 40px;
        }
        h2 {
            text-align: center;
            font-size: 36px;
            font-weight: 700;
            margin-bottom: 30px;
            color: #000000;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            background: #fff;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            border-radius: 8px;
            overflow: hidden;
        }
        th, td {
            padding: 16px;
            text-align: center;
            border-bottom: 1px solid #f0f0f0;
            font-size: 18px;
        }
        th {
            background-color: #f8f9fa;
            font-weight: 600;
        }
        tr:hover {
            background-color: #f8f9fa;
        }

        /* 按钮样式 */
        .edit-btn {
            padding: 8px 16px;
            background: #28a745;
            color: white;
            text-decoration: none;
            border-radius: 6px;
            font-size: 16px;
            margin-right: 4px;
        }
        .delete-btn {
            padding: 8px 16px;
            background: #dc3545;
            color: white;
            text-decoration: none;
            border-radius: 6px;
            font-size: 16px;
            border: none;
            cursor: pointer;
        }
        .back-btn {
            display: block;
            width: 100%;
            margin-top: 30px;
            padding: 18px;
            background: #2563eb;
            color: white;
            text-align: center;
            font-size: 24px;
            font-weight: 600;
            text-decoration: none;
            border-radius: 12px;
        }
    </style>
</head>
<body>

<h2>All Applicants</h2>

<table>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Email</th>
        <th>Skills</th>
        <th>CV</th>
        <th>Operation</th>
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
            <a href="profile_uploads/<%= p.getCvPath() %>" target="_blank" style="color:#0d6efd">View PDF</a>
        </td>

        <td>
            <a href="profile?id=<%= p.getId() %>" class="edit-btn">Edit</a>
            <a href="delete?id=<%= p.getId() %>"
               class="delete-btn"
               onclick="return confirm('Are you sure you want to delete this profile?');">
                Delete
            </a>
        </td>
    </tr>
    <%
            }
        }
    %>
</table>

<a href="index.jsp" class="back-btn">Back</a>

</body>
</html>