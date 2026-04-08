<%@ page import="model.Profile" %>
<%
    Profile profile = (Profile) request.getAttribute("profile");
%>

<h2>Profile</h2>

<form action="profile" method="post">
    Name: <input type="text" name="name"><br>
    ID: <input type="text" name="id"><br>
    Email: <input type="text" name="email"><br>
    Skills: <input type="text" name="skills"><br>
    Major: <input type="text" name="major"><br>
    CV Path: <input type="text" name="cv"><br>
    <button type="submit">Save</button>
</form>

<% if (profile != null) { %>
<h3>Saved Profile:</h3>
Name: <%= profile.getName() %><br>
Email: <%= profile.getEmail() %><br>
CV: <%= profile.getCvPath() %>
<% } %>