package codesquad.business.model;

public record User(String userId,
                   String password,
                   String name,
                   String email) {
}
