package org.delcom.ifs25016pbo2026springbootcashflow;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Meta;
import com.vaadin.flow.server.AppShellSettings;

/** Pengaturan halaman global (SEO dasar: meta description dan favicon). */
@Meta(name = "description",
        content = "Cashflow: aplikasi web pencatat pemasukan, pengeluaran, dan saldo keuangan pribadi.")
public class AppShell implements AppShellConfigurator {

    @Override
    public void configurePage(AppShellSettings settings) {
        settings.addFavIcon("icon", "icons/favicon.svg", "any");
    }
}
