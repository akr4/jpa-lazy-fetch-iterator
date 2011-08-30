Iterator which fetches rows from DB on demand manner.

Example
=======

```java
@PersistenceContext
private EntityManager em;

public Iterable<Employee> getAllEmployees() {
    return new Iterable<Employee>() {
        @Override public Iterator<Employee> iterator() {
            return new LazyFetchIterator<Employee>(
                em,
                em.createQuery("select e from Employee e order by e.id", Employee.class),
                em.createQuery("select count(e) from Employee e", Long.class).getSingleResult(),
                1000);
        }
    };
}
```