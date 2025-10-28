package com.cts.process;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class CtsToolWindows implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JBLabel hellowThis = new JBLabel("hellow this");
        ContentFactory instance = ContentFactory.SERVICE.getInstance();
        Content testHellow = instance.createContent(hellowThis, "test Hellow", false);

        toolWindow.getContentManager().addContent(testHellow);
    }
}
