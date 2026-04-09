<h2>Application Form</h2>

<form action="profile" method="post" enctype="multipart/form-data">

    Name: <input type="text" name="name" value="${profile.name}"><br><br>

    ID: <input type="text" name="id" value="${profile.id}"><br><br>

    Email: <input type="email" name="email" value="${profile.email}"><br><br>

    Skills: <input type="text" name="skills" value="${profile.skills}"><br><br>

    Major: <input type="text" name="major" value="${profile.major}"><br><br>

    Upload CV (PDF):
    <input type="file" name="cv"><br><br>

    <!-- 保留旧文件 -->
    <input type="hidden" name="existingCv" value="${profile.cvPath}">

    <input type="submit" value="Save">

</form>
