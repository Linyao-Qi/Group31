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
        .field-hint {display: none; margin-top: 8px; font-size: 13px;}
        .field-hint.fail-text {display: block; color: #991b1b;}
        .field-hint.success-text {display: block; color: #166534;}
        .hidden {display: none;}
    </style>
</head>
<body>
    <div class="login-box">
        <div class="title">Teaching Assistant Recruitment Login</div>

        <%
            String selectedUserType = request.getAttribute("selectedUserType") == null
                    ? request.getParameter("userType")
                    : String.valueOf(request.getAttribute("selectedUserType"));
            if (selectedUserType == null) {
                selectedUserType = "";
            }
            selectedUserType = selectedUserType.trim().toUpperCase();

            String selectedAction = request.getAttribute("selectedAction") == null
                    ? request.getParameter("action")
                    : String.valueOf(request.getAttribute("selectedAction"));
            if (selectedAction == null) {
                selectedAction = "";
            }
            selectedAction = selectedAction.trim().toLowerCase();

            String userIdValue = request.getAttribute("userIdValue") == null
                    ? request.getParameter("userId")
                    : String.valueOf(request.getAttribute("userIdValue"));
            if (userIdValue == null) {
                userIdValue = "";
            }
            userIdValue = userIdValue
                    .replace("&", "&amp;")
                    .replace("\"", "&quot;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");

            String registerUserIdValue = request.getAttribute("registerUserIdValue") == null
                    ? String.valueOf(request.getAttribute("nextTaId"))
                    : String.valueOf(request.getAttribute("registerUserIdValue"));
            if (registerUserIdValue == null || "null".equals(registerUserIdValue)) {
                registerUserIdValue = "";
            }
            registerUserIdValue = registerUserIdValue
                    .replace("&", "&amp;")
                    .replace("\"", "&quot;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
        %>

        <select class="user-type-select" id="userType" onchange="changeUserType()">
            <option value="">-- Select User Type --</option>
            <option value="MO" <%= "MO".equals(selectedUserType) ? "selected" : "" %>>MO</option>
            <option value="TA" <%= "TA".equals(selectedUserType) ? "selected" : "" %>>TA</option>
            <option value="ADMIN" <%= "ADMIN".equals(selectedUserType) ? "selected" : "" %>>ADMIN</option>
        </select>

        <%
            String msg = (String) request.getAttribute("msg");
            if (msg != null) {
                String cls = msg.contains("success") ? "success" : "fail";
                out.print("<div class='msg " + cls + "'>" + msg + "</div>");
            }
        %>

        <div class="tab-group" id="tabGroup">
            <div class="tab active" onclick="switchTab('login')">Login</div>
            <div class="tab" onclick="switchTab('register')">Register</div>
        </div>

        <div id="loginForm" class="<%= selectedUserType.isEmpty() || "register".equals(selectedAction) ? "hidden" : "" %>">
            <form id="loginFormElement" action="${pageContext.request.contextPath}/login" method="post" onsubmit="return validateUserType()">
                <input type="hidden" name="userType" id="loginUserType">
                <div class="form-item">
                    <label id="loginIdLabel">User ID:</label>
                    <input type="text" name="userId" required placeholder="Enter your ID"
                           value="<%= userIdValue %>">
                </div>
                <div class="form-item">
                    <label>Password:</label>
                    <input type="password" name="password" required placeholder="Enter your password">
                </div>
                <button type="submit">Login</button>
            </form>
        </div>

        <div id="registerForm" class="<%= "register".equals(selectedAction) ? "" : "hidden" %>">
            <form action="${pageContext.request.contextPath}/login" method="post" onsubmit="return validateRegister()">
                <input type="hidden" name="action" value="register">
                <input type="hidden" name="userType" id="registerUserType">
                <div class="form-item">
                    <label>User ID:</label>
                    <input type="text" name="userId" id="registerUserIdInput" required placeholder="Enter your ID"
                           oninput="scheduleRegisterUserIdCheck()" onblur="checkRegisterUserId()"
                           value="<%= registerUserIdValue %>">
                    <div id="registerUserIdHint" class="field-hint"></div>
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
        let registerIdTimer = null;

        function changeUserType() {
            const userType = document.getElementById('userType').value;
            const selectedAction = '<%= selectedAction %>';
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
                if (selectedAction === 'register') {
                    document.querySelectorAll('.tab')[1].classList.add('active');
                    loginForm.style.display = 'none';
                    registerForm.classList.remove('hidden');
                    checkRegisterUserId();
                } else {
                    document.querySelectorAll('.tab')[0].classList.add('active');
                }
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

        function validateRegister() {
            const userType = document.getElementById('userType').value;
            const password = document.querySelector('#registerForm input[name="password"]').value;
            const confirmPassword = document.querySelector('#registerForm input[name="confirmPassword"]').value;
            const hint = document.getElementById('registerUserIdHint');

            if (userType !== 'TA') {
                alert('Please select TA before registering.');
                return false;
            }
            if (hint.dataset.status === 'exists') {
                alert('User ID already exists!');
                return false;
            }
            if (password !== confirmPassword) {
                alert('Passwords do not match.');
                return false;
            }
            return true;
        }

        function scheduleRegisterUserIdCheck() {
            clearTimeout(registerIdTimer);
            registerIdTimer = setTimeout(checkRegisterUserId, 300);
        }

        function checkRegisterUserId() {
            const userType = document.getElementById('userType').value;
            const input = document.getElementById('registerUserIdInput');
            const hint = document.getElementById('registerUserIdHint');
            const userId = input.value.trim();

            hint.dataset.status = '';
            hint.textContent = '';
            hint.className = 'field-hint';

            if (userType !== 'TA' || !userId) {
                return;
            }

            fetch('${pageContext.request.contextPath}/login?action=checkTaId&userId=' + encodeURIComponent(userId))
                .then(response => response.text())
                .then(status => {
                    if (input.value.trim() !== userId) {
                        return;
                    }
                    if (status === 'exists') {
                        hint.dataset.status = 'exists';
                        hint.textContent = 'User ID already exists!';
                        hint.className = 'field-hint fail-text';
                    } else if (status === 'available') {
                        hint.dataset.status = 'available';
                        hint.textContent = 'User ID is available.';
                        hint.className = 'field-hint success-text';
                    }
                })
                .catch(() => {
                    hint.dataset.status = '';
                    hint.textContent = '';
                    hint.className = 'field-hint';
                });
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

        changeUserType();
    </script>
</body>
</html>
