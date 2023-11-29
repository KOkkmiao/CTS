package com.cts.process;

import com.intellij.openapi.editor.Editor;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Properties;

/**
 * @author xpmiao
 * @date 2023/11/27
 */
public class PropertiesShow {
    private CommonShow parentShow;
    private Font song = new Font("宋体", Font.BOLD, 14);
    private Color index = new Color(204, 120, 50);
    private Color textColor = new Color(255, 198, 109);
    public PropertiesShow() {
        parentShow = CommonShow.DEFAULT_COMMON;
    }
    public void show(Map<String, Properties> values,String selectText, Editor editor){
        JPanel jPanel = new JPanel();
        values.forEach((key,value)->{
            String property = value.getProperty(selectText);
            if (property != null) {
                JLabel jTextPane = new JLabel();
                jTextPane.setText(key);
                jTextPane.setForeground(index);
                jTextPane.setFont(song);
                jPanel.add(jTextPane);
                if (property.length()>350) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; ; ) {
                        int j = i + 200;
                        if (j>property.length()) {
                            stringBuilder.append(property.substring(i,property.length()));
                            break;
                        }
                        stringBuilder.append(property.substring(i,j));
                        stringBuilder.append("\n");
                        i += 200;
                    }
                    property = stringBuilder.toString();
                }
                JTextPane text = new JTextPane();
                text.setForeground(textColor);
                text.setFont(song);
                text.setBorder(null);
                text.setText(" "+property);
                jPanel.add(text);
            }
        });
        parentShow.show(new JScrollPane(jPanel), editor);
    }
}
