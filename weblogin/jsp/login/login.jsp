<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Teaching Assistant Recruitment Login</title>
    <style>
        * {box-sizing: border-box; margin: 0; padding: 0;}
        body {font-family: Arial, sans-serif; max-width: 450px; margin: 80px auto; padding: 0 20px; background: #f8fafc;}
        .login-box {background: white; border-radius: 10px; padding: 30px; box-shadow: 0 2px 15px rgba(0,0,0,0.1);}
        .title {text-align: center; color: #333; margin-bottom: 30px; font-size: 24px;}
        .user-type-select {width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 5px; font-size: 14px; margin-bottom: 20px;}
        .tab-group {display: flex; border-bottom: 1px solid #ddd; margin-bottom: 20px; display: none;}
        .tab {flex: 1; padding: 12px; text-align: center; cursor: pointer; font-size: 16px; color: #666; border-bottom: 3px solid transparent;}
        .tab.active {color: #2563eb; border-bottom-color: #2563eb; font-weight: bold;}
        .form-item {margin: 15px 0;}
        label {display: block; margin-bottom: 8px; color: #333; font-size: 14px;}
        input {width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 5px; font-size: 14px; transition: border 0.3s;}
        input:focus {border-color: #2563eb; outline: none;}
        button {width: 100%; padding: 12px; background: #2563eb; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; margin-top: 10px; transition: background 0.3s;}
        button:hover {background: #1d4ed8;}
        .msg {margin: 20px 0; padding: 10px; border-radius: 5px; text-align: center; font-size: 14px;}
        .success {background: #dcfce7; color: #166534;}
        .fail {background: #fee2e2; color: #991b1b;}
        .hidden {display: none;}
    </style>
</head>
<body>
    <div class="login-box">
        <div class="title">Teaching Assistant Recruitment Login</div>

        <select class="user-type-select" id="userType" onchange="changeUserType()">
            <option value="">-- Select User Type --</option>
            <option value="MO">MO</option>
            <option value="TA">TA</option>
            <option value="ADMIN">ADMIN</option>
        </select>

        <%
            String msg = (String) request.getAttribute("msg");
            if (msg != null) {
                String cls = msg.contains("success") ? "success" : "fail";
                out.print("<div class='msg " + cls + "'>" + msg + "</div>");
            }
        %>

        <!-- 登录/注册 标签栏 -->
        <div class="tab-group" id="tabGroup">
            <div class="tab active" onclick="switchTab('login')">Login</div>
            <div class="tab" onclick="switchTab('register')">Register</div>
        </div>

        <!-- 登录表单（和注册完全一样样式） -->
        <div id="loginForm" class="hidden">
            <form id="loginFormElement" action="${pageContext.request.contextPath}/login" method="post" onsubmit="return validateUserType()">
                <input type="hidden" name="userType" id="loginUserType">
                <div class="form-item">
                    <label id="loginIdLabel">User ID:</label>
                    <input type="text" name="userId" required placeholder="Enter your ID">
                </div>
                <div class="form-item">
                    <label>Password:</label>
                    <input type="password" name="password" required placeholder="Enter your password">
                </div>
                <button type="submit">Login</button>
            </form>
        </div>

        <!-- 注册表单 -->
        <div id="registerForm" class="hidden">
            <form action="${pageContext.request.contextPath}/register" method="post">
                <input type="hidden" name="userType" id="registerUserType">
                <div class="form-item">
                    <label>User ID:</label>
                    <input type="text" name="userId" required placeholder="Enter your ID">
                </div>
                <div class="form-item">
                    <label>Password:</label>
                    <input type="password" name="password" required placeholder="Enter your password">
                </div>
                <div class="form-item">
                    <label>Confirm Password:</label>
                    <input type="password" name="confirmPassword" required placeholder="Confirm your password">
                </div>
                <button type="submit">Register</button>
            </form>
        </div>
    </div>

    <script>
        function changeUserType() {
            const userType = document.getElementById('userType').value;
            const loginForm = document.getElementById('loginForm');
            const registerForm = document.getElementById('registerForm');
            const tabGroup = document.getElementById('tabGroup');
            const loginIdLabel = document.getElementById('loginIdLabel');
            const loginUserType = document.getElementById('loginUserType');
            const registerUserType = document.getElementById('registerUserType');
            const loginFormElement = document.getElementById('loginFormElement');

            loginUserType.value = userType;
            registerUserType.value = userType;
            loginFormElement.action = '${pageContext.request.contextPath}/login';

            if (userType === 'MO') {
                loginIdLabel.innerText = 'MO ID:';
                tabGroup.style.display = 'none';
                loginForm.style.display = 'block';
                registerForm.classList.add('hidden');
            } else if (userType === 'TA') {
                loginIdLabel.innerText = 'User ID:';
                tabGroup.style.display = 'flex';
                loginForm.style.display = 'block';
                registerForm.classList.add('hidden');
                document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
                document.querySelectorAll('.tab')[0].classList.add('active');
            } else if (userType === 'ADMIN') {
                loginIdLabel.innerText = 'Admin ID:';
                loginFormElement.action = '${pageContext.request.contextPath}/admin/unified-login';
                tabGroup.style.display = 'none';
                loginForm.style.display = 'block';
                registerForm.classList.add('hidden');
            } else {
                loginForm.style.display = 'none';
                tabGroup.style.display = 'none';
                registerForm.classList.add('hidden');
            }
        }

        function validateUserType() {
            const userType = document.getElementById('userType').value;
            if (!userType) {
                alert('Please select a user type.');
                return false;
            }
            return true;
        }

        function switchTab(type) {
            const loginForm = document.getElementById('loginForm');
            const registerForm = document.getElementById('registerForm');
            const tabs = document.querySelectorAll('.tab');
            tabs.forEach(t => t.classList.remove('active'));

            if (type === 'login') {
                tabs[0].classList.add('active');
                loginForm.style.display = 'block';
                registerForm.classList.add('hidden');
            } else {
                tabs[1].classList.add('active');
                loginForm.style.display = 'none';
                registerForm.classList.remove('hidden');
            }
        }
    </script>
</body>
</html>
