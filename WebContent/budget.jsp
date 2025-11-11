<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Budget - Personal Finance Tracker</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="container">
    <header class="topbar">
        <h1>Budget</h1>
        <nav>
            <a href="dashboard">Dashboard</a>
            <a href="transactions">Transactions</a>
            <a href="budget">Budget</a>
            <a href="reports">Reports</a>
        </nav>
    </header>
    <div class="card">
        <h2>Monthly Budget</h2>
        <form method="post" action="budget" class="grid-2">
            <label>Month
                <input type="text" value="${yearMonth}" disabled>
            </label>
            <label>Limit ($)
                <input type="number" step="0.01" name="limit" value="${limit}" required>
            </label>
            <div class="actions">
                <button type="submit">Save</button>
            </div>
        </form>
    </div>
</div>
</body>
</html>


