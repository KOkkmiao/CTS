package com.cts.process;

import com.alibaba.fastjson2.JSONObject;
import com.cts.process.ui.BalloonPopupBuilder;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.TextRange;
import com.intellij.ui.JBColor;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBComboBoxLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.PositionTracker;
import com.intellij.util.ui.UIUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author xpmiao
 * @date 2023/11/27
 */
public class PropertiesShow {
    private CommonShow parentShow;
    private Font song = new Font("宋体", Font.BOLD, 14);
    private Font songError = new Font("宋体", Font.BOLD, 15);
    private Color index = new Color(204, 120, 50);
    private Color white = new Color(255, 255, 255, 237);
    private Color textColor = new Color(255, 198, 109);
    public PropertiesShow() {
        parentShow = CommonShow.DEFAULT_COMMON;
    }
    public void show(Map<String, Properties> values, String grayName, String selectText,
            Editor editor) {
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        JPanel content = new JPanel(flowLayout);
        AppSettingsState instance = AppSettingsState.getInstance();
        int appMappings = JSONObject.parseObject(instance.fetchText).getIntValue("bigWidth", 50);
        boolean empty = true;
        String grayScope = StringUtils.isNoneBlank(grayName) ? grayName.substring(selectText.length()) : grayName;
        int count = 0;
        LinkedList<JPanel> sort = new LinkedList<>();
        for (Map.Entry<String, Properties> entry : values.entrySet()) {
            String property = entry.getValue().getProperty(selectText);
            boolean gray = false;
            if (property == null) {
                property = entry.getValue().getProperty(grayName);
                if (property == null) {
                    continue;
                }
                gray = true;
            }
            JPanel jPanel = concatKeyValue(entry.getKey(), selectText, gray ? grayScope : "", property, appMappings);
            if (entry.getKey().contains("-pro")) {
                sort.addFirst(jPanel);
            }else{
                sort.addLast(jPanel);
            }
            count++;
            empty = false;
        }

        if (empty) {
            JLabel jTextPane = new JLabel();
            jTextPane.setText("select key is empty");
            jTextPane.setForeground(textColor);
            jTextPane.setFont(songError);
            content.add(jTextPane);
        }else {
            for (JPanel jPanel : sort) {
                content.add(jPanel);
            }
        }
        int row = count / 2 + (count % 2 > 0 ? 1 : 0);

        GridLayout gridLayout = new GridLayout(row, 2, 10, 10);
        content.setLayout(gridLayout);
        JBScrollPane jbScrollPane = new JBScrollPane(content);
        JRootPane root = Objects.requireNonNull(UIUtil.getRootPane(editor.getContentComponent()));
        root.getLayeredPane().add(jbScrollPane);
        if (!new Rectangle(root.getSize()).contains(new Rectangle(content.getPreferredSize()))) {
            gridLayout.setRows(row - 1);
        }
        Balloon balloon = new BalloonPopupBuilder(content)
                .setDialogMode(true)
                .setFillColor(JBUI.CurrentTheme.CustomFrameDecorations.paneBackground())
                .setBorderColor(Color.darkGray)
                .setShadow(true)
                .setAnimationCycle(200)
                .setHideOnClickOutside(true)
                .setHideOnAction(true)
                .setHideOnFrameResize(true)
                .setHideOnCloseClick(true)
                .setHideOnKeyOutside(true)
                .setBlockClicksThroughBalloon(true)
                .setCloseButtonEnabled(false)
                .createBalloon();
        RangeMarker rangeMarker =
                editor.getDocument().createRangeMarker(new TextRange(editor.getSelectionModel().getSelectionStart(),
                        editor.getSelectionModel().getSelectionEnd()));
        balloon.show(new Point(editor, rangeMarker), Balloon.Position.below);

    }
    public JPanel concatKeyValue(String keyText, String selectText, String Scope, String valueText, int bigWith) {
        BorderLayout borderLayout = new BorderLayout();
        JPanel container = new JPanel(borderLayout);

        // container.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        JLabel key = new JLabel();
        key.setForeground(index);
        key.setFont(song);
        key.setText(keyText + Scope);
        JTextArea value = new JTextArea();
        value.setFont(song);
        value.setForeground(textColor);
        value.setAutoscrolls(true);
        value.setText(valueText);
        value.setOpaque(false);
        value.setLineWrap(true);
        value.setEditable(true);
        // value.setWrapStyleWord(true);
        container.add(key, BorderLayout.NORTH);


        if (valueText.length() > 350) {
            JBScrollPane jScrollBar = new JBScrollPane(value);
            // jScrollBar.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            jScrollBar.setPreferredSize(new Dimension(bigWith * 10, 50));
            container.add(jScrollBar, BorderLayout.SOUTH);
        } else {
            JBScrollPane jScrollBar = new JBScrollPane(value);
            // jScrollBar.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            // jScrollBar.setPreferredSize(new Dimension(bigWith * 10, 0));
            container.add(jScrollBar, BorderLayout.SOUTH);
            // container.add(value, BorderLayout.SOUTH);
        }
        return container;
    }
    public static class Point extends PositionTracker<Balloon> {
        private Editor editor;
        private RangeMarker rangeMarker;
        public Point(Editor editor, RangeMarker rangeMarker) {
            super(editor.getContentComponent());
            this.editor = editor;
            this.rangeMarker = rangeMarker;

        }
        @Override
        public RelativePoint recalculateLocation(@NotNull Balloon object) {
            VisualPosition startPosition = editor.offsetToVisualPosition(rangeMarker.getStartOffset(), true, false);
            VisualPosition endPosition = editor.offsetToVisualPosition(rangeMarker.getEndOffset(), false, false);
            java.awt.Point startPoint = editor.visualPositionToXY(startPosition);
            java.awt.Point endPoint = editor.visualPositionToXY(endPosition);

            int lineHeight = editor.getLineHeight();
            int centerX = (int) ((startPoint.x + endPoint.x) * 0.5f);
            int x = Math.min(centerX, endPoint.x);
            int y = endPoint.y + lineHeight;
            Rectangle visibleArea = editor.getScrollingModel().getVisibleArea();
            // java.awt.Point point = new  java.awt.Point(visibleArea.x + visibleArea.width / 3, visibleArea.y +
            // visibleArea.height / 2);
            java.awt.Point point = new java.awt.Point(x, y);
            return new RelativePoint(getComponent(), point);
        }
    }
}
