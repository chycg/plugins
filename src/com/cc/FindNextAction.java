package com.cc;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.project.Project;

/**
 * @author Anna Bulenkova
 */
public class FindNextAction extends AnAction {

    @Override
    public void actionPerformed(final AnActionEvent e) {
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        final Document document = editor.getDocument();
        final SelectionModel selectionModel = editor.getSelectionModel();
        final CaretModel caretModel = editor.getCaretModel();

        if (!selectionModel.hasSelection())
            return;

        final int start = selectionModel.getSelectionStart();
        final int end = selectionModel.getSelectionEnd();
        WriteCommandAction.runWriteCommandAction(project, () -> {
                    String selection = selectionModel.getSelectedText();
                    if (selection == null || selection.isEmpty())
                        return;

                    int index = document.getText().indexOf(selection, end + 1);
                    if (index < 0) {
                        index = document.getText().indexOf(selection, 0);
                    }

                    if (index != start) {
                        selectionModel.removeSelection();
                        selectionModel.setSelection(index, index + selection.length());
                        caretModel.moveToOffset(caretModel.getOffset() + index - start);
                        editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                    }
                }
        );
    }

    @Override
    public void update(final AnActionEvent e) {
        final Project project = e.getProject();
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setVisible((project != null && editor != null && editor.getSelectionModel().hasSelection()));
    }
}