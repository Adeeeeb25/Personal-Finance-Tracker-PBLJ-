package com.finance.controller;

import com.finance.model.Transaction;
import com.finance.model.XMLHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "TransactionServlet", urlPatterns = {"/transactions"})
public class TransactionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(false) == null || req.getSession(false).getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        File xmlFile = new File(getServletContext().getRealPath("/data/finance.xml"));
        XMLHandler handler = new XMLHandler(xmlFile);
        handler.initializeIfMissing();

        String category = req.getParameter("category");
        String fromStr = req.getParameter("from");
        String toStr = req.getParameter("to");
        String q = req.getParameter("q");
        LocalDate from = fromStr == null || fromStr.isBlank() ? null : LocalDate.parse(fromStr);
        LocalDate to = toStr == null || toStr.isBlank() ? null : LocalDate.parse(toStr);

        List<Transaction> items = handler.listTransactions(category, from, to, q);
        req.setAttribute("transactions", items);
        req.getRequestDispatcher("/addTransaction.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(false) == null || req.getSession(false).getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String action = req.getParameter("action");
        File xmlFile = new File(getServletContext().getRealPath("/data/finance.xml"));
        XMLHandler handler = new XMLHandler(xmlFile);
        handler.initializeIfMissing();

        if ("create".equalsIgnoreCase(action)) {
            Transaction tx = readTransactionFromRequest(req);
            handler.addTransaction(tx);
        } else if ("update".equalsIgnoreCase(action)) {
            Transaction tx = readTransactionFromRequest(req);
            handler.updateTransaction(tx);
        } else if ("delete".equalsIgnoreCase(action)) {
            String id = req.getParameter("id");
            if (id != null && !id.isBlank()) {
                handler.deleteTransaction(id);
            }
        }
        resp.sendRedirect(req.getContextPath() + "/transactions");
    }

    private Transaction readTransactionFromRequest(HttpServletRequest req) {
        String id = req.getParameter("id");
        String type = req.getParameter("type");
        String category = req.getParameter("category");
        BigDecimal amount = new BigDecimal(req.getParameter("amount"));
        LocalDate date = LocalDate.parse(req.getParameter("date"));
        String note = req.getParameter("note");
        return new Transaction(id, type, category, amount, date, note);
    }
}
