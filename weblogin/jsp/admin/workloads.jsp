<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Collections" %>
<%@ page import="Admin.AdminWorkload" %>
<%
    List<AdminWorkload> workloads = (List<AdminWorkload>) request.getAttribute("workloads");
    Set<String> moduleCodes = (Set<String>) request.getAttribute("moduleCodes");
    Set<String> statuses = (Set<String>) request.getAttribute("statuses");
    Set<String> moIds = (Set<String>) request.getAttribute("moIds");
    if (workloads == null) workloads = Collections.emptyList();
    if (moduleCodes == null) moduleCodes = Collections.emptySet();
    if (statuses == null) statuses = Collections.emptySet();
    if (moIds == null) moIds = Collections.emptySet();
    String selectedModuleCode = request.getAttribute("selectedModuleCode") == null ? "" : String.valueOf(request.getAttribute("selectedModuleCode"));
    String selectedStatus = request.getAttribute("selectedStatus") == null ? "" : String.valueOf(request.getAttribute("selectedStatus"));
    String selectedMoId = request.getAttribute("selectedMoId") == null ? "" : String.valueOf(request.getAttribute("selectedMoId"));
    String message = (String) request.getAttribute("message");
    Boolean hasUnsaved = (Boolean) request.getAttribute("hasUnsavedChanges");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Workloads</title>
    <style>
        * { box-sizing: border-box; }
        body { margin: 0; background: #f3f6fb; color: #1f2937; font-family: Arial, Helvetica, sans-serif; font-size: 14px; }
        .page { width: min(1180px, calc(100% - 48px)); margin: 0 auto; padding: 32px 0 40px; }
        .nav { display: flex; align-items: center; justify-content: space-between; gap: 16px; padding: 12px 14px; margin-bottom: 18px; background: #fff; border: 1px solid #e5e7eb; border-radius: 14px; box-shadow: 0 8px 22px rgba(15, 23, 42, 0.06); }
        .brand { font-weight: 800; color: #111827; white-space: nowrap; }
        .nav-links { display: flex; align-items: center; justify-content: flex-end; flex-wrap: wrap; gap: 8px; }
        .nav a, .nav button { display: inline-flex; align-items: center; justify-content: center; min-height: 34px; padding: 8px 11px; border-radius: 9px; border: 1px solid transparent; background: transparent; color: #374151; font: inherit; text-decoration: none; cursor: pointer; }
        .nav a:hover, .nav button:hover { background: #eff6ff; color: #1d4ed8; }
        .nav .active { background: #2563eb; color: #fff; }
        .nav .active:hover { background: #1d4ed8; color: #fff; }
        .header { margin-bottom: 18px; }
        h2 { margin: 0; font-size: 28px; color: #111827; }
        .subtitle { margin: 7px 0 0; color: #6b7280; }
        .card { background: #fff; border: 1px solid #e5e7eb; border-radius: 14px; box-shadow: 0 8px 22px rgba(15, 23, 42, 0.06); padding: 22px; margin-bottom: 18px; }
        .stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(190px, 1fr)); gap: 12px; margin-bottom: 18px; }
        .stat { border: 1px solid #e5e7eb; border-radius: 12px; background: #f8fbff; padding: 14px; }
        .stat-label { color: #6b7280; font-size: 13px; margin-bottom: 8px; }
        .stat-value { color: #111827; font-size: 24px; line-height: 1; font-weight: 800; }
        .danger .stat-value { color: #b91c1c; }
        .filters { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 14px; align-items: end; }
        label { display: block; margin-bottom: 6px; color: #374151; font-weight: 700; }
        select { width: 100%; height: 38px; border: 1px solid #d1d5db; border-radius: 9px; padding: 8px 10px; background: #fff; font: inherit; }
        select:focus { outline: none; border-color: #2563eb; box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.13); }
        .controls { display: flex; align-items: center; flex-wrap: wrap; gap: 10px; margin-top: 14px; }
        .inline-form { display: inline-flex; margin: 0; }
        .btn, button {
            display: inline-flex; align-items: center; justify-content: center; min-height: 38px; padding: 9px 14px;
            border-radius: 9px; border: 1px solid #d1d5db; background: #fff; color: #374151;
            font: inherit; line-height: 1; text-decoration: none; cursor: pointer;
        }
        .btn:hover, button:hover { background: #f9fafb; border-color: #9ca3af; }
        .primary { background: #2563eb; border-color: #2563eb; color: #fff; }
        .primary:hover { background: #1d4ed8; border-color: #1d4ed8; }
        button:disabled { cursor: not-allowed; opacity: 0.55; }
        .message { padding: 11px 13px; margin-bottom: 14px; border-radius: 10px; background: #fffbeb; border: 1px solid #fde68a; color: #92400e; }
        .warn { color: #b91c1c; font-weight: 700; }
        .table-wrap { width: 100%; overflow-x: auto; }
        table { width: 100%; border-collapse: separate; border-spacing: 0; font-size: 13px; }
        th, td { border-bottom: 1px solid #e5e7eb; padding: 11px 12px; text-align: left; vertical-align: top; }
        th { background: #eef4ff; color: #374151; font-weight: 700; white-space: nowrap; }
        tbody tr:hover { background: #f9fbff; }
        tbody tr:last-child td { border-bottom: 0; }
        .pill { display: inline-flex; border-radius: 999px; padding: 4px 9px; font-size: 12px; font-weight: 700; white-space: nowrap; }
        .normal { background: #dcfce7; color: #166534; }
        .overloaded { background: #fee2e2; color: #991b1b; }
        @media (max-width: 720px) { .page { width: calc(100% - 28px); padding-top: 22px; } .nav { flex-direction: column; } }
    </style>
</head>
<body>
<div class="page">
    <div class="nav">
        <div class="brand">Admin System</div>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/admin/home">Home</a>
            <a class="active" href="${pageContext.request.contextPath}/admin/workloads">CheckWorkload</a>
            <a href="${pageContext.request.contextPath}/admin/posts">ClosePost</a>
            <a href="${pageContext.request.contextPath}/admin/report">Recruitment Report</a>
            <form class="inline-form" method="post" action="${pageContext.request.contextPath}/admin/logout">
                <button type="submit">Logout</button>
            </form>
        </div>
    </div>

    <div class="header">
        <h2>CheckWorkload</h2>
        <p class="subtitle">Monitor TA workload status and reassign overloaded records.</p>
    </div>

    <div class="stats">
        <div class="stat">
            <div class="stat-label">Total Active TAs</div>
            <div class="stat-value"><%= request.getAttribute("totalActiveTAs") %></div>
        </div>
        <div class="stat">
            <div class="stat-label">Total Assigned Modules</div>
            <div class="stat-value"><%= request.getAttribute("totalAssignedModules") %></div>
        </div>
        <div class="stat danger">
            <div class="stat-label">Overloaded TAs</div>
            <div class="stat-value"><%= request.getAttribute("overloadedCount") %></div>
        </div>
    </div>

    <% if (message != null && !message.isEmpty()) { %>
    <div class="message"><%= message %></div>
    <% } %>

    <div class="card">
        <form method="get" action="${pageContext.request.contextPath}/admin/workloads">
            <div class="filters">
                <div>
                    <label for="moduleCode">Module Code</label>
                    <select id="moduleCode" name="moduleCode">
                        <option value="">All</option>
                        <% for (String value : moduleCodes) { %>
                        <option value="<%= value %>" <%= value.equals(selectedModuleCode) ? "selected" : "" %>><%= value %></option>
                        <% } %>
                    </select>
                </div>
                <div>
                    <label for="status">Status</label>
                    <select id="status" name="status">
                        <option value="">All</option>
                        <% for (String value : statuses) { %>
                        <option value="<%= value %>" <%= value.equals(selectedStatus) ? "selected" : "" %>><%= value %></option>
                        <% } %>
                    </select>
                </div>
                <div>
                    <label for="moId">MO ID</label>
                    <select id="moId" name="moId">
                        <option value="">All</option>
                        <% for (String value : moIds) { %>
                        <option value="<%= value %>" <%= value.equals(selectedMoId) ? "selected" : "" %>><%= value %></option>
                        <% } %>
                    </select>
                </div>
                <div>
                    <button class="primary" type="submit">Apply</button>
                </div>
            </div>
        </form>

        <div class="controls">
            <a class="btn" href="${pageContext.request.contextPath}/admin/workloads">Reset Filters</a>
            <a class="btn" href="${pageContext.request.contextPath}/admin/workloads?refresh=1">Refresh</a>
            <form class="inline-form" method="post" action="${pageContext.request.contextPath}/admin/workloads">
                <input type="hidden" name="action" value="save"/>
                <button class="primary" type="submit">Save</button>
            </form>
            <a class="btn" href="${pageContext.request.contextPath}/admin/home">Back</a>
            <% if (Boolean.TRUE.equals(hasUnsaved)) { %>
            <span class="warn">Unsaved changes</span>
            <% } %>
        </div>
    </div>

    <div class="card">
        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th>MO Name</th>
                    <th>TA ID</th>
                    <th>TA Name</th>
                    <th>Module Name</th>
                    <th>Module Code</th>
                    <th>Course WorkHour</th>
                    <th>TA Total WorkHour</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <% for (AdminWorkload w : workloads) { %>
                <tr>
                    <td><%= w.getMoName() %></td>
                    <td><%= w.getTaId() %></td>
                    <td><%= w.getTaName() %></td>
                    <td><%= w.getModuleName() %></td>
                    <td><%= w.getModuleCode() %></td>
                    <td><%= w.getCourseWorkHour() %></td>
                    <td><%= w.getTaTotalWorkHour() %></td>
                    <td><span class="pill <%= "Overloaded".equalsIgnoreCase(w.getStatus()) ? "overloaded" : "normal" %>"><%= w.getStatus() %></span></td>
                    <td>
                        <form class="inline-form" method="post" action="${pageContext.request.contextPath}/admin/workloads">
                            <input type="hidden" name="action" value="reassign"/>
                            <input type="hidden" name="taId" value="<%= w.getTaId() %>"/>
                            <input type="hidden" name="moduleCode" value="<%= w.getModuleCode() %>"/>
                            <input type="hidden" name="moId" value="<%= w.getMoId() %>"/>
                            <input type="hidden" name="selectedModuleCode" value="<%= selectedModuleCode %>"/>
                            <input type="hidden" name="selectedStatus" value="<%= selectedStatus %>"/>
                            <input type="hidden" name="selectedMoId" value="<%= selectedMoId %>"/>
                            <button type="submit" <%= "Overloaded".equalsIgnoreCase(w.getStatus()) ? "" : "disabled" %>>Reassign</button>
                        </form>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>
