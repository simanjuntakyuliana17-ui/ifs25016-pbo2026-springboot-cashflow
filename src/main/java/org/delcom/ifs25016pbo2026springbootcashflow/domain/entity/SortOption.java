package org.delcom.ifs25016pbo2026springbootcashflow.domain.entity;

import java.util.Comparator;

/** Kriteria pengurutan transaksi; setiap opsi membawa comparator-nya sendiri. */
public enum SortOption {
    /** Jumlah terkecil lebih dulu. */
    AMOUNT_ASC(Comparator.comparing(Transaction::getAmount)),

    /** Jumlah terbesar lebih dulu. */
    AMOUNT_DESC(Comparator.comparing(Transaction::getAmount).reversed()),

    /** Pemasukan ditampilkan lebih dulu. */
    INCOME_FIRST(Comparator.comparing(Transaction::getType)),

    /** Pengeluaran ditampilkan lebih dulu. */
    EXPENSE_FIRST(Comparator.comparing(Transaction::getType).reversed());

    private final Comparator<Transaction> comparator;

    SortOption(Comparator<Transaction> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Transaction> comparator() {
        return comparator;
    }
}
