package net.physalis.jpa;

import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LazyFetchIteratorTest {

    @Test
    public void noResult() {
        EntityManager em = mock(EntityManager.class);
        @SuppressWarnings("unchecked")
        TypedQuery<Integer> query = mock(TypedQuery.class);
        LazyFetchIterator<Integer> iterator = new LazyFetchIterator<Integer>(em, query, 0, 3);

        assertThat(iterator.hasNext(), is(false));
        try {
            iterator.next();
            fail();
        } catch (NoSuchElementException e) {
        }
    }

    @Test
    public void lessThanPageSize() {
        EntityManager em = mock(EntityManager.class);

        @SuppressWarnings("unchecked")
        TypedQuery<Integer> query = mock(TypedQuery.class);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(3)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(0, 1));

        LazyFetchIterator<Integer> iterator = new LazyFetchIterator<Integer>(em, query, 2, 3);

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(0));

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(1));

        assertThat(iterator.hasNext(), is(false));
        try {
            iterator.next();
            fail();
        } catch (NoSuchElementException e) {
        }
    }

    @Test
    public void sameAsPageSize() {
        EntityManager em = mock(EntityManager.class);

        @SuppressWarnings("unchecked")
        TypedQuery<Integer> query = mock(TypedQuery.class);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(3)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(0, 1, 2, 3));

        LazyFetchIterator<Integer> iterator = new LazyFetchIterator<Integer>(em, query, 3, 3);

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(0));

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(1));

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(2));

        assertThat(iterator.hasNext(), is(false));
        try {
            iterator.next();
            fail();
        } catch (NoSuchElementException e) {
        }
    }

    @Test
    public void pagination() {
        EntityManager em = mock(EntityManager.class);

        @SuppressWarnings("unchecked")
        TypedQuery<Integer> query = mock(TypedQuery.class);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(3)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(0, 1, 2));

        LazyFetchIterator<Integer> iterator = new LazyFetchIterator<Integer>(em, query, 4, 3);

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(0));

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(1));

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(2));

        when(query.setFirstResult(3)).thenReturn(query);
        when(query.setMaxResults(3)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(3));

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(3));

        assertThat(iterator.hasNext(), is(false));
        try {
            iterator.next();
            fail();
        } catch (NoSuchElementException e) {
        }
    }
}
