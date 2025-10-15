package com.cts.process;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class UnicodeAction extends AnAction {

    public void actionPerformed(@NotNull AnActionEvent e){
        ActionManager.getInstance().getAction("CollapseRegion").actionPerformed(e);
    }

}