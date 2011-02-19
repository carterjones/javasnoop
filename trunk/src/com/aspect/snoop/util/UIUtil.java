package com.aspect.snoop.util;

/*
 * Copyright, Aspect Security, Inc.
 *
 * This file is part of JavaSnoop.
 *
 * JavaSnoop is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JavaSnoop is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaSnoop.  If not, see <http://www.gnu.org/licenses/>.
 */



import com.aspect.snoop.agent.AgentLogger;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class UIUtil {

    public static void waitForInput(JDialog view) {
        while (view.isShowing() ) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {}
        }
    }

    public static void showErrorMessage(JDialog dialog, String msg) {
        JOptionPane.showMessageDialog(dialog, msg, "JavaSnoop", JOptionPane.ERROR_MESSAGE);
    }

    public static void showErrorMessage(JFrame frame, String msg) {
        JOptionPane.showMessageDialog(frame, msg, "JavaSnoop", JOptionPane.ERROR_MESSAGE);
    }

    public static void pause(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException ex) { }
    }
}
