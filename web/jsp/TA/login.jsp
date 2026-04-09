<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>TA Login</title>
    <style>
        body { font-family: Arial, sans-serif; background: #f8fafc; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
        .card { background: white; border: 1px solid #ddd; border-radius: 8px; padding: 40px 50px; min-width: 360px; box-shadow: 0 2px 10px rgba(0,0,0,0.08); }
        h2 { text-align: center; color: #1e293b; margin-bottom: 28px; }
        .field { margin-bottom: 18px; }
        .field label { display: block; font-size: 14px; color: #374151; margin-bottom: 6px; }
        .field input { width: 100%; padding: 10px 12px; border: 1px solid #ddd; border-radius: 4px; font-size: 15px; box-sizing: border-box; }
        .field input:focus { outline: none; border-color: #2563eb; }
        .pw-wrap { position: relative; }
        .pw-wrap input { padding-right: 40px; }
        .pw-toggle { position: absolute; right: 10px; top: 50%; transform: translateY(-50%); background: none; border: none; cursor: pointer; color: #94a3b8; font-size: 16px; padding: 0; }
        #password::-ms-reveal, #password::-ms-clear { display: none; }
        #password::-webkit-credentials-auto-fill-button { display: none; }
        .btn { width: 100%; padding: 12px; background: #2563eb; color: white; border: none; border-radius: 4px; font-size: 16px; cursor: pointer; margin-top: 8px; }
        .btn:hover { background: #1d4ed8; }
        .error { background: #fee2e2; color: #991b1b; padding: 10px 14px; border-radius: 4px; font-size: 14px; margin-bottom: 16px; }
    </style>
</head>
<body>
<div class="card">
    <h2>TA Login</h2>
    <% if (request.getAttribute("error") != null) { %>
        <div class="error">${error}</div>
    <% } %>
    <form method="post" action="${pageContext.request.contextPath}/ta/login">
        <div class="field">
            <label>TA ID</label>
            <input type="text" name="taId" placeholder="e.g. TA001" required autofocus
                   value="<%= request.getAttribute("taIdValue") != null ? request.getAttribute("taIdValue") : "" %>">
        </div>
        <div class="field">
            <label>Password</label>
            <div class="pw-wrap">
                <input type="password" id="password" name="password" placeholder="Enter your password" required>
                <button type="button" class="pw-toggle" id="pwBtn" onclick="var i=document.getElementById('password');var b=document.getElementById('pwBtn');if(i.type==='password'){i.type='text';b.innerHTML='<svg width=&quot;18&quot; height=&quot;18&quot; viewBox=&quot;0 0 24 24&quot; fill=&quot;none&quot; stroke=&quot;currentColor&quot; stroke-width=&quot;2&quot;><path d=&quot;M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24&quot;/><line x1=&quot;1&quot; y1=&quot;1&quot; x2=&quot;23&quot; y2=&quot;23&quot;/></svg>';}else{i.type='password';b.innerHTML='<svg width=&quot;18&quot; height=&quot;18&quot; viewBox=&quot;0 0 24 24&quot; fill=&quot;none&quot; stroke=&quot;currentColor&quot; stroke-width=&quot;2&quot;><path d=&quot;M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z&quot;/><circle cx=&quot;12&quot; cy=&quot;12&quot; r=&quot;3&quot;/></svg>';}"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg></button>
            </div>
        </div>
        <button type="submit" class="btn">Login</button>
    </form>
</div>
</body>
</html>
