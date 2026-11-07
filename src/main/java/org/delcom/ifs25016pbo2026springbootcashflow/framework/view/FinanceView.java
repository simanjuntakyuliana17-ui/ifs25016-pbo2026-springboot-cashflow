package org.delcom.ifs25016pbo2026springbootcashflow.framework.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.delcom.ifs25016pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25016pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25016pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25016pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25016pbo2026springbootcashflow.usecase.FinanceUseCase;

import java.util.Comparator;
import java.util.List;

/**
 * Tampilan web Vaadin aplikasi Cashflow.
 * Pengurutan lewat klik header kolom; hapus lewat dialog konfirmasi.
 */
@Route("")
@PageTitle("Cashflow - Catatan Keuangan Pribadi")
public class FinanceView extends VerticalLayout {

    private final FinanceUseCase useCase;
    private final FinancePresenter presenter;

    private final Grid<Transaction> grid = new Grid<>(Transaction.class, false);
    private final TextField searchField = new TextField();
    private final Paragraph statusLabel = new Paragraph();
    private final Span emptyState = new Span();
    private final Span totalCount = new Span("0");
    private final Span incomeValue = new Span();
    private final Span expenseValue = new Span();
    private final Span balanceValue = new Span();

    public FinanceView(FinanceUseCase useCase, FinancePresenter presenter) {
        this.useCase = useCase;
        this.presenter = presenter;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        // Aksesibilitas: halaman punya landmark utama
        getElement().setAttribute("role", "main");

        configureGrid();
        searchField.setLabel("Cari keterangan");
        searchField.setPlaceholder("Ketik kata kunci...");
        searchField.setMinWidth("12rem");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);

