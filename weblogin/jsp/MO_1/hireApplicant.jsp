<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String currentMoId = (String) session.getAttribute("userId");
    if (!"MO".equals(session.getAttribute("userType")) || currentMoId == null) {
        response.sendRedirect(request.getContextPath() + "/login");
    } else {
        response.sendRedirect(request.getContextPath() + "/jsp/MO_1/moApplicantList.jsp");
    }
%>
