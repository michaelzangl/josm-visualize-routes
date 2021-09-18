package org.openstreetmap.josm.plugins.visualizeroutes.gui.utils;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;
import java.util.stream.Stream;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeListener;

import org.openstreetmap.josm.gui.dialogs.relation.RelationEditor;
import org.openstreetmap.josm.gui.dialogs.relation.actions.IRelationEditorActionAccess;

/**
 * Allows adding / removing one tab from the Relation window.
 */
public abstract class AbstractTabManager<T extends Component> {
    private final JTabbedPane tabPanel;
    private final EnhancedRelationEditorAccess editorAccess;
    private JScrollPane tabContent = null;
    private ChangeListener tabListener;
    private Point lastScroll;

    public AbstractTabManager(EnhancedRelationEditorAccess editorAccess) {
        JDialog dialog = (JDialog) editorAccess.getEditor();
        Container editorComponent = dialog.getContentPane();

        this.tabPanel = Stream.of(editorComponent.getComponents())
            .filter(it -> it instanceof JTabbedPane)
            .map(it -> (JTabbedPane) it)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Relation editor did not have a tab panel"));

        this.editorAccess = editorAccess;

        updateTab();
        editorAccess.addChangeListener(__ -> updateTab());
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                possiblyDispose();
            }
        });
    }

    private void updateTab() {
        TabAndDisplay<T> toShow = getTabToShow(editorAccess);

        if (toShow.shouldDisplay()) {
            if (tabContent == null) {
                tabContent = new JScrollPane();
                tabContent.getVerticalScrollBar().setUnitIncrement(20);
                tabContent.getHorizontalScrollBar().setUnitIncrement(20);
                tabPanel.add(toShow.getTitle(), tabContent);
                tabListener = e -> {
                    showIfVisible(toShow);
                };
                tabPanel.addChangeListener(tabListener);
            }
            // This makes adding the component lazy => complex layouts don't need to be computed immediately
            showIfVisible(toShow);
        } else {
            if (tabContent != null) {
                tabPanel.remove(tabContent);
                tabPanel.removeChangeListener(tabListener);
                tabContent = null;
            }
        }
    }

    /**
     * If the user navigated to the current tab, add the tab content to the tab panel
     * @param toShow The tab to show
     */
    private void showIfVisible(TabAndDisplay<T> toShow) {
        if (tabPanel.getSelectedComponent() == tabContent) {
            var viewport = this.tabContent.getViewport();
            if (viewport.getView() == null) {
                T newContent = toShow.getTabContent();
                Objects.requireNonNull(newContent, "newContent");
                viewport.setView(newContent);

                if (lastScroll != null) {
                    this.tabContent.getHorizontalScrollBar().setValue(lastScroll.x);
                    this.tabContent.getVerticalScrollBar().setValue(lastScroll.y);
                    lastScroll = null;
                }
            } else {
                update((T) viewport.getView());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void possiblyDispose() {
        if (this.tabContent != null) {
            JViewport viewport = this.tabContent.getViewport();
            if (viewport.getView() != null) {
                lastScroll = new Point(
                        this.tabContent.getHorizontalScrollBar().getValue(),
                        this.tabContent.getVerticalScrollBar().getValue());
                dispose((T) viewport.getView());
                viewport.setView(null);
            }
        }
    }

    /**
     * Called when the tab logic detected changes to the current relation but did re-use the component.
     * @param view The current content.
     */
    protected void update(T view) {
        // Nop
    }

    /**
     * Called when the tab logic removes the tab or when the window is closed.
     * @param view The view that was just removed
     */
    protected void dispose(T view) {
        // Nop
    }

    /**
     * Get the information about the tab that is managed by this tab manager
     * @param editorAccess The editor access of the current relation editor
     * @return The tab and wether it should be displayed
     */
    protected abstract TabAndDisplay<T> getTabToShow(EnhancedRelationEditorAccess editorAccess);

    public interface TabAndDisplay<T extends Component> {
        boolean shouldDisplay();
        T getTabContent();
        String getTitle();
    }
}
