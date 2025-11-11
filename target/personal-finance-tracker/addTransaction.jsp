<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.finance.model.Transaction" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Transactions - Personal Finance Tracker</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="container">
    <header class="topbar">
        <h1>Transactions</h1>
        <nav>
            <a href="dashboard">Dashboard</a>
            <a href="transactions">Transactions</a>
            <a href="budget">Budget</a>
            <a href="reports">Reports</a>
        </nav>
    </header>

    <div class="card">
        <h2>Add / Edit Transaction</h2>
        <form method="post" action="transactions" class="grid-2">
            <input type="hidden" name="id" id="id">
            <label>Type
                <select name="type" required>
                    <option value="Expense">Expense</option>
                    <option value="Income">Income</option>
                </select>
            </label>
            <label>Category
                <select name="category" required>
                    <option>Food</option>
                    <option>Travel</option>
                    <option>Bills</option>
                    <option>Shopping</option>
                    <option>Salary</option>
                    <option>Other</option>
                </select>
            </label>
            <label>Amount
                <input type="number" step="0.01" name="amount" required>
            </label>
            <label>Date
                <input type="date" name="date" required>
            </label>
            <label>Note
                <input type="text" name="note">
            </label>
            <div class="actions">
                <button type="submit" name="action" value="create">Save</button>
                <button type="submit" name="action" value="update">Update</button>
            </div>
        </form>
    </div>

    <div class="card">
        <h2>Search & Filter</h2>
        <form method="get" action="transactions" class="grid-3">
            <label>Category
                <select name="category">
                    <option value="">All</option>
                    <option>Food</option>
                    <option>Travel</option>
                    <option>Bills</option>
                    <option>Shopping</option>
                    <option>Salary</option>
                    <option>Other</option>
                </select>
            </label>
            <label>From
                <input type="date" name="from">
            </label>
            <label>To
                <input type="date" name="to">
            </label>
            <label>Keyword
                <input type="text" name="q" placeholder="category or note">
            </label>
            <div class="actions">
                <button type="submit">Filter</button>
            </div>
        </form>
    </div>

    <div class="card">
        <h2>All Transactions</h2>
        <table>
            <thead>
            <tr>
                <th>Type</th>
                <th>Category</th>
                <th>Amount</th>
                <th>Date</th>
                <th>Note</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <%
                List<Transaction> list = (List<Transaction>) request.getAttribute("transactions");
                if (list != null) {
                    for (Transaction t : list) {
            %>
            <tr>
                <td><%= t.getType() %></td>
                <td><%= t.getCategory() %></td>
                <td>$<%= t.getAmount().toPlainString() %></td>
                <td><%= t.getDate().toString() %></td>
                <td><%= t.getNote() == null ? "" : t.getNote() %></td>
                <td>
                    <form method="post" action="transactions" style="display:inline">
                        <input type="hidden" name="id" value="<%= t.getId() %>">
                        <button type="submit" name="action" value="delete" class="danger">Delete</button>
                    </form>
                </td>
            </tr>
            <%
                    }
                }
            %>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>


