package org.delcom.ifs25016pbo2026springbootcashflow.usecase;

import org.delcom.ifs25016pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25016pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25016pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25016pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25016pbo2026springbootcashflow.domain.repository.ITransactionRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Proses bisnis pencatatan keuangan. Tidak ada I/O UI; kegagalan dilaporkan
 * lewat checked domain exception.
 */
public class FinanceUseCase {
    private final ITransactionRepository repository;

    public FinanceUseCase(ITransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    public Transaction addIncome(String description, String amountText) throws ValidationException {
        String desc = validateDescription(description);
        return repository.saveIncome(desc, parseAmount(amountText));
    }

    public Transaction addExpense(String description, String amountText) throws ValidationException {
        String desc = validateDescription(description);
        return repository.saveExpense(desc, parseAmount(amountText));
    }

    public void removeTransaction(int id) throws EntityNotFoundException {
        if (!repository.deleteById(id)) {
            throw new EntityNotFoundException(id);
        }
    }

    public List<Transaction> searchTransactions(String keyword) {
        String key = keyword == null ? "" : keyword.trim().toLowerCase();
        return repository.findBy(t -> t.getDescription().toLowerCase().contains(key));
    }

    public List<Transaction> sortTransactions(SortOption option) {
        return repository.findAll().stream().sorted(option.comparator()).toList();
    }

    public BigDecimal getTotalIncome() {
        return sum(true);
    }

    public BigDecimal getTotalExpense() {
        return sum(false);
    }

    /** Saldo = total pemasukan - total pengeluaran. */
    public BigDecimal getBalance() {
        return repository.findAll().stream()
                .map(Transaction::signedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sum(boolean income) {
        return repository.findAll().stream()
                .filter(t -> t.isIncome() == income)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String validateDescription(String description) throws ValidationException {
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Keterangan tidak boleh kosong");
        }
        return description.trim();
    }

    private BigDecimal parseAmount(String text) throws ValidationException {
        if (text == null || text.trim().isEmpty()) {
            throw new ValidationException("Jumlah tidak boleh kosong");
        }
        BigDecimal amount;
        try {
            amount = new BigDecimal(text.trim());
        } catch (NumberFormatException ex) {
            throw new ValidationException("Jumlah harus berupa angka");
        }
        if (amount.signum() <= 0) {
            throw new ValidationException("Jumlah harus lebih dari 0");
        }
        return amount;
    }
}
