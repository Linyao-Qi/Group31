<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Application Form</title>
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
            max-width: 700px;
            margin: 0 auto;
        }

        /* 标题 */
        h2 {
            text-align: center;
            font-size: 36px;
            font-weight: 700;
            margin-bottom: 40px;
            color: #000;
        }

        /* 表单样式 */
        form {
            width: 100%;
        }

        /* 表单项 */
        .form-group {
            margin-bottom: 24px;
        }

        label {
            display: block;
            font-size: 18px;
            font-weight: 600;
            margin-bottom: 8px;
            color: #222;
        }

        input[type="text"],
        input[type="email"],
        input[type="file"] {
            width: 100%;
            padding: 14px 16px;
            font-size: 18px;
            border: 1px solid #ddd;
            border-radius: 8px;
            outline: none;
        }

        input:focus {
            border-color: #2563eb;
        }

        input[type="submit"] {
            width: 100%;
            padding: 18px;
            font-size: 22px;
            font-weight: 600;
            background-color: #2563eb;
            color: white;
            border: none;
            border-radius: 12px;
            cursor: pointer;
            margin-top: 10px;
        }

        input[type="submit"]:hover {
            background-color: #1d4ed8;
        }

        /* 返回首页按钮样式 */
        .back-btn {
            display: block;
            width: 100%;
            padding: 18px;
            font-size: 22px;
            font-weight: 600;
            background-color: #64748b;
            color: white;
            text-align: center;
            text-decoration: none;
            border-radius: 12px;
            margin-top: 16px;
        }
        .back-btn:hover {
            background-color: #475569;
        }
    </style>
</head>
<body>

<h2>Application Form</h2>

<form action="profile" method="post" enctype="multipart/form-data">
    <div class="form-group">
        <label>Name</label>
        <input type="text" name="name" value="${profile.name}">
    </div>

    <div class="form-group">
        <label>ID</label>
        <input type="text" name="id" value="${profile.id}" ${empty profile.id ? '' : 'readonly'}>
    </div>

    <div class="form-group">
        <label>Email</label>
        <input type="email" name="email" value="${profile.email}">
    </div>

    <div class="form-group">
        <label>Skills</label>
        <input type="text" name="skills" value="${profile.skills}">
    </div>

    <div class="form-group">
        <label>Major</label>
        <input type="text" name="major" value="${profile.major}">
    </div>

    <div class="form-group">
        <label>Upload CV (PDF)</label>
        <input type="file" name="cv">
    </div>

    <input type="hidden" name="existingCv" value="${profile.cvPath}">
    <input type="submit" value="Save">
</form>

<!-- 新增：返回首页按钮 -->
<a href="index.jsp" class="back-btn">Back to Home</a>

</body>
</html>