package com.cts.process;


import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class SimpleFoldingBuilder extends FoldingBuilderEx implements DumbAware {

  @Override
  public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root,
          @NotNull Document document,
          boolean quick) {
    // Initialize the group of folding regions that will expand/collapse together.
    FoldingGroup group = FoldingGroup.newGroup("ConfigurationFunc");
    // Initialize the list of folding regions
    List<FoldingDescriptor> descriptors = new ArrayList<>();
    root.accept(new PsiRecursiveElementWalkingVisitor() {
      @Override
      public void visitElement(@NotNull PsiElement element) {
        super.visitElement(element);
        // if (element instanceof CompositePsiElement) {
          String debugName = element.getNode().getElementType().getDebugName();
          if (debugName.equals("EXPRESSION_STATEMENT")) {
            String contain = element.getText();

            if (contain.startsWith("Configfunction.")) {
              int leftBracket = contain.indexOf("\"", contain.lastIndexOf("Configfunction."));
              int rightBracket = contain.indexOf("\"", leftBracket + 1);
              int startIndex = leftBracket;
              int endIndex = 0;
              while (true) {
                // 第一个逗号
                int containDot = contain.indexOf(",", leftBracket + 1);
                // 第一个结束符。
                int containRightBracket = contain.indexOf(");", leftBracket + 1);
                if (containDot != -1) {
                  endIndex = containDot - 1;
                  break;
                }
                if (containRightBracket != -1) {
                  endIndex = containRightBracket -1;
                  break;
                }
              }
              String key = contain.substring(leftBracket, endIndex);
              descriptors.add(new FoldingDescriptor(element.getNode(),
                      new TextRange(element.getTextOffset() + leftBracket,
                              element.getTextOffset() + endIndex),
                      group));
            }
          }
        // }
      }

      @Override
      public void visitComment(@NotNull PsiComment comment) {
        this.visitElement(comment);
      }

    });
    // root.accept(new JavaRecursiveElementWalkingVisitor() {
    //
    //   @Override
    //   public void visitLiteralExpression(@NotNull PsiLiteralExpression literalExpression) {
    //     super.visitLiteralExpression(literalExpression);
    //
    //     String value = PsiLiteralUtil.getStringLiteralContent(literalExpression);
    //     if (value != null &&
    //             value.startsWith(SimpleAnnotator.SIMPLE_PREFIX_STR + SimpleAnnotator.SIMPLE_SEPARATOR_STR)) {
    //       Project project = literalExpression.getProject();
    //       String key = value.substring(
    //               SimpleAnnotator.SIMPLE_PREFIX_STR.length() + SimpleAnnotator.SIMPLE_SEPARATOR_STR.length()
    //       );
    //       // find SimpleProperty for the given key in the project
    //       SimpleProperty simpleProperty = ContainerUtil.getOnlyItem(SimpleUtil.findProperties(project, key));
    //       if (simpleProperty != null) {
    //         // Add a folding descriptor for the literal expression at this node.
    //         descriptors.add(new FoldingDescriptor(literalExpression.getNode(),
    //                 new TextRange(literalExpression.getTextRange().getStartOffset() + 1,
    //                         literalExpression.getTextRange().getEndOffset() - 1),
    //                 group, Collections.singleton(simpleProperty)));
    //       }
    //     }
    //   }
    // });

    return descriptors.toArray(FoldingDescriptor.EMPTY);
  }

  /**
   * Gets the Simple Language 'value' string corresponding to the 'key'
   *
   * @param node Node corresponding to PsiLiteralExpression containing a string in the format
   *             SIMPLE_PREFIX_STR + SIMPLE_SEPARATOR_STR + Key, where Key is
   *             defined by the Simple language file.
   */
  @Nullable
  @Override
  public String getPlaceholderText(@NotNull ASTNode node) {

    return "test";
  }

  @Override
  public boolean isCollapsedByDefault(@NotNull ASTNode node) {
    return true;
  }

}