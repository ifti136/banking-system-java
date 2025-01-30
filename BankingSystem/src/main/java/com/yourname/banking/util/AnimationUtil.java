package com.yourname.banking.util;

import javax.swing.*;
import java.awt.*;

public class AnimationUtil {
    public static void fadeButtonColor(JButton button, Color startColor, Color endColor) {
        int steps = 15; // Number of animation steps
        int delay = 15; // Delay between steps (ms)

        new Thread(() -> {
            for (int i = 1; i <= steps; i++) {
                float ratio = (float) i / steps;
                Color intermediateColor = blendColors(startColor, endColor, ratio);
                SwingUtilities.invokeLater(() -> button.setBackground(intermediateColor));
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    private static Color blendColors(Color start, Color end, float ratio) {
        int r = (int) (start.getRed() + (end.getRed() - start.getRed()) * ratio);
        int g = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * ratio);
        int b = (int) (start.getBlue() + (end.getBlue() - start.getBlue()) * ratio);
        return new Color(r, g, b);
    }
} 