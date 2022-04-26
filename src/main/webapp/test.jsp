<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Test the model</title>
</head>
<body>

<h3>Test File Upload:</h3>
      Select a file to run test: <br />
      <form action = "test" method = "post" enctype = "multipart/form-data" >
         <input type = "file" name = "file" />
         <br />
         <input type = "submit" />
      </form>

</body>
</html>