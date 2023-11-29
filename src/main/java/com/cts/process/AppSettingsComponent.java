// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.cts.process;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent {

  private final JPanel myMainPanel;
  private final JBTextArea input_fetch_content = new JBTextArea("Input fetch content",25,0);
  private final JBTextField output_fetch_content = new JBTextField();

  public AppSettingsComponent() {
    // output_fetch_content.setText();
    myMainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(new JBLabel("Input your json comment: "), input_fetch_content, 1, false)
            .addComponentFillVertically(new JPanel(), 0)
            .getPanel();

  }

  public JPanel getPanel() {
    return myMainPanel;
  }

  public JComponent getPreferredFocusedComponent() {
    return input_fetch_content;
  }

  public String getInput_fetch_content() {
    return input_fetch_content.getText();
  }
  public void setInput_fetch_content(String fetchText) {
    input_fetch_content.setText(fetchText);
  }

  public String getOutput_fetch_content() {
    return output_fetch_content.getText();
  }
  public void setOutput_fetch_content(String fetchText) {
    output_fetch_content.setText(fetchText);
  }
}