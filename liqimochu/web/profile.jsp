<form action="profile" method="post" enctype="multipart/form-data">
    Name: <input type="text" name="name"><br>
    ID: <input type="text" name="id"><br>
    Email: <input type="email" name="email"><br>
    Skills: <input type="text" name="skills"><br>
    Major: <input type="text" name="major"><br>

    CV: <input type="file" name="cv"><br>

    <button type="submit">Save</button>
</form>

<hr>

<c:if test="${not empty profile}">
    <p>Name: ${profile.name}</p >
    <p>ID: ${profile.id}</p >
    <p>Email: ${profile.email}</p >
    <p>Skills: ${profile.skills}</p >
    <p>Major: ${profile.major}</p >
    <p>CV Path: ${profile.cvPath}</p >
</c:if>