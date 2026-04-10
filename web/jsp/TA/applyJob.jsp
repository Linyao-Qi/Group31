<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.Job, TA.TAProfile" %>
<html>
<head>
    <title>Apply for Job</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 40px auto; padding: 0 20px; }
        .nav { margin-bottom: 30px; text-align: center; }
        .nav a { margin: 0 12px; color: #2563eb; text-decoration: none; font-size: 15px; }
        .nav a:hover { text-decoration: underline; }
        .nav form { display: inline; }
        .nav button { background: none; border: none; color: #2563eb; font-size: 15px; cursor: pointer; padding: 0; margin: 0 12px; }
        .nav button:hover { text-decoration: underline; }
        h2 { color: #1e293b; }
        .job-info { background: #f8fafc; border: 1px solid #ddd; border-radius: 6px; padding: 18px 22px; margin-bottom: 28px; }
        .job-info h3 { margin-top: 0; color: #2563eb; }
        .job-info table { width: 100%; border-collapse: collapse; }
        .job-info td { padding: 5px 10px; font-size: 14px; color: #374151; }
        .job-info td:first-child { font-weight: bold; width: 160px; color: #1e293b; }
        .field { margin-bottom: 16px; }
        .field label { display: block; font-size: 14px; color: #374151; margin-bottom: 6px; font-weight: bold; }
        label.file-label { display: inline-block; padding: 8px 16px; background: #f1f5f9; border: 1px solid #ddd; border-radius: 4px; font-size: 13px; cursor: pointer; color: #374151; }
        label.file-label:hover { background: #e2e8f0; }
        .file-name { margin-left: 10px; font-size: 13px; color: #64748b; }
        .field input[type=text], .field input[type=email], .field textarea {
            width: 100%; padding: 9px 12px; border: 1px solid #ddd;
            border-radius: 4px; font-size: 14px; box-sizing: border-box;
        }
        .field textarea { height: 90px; resize: vertical; }
        .field input:focus, .field textarea:focus { outline: none; border-color: #2563eb; }
        .field small { color: #64748b; font-size: 12px; }
        .btn { padding: 10px 28px; background: #2563eb; color: white; border: none; border-radius: 4px; font-size: 15px; cursor: pointer; }
        .btn:hover { background: #1d4ed8; }
        .btn-back { padding: 10px 20px; background: #f1f5f9; color: #374151; border: 1px solid #ddd; border-radius: 4px; font-size: 15px; cursor: pointer; text-decoration: none; margin-right: 12px; }
        .btn-cv-action { padding: 4px 10px; font-size: 12px; border-radius: 4px; border: 1px solid #ddd; cursor: pointer; background: #f1f5f9; color: #374151; }
        .btn-cv-action:hover { background: #e2e8f0; }
        .msg   { background: #dcfce7; color: #166534; padding: 10px 14px; border-radius: 4px; margin-bottom: 18px; font-size: 14px; }
        .error { background: #fee2e2; color: #991b1b; padding: 10px 14px; border-radius: 4px; margin-bottom: 18px; font-size: 14px; }
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
    <!-- Job Summary -->
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

    <!-- Application Form -->
    <% if (request.getAttribute("msg") == null) { %>
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
    <% } %>
    <% } else { %>
        <p>Job not found. <a href="${pageContext.request.contextPath}/ta/jobs">Back to Job List</a></p>
    <% } %>
</body>
</html>
