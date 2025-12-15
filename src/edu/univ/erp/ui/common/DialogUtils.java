package edu.univ.erp.ui.common;

import javax.swing.*;
import java.awt.*;

public class DialogUtils {
    private static JDialog loadingDialog;

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public static void showLoading(Window parent, String message) {
        if (loadingDialog != null && loadingDialog.isShowing()) return;
        Frame owner = parent instanceof Frame ? (Frame) parent : JOptionPane.getRootFrame();
        loadingDialog = new JDialog(owner, "Please wait...", Dialog.ModalityType.APPLICATION_MODAL);
        JPanel p = new JPanel(new BorderLayout(8,8));
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        p.add(new JLabel(message, SwingConstants.CENTER), BorderLayout.CENTER);
        loadingDialog.getContentPane().add(p);
        loadingDialog.pack();
        loadingDialog.setResizable(false);
        loadingDialog.setLocationRelativeTo(parent);
        // run showing on EDT
        SwingUtilities.invokeLater(() -> loadingDialog.setVisible(true));
    }

    public static void hideLoading() {
        if (loadingDialog != null) {
            loadingDialog.setVisible(false);
            loadingDialog.dispose();
            loadingDialog = null;
        }
    }

    public static void makeTableSortable(JTable table) {
        if (table != null) table.setAutoCreateRowSorter(true);
    }
}
