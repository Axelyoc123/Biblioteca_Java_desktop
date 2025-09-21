// src/main/java/app/model/ComboItem.java
package app.model;

public class ComboItem {
    private final int id;
    private final String label;

    public ComboItem(int id, String label) {
        this.id = id;
        this.label = label;
    }
    public int getId() { return id; }
    public String getLabel() { return label; }

    @Override public String toString() {
        return label; // así el combo muestra el nombre
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComboItem)) return false;
        return id == ((ComboItem) o).id;
    }

    @Override public int hashCode() { return Integer.hashCode(id); }
}
