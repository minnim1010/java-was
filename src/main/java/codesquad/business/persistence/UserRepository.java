package codesquad.business.persistence;

import codesquad.business.model.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {

    private static final Map<String, User> repository = new ConcurrentHashMap<>();

    public void save(User user) {
        repository.put(user.getUserId(), user);
    }

    public Optional<User> findById(String userId) {
        return Optional.ofNullable(repository.get(userId));
    }

    public List<User> findAll() {
        return List.copyOf(repository.values());
    }

    public void delete(String userId) {
        repository.remove(userId);
    }
}
