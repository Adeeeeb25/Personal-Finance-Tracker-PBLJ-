<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dashboard - Personal Finance Tracker</title>
    <link rel="stylesheet" href="css/style.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="js/charts.js"></script>
</head>
<body>
<div class="container">
    <header class="topbar">
        <h1>Dashboard</h1>
        <nav>
            <a href="dashboard">Dashboard</a>
            <a href="transactions">Transactions</a>
            <a href="budget">Budget</a>
            <a href="reports">Reports</a>
        </nav>
    </header>

    <div class="grid">
        <div class="card">
            <h2>Summary - <%= request.getAttribute("month") %></h2>
            <div class="stats">
                <div class="stat"><span>Income</span><strong>$<%= request.getAttribute("income") %></strong></div>
                <div class="stat"><span>Expenses</span><strong>$<%= request.getAttribute("expense") %></strong></div>
                <div class="stat"><span>Savings</span><strong>$<%= request.getAttribute("savings") %></strong></div>
            </div>
            <%
                Boolean alert = (Boolean) request.getAttribute("budgetAlert");
                if (alert != null && alert) {
            %>
            <div class="alert">Alert: Expenses have reached 90% of budget ($<%= request.getAttribute("budgetLimit") %>).</div>
            <% } %>
        </div>
        <div class="card">
            <h2>Spending by Category</h2>
            <canvas id="pieChart"></canvas>
        </div>
        <div class="card">
            <h2>Income vs Expense</h2>
            <canvas id="barChart"></canvas>
        </div>
    </div>
</div>
<script>
    renderPieChart([<%= request.getAttribute("catLabels") %>], [<%= request.getAttribute("catValues") %>]);
    renderBarChart(["Income","Expense"], [<%= request.getAttribute("income") %>, <%= request.getAttribute("expense") %>]);
</script>
</body>
</html>


