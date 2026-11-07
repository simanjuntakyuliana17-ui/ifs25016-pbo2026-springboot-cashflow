package org.delcom.ifs25016pbo2026springbootcashflow.adapter.presenter;

import org.delcom.ifs25016pbo2026springbootcashflow.domain.entity.Transaction;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Memformat data transaksi menjadi teks siap tampil. */
public class FinancePresenter {
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** 1000000 -> "Rp 1,000,000.00". */
    public String formatMoney(BigDecimal amount) {
        DecimalFormat df = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));
        return "Rp " + df.format(amount);
    }

    public String formatType(Transaction t) {
        return t.getType().label();
    }

    public String formatCreatedAt(Transaction t) {
        return t.getCreatedAt().format(DATE_TIME);
    }

    /** [id] keterangan | Rp 1,000,000.00 | [Pemasukan] */
    public String format(Transaction t) {
        return String.format("[%d] %s | %s | [%s]",
                t.getId(), t.getDescription(), formatMoney(t.getAmount()), formatType(t));
    }

    public String formatBalance(BigDecimal balance) {
        return "Saldo: " + formatMoney(balance);
    }

    public String showAddSuccess(Transaction t) {
        return "Berhasil menambah transaksi: " + format(t);
    }

    public String showRemoveSuccess() {
        return "Berhasil menghapus transaksi.";
    }

    public String showRemoveFailed(int id) {
        return String.format("[!] Gagal menghapus transaksi dengan ID: %d.", id);
    }

    public String showEmpty() {
        return "- Data transaksi belum tersedia!";
    }

    public String showNotFound() {
        return "- Transaksi tidak ditemukan!";
    }

    public String showValidationError(String message) {
        return "[!] " + message;
    }
}
