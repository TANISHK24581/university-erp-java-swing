package edu.univ.erp.ui.common;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.*;
import java.awt.*;

public final class UITheme {

    private UITheme() {}

    public static void applyBlueTheme() {
        try {
            FlatLightLaf.setup();

            UIManager.put("Component.focusColor", new Color(30, 90, 200));
            UIManager.put("Button.startBackground", new Color(40, 110, 220));
            UIManager.put("Button.endBackground", new Color(30, 90, 200));
            UIManager.put("Button.hoverBackground", new Color(50, 130, 230));

            UIManager.put("TabbedPane.selectedBackground", new Color(30, 90, 200));
            UIManager.put("TabbedPane.underlineColor", new Color(30, 90, 200));
            UIManager.put("TabbedPane.selectedForeground", Color.WHITE);

            UIManager.put("Table.selectionBackground", new Color(30, 90, 200));
            UIManager.put("Table.selectionForeground", Color.WHITE);
            UIManager.put("TableHeader.background", new Color(230, 235, 245));
            UIManager.put("TableHeader.foreground", new Color(40, 40, 40));
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));

            UIManager.put("Button.arc", 15);
            UIManager.put("Component.focusWidth", 1);

            UIManager.put("Panel.background", new Color(245, 247, 250));
            UIManager.put("TabbedPane.background", new Color(245, 247, 250));
            UIManager.put("Table.background", Color.WHITE);

            FlatLaf.updateUI();

        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf theme: " + e.getMessage());
        }
    }
}
