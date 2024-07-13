package codesquad.application.model;

import java.util.Objects;

public final class User {
    private final String userId;
    private final String password;
    private final String name;
    private final String email;

    public User(String userId,
                String password,
                String name,
                String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public boolean matchPassword(String password) {
        return this.password.equals(password);
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (User) obj;
        return Objects.equals(this.userId, that.userId) &&
                Objects.equals(this.password, that.password) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, password, name, email);
    }

    @Override
    public String toString() {
        return "User[" +
                "userId=" + userId + ", " +
                "password=" + password + ", " +
                "name=" + name + ", " +
                "email=" + email + ']';
    }

}
