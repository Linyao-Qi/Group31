<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="TA.TAProfile" %>
<html>
<head>
    <title>My Profile</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 700px; margin: 40px auto; padding: 0 20px; }
        .nav { margin-bottom: 30px; text-align: center; }
        .nav a { margin: 0 12px; color: #2563eb; text-decoration: none; font-size: 15px; }
        .nav a:hover { text-decoration: underline; }
        .nav form { display: inline; }
        .nav button { background: none; border: none; color: #2563eb; font-size: 15px; cursor: pointer; padding: 0; margin: 0 12px; }
        .nav button:hover { text-decoration: underline; }
        h2 { color: #1e293b; margin-bottom: 24px; }
        .field { margin-bottom: 16px; }
        .field label { display: block; font-size: 14px; color: #374151; margin-bottom: 6px; font-weight: bold; }
        .field input[type=text], .field input[type=email] {
            width: 100%; padding: 9px 12px; border: 1px solid #ddd;
            border-radius: 4px; font-size: 14px; box-sizing: border-box;
        }
        .field input:focus { outline: none; border-color: #2563eb; }
        .field small { color: #64748b; font-size: 12px; }
        .btn { padding: 10px 28px; background: #2563eb; color: white; border: none; border-radius: 4px; font-size: 15px; cursor: pointer; }
        .btn:hover { background: #1d4ed8; }
        .msg   { background: #dcfce7; color: #166534; padding: 10px 14px; border-radius: 4px; margin-bottom: 18px; font-size: 14px; }
        .error { background: #fee2e2; color: #991b1b; padding: 10px 14px; border-radius: 4px; margin-bottom: 18px; font-size: 14px; }
        .cv-current { display: flex; align-items: center; gap: 10px; margin-top: 8px; padding: 8px 12px; background: #f8fafc; border: 1px solid #ddd; border-radius: 4px; font-size: 13px; color: #374151; }
        .cv-current span { flex: 1; }
        .btn-cv-action { display: inline-flex; align-items: center; justify-content: center; padding: 4px 12px; font-size: 12px; border-radius: 4px; border: 1px solid #ddd; cursor: pointer; background: #f1f5f9; color: #374151; box-sizing: border-box; margin: 0; white-space: nowrap; }
        label.btn-cv-action { display: inline-flex; font-size: 12px; font-weight: normal; margin-bottom: 0; }
        .btn-cv-action:hover { background: #e2e8f0; }
        .btn-cv-remove { border-color: #fca5a5; background: #fff; color: #dc2626; }
        .btn-cv-remove:hover { background: #fee2e2; }
        label.file-label { display: inline-block; padding: 8px 16px; background: #f1f5f9; border: 1px solid #ddd; border-radius: 4px; font-size: 13px; cursor: pointer; color: #374151; }
        label.file-label:hover { background: #e2e8f0; }
        .file-name { margin-left: 10px; font-size: 13px; color: #64748b; }
    </style>
</head>
<body>
    <div class="nav">
        <a href="${pageContext.request.contextPath}/ta/home">Home</a>
        <a href="${pageContext.request.contextPath}/ta/jobs">Job List</a>
        <a href="${pageContext.request.contextPath}/ta/status">My Applications</a>
        <a href="${pageContext.request.contextPath}/ta/profile">Profile</a>
        <form method="post" action="${pageContext.request.contextPath}/ta/logout">
            <button type="submit">Logout</button>
        </form>
    </div>

    <h2>My Profile</h2>
    <p style="color:#64748b;font-size:14px;margin-bottom:20px;">Your profile information will be pre-filled when you apply for jobs.</p>

    <% if (request.getAttribute("msg") != null) { %>
        <div class="msg">${msg}</div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
        <div class="error">${error}</div>
    <% } %>

    <%
        TAProfile profile = (TAProfile) request.getAttribute("profile");
        String pName   = profile != null ? profile.getName()   : "";
        String pEmail  = profile != null ? profile.getEmail()  : "";
        String pSkills = profile != null ? profile.getSkills() : "";
        String pMajor  = profile != null ? profile.getMajor()  : "";
        String pCvPath = profile != null ? profile.getCvPath() : "";
        String pCvName = (pCvPath != null && !pCvPath.isEmpty())
                ? pCvPath.substring(pCvPath.lastIndexOf('/') + 1) : "";
    %>

    <form method="post" action="${pageContext.request.contextPath}/ta/profile" enctype="multipart/form-data">
        <div class="field">
            <label>TA ID</label>
            <input type="text" value="<%= session.getAttribute("taId") %>" disabled>
        </div>
        <div class="field">
            <label>Name</label>
            <input type="text" name="name" value="<%= pName %>" required placeholder="Full name">
        </div>
        <div class="field">
            <label>Email</label>
            <input type="email" name="email" value="<%= pEmail %>" required placeholder="your@email.com">
        </div>
        <div class="field">
            <label>Skills</label>
            <input type="text" name="skills" value="<%= pSkills %>" placeholder="e.g. Java, Python, SQL">
            <small>Separate multiple skills with commas</small>
        </div>
        <div class="field">
            <label>Major</label>
            <input type="text" name="major" value="<%= pMajor %>" placeholder="e.g. Computer Science">
        </div>
        <script>
        var _cvHadOriginal = <%= (pCvName != null && !pCvName.isEmpty()) ? "true" : "false" %>;
        var _cvRemoving = false;
        function clearCvFile() {
            document.getElementById('cvFile').value = '';
            document.getElementById('cvFileName').textContent = 'No file chosen';
            document.getElementById('cvChooseBtn').textContent = 'Choose File';
            document.getElementById('cvClearBtn').style.display = 'none';
            if (_cvHadOriginal && !_cvRemoving) {
                document.getElementById('cvCurrentRow').style.display = 'flex';
                document.getElementById('cvUploadRow').style.display = 'none';
            } else if (_cvRemoving) {
                document.getElementById('removeCvInput').value = 'true';
            }
        }
        </script>
        <div class="field">
            <label>CV</label>
            <input type="hidden" name="removeCv" id="removeCvInput" value="false">
            <% if (pCvName != null && !pCvName.isEmpty()) { %>
            <div class="cv-current" id="cvCurrentRow">
                <span>&#128196; <%= pCvName %></span>
                <label class="btn-cv-action" for="cvFile" id="replaceBtn">Replace</label>
                <button type="button" class="btn-cv-action btn-cv-remove" onclick="
                    _cvRemoving = true;
                    document.getElementById('removeCvInput').value='true';
                    document.getElementById('cvCurrentRow').style.display='none';
                    document.getElementById('cvUploadRow').style.display='flex';
                    document.getElementById('cvFileName').textContent='No file chosen';
                    document.getElementById('cvClearBtn').style.display='none';
                ">Remove</button>
            </div>
            <div id="cvUploadRow" style="display:none;align-items:center;margin-top:6px;">
            <% } else { %>
            <div id="cvUploadRow" style="display:flex;align-items:center;">
            <% } %>
                <label class="file-label" for="cvFile" id="cvChooseBtn">Choose File</label>
                <span class="file-name" id="cvFileName">No file chosen</span>
                <button type="button" id="cvClearBtn" class="btn-cv-action"
                        style="display:none;margin-left:6px;" onclick="clearCvFile()">✕</button>
            </div>
            <input type="file" id="cvFile" name="cv" accept=".pdf,.doc,.docx" style="display:none"
                   onchange="if(this.files&&this.files[0]){
                       document.getElementById('cvFileName').textContent=this.files[0].name;
                       document.getElementById('cvChooseBtn').textContent='Change File';
                       document.getElementById('removeCvInput').value='false';
                       document.getElementById('cvClearBtn').style.display='inline-block';
                       document.getElementById('cvUploadRow').style.display='flex';
                       if(_cvHadOriginal&&!_cvRemoving){
                           document.getElementById('cvCurrentRow').style.display='none';
                       }
                   }">
        </div>
        <button type="submit" class="btn">Save Profile</button>
    </form>
</body>
</html>
