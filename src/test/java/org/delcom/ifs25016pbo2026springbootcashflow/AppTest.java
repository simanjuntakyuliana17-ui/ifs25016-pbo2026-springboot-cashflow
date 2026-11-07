package org.delcom.ifs25016pbo2026springbootcashflow;

import org.delcom.ifs25016pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25016pbo2026springbootcashflow.domain.repository.ITransactionRepository;
import org.delcom.ifs25016pbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AppTest {

    @Autowired
    private ITransactionRepository repository;
    @Autowired
    private FinanceUseCase useCase;
    @Autowired
    private FinancePresenter presenter;

    @Test
    void beansTerhubung() {
        assertNotNull(repository);
        assertNotNull(useCase);
        assertNotNull(presenter);
    }

    @Test
    void saldoKonsisten() throws Exception {
        BigDecimal before = useCase.getBalance();
        var in = useCase.addIncome("Gaji", "1000000");
        useCase.addExpense("Makan", "25000");
        assertEquals(before.add(new BigDecimal("975000")), useCase.getBalance());
        useCase.removeTransaction(in.getId());
        assertEquals(before.subtract(new BigDecimal("25000")), useCase.getBalance());
    }

    @Test
    void formatUang() {
        assertEquals("Rp 1,000,000.00", presenter.formatMoney(new BigDecimal("1000000")));
    }
}
