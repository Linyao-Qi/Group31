<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String error = request.getAttribute("error") == null ? "" : String.valueOf(request.getAttribute("error"));
    String username = request.getAttribute("username") == null ? "" : String.valueOf(request.getAttribute("username"));
%>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Access</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .wrap { max-width: 520px; margin: 0 auto; }
        .card { border: 1px solid #ddd; border-radius: 8px; padding: 24px; }
        .entry { text-align: center; }
        .entry button { padding: 10px 18px; }
        .login { display: none; }
        .row { margin-bottom: 12px; }
        .row label { display: block; margin-bottom: 4px; }
        .row input { width: 100%; padding: 8px; box-sizing: border-box; }
        .actions { display: flex; gap: 8px; }
        .error { color: #b00020; margin-bottom: 10px; }
    </style>
</head>
<body>
<div class="wrap">
    <h2>TA Job System</h2>

    <div id="entryCard" class="card entry">
        <p>Administrator access only.</p>
        <button type="button" id="goLoginBtn">Administrator Login</button>
    </div>

    <div id="loginCard" class="card login">
        <% if (!error.isEmpty()) { %>
        <div class="error"><%= error %></div>
        <% } %>
        <form method="post" action="${pageContext.request.contextPath}/admin/login">
            <div class="row">
                <label for="username">Username</label>
                <input id="username" name="username" type="text" value="<%= username %>" autocomplete="username"/>
            </div>
            <div class="row">
                <label for="password">Password</label>
                <input id="password" name="password" type="password" autocomplete="current-password"/>
            </div>
            <div class="actions">
                <button type="button" id="backBtn">Back</button>
                <button type="submit">Login</button>
            </div>
        </form>
    </div>
</div>

<script>
    const entryCard = document.getElementById("entryCard");
    const loginCard = document.getElementById("loginCard");
    const goLoginBtn = document.getElementById("goLoginBtn");
    const backBtn = document.getElementById("backBtn");
    const hasError = <%= error.isEmpty() ? "false" : "true" %>;

    function showEntry() {
        entryCard.style.display = "block";
        loginCard.style.display = "none";
    }

    function showLogin() {
        entryCard.style.display = "none";
        loginCard.style.display = "block";
    }

    goLoginBtn.addEventListener("click", showLogin);
    backBtn.addEventListener("click", showEntry);

    if (hasError) {
        showLogin();
    } else {
        showEntry();
    }
</script>
</body>
</html>

