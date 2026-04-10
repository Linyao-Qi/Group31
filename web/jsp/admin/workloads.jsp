<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="Admin.AdminWorkload" %>
<%
    List<AdminWorkload> workloads = (List<AdminWorkload>) request.getAttribute("workloads");
    Set<String> moduleCodes = (Set<String>) request.getAttribute("moduleCodes");
    Set<String> statuses = (Set<String>) request.getAttribute("statuses");
    Set<String> moIds = (Set<String>) request.getAttribute("moIds");
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
        body { font-family: Arial, sans-serif; margin: 24px; }
        table { border-collapse: collapse; width: 100%; font-size: 13px; }
        th, td { border: 1px solid #ccc; padding: 6px 8px; text-align: left; }
        th { background: #f5f5f5; }
        .top { display: flex; gap: 24px; margin-bottom: 12px; }
        .warn { color: #b00020; }
        .controls { margin: 14px 0; }
        .controls form, .controls a { display: inline-block; margin-right: 8px; }
        .msg { padding: 8px 10px; margin: 10px 0; background: #fff4d6; border: 1px solid #f0d58b; }
    </style>
</head>
<body>
<h2>CheckWorkload</h2>
<div class="top">
    <div>Total Active TAs: <strong><%= request.getAttribute("totalActiveTAs") %></strong></div>
    <div>Total Assigned Modules: <strong><%= request.getAttribute("totalAssignedModules") %></strong></div>
    <div class="warn">Overloaded TAs: <strong><%= request.getAttribute("overloadedCount") %></strong></div>
</div>

<% if (message != null && !message.isEmpty()) { %>
<div class="msg"><%= message %></div>
<% } %>

<form method="get" action="${pageContext.request.contextPath}/admin/workloads">
    Module Code:
    <select name="moduleCode">
        <option value="">All</option>
        <% for (String value : moduleCodes) { %>
        <option value="<%= value %>" <%= value.equals(selectedModuleCode) ? "selected" : "" %>><%= value %></option>
        <% } %>
    </select>
    Status:
    <select name="status">
        <option value="">All</option>
        <% for (String value : statuses) { %>
        <option value="<%= value %>" <%= value.equals(selectedStatus) ? "selected" : "" %>><%= value %></option>
        <% } %>
    </select>
    MO ID:
    <select name="moId">
        <option value="">All</option>
        <% for (String value : moIds) { %>
        <option value="<%= value %>" <%= value.equals(selectedMoId) ? "selected" : "" %>><%= value %></option>
        <% } %>
    </select>
    <button type="submit">Apply</button>
</form>

<div class="controls">
    <a href="${pageContext.request.contextPath}/admin/workloads">Reset Filters</a>
    <a href="${pageContext.request.contextPath}/admin/workloads?refresh=1">Refresh</a>
    <form method="post" action="${pageContext.request.contextPath}/admin/workloads">
        <input type="hidden" name="action" value="save"/>
        <button type="submit">Save</button>
    </form>
    <a href="${pageContext.request.contextPath}/admin/home">Back</a>
    <% if (Boolean.TRUE.equals(hasUnsaved)) { %>
    <span class="warn">Unsaved changes</span>
    <% } %>
</div>

<table>
    <thead>
    <tr>
        <th>MO Name</th>
        <th>MO ID</th>
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
        <td><%= w.getMoId() %></td>
        <td><%= w.getTaId() %></td>
        <td><%= w.getTaName() %></td>
        <td><%= w.getModuleName() %></td>
        <td><%= w.getModuleCode() %></td>
        <td><%= w.getCourseWorkHour() %></td>
        <td><%= w.getTaTotalWorkHour() %></td>
        <td class="<%= "Overloaded".equalsIgnoreCase(w.getStatus()) ? "warn" : "" %>"><%= w.getStatus() %></td>
        <td>
            <form method="post" action="${pageContext.request.contextPath}/admin/workloads">
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
</body>
</html>

