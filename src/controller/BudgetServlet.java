package com.finance.controller;

import com.finance.model.Budget;
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

@WebServlet(name = "BudgetServlet", urlPatterns = {"/budget"})
public class BudgetServlet extends HttpServlet {
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
        Budget b = handler.getBudget(ym);
        req.setAttribute("yearMonth", ym.toString());
        req.setAttribute("limit", b == null ? "" : b.getMonthlyLimit().toPlainString());
        req.getRequestDispatcher("/budget.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(false) == null || req.getSession(false).getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        File xmlFile = new File(getServletContext().getRealPath("/data/finance.xml"));
        XMLHandler handler = new XMLHandler(xmlFile);
        handler.initializeIfMissing();
        String limit = req.getParameter("limit");
        YearMonth ym = YearMonth.now();
        handler.setBudget(new Budget(ym.toString(), new BigDecimal(limit)));
        resp.sendRedirect(req.getContextPath() + "/dashboard");
    }
}


