<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Login</title>
</head>
<body>

	<h2>Login</h2>
	<form action="LoginUser" method="post">
		<label for="username">UserName:</label> <input type="text"
			id="username" name="username" required><br>
		<br> <label for="password">Password:</label> <input
			type="password" id="password" name="password" required><br>
		<br> <input type="submit" value="Login">
		<button>
			<a href="Registration.jsp">Register</a>
		</button>

	</form>
</body>
</html>