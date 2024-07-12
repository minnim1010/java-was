package codesquad.config;

import codesquad.business.handler.LoginHandler;
import codesquad.business.handler.LogoutHandler;
import codesquad.business.handler.UserCreateHandler;
import codesquad.business.handler.UserListHandler;
import codesquad.business.persistence.UserRepository;

public class GlobalBeanContainer {

    private static GlobalBeanContainer instance;

    private final UserRepository userRepository;
    private final UserCreateHandler userRequestHandler;
    private final LoginHandler loginHandler;
    private final LogoutHandler logoutRequestHandler;
    private final UserListHandler userListRequestHandler;

    // ----------------------------------------------------- Constructor

    private GlobalBeanContainer() {
        this.userRepository = setUserRepository();
        this.userRequestHandler = setUserProcessor(userRepository);
        this.loginHandler = setLoginProcessor(userRepository);
        this.logoutRequestHandler = setLogoutProcessor();
        this.userListRequestHandler = setUserListHandler(userRepository);
    }

    public static GlobalBeanContainer getInstance() {

        if (instance == null) {
            instance = new GlobalBeanContainer();
        }
        return instance;
    }

    // ----------------------------------------------------- Getter
    public UserRepository userRepository() {
        return userRepository;
    }

    public UserCreateHandler userRequestHandler() {
        return userRequestHandler;
    }

    public LoginHandler loginRequestHandler() {
        return loginHandler;
    }

    public LogoutHandler logoutRequestHandler() {
        return logoutRequestHandler;
    }

    public UserListHandler userListRequestHandler() {
        return userListRequestHandler;
    }

    // ----------------------------------------------------- Setter
    protected UserRepository setUserRepository() {
        return new UserRepository();
    }

    protected UserCreateHandler setUserProcessor(UserRepository userRepository) {
        return new UserCreateHandler(userRepository);
    }

    protected LoginHandler setLoginProcessor(UserRepository userRepository) {
        return new LoginHandler(userRepository);
    }

    protected LogoutHandler setLogoutProcessor() {
        return new LogoutHandler();
    }

    protected UserListHandler setUserListHandler(UserRepository userRepository) {
        return new UserListHandler(userRepository);
    }
}
