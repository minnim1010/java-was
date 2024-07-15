package codesquad.application.persistence;

import codesquad.application.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    void save(User user);

    Optional<User> findById(String userId);

    List<User> findAll();

    void delete(String userId);

    void deleteAll();
}
