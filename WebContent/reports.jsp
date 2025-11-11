<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Reports - Personal Finance Tracker</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="container">
    <header class="topbar">
        <h1>Reports</h1>
        <nav>
            <a href="dashboard">Dashboard</a>
            <a href="transactions">Transactions</a>
            <a href="budget">Budget</a>
            <a href="reports">Reports</a>
        </nav>
    </header>
    <div class="card">
        <h2>Export Current Month</h2>
        <form method="post" action="reports" class="grid-2">
            <label>Format
                <select name="format">
                    <option value="csv">CSV</option>
                    <option value="xml">XML</option>
                </select>
            </label>
            <div class="actions">
                <button type="submit">Download</button>
            </div>
        </form>
    </div>
</div>
</body>
</html>


