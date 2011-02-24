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

package com.aspect.snoop.ui.hook;

import com.aspect.snoop.Condition;
import com.aspect.snoop.FunctionHook;
import com.aspect.snoop.FunctionHook.Mode;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.aspect.snoop.ui.condition.ConditionTableModel;

public class FunctionHookTableSelectionListener implements ListSelectionListener {

    JTable functionHookTable;
    JTable conditionTable;
    JButton btnAddNewCondition;

    JRadioButton rdoAlwaysHook;
    JRadioButton rdoDontHookIf;
    JRadioButton rdoHookIf;

    JCheckBox chkTamperParameters;
    JCheckBox chkTamperReturnValue;
    
    JCheckBox chkRunScript;
    JButton btnBrowseForScript;
    JCheckBox chkPause;
    JCheckBox chkPrintParameters;
    JCheckBox chkPrintStackTrace;
    JCheckBox chkOutputToFile;
    JCheckBox chkOutputToConsole;
    JTextField txtOutputFile;

    JButton btnBrowseForOutputFile;
    ButtonGroup btnGrpCondition;

    List<Component> stuffToEnable;


    // It is necessary to keep the table since it is not possible
    // to determine the table from the event's source.

    // also need all these other buttons and shit to enable/disable
    // when a row is selected
    public FunctionHookTableSelectionListener(JTable functionHookTable,
            JTable conditionTable,
            JButton btnAddNewCondition,
            JRadioButton rdoAlwaysHook,
            JRadioButton rdoDontHookIf,
            JRadioButton rdoHookIf,
            JCheckBox chkTamperParameters,
            JCheckBox chkTamperReturnValue,
            JCheckBox chkRunScript,
            JButton btnBrowseForScript,
            JCheckBox chkPause,
            JCheckBox chkPrintParameters,
            JCheckBox chkPrintStackTrace,
            JCheckBox chkOutputToFile,
            JCheckBox chkOutputToConsole,
            JTextField txtOutputFile,
            JButton btnBrowseForOutputFile,
            ButtonGroup btnGrpCondition) {

        this.functionHookTable = functionHookTable;
        this.conditionTable = conditionTable;
        this.btnAddNewCondition = btnAddNewCondition;
        this.rdoAlwaysHook = rdoAlwaysHook;
        this.rdoDontHookIf = rdoDontHookIf;
        this.rdoHookIf = rdoHookIf;

        this.chkTamperParameters = chkTamperParameters;
        this.chkTamperReturnValue = chkTamperReturnValue;
        
        this.chkRunScript = chkRunScript;
        this.btnBrowseForScript = btnBrowseForScript;
        this.chkPause = chkPause;
        this.chkPrintParameters = chkPrintParameters;
        this.chkPrintStackTrace = chkPrintStackTrace;
        this.chkOutputToConsole = chkOutputToConsole;
        this.chkOutputToFile = chkOutputToFile;
        this.txtOutputFile = txtOutputFile;
        this.btnBrowseForOutputFile = btnBrowseForOutputFile;

        this.btnGrpCondition = btnGrpCondition;

        stuffToEnable = new ArrayList<Component>();
        stuffToEnable.add(rdoAlwaysHook);
        stuffToEnable.add(rdoDontHookIf);
        stuffToEnable.add(rdoHookIf);
        stuffToEnable.add(chkTamperParameters);
        stuffToEnable.add(chkTamperReturnValue);
        stuffToEnable.add(chkRunScript);
        stuffToEnable.add(btnBrowseForScript);
        stuffToEnable.add(chkPause);
        stuffToEnable.add(chkPrintParameters);
        stuffToEnable.add(chkPrintStackTrace);
        stuffToEnable.add(chkOutputToFile);
        stuffToEnable.add(chkOutputToConsole);
        stuffToEnable.add(txtOutputFile);
        stuffToEnable.add(btnBrowseForOutputFile);
        stuffToEnable.add(conditionTable);
        
        disableAll();

    }

    public void valueChanged(ListSelectionEvent e) {
        
        // check if the mouse button has not yet been released
        if ( e.getValueIsAdjusting() ) {
            
            // have to update the conditions table now that the selection has changed

            FunctionsHookedTableModel model = (FunctionsHookedTableModel) functionHookTable.getModel();

            // step #1: figure out what FunctionHook is represented by the row
            int selectedRow = functionHookTable.getSelectedRow();

            if ( selectedRow != -1 ) {

                btnAddNewCondition.setEnabled(true);
                
                FunctionHook hook = model.getHookFromRow(selectedRow);

                // step #2: get the conditions from said FunctionHook
                List<Condition> conditions = hook.getConditions();

                // step #3: set the model to said conditions
                ((ConditionTableModel)conditionTable.getModel()).setConditions(conditions);
                conditionTable.updateUI();
                
                enableAll();

                chkTamperParameters.setSelected(hook.shouldTamperParameters());

                if ( hook.getParameterTypes().length == 0 ) {
                    chkTamperParameters.setEnabled(false);
                    chkPrintParameters.setEnabled(false);
                }

                chkTamperReturnValue.setSelected(hook.shouldTamperReturnValue());
                
                if ( hook.getReturnType().getName().equals("void") ) {
                    chkTamperReturnValue.setEnabled(false);
                }

                chkRunScript.setSelected(hook.shouldRunScript());
                chkPause.setSelected(hook.shouldPause());
                chkPrintParameters.setSelected(hook.shouldPrintParameters());
                chkPrintStackTrace.setSelected(hook.shouldPrintStackTrace());
                chkOutputToConsole.setSelected(hook.isOutputToConsole());
                chkOutputToFile.setSelected(hook.isOutputToFile());
                txtOutputFile.setText(hook.getOutputFile());

                rdoAlwaysHook.setSelected(hook.getMode().equals(Mode.AlwaysIntercept));
                rdoHookIf.setSelected(hook.getMode().equals(Mode.InterceptIf));
                rdoDontHookIf.setSelected(hook.getMode().equals(Mode.DontInterceptIf));
                
            } else {
                
                btnAddNewCondition.setEnabled(false);

                disableAll();

                clearAll();
            }
        }

    }

    private void clearAll() {
        for(Component c : stuffToEnable) {
            if ( c instanceof JTextField ) {
                ((JTextField)c).setText("");
            } else if ( c instanceof JCheckBox ) {
                ((JCheckBox)c).setSelected(false);
            } else if ( c instanceof JRadioButton ) {
                ((JRadioButton)c).setSelected(false);
            }
        }

        btnGrpCondition.clearSelection();

        ConditionTableModel model = (ConditionTableModel) conditionTable.getModel();
        model.setConditions(new ArrayList<Condition>());
        conditionTable.repaint();
        conditionTable.updateUI();
    }

    private void disableAll() {
        for (Component c : stuffToEnable) {
            c.setEnabled(false);
        }
    }

    private void enableAll() {
        for (Component c : stuffToEnable) {
            c.setEnabled(true);
        }
    }
    
}
