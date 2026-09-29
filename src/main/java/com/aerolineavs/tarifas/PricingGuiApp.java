package com.aerolineavs.tarifas;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada de la aplicación gráfica.
 */
public final class PricingGuiApp {

    private PricingGuiApp() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            IPricingService pricingService = new PricingServiceImpl();
            new PricingWindow(pricingService).setVisible(true);
        });
    }
}