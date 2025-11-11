package com.finance.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Budget implements Serializable {
    private static final long serialVersionUID = 1L;

    private String yearMonth; // e.g., 2025-11
    private BigDecimal monthlyLimit;

    public Budget() {
    }

    public Budget(String yearMonth, BigDecimal monthlyLimit) {
        this.yearMonth = yearMonth;
        this.monthlyLimit = monthlyLimit;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public BigDecimal getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(BigDecimal monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }
}
