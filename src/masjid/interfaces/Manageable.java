package masjid.interfaces;

import java.util.ArrayList;

/**
 * Manageable interface - demonstrates Interface OOP concept.
 * Any manageable entity must support CRUD operations.
 *
 * @param <T> the type of entity being managed
 */
public interface Manageable<T> {
    void add(T item);
    void update(String id, T item);
    void delete(String id);
    T search(String id);
    ArrayList<T> getAll();
    ArrayList<T> searchByKeyword(String keyword);
}
