package net.physalis.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterator which fetches rows from DB on demand.
 */
public class LazyFetchIterator<T> implements Iterator<T> {

    private static final Logger LOG = LoggerFactory.getLogger(LazyFetchIterator.class);

    private final EntityManager em;

    private final TypedQuery<T> query;

    private final long totalSize;

    private final int pageSize;

    private int baseIndex;

    private List<T> buffer;

    private int bufferIndex;

    /**
     * @param em EntityManager which is used to control entity cache in PersistenceContext
     * @param query used to fetch next data set. must have order by clause.
     * @param totalSize record count which the argument query will return
     * @param pageSize record count fetched by a query
     */
    public LazyFetchIterator(EntityManager em, TypedQuery<T> query, long totalSize, int pageSize) {
        this.em = em;
        this.query = query;
        this.totalSize = totalSize;
        this.pageSize = pageSize;
    }

    @Override
    public boolean hasNext() {
        return baseIndex + bufferIndex < totalSize;
    }

    @Override
    public T next() {
        if (hasNext()) {
            if (buffer == null || bufferIndex >= buffer.size()) {
                fetchNext();
            }
            if (bufferIndex < buffer.size()) {
                return getNext();
            }
        }
        throw new NoSuchElementException();
    }

    private void fetchNext() {
        em.flush();
        em.clear();
        baseIndex += buffer == null ? 0 : buffer.size();
        buffer = query.setFirstResult(baseIndex).setMaxResults(pageSize).getResultList();
        bufferIndex = 0;
    }

    private T getNext() {
        return buffer.get(bufferIndex++);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
