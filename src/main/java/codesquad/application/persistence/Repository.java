package codesquad.application.persistence;

import java.util.List;
import java.util.Optional;

public interface Repository<T, I> {

    void save(T t);

    Optional<T> findById(I id);

    List<T> findAll();

    void deleteAll();
}
