<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login - Personal Finance Tracker</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="container">
    <h1>Personal Finance Tracker</h1>
    <div class="card">
        <h2>Login</h2>
        <form method="post" action="login">
            <label>Username</label>
            <input type="text" name="username" required>
            <label>Password</label>
            <input type="password" name="password" required>
            <button type="submit">Sign in</button>
        </form>
        <div class="error"><%= request.getAttribute("error") == null ? "" : request.getAttribute("error") %></div>
    </div>
    <p class="muted">Demo user: admin / admin</p>
</div>
</body>
</html>