        add(new H1("Cashflow"), buildSummaryCards(), buildToolbar(), statusLabel, grid);
        refreshAll();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        // Aksesibilitas/SEO: deklarasikan bahasa halaman
        getElement().executeJs("document.documentElement.lang = 'id'");
    }

    /** Warna eksplisit dengan kontras tinggi (>= 4.5:1) dan target sentuh minimal 44px. */
    private void styleButton(Button button, String background, String foreground) {
        button.getStyle()
                .set("--vaadin-button-background", background)
                .set("--vaadin-button-text-color", foreground)
                .set("background", background)
                .set("color", foreground)
                .set("min-height", "44px")
                .set("min-width", "44px");
    }

    // ---------- ringkasan ----------
    private HorizontalLayout buildSummaryCards() {
        HorizontalLayout row = new HorizontalLayout(
                card("Total Transaksi", totalCount, "#DBEAFE", "#1D4ED8", "#1E40AF"),
                card("Total Pemasukan", incomeValue, "#DCFCE7", "#16A34A", "#166534"),
                card("Total Pengeluaran", expenseValue, "#FEE2E2", "#DC2626", "#991B1B"),
                card("Saldo", balanceValue, "#FEF3C7", "#D97706", "#92400E"));
        row.setWidthFull();
        row.getStyle().set("align-items", "stretch").set("flex-wrap", "wrap");
        return row;
    }

    private Div card(String label, Span value, String bg, String accent, String valueColor) {
        Span caption = new Span(label);
        caption.getStyle().set("display", "block").set("font-size", "0.875rem")
                .set("font-weight", "500").set("color", valueColor);
        value.getStyle().set("display", "block").set("margin-top", "0.35rem")
                .set("font-size", "1.5rem").set("font-weight", "700").set("color", valueColor);
        Div card = new Div(caption, value);
        card.getStyle().set("flex", "1 1 200px").set("min-width", "0").set("background", bg)
                .set("border", "1px solid " + accent).set("border-left", "6px solid " + accent)
                .set("border-radius", "12px").set("padding", "1rem 1.1rem")
                .set("box-sizing", "border-box");
        return card;
    }

    private void updateSummary() {
        totalCount.setText(String.valueOf(useCase.getAllTransactions().size()));
        incomeValue.setText(presenter.formatMoney(useCase.getTotalIncome()));
        expenseValue.setText(presenter.formatMoney(useCase.getTotalExpense()));
        balanceValue.setText(presenter.formatMoney(useCase.getBalance()));
    }

    // ---------- grid ----------
    private void configureGrid() {
        grid.addColumn(Transaction::getId).setHeader("ID").setKey("id")
                .setAutoWidth(true).setFlexGrow(0).setSortable(true)
                .setComparator(Transaction::getId);
        grid.addColumn(Transaction::getDescription).setHeader("Keterangan").setKey("description")
                .setFlexGrow(1).setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getDescription,
                        String.CASE_INSENSITIVE_ORDER));
        grid.addColumn(t -> presenter.formatMoney(t.getAmount())).setHeader("Jumlah").setKey("amount")
                .setAutoWidth(true).setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getAmount));
        grid.addColumn(presenter::formatType).setHeader("Tipe").setKey("type")
                .setAutoWidth(true).setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getType));
        grid.addColumn(presenter::formatCreatedAt).setHeader("Waktu").setKey("createdAt")
                .setAutoWidth(true).setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getCreatedAt));
        grid.addComponentColumn(this::actionButtons).setHeader("Tindakan").setKey("tindakan")
                .setAutoWidth(true).setFlexGrow(0);
        grid.setEmptyStateComponent(emptyState);
        grid.getElement().setAttribute("aria-label", "Daftar transaksi keuangan");
        grid.setSizeFull();
    }

    private HorizontalLayout actionButtons(Transaction t) {
        Button delete = new Button("Hapus", VaadinIcon.TRASH.create(), e -> openDeleteDialog(t));
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY_INLINE);
        styleButton(delete, "transparent", "#B91C1C");
        delete.getElement().setAttribute("aria-label", "Hapus transaksi " + t.getId());
        return new HorizontalLayout(delete);
    }

    // ---------- toolbar ----------
    private HorizontalLayout buildToolbar() {
        Button search = new Button("Cari", VaadinIcon.SEARCH.create(), e -> search());
        Button refresh = new Button("Muat Ulang", VaadinIcon.REFRESH.create(), e -> refreshAll());
        Button income = new Button("Tambah Pemasukan", VaadinIcon.PLUS.create(), e -> openAddDialog(true));
        income.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        Button expense = new Button("Tambah Pengeluaran", VaadinIcon.MINUS.create(), e -> openAddDialog(false));
        expense.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

        styleButton(search, "#E0E7FF", "#1E3A8A");
        styleButton(refresh, "#E0E7FF", "#1E3A8A");
        styleButton(income, "#166534", "#FFFFFF");
        styleButton(expense, "#B91C1C", "#FFFFFF");

        HorizontalLayout bar = new HorizontalLayout(searchField, search, refresh, income, expense);
        bar.setWidthFull();
        bar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        bar.getStyle().set("flex-wrap", "wrap");
        bar.setFlexGrow(1, searchField);
        return bar;
    }

    // ---------- dialog ----------
    private void openAddDialog(boolean income) {
        TextField description = new TextField("Keterangan");
        description.setWidthFull();
        TextField amount = new TextField("Jumlah (Rp)");
        amount.setWidthFull();

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(income ? "Tambah Pemasukan" : "Tambah Pengeluaran");
        dialog.setWidth("450px");
        dialog.add(new VerticalLayout(description, amount));

        Button save = new Button("Simpan", VaadinIcon.CHECK.create(), e -> {
            try {
                Transaction created = income
                        ? useCase.addIncome(description.getValue(), amount.getValue())
                        : useCase.addExpense(description.getValue(), amount.getValue());
                showSuccess(presenter.showAddSuccess(created));
                dialog.close();
                refreshAll();
            } catch (ValidationException ex) {
                showError(presenter.showValidationError(ex.getMessage()));
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Batal", VaadinIcon.CLOSE.create(), e -> dialog.close());
        dialog.getFooter().add(cancel, save);
        dialog.open();
    }

    private void openDeleteDialog(Transaction t) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Menghapus Transaksi");
        dialog.add(new Paragraph("Yakin ingin menghapus transaksi \"" + t.getDescription()
                + "\" (ID " + t.getId() + ")?"));
        Button confirm = new Button("Hapus", VaadinIcon.TRASH.create(), e -> {
            deleteTransaction(t);
            dialog.close();
        });
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        Button cancel = new Button("Batal", VaadinIcon.CLOSE.create(), e -> dialog.close());
        dialog.getFooter().add(cancel, confirm);
        dialog.open();
    }

    // ---------- aksi ----------
    private void deleteTransaction(Transaction t) {
        try {
            useCase.removeTransaction(t.getId());
            showSuccess(presenter.showRemoveSuccess());
        } catch (EntityNotFoundException ex) {
            showError(presenter.showRemoveFailed(ex.getId()));
        }
        refreshAll();
    }

    private void search() {
        String keyword = searchField.getValue();
        if (keyword.isBlank()) {
            refreshAll();
            return;
        }
        emptyState.setText(presenter.showNotFound());
        refreshGrid(useCase.searchTransactions(keyword), "Hasil Pencarian: \"" + keyword + "\"");
    }

    private void refreshAll() {
        emptyState.setText(presenter.showEmpty());
        searchField.clear();
        refreshGrid(useCase.getAllTransactions(), "Daftar Transaksi");
    }

    private void refreshGrid(List<Transaction> items, String title) {
        grid.setItems(items);
        updateSummary();
        statusLabel.setText(items.isEmpty()
                ? title + " — " + emptyState.getText().replaceFirst("^- ", "")
                : title);
    }

    private void showSuccess(String msg) {
        Notification.show(msg, 3000, Notification.Position.BOTTOM_START)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String msg) {
        Notification.show(msg, 4000, Notification.Position.BOTTOM_START)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
