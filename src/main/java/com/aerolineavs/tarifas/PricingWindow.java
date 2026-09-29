package com.aerolineavs.tarifas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

/**
 * Ventana Swing para solicitar y mostrar una recomendación de tarifa.
 */
public final class PricingWindow extends JFrame {

    private static final Color PAGE = new Color(243, 246, 241);
    private static final Color INK = new Color(30, 48, 39);
    private static final Color MUTED = new Color(105, 119, 109);
    private static final Color ACCENT = new Color(167, 211, 145);
    private static final Color LINE = new Color(222, 229, 220);
    private static final Font BODY = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Border CARD_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LINE),
            BorderFactory.createEmptyBorder(22, 22, 22, 22)
    );

    private final IPricingService pricingService;
    private final JSpinner ageInput = new JSpinner(new SpinnerNumberModel(30, 0, 120, 1));
    private final JSpinner flightsInput = new JSpinner(new SpinnerNumberModel(6, 0, 365, 1));
    private final JSpinner incomeInput = new JSpinner(
            new SpinnerNumberModel(25000.0, 0.0, 10000000.0, 1000.0)
    );
    private final JComboBox<TipoViajero> travelerTypeInput = new JComboBox<>(TipoViajero.values());
    private final JComboBox<ClaseVuelo> flightClassInput = new JComboBox<>(ClaseVuelo.values());
    private final JComboBox<RegionDestino> destinationInput = new JComboBox<>(RegionDestino.values());
    private final JCheckBox childrenInput = new JCheckBox("Viaja con niños");
    private final JCheckBox parentsInput = new JCheckBox("Vive con sus padres");
    private final JLabel statusLabel = new JLabel("PENDIENTE", SwingConstants.LEFT);
    private final JLabel tariffLabel = new JLabel("Sin evaluar", SwingConstants.LEFT);
    private final JLabel discountLabel = new JLabel("--%", SwingConstants.LEFT);
    private final JTextArea assumptionsText = new JTextArea();
    private final JButton calculateButton = new JButton("Calcular tarifa");

    public PricingWindow(IPricingService pricingService) {
        super("Tarifas | Aerolínea");
        this.pricingService = pricingService;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850, 610));
        setContentPane(buildContent());
        getRootPane().setDefaultButton(calculateButton);
        pack();
        setLocationRelativeTo(null);
        parentsInput.setEnabled(travelerTypeInput.getSelectedItem() == TipoViajero.TRABAJADOR_JOVEN);
        travelerTypeInput.addActionListener(event -> {
            boolean isYoungWorker = travelerTypeInput.getSelectedItem() == TipoViajero.TRABAJADOR_JOVEN;
            parentsInput.setEnabled(isYoungWorker);
            if (!isYoungWorker) {
                parentsInput.setSelected(false);
            }
        });
    }

    private JPanel buildContent() {
        JPanel page = new JPanel(new BorderLayout(0, 22));
        page.setBackground(PAGE);
        page.add(buildHeader(), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(18, 0));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(0, 24, 24, 24));
        content.add(buildProfileCard(), BorderLayout.CENTER);
        content.add(buildResultCard(), BorderLayout.EAST);
        page.add(content, BorderLayout.CENTER);
        return page;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(INK);
        header.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));

        JLabel eyebrow = new JLabel("AEROLÍNEA  /  TARIFAS");
        eyebrow.setForeground(ACCENT);
        eyebrow.setFont(BODY.deriveFont(Font.BOLD, 11f));

        JLabel title = new JLabel("Encuentra tu tarifa");
        title.setForeground(Color.WHITE);
        title.setFont(BODY.deriveFont(Font.BOLD, 27f));

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(eyebrow);
        titles.add(Box.createVerticalStrut(5));
        titles.add(title);
        header.add(titles, BorderLayout.WEST);

        JLabel mark = new JLabel("PRICING  ·  01", SwingConstants.RIGHT);
        mark.setForeground(new Color(194, 207, 197));
        mark.setFont(BODY.deriveFont(Font.BOLD, 11f));
        header.add(mark, BorderLayout.EAST);
        return header;
    }

    private JPanel buildProfileCard() {
        JPanel card = new JPanel(new BorderLayout(0, 20));
        card.setBackground(Color.WHITE);
        card.setBorder(CARD_BORDER);

        JLabel heading = new JLabel("Perfil del viajero");
        heading.setForeground(INK);
        heading.setFont(BODY.deriveFont(Font.BOLD, 19f));
        card.add(heading, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(3, 2, 14, 14));
        fields.setOpaque(false);
        fields.add(createField("Edad", ageInput));
        fields.add(createField("Tipo de viajero", travelerTypeInput));
        fields.add(createField("Vuelos al año", flightsInput));
        fields.add(createField("Clase preferida", flightClassInput));
        fields.add(createField("Ingresos anuales (€)", incomeInput));
        fields.add(createField("Destino preferido", destinationInput));

        JPanel options = new JPanel(new GridLayout(1, 2, 14, 0));
        options.setOpaque(false);
        styleCheckBox(childrenInput);
        styleCheckBox(parentsInput);
        options.add(childrenInput);
        options.add(parentsInput);

        JPanel inputs = new JPanel(new BorderLayout(0, 16));
        inputs.setOpaque(false);
        inputs.add(fields, BorderLayout.CENTER);
        inputs.add(options, BorderLayout.SOUTH);
        card.add(inputs, BorderLayout.CENTER);

        JButton resetButton = new JButton("Restablecer");
        styleSecondaryButton(resetButton);
        resetButton.addActionListener(event -> resetForm());
        stylePrimaryButton(calculateButton);
        calculateButton.addActionListener(event -> calculateTariff());

        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);
        actions.add(resetButton, BorderLayout.WEST);
        actions.add(calculateButton, BorderLayout.EAST);
        card.add(actions, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildResultCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(INK);
        card.setBorder(BorderFactory.createEmptyBorder(24, 22, 24, 22));
        card.setPreferredSize(new Dimension(270, 0));

        JLabel eyebrow = new JLabel("RECOMENDACIÓN");
        eyebrow.setForeground(ACCENT);
        eyebrow.setFont(BODY.deriveFont(Font.BOLD, 11f));
        eyebrow.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(eyebrow);
        card.add(Box.createVerticalStrut(18));

        statusLabel.setForeground(new Color(194, 207, 197));
        statusLabel.setFont(BODY.deriveFont(Font.BOLD, 10f));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(8));

        tariffLabel.setForeground(Color.WHITE);
        tariffLabel.setFont(BODY.deriveFont(Font.BOLD, 24f));
        tariffLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(tariffLabel);
        card.add(Box.createVerticalStrut(22));

        JLabel discountCaption = new JLabel("DESCUENTO");
        discountCaption.setForeground(new Color(194, 207, 197));
        discountCaption.setFont(BODY.deriveFont(Font.BOLD, 10f));
        discountCaption.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(discountCaption);

        discountLabel.setForeground(ACCENT);
        discountLabel.setFont(BODY.deriveFont(Font.BOLD, 38f));
        discountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(discountLabel);
        card.add(Box.createVerticalStrut(18));

        JPanel divider = new JPanel();
        divider.setBackground(new Color(68, 86, 74));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(divider);
        card.add(Box.createVerticalStrut(16));

        assumptionsText.setEditable(false);
        assumptionsText.setFocusable(false);
        assumptionsText.setLineWrap(true);
        assumptionsText.setWrapStyleWord(true);
        assumptionsText.setOpaque(false);
        assumptionsText.setForeground(new Color(194, 207, 197));
        assumptionsText.setFont(BODY.deriveFont(12f));
        assumptionsText.setMargin(new Insets(0, 0, 0, 0));
        assumptionsText.setAlignmentX(Component.LEFT_ALIGNMENT);
        assumptionsText.setText("El resultado aparecerá aquí.");
        card.add(assumptionsText);
        return card;
    }

    private JPanel createField(String labelText, Component input) {
        JPanel field = new JPanel(new BorderLayout(0, 7));
        field.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setForeground(INK);
        label.setFont(BODY.deriveFont(Font.BOLD, 12f));
        field.add(label, BorderLayout.NORTH);

        input.setFont(BODY);
        input.setPreferredSize(new Dimension(160, 38));
        if (input instanceof JSpinner spinner) {
            spinner.setBorder(BorderFactory.createLineBorder(LINE));
            spinner.getEditor().getComponent(0).setFont(BODY);
        } else if (input instanceof JComboBox<?> comboBox) {
            comboBox.setBackground(Color.WHITE);
            comboBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus
                ) {
                    JLabel rendered = (JLabel) super.getListCellRendererComponent(
                            list, value, index, isSelected, cellHasFocus
                    );
                    rendered.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                    rendered.setText(displayValue(value));
                    return rendered;
                }
            });
        }
        field.add(input, BorderLayout.CENTER);
        return field;
    }

    private void styleCheckBox(JCheckBox checkBox) {
        checkBox.setOpaque(false);
        checkBox.setForeground(INK);
        checkBox.setFont(BODY.deriveFont(Font.PLAIN, 13f));
    }

    private void stylePrimaryButton(JButton button) {
        button.setBackground(INK);
        button.setForeground(Color.WHITE);
        button.setFont(BODY.deriveFont(Font.BOLD, 13f));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(11, 18, 11, 18));
        button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton button) {
        button.setBackground(Color.WHITE);
        button.setForeground(INK);
        button.setFont(BODY.deriveFont(Font.BOLD, 13f));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
    }

    private void calculateTariff() {
        ClientePotencial cliente = new ClientePotencial(
                ((Number) ageInput.getValue()).intValue(),
                ((Number) flightsInput.getValue()).intValue(),
                (TipoViajero) travelerTypeInput.getSelectedItem(),
                (ClaseVuelo) flightClassInput.getSelectedItem(),
                (RegionDestino) destinationInput.getSelectedItem(),
                ((Number) incomeInput.getValue()).doubleValue(),
                childrenInput.isSelected(),
                parentsInput.isSelected()
        );

        ResultadoTarifa result = pricingService.evaluar(cliente);
        statusLabel.setText("RESULTADO ACTUALIZADO");
        tariffLabel.setText(result.tarifa().getNombre());
        discountLabel.setText(result.tarifa().getDescuentoPorcentaje() + "%");
        assumptionsText.setText(result.suposiciones());
    }

    private void resetForm() {
        ageInput.setValue(30);
        flightsInput.setValue(6);
        incomeInput.setValue(25000.0);
        travelerTypeInput.setSelectedItem(TipoViajero.GENERAL);
        flightClassInput.setSelectedItem(ClaseVuelo.TURISTA);
        destinationInput.setSelectedItem(RegionDestino.EUROPA);
        childrenInput.setSelected(false);
        parentsInput.setSelected(false);
        statusLabel.setText("PENDIENTE");
        tariffLabel.setText("Sin evaluar");
        discountLabel.setText("--%");
        assumptionsText.setText("El resultado aparecerá aquí.");
    }

    private String displayValue(Object value) {
        if (value instanceof TipoViajero tipo) {
            return switch (tipo) {
                case MENOR -> "Menor";
                case ESTUDIANTE_UNIVERSITARIO_DESPLAZADO -> "Estudiante desplazado";
                case TRABAJADOR_JOVEN -> "Joven profesional";
                case GENERAL -> "General";
            };
        }
        if (value instanceof ClaseVuelo clase) {
            return clase == ClaseVuelo.TURISTA ? "Turista" : "Business";
        }
        if (value instanceof RegionDestino destino) {
            return switch (destino) {
                case EUROPA -> "Europa";
                case ASIA -> "Asia";
                case AMERICA -> "América";
                case OTRA -> "Otra región";
            };
        }
        return value == null ? "" : value.toString();
    }
}