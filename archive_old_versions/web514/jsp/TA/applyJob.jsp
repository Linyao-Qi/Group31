<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.Job, TA.TAProfile" %>
<html>
<head>
    <title>Apply for Job</title>
    <style>
        :root {
            --ink: #1e293b;
            --muted: #475569;
            --brand: #2563eb;
            --brand-2: #1d4ed8;
            --line: #e2e8f0;
        }
        body { font-family: "Segoe UI", "Trebuchet MS", sans-serif; max-width: 860px; margin: 28px auto; padding: 0 20px 24px; color: var(--ink); background: #f8fafc; }
        .nav { margin-bottom: 18px; text-align: center; background: #ffffff; border: 1px solid var(--line); border-radius: 10px; padding: 12px 10px; }
        .nav a { margin: 0 12px; color: #1e40af; text-decoration: none; font-size: 15px; font-weight: 600; }
        .nav a:hover { color: #1d4ed8; text-decoration: underline; }
        .nav form { display: inline; }
        .nav button { background: none; border: none; color: #1e40af; font-size: 15px; cursor: pointer; padding: 0; margin: 0 12px; font-weight: 600; }
        .nav button:hover { color: #1d4ed8; text-decoration: underline; }
        h2 { color: #0f172a; margin: 0 0 18px; font-size: 26px; }
        .job-info { background: #ffffff; border: 1px solid var(--line); border-radius: 10px; padding: 20px 24px; margin-bottom: 24px; box-shadow: 0 4px 12px rgba(15,23,42,0.05); }
        .job-info h3 { margin: 0 0 14px; color: #1e40af; font-size: 16px; }
        .job-info table { width: 100%; border-collapse: collapse; }
        .job-info td { padding: 5px 10px; font-size: 14px; color: #374151; }
        .job-info td:first-child { font-weight: 700; width: 160px; color: var(--ink); }
        .form-card { background: #ffffff; border: 1px solid var(--line); border-radius: 10px; padding: 24px; box-shadow: 0 4px 12px rgba(15,23,42,0.05); }
        .field { margin-bottom: 16px; }
        .field label { display: block; font-size: 13px; color: var(--muted); margin-bottom: 6px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.4px; }
        label.file-label { display: inline-block; padding: 8px 16px; background: #f1f5f9; border: 1px solid #cbd5e1; border-radius: 8px; font-size: 13px; cursor: pointer; color: #374151; font-weight: normal; text-transform: none; letter-spacing: 0; }
        label.file-label:hover { background: #e2e8f0; }
        .file-name { margin-left: 10px; font-size: 13px; color: #64748b; }
        .field input[type=text], .field input[type=email], .field textarea {
            width: 100%; padding: 9px 12px; border: 1px solid #cbd5e1;
            border-radius: 8px; font-size: 14px; box-sizing: border-box;
        }
        .field textarea { height: 90px; resize: vertical; }
        .field input:focus, .field textarea:focus { outline: none; border-color: #2563eb; }
        .field small { color: #64748b; font-size: 12px; }
        .btn { padding: 10px 28px; background: var(--brand); color: white; border: none; border-radius: 8px; font-size: 15px; cursor: pointer; font-weight: 700; }
        .btn:hover { background: var(--brand-2); }
        .btn-back { padding: 10px 20px; background: #f1f5f9; color: #374151; border: 1px solid #cbd5e1; border-radius: 8px; font-size: 15px; cursor: pointer; text-decoration: none; margin-right: 12px; font-weight: 600; }
        .btn-cv-action { padding: 4px 10px; font-size: 12px; border-radius: 6px; border: 1px solid #cbd5e1; cursor: pointer; background: #f1f5f9; color: #374151; }
        .btn-cv-action:hover { background: #e2e8f0; }
        .msg   { background: #dcfce7; color: #166534; padding: 10px 14px; border-radius: 6px; margin-bottom: 18px; font-size: 14px; }
        .error { background: #fee2e2; color: #991b1b; padding: 10px 14px; border-radius: 6px; margin-bottom: 18px; font-size: 14px; }
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

    <h2>Apply for Job</h2>

    <% if (request.getAttribute("msg") != null) { %>
        <div class="msg">${msg}
            <a href="${pageContext.request.contextPath}/ta/status">View My Applications &rarr;</a>
        </div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
        <div class="error"><%= request.getAttribute("error") %></div>
    <% } %>

    <%
        Job job = (Job) request.getAttribute("job");
        TAProfile profile = (TAProfile) request.getAttribute("profile");
        String pName   = profile != null ? profile.getName()   : "";
        String pEmail  = profile != null ? profile.getEmail()  : "";
        String pSkills = profile != null ? profile.getSkills() : "";
        String pMajor  = profile != null ? profile.getMajor()  : "";
    %>

    <% if (job != null) { %>
    <div class="job-info">
        <h3>Job Details</h3>
        <table>
            <tr><td>Subject</td><td><%= job.getSubject() %></td></tr>
            <tr><td>Work Type</td><td><%= job.getWorkType() %></td></tr>
            <tr><td>Description</td><td><%= job.getDescription() %></td></tr>
            <tr><td>Required Skills</td><td><%= job.getSkillRequirement() %></td></tr>
            <tr><td>Hours/Week</td><td><%= job.getHoursPerWeek() %>h</td></tr>
            <tr><td>Compensation</td><td><%= job.getCompensation() %></td></tr>
        </table>
    </div>

    <% if (request.getAttribute("msg") == null) { %>
    <div class="form-card">
    <script>var cvSelected = false;</script>
    <form method="post" action="${pageContext.request.contextPath}/ta/apply" enctype="multipart/form-data"
          onsubmit="if(!cvSelected){alert('Please upload your CV before submitting.');return false;}">
        <input type="hidden" name="jobId" value="<%= job.getJobId() %>">
        <input type="hidden" name="moId"  value="<%= job.getMoId() %>">

        <div class="field">
            <label>Your Name</label>
            <input type="text" name="name" value="<%= pName %>" required placeholder="Full name">
        </div>
        <div class="field">
            <label>Email</label>
            <input type="email" name="email" value="<%= pEmail %>" required placeholder="your@email.com">
        </div>
        <div class="field">
            <label>Major</label>
            <input type="text" name="major" value="<%= pMajor %>" required placeholder="e.g. Computer Science">
        </div>
        <div class="field">
            <label>Skills</label>
            <input type="text" name="skills" value="<%= pSkills %>" required placeholder="e.g. Java, Python, SQL">
            <small>Separate multiple skills with commas</small>
        </div>
        <div class="field">
            <label>Self Introduction (optional)</label>
            <textarea name="intro" placeholder="Briefly describe your relevant experience and why you are suitable for this position..."></textarea>
        </div>
        <div class="field">
            <label>Upload CV</label>
            <label class="file-label" for="cvFile" id="cvBtn">Choose File</label>
            <span class="file-name" id="cvFileName">No file chosen</span>
            <button type="button" id="cvClearBtn" class="btn-cv-action"
                    style="display:none;margin-left:6px;" onclick="
                        document.getElementById('cvFile').value='';
                        document.getElementById('cvFileName').textContent='No file chosen';
                        document.getElementById('cvBtn').textContent='Choose File';
                        document.getElementById('cvClearBtn').style.display='none';
                        cvSelected=false;
                    ">✕</button>
            <input type="file" id="cvFile" name="cv" accept=".pdf,.doc,.docx" style="display:none"
                   onchange="if(this.files&&this.files[0]){cvSelected=true;document.getElementById('cvFileName').textContent=this.files[0].name;document.getElementById('cvBtn').textContent='Change File';document.getElementById('cvClearBtn').style.display='inline-block';}">
        </div>
        <a href="${pageContext.request.contextPath}/ta/jobs" class="btn-back">Back</a>
        <button type="submit" class="btn">Submit Application</button>
    </form>
    </div>
    <% } %>
    <% } else { %>
        <p>Job not found. <a href="${pageContext.request.contextPath}/ta/jobs">Back to Job List</a></p>
    <% } %>
</body>
</html>
