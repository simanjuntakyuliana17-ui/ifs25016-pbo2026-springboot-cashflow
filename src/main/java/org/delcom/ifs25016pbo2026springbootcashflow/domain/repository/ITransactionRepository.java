package org.delcom.ifs25016pbo2026springbootcashflow.domain.repository;

import org.delcom.ifs25016pbo2026springbootcashflow.domain.entity.Transaction;

import java.math.BigDecimal;

/** Port spesifik transaksi; ID dihasilkan otomatis oleh implementasi. */
public interface ITransactionRepository extends IRepository<Transaction, Integer> {
    Transaction saveIncome(String description, BigDecimal amount);

    Transaction saveExpense(String description, BigDecimal amount);
}
