package com.cts.process;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;

import javax.swing.*;
import java.awt.*;

/**
 * @author xpmiao
 * @date 2023/11/27
 */
public class CommonShow {
    public static CommonShow DEFAULT_COMMON = new CommonShow();
    /**
     *
     * @param jPanel
     */
    public void show(Component jPanel, Editor editor) {
        JBPopup ssss = JBPopupFactory.getInstance().createMessage("");
        ssss.setAdText("Parse result", SwingConstants.CENTER);

        // ssss.getContent().setSize(new Dimension(300,200));
        ssss.getContent().add(jPanel);
        ssss.showInBestPositionFor(editor);
    }
}
