package com.example.adapters.inbound.swing.view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public abstract class ProductView extends JPanel {

    private static final long serialVersionUID = 1L;

    protected static final Color PRIMARY = new Color(108, 62, 184);
    protected static final Color PRIMARY_LIGHT = new Color(167, 139, 250);
    protected static final Color SECONDARY = new Color(45, 42, 74);
    protected static final Color MAIN_BG = new Color(243, 235, 255);
    protected static final Color SURFACE = new Color(229, 231, 235);
    protected static final Color TEXT_MAIN = new Color(31, 41, 55);
    protected static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    protected static final Color SUCCESS = new Color(16, 185, 129);
    protected static final Color ERROR = new Color(239, 68, 68);
    protected static final Color WARNING = new Color(245, 158, 11);

    protected ProductView() {
        setBackground(MAIN_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    protected static Font titleFont() {
        return new Font(Font.SANS_SERIF, Font.BOLD, 20);
    }

    protected static Font subtitleFont() {
        return new Font(Font.SANS_SERIF, Font.BOLD, 15);
    }

    protected static Font normalFont() {
        return new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    }
}
