package org.openstreetmap.josm.plugins.visualizeroutes.gui.linear;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.osm.Tagged;
import org.openstreetmap.josm.gui.dialogs.relation.actions.IRelationEditorActionAccess;
import org.openstreetmap.josm.gui.tagging.TagModel;

/**
 * Allows reading from an existing relation or the relation editor.
 */
public interface RelationAccess extends Tagged {
    default String get(String key) {
        return getKeys().get(key);
    }

    List<RelationMember> getMembers();

    @Override
    default void setKeys(Map<String, String> keys) {
        throw new UnsupportedOperationException("Read only");
    }

    @Override
    default void put(String key, String value) {
        throw new UnsupportedOperationException("Read only");
    }

    @Override
    default void remove(String key) {
        throw new UnsupportedOperationException("Read only");
    }

    @Override
    default boolean hasKeys() {
        return getNumKeys() > 0;
    }

    @Override
    default Collection<String> keySet() {
        return getKeys().keySet();
    }

    @Override
    default int getNumKeys() {
        return getKeys().size();
    }

    @Override
    default void removeAll() {
        throw new UnsupportedOperationException("Read only");
    }

    static RelationAccess of(Relation relation) {
        return new RelationAccess() {
            @Override
            public Map<String, String> getKeys() {
                return relation.getKeys();
            }

            @Override
            public String get(String key) {
                return relation.get(key);
            }

            @Override
            public List<RelationMember> getMembers() {
                return relation.getMembers();
            }

            @Override
            public Relation getRelation() {
                return relation;
            }

            @Override
            public long getRelationId() {
                return relation.getId();
            }
        };
    }
    static RelationAccess of(IRelationEditorActionAccess editor) {
        return new RelationAccess() {
            @Override
            public String get(String key) {
                TagModel tagModel = editor.getTagModel().get(key);
                return tagModel == null ? null : tagModel.getValue();
            }

            @Override
            public Map<String, String> getKeys() {
                return editor.getTagModel().getTags();
            }

            @Override
            public List<RelationMember> getMembers() {
                return RelationEditorAccessUtils.streamMembers(editor)
                    .collect(Collectors.toList());
            }

            @Override
            public long getRelationId() {
                return editor.getEditor().getRelation() != null ? editor.getEditor().getRelation().getId() : -1;
            }
        };
    }

    default Relation getRelation() {
        return null;
    }

    default long getRelationId() {
        return -1;
    }

    default boolean hasTag(String key, String value) {
        return value.equals(get(key));
    }

    default boolean isMultipolygon() {
        return hasTag("type", "multipolygon");
    }
}
