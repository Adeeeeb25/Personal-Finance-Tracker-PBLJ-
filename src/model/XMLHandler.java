package com.finance.model;

import com.finance.util.EncryptionUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class XMLHandler {
    private final File xmlFile;

    public XMLHandler(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    public synchronized void initializeIfMissing() {
        if (!xmlFile.getParentFile().exists()) {
            xmlFile.getParentFile().mkdirs();
        }
        if (!xmlFile.exists()) {
            try {
                Document doc = newDocument();
                Element root = doc.createElement("finance");
                doc.appendChild(root);
                Element users = doc.createElement("users");
                Element user = doc.createElement("user");
                user.setAttribute("username", "admin");
                user.setAttribute("password", "admin"); // demo only
                users.appendChild(user);
                root.appendChild(users);
                Element budgets = doc.createElement("budgets");
                root.appendChild(budgets);
                Element transactions = doc.createElement("transactions");
                root.appendChild(transactions);
                writeDocument(doc);
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize XML storage", e);
            }
        }
    }

    public boolean authenticate(String username, String passwordPlain) {
        try {
            Document doc = readDocument();
            NodeList list = doc.getElementsByTagName("user");
            for (int i = 0; i < list.getLength(); i++) {
                Element u = (Element) list.item(i);
                String uName = u.getAttribute("username");
                String pw = u.getAttribute("password");
                if (username.equals(uName) && passwordPlain.equals(pw)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed", e);
        }
    }

    public List<Transaction> listTransactions(String category, LocalDate from, LocalDate to, String keyword) {
        try {
            Document doc = readDocument();
            NodeList nodes = doc.getElementsByTagName("transaction");
            List<Transaction> results = new ArrayList<>();
            for (int i = 0; i < nodes.getLength(); i++) {
                Element t = (Element) nodes.item(i);
                Transaction tx = elementToTransaction(t);
                if (category != null && !category.isBlank() && !category.equalsIgnoreCase(tx.getCategory())) {
                    continue;
                }
                if (from != null && tx.getDate().isBefore(from)) {
                    continue;
                }
                if (to != null && tx.getDate().isAfter(to)) {
                    continue;
                }
                if (keyword != null && !keyword.isBlank()) {
                    String all = (tx.getCategory() + " " + tx.getNote()).toLowerCase();
                    if (!all.contains(keyword.toLowerCase())) {
                        continue;
                    }
                }
                results.add(tx);
            }
            results.sort((a, b) -> b.getDate().compareTo(a.getDate()));
            return results;
        } catch (Exception e) {
            throw new RuntimeException("Failed to list transactions", e);
        }
    }

    public Transaction addTransaction(Transaction tx) {
        try {
            Document doc = readDocument();
            Element root = (Element) doc.getElementsByTagName("transactions").item(0);
            if (tx.getId() == null || tx.getId().isBlank()) {
                tx.setId(UUID.randomUUID().toString());
            }
            Element t = doc.createElement("transaction");
            t.setAttribute("id", tx.getId());
            appendChildWithText(doc, t, "type", tx.getType());
            appendChildWithText(doc, t, "category", tx.getCategory());
            String amountString = tx.getAmount().toPlainString();
            if ("Income".equalsIgnoreCase(tx.getType())) {
                amountString = EncryptionUtil.encrypt(amountString);
                Element amountEl = doc.createElement("amount");
                amountEl.setAttribute("encrypted", "true");
                amountEl.setTextContent(amountString);
                t.appendChild(amountEl);
            } else {
                appendChildWithText(doc, t, "amount", amountString);
            }
            appendChildWithText(doc, t, "date", tx.getDate().toString());
            appendChildWithText(doc, t, "note", tx.getNote() == null ? "" : tx.getNote());
            root.appendChild(t);
            writeDocument(doc);
            return tx;
        } catch (Exception e) {
            throw new RuntimeException("Failed to add transaction", e);
        }
    }

    public boolean updateTransaction(Transaction tx) {
        try {
            Document doc = readDocument();
            Element found = findTransactionElement(doc, tx.getId());
            if (found == null) return false;
            setChildText(doc, found, "type", tx.getType());
            setChildText(doc, found, "category", tx.getCategory());
            // Replace amount element
            Element amountEl = getFirstChild(found, "amount");
            if (amountEl != null) {
                found.removeChild(amountEl);
            }
            String amountString = tx.getAmount().toPlainString();
            if ("Income".equalsIgnoreCase(tx.getType())) {
                amountString = EncryptionUtil.encrypt(amountString);
                Element newAmount = doc.createElement("amount");
                newAmount.setAttribute("encrypted", "true");
                newAmount.setTextContent(amountString);
                found.appendChild(newAmount);
            } else {
                appendChildWithText(doc, found, "amount", amountString);
            }
            setChildText(doc, found, "date", tx.getDate().toString());
            setChildText(doc, found, "note", tx.getNote() == null ? "" : tx.getNote());
            writeDocument(doc);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update transaction", e);
        }
    }

    public boolean deleteTransaction(String id) {
        try {
            Document doc = readDocument();
            Element t = findTransactionElement(doc, id);
            if (t == null) return false;
            t.getParentNode().removeChild(t);
            writeDocument(doc);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete transaction", e);
        }
    }

    public Budget getBudget(YearMonth ym) {
        try {
            Document doc = readDocument();
            NodeList list = doc.getElementsByTagName("budget");
            for (int i = 0; i < list.getLength(); i++) {
                Element b = (Element) list.item(i);
                String ymStr = b.getElementsByTagName("month").item(0).getTextContent();
                if (ym.toString().equals(ymStr)) {
                    String limitStr = b.getElementsByTagName("limit").item(0).getTextContent();
                    return new Budget(ymStr, new BigDecimal(limitStr));
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get budget", e);
        }
    }

    public void setBudget(Budget budget) {
        try {
            Document doc = readDocument();
            Element budgets = (Element) doc.getElementsByTagName("budgets").item(0);
            Element existing = null;
            NodeList list = doc.getElementsByTagName("budget");
            for (int i = 0; i < list.getLength(); i++) {
                Element b = (Element) list.item(i);
                String ymStr = b.getElementsByTagName("month").item(0).getTextContent();
                if (budget.getYearMonth().equals(ymStr)) {
                    existing = b;
                    break;
                }
            }
            if (existing == null) {
                existing = doc.createElement("budget");
                appendChildWithText(doc, existing, "month", budget.getYearMonth());
                appendChildWithText(doc, existing, "limit", budget.getMonthlyLimit().toPlainString());
                budgets.appendChild(existing);
            } else {
                setChildText(doc, existing, "limit", budget.getMonthlyLimit().toPlainString());
            }
            writeDocument(doc);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set budget", e);
        }
    }

    public Map<String, Object> getMonthlySummary(YearMonth ym) {
        List<Transaction> monthly = listTransactions(null, ym.atDay(1), ym.atEndOfMonth(), null);
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;
        Map<String, BigDecimal> byCategory = new LinkedHashMap<>();
        for (Transaction t : monthly) {
            BigDecimal amt = t.getAmount();
            if ("Income".equalsIgnoreCase(t.getType())) {
                income = income.add(amt);
            } else {
                expense = expense.add(amt);
                byCategory.put(t.getCategory(), byCategory.getOrDefault(t.getCategory(), BigDecimal.ZERO).add(amt));
            }
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("income", income);
        map.put("expense", expense);
        map.put("savings", income.subtract(expense));
        map.put("byCategory", byCategory);
        return map;
    }

    public String exportCSV(YearMonth ym) {
        List<Transaction> rows = listTransactions(null, ym.atDay(1), ym.atEndOfMonth(), null);
        String header = "id,type,category,amount,date,note";
        String body = rows.stream()
                .map(t -> String.join(",",
                        escape(t.getId()),
                        escape(t.getType()),
                        escape(t.getCategory()),
                        escape(t.getAmount().toPlainString()),
                        escape(t.getDate().toString()),
                        escape(t.getNote() == null ? "" : t.getNote())))
                .collect(Collectors.joining("\n"));
        return header + "\n" + body + (body.isEmpty() ? "" : "\n");
    }

    public String exportXML(YearMonth ym) {
        try {
            Document doc = newDocument();
            Element root = doc.createElement("report");
            root.setAttribute("month", ym.toString());
            doc.appendChild(root);
            List<Transaction> rows = listTransactions(null, ym.atDay(1), ym.atEndOfMonth(), null);
            for (Transaction tx : rows) {
                Element t = doc.createElement("transaction");
                t.setAttribute("id", tx.getId());
                appendChildWithText(doc, t, "type", tx.getType());
                appendChildWithText(doc, t, "category", tx.getCategory());
                appendChildWithText(doc, t, "amount", tx.getAmount().toPlainString());
                appendChildWithText(doc, t, "date", tx.getDate().toString());
                appendChildWithText(doc, t, "note", tx.getNote() == null ? "" : tx.getNote());
                root.appendChild(t);
            }
            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter sw = new StringWriter();
            tf.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (Exception e) {
            throw new RuntimeException("XML export failed", e);
        }
    }

    private Transaction elementToTransaction(Element t) {
        String id = t.getAttribute("id");
        String type = getChildText(t, "type");
        String category = getChildText(t, "category");
        Element amountEl = getFirstChild(t, "amount");
        String amtText = amountEl != null ? amountEl.getTextContent() : "0";
        if (amountEl != null && "true".equalsIgnoreCase(amountEl.getAttribute("encrypted"))) {
            amtText = EncryptionUtil.decrypt(amtText);
        }
        BigDecimal amount = new BigDecimal(amtText);
        LocalDate date = LocalDate.parse(getChildText(t, "date"));
        String note = getChildText(t, "note");
        return new Transaction(id, type, category, amount, date, note);
    }

    private Document readDocument() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        dbf.setNamespaceAware(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(xmlFile), StandardCharsets.UTF_8)) {
            return db.parse(new InputSource(isr));
        }
    }

    private Document newDocument() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.newDocument();
    }

    private void writeDocument(Document doc) throws Exception {
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        try (FileWriter fw = new FileWriter(xmlFile, false)) {
            tf.transform(new DOMSource(doc), new StreamResult(fw));
        }
    }

    private static void appendChildWithText(Document doc, Element parent, String tag, String text) {
        Element el = doc.createElement(tag);
        el.setTextContent(text);
        parent.appendChild(el);
    }

    private static void setChildText(Document doc, Element parent, String tag, String text) {
        Element el = getFirstChild(parent, tag);
        if (el == null) {
            appendChildWithText(doc, parent, tag, text);
        } else {
            el.setTextContent(text);
        }
    }

    private static Element getFirstChild(Element parent, String tag) {
        NodeList nl = parent.getElementsByTagName(tag);
        if (nl.getLength() > 0) {
            Node n = nl.item(0);
            if (n instanceof Element) {
                return (Element) n;
            }
        }
        return null;
    }

    private static String getChildText(Element parent, String tag) {
        Element el = getFirstChild(parent, tag);
        return el == null ? "" : el.getTextContent();
    }

    private static Element findTransactionElement(Document doc, String id) {
        NodeList list = doc.getElementsByTagName("transaction");
        for (int i = 0; i < list.getLength(); i++) {
            Element t = (Element) list.item(i);
            if (id.equals(t.getAttribute("id"))) {
                return t;
            }
        }
        return null;
    }

    private static String escape(String s) {
        if (s == null) return "";
        String v = s.replace("\"", "\"\"");
        if (v.contains(",") || v.contains("\n") || v.contains("\"")) {
            return "\"" + v + "\"";
        }
        return v;
    }
}


