package org.openstreetmap.josm.plugins.visualizeroutes.gui.utils;

import org.openstreetmap.josm.gui.dialogs.relation.IRelationEditor;
import org.openstreetmap.josm.gui.dialogs.relation.MemberTable;
import org.openstreetmap.josm.gui.dialogs.relation.MemberTableModel;
import org.openstreetmap.josm.gui.dialogs.relation.RelationEditor;
import org.openstreetmap.josm.gui.dialogs.relation.SelectionTable;
import org.openstreetmap.josm.gui.dialogs.relation.SelectionTableModel;
import org.openstreetmap.josm.gui.dialogs.relation.actions.IRelationEditorActionAccess;
import org.openstreetmap.josm.gui.tagging.TagEditorModel;
import org.openstreetmap.josm.gui.tagging.ac.AutoCompletingTextField;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EnhancedRelationEditorAccess implements IRelationEditorActionAccess {

    private final IRelationEditorActionAccess relationEditorActionAccess;
    private final List<RelationEditorChangeListener> relationEditorChangeListeners = new CopyOnWriteArrayList<>();

    public EnhancedRelationEditorAccess(IRelationEditorActionAccess relationEditorActionAccess) {
        this.relationEditorActionAccess = relationEditorActionAccess;

        JDialog dialog = (JDialog) relationEditorActionAccess.getEditor();
        dialog.getContentPane().addPropertyChangeListener(RelationEditor.RELATION_PROP, __ -> fireChange());
        relationEditorActionAccess.getMemberTableModel().addTableModelListener(__ -> fireChange());
        relationEditorActionAccess.getTagModel().addPropertyChangeListener(__ -> fireChange());
        relationEditorActionAccess.getTagModel().addTableModelListener(__ -> fireChange());
    }

    private void fireChange() {
        relationEditorChangeListeners.forEach(it -> it.relationEditorChanged(new RelationEditorChangeEvent()));
    }

    @Override
    public void addMemberTableAction(String actionMapKey, Action action) {
        relationEditorActionAccess.addMemberTableAction(actionMapKey, action);
    }

    @Override
    public MemberTable getMemberTable() {
        return relationEditorActionAccess.getMemberTable();
    }

    @Override
    public MemberTableModel getMemberTableModel() {
        return relationEditorActionAccess.getMemberTableModel();
    }

    @Override
    public SelectionTable getSelectionTable() {
        return relationEditorActionAccess.getSelectionTable();
    }

    @Override
    public SelectionTableModel getSelectionTableModel() {
        return relationEditorActionAccess.getSelectionTableModel();
    }

    @Override
    public IRelationEditor getEditor() {
        return relationEditorActionAccess.getEditor();
    }

    @Override
    public TagEditorModel getTagModel() {
        return relationEditorActionAccess.getTagModel();
    }

    @Override
    public AutoCompletingTextField getTextFieldRole() {
        return relationEditorActionAccess.getTextFieldRole();
    }

    public void addChangeListener(RelationEditorChangeListener listener) {
        relationEditorChangeListeners.add(listener);
    }

    public void removeChangeListener(RelationEditorChangeListener listener) {
        relationEditorChangeListeners.remove(listener);
    }
}
