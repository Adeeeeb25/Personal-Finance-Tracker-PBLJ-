package com.finance.controller;

import com.finance.model.XMLHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(false) == null || req.getSession(false).getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        File xmlFile = new File(getServletContext().getRealPath("/data/finance.xml"));
        XMLHandler handler = new XMLHandler(xmlFile);
        handler.initializeIfMissing();
        YearMonth ym = YearMonth.now();
        Map<String, Object> summary = handler.getMonthlySummary(ym);
        BigDecimal income = (BigDecimal) summary.get("income");
        BigDecimal expense = (BigDecimal) summary.get("expense");
        BigDecimal savings = (BigDecimal) summary.get("savings");
        @SuppressWarnings("unchecked")
        Map<String, BigDecimal> byCategory = (Map<String, BigDecimal>) summary.getOrDefault("byCategory", new LinkedHashMap<>());

        // Budget and alert threshold detection
        var budget = handler.getBudget(ym);
        boolean budgetAlert = false;
        BigDecimal budgetLimit = null;
        if (budget != null) {
            budgetLimit = budget.getMonthlyLimit();
            if (budgetLimit.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal threshold = budgetLimit.multiply(new BigDecimal("0.90"));
                budgetAlert = expense.compareTo(threshold) >= 0;
            }
        }

        // Prepare chart inputs as comma-separated lists
        String labels = byCategory.keySet().stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(","));
        String values = byCategory.values().stream().map(BigDecimal::toPlainString).collect(Collectors.joining(","));

        req.setAttribute("month", ym.toString());
        req.setAttribute("income", income.toPlainString());
        req.setAttribute("expense", expense.toPlainString());
        req.setAttribute("savings", savings.toPlainString());
        req.setAttribute("catLabels", labels);
        req.setAttribute("catValues", values);
        req.setAttribute("budgetLimit", budgetLimit == null ? "" : budgetLimit.toPlainString());
        req.setAttribute("budgetAlert", budgetAlert);
        req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
    }
}
