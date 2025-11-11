package com.finance.controller;

import com.finance.model.XMLHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.YearMonth;

@WebServlet(name = "ReportServlet", urlPatterns = {"/reports"})
public class ReportServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(false) == null || req.getSession(false).getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.getRequestDispatcher("/reports.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(false) == null || req.getSession(false).getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String format = req.getParameter("format");
        if (format == null) format = "csv";
        File xmlFile = new File(getServletContext().getRealPath("/data/finance.xml"));
        XMLHandler handler = new XMLHandler(xmlFile);
        handler.initializeIfMissing();
        YearMonth ym = YearMonth.now();
        if ("xml".equalsIgnoreCase(format)) {
            String content = handler.exportXML(ym);
            resp.setContentType("application/xml");
            resp.setHeader("Content-Disposition", "attachment; filename=\"report-" + ym + ".xml\"");
            resp.getOutputStream().write(content.getBytes(StandardCharsets.UTF_8));
        } else {
            String content = handler.exportCSV(ym);
            resp.setContentType("text/csv");
            resp.setHeader("Content-Disposition", "attachment; filename=\"report-" + ym + ".csv\"");
            resp.getOutputStream().write(content.getBytes(StandardCharsets.UTF_8));
        }
    }
}


