package codesquad.business.persistence;

import codesquad.business.model.User;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {

    private static final Map<Integer, User> repository = new ConcurrentHashMap<>();

    public void save(User user) {
        repository.put(user.userId().hashCode(), user);
    }

    public User findById(int userId) {
        return repository.get(userId);
    }

    public void delete(int userId) {
        repository.remove(userId);
    }
}
