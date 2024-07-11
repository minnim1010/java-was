package codesquad.config;

import codesquad.business.handler.LoginHandler;
import codesquad.business.handler.LogoutHandler;
import codesquad.business.handler.UserHandler;
import codesquad.business.persistence.UserRepository;

public class GlobalBeanContainer {

    private static GlobalBeanContainer instance;

    private final UserRepository userRepository;
    private final UserHandler userRequestHandler;
    private final LoginHandler loginHandler;
    private final LogoutHandler logoutRequestHandler;

    // ----------------------------------------------------- Constructor

    private GlobalBeanContainer() {
        this.userRepository = setUserRepository();
        this.userRequestHandler = setUserProcessor(userRepository);
        this.loginHandler = setLoginProcessor(userRepository);
        this.logoutRequestHandler = setLogoutProcessor();
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

    public UserHandler userRequestHandler() {
        return userRequestHandler;
    }

    public LoginHandler loginRequestHandler() {
        return loginHandler;
    }

    public LogoutHandler logoutRequestHandler() {
        return logoutRequestHandler;
    }

    // ----------------------------------------------------- Setter
    protected UserRepository setUserRepository() {
        return new UserRepository();
    }

    protected UserHandler setUserProcessor(UserRepository userRepository) {
        return new UserHandler(userRepository);
    }

    protected LoginHandler setLoginProcessor(UserRepository userRepository) {
        return new LoginHandler(userRepository);
    }

    protected LogoutHandler setLogoutProcessor() {
        return new LogoutHandler();
    }
}
