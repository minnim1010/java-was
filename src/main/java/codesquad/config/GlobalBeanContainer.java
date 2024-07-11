package codesquad.config;

import codesquad.business.handler.LoginRequestHandler;
import codesquad.business.handler.LogoutRequestHandler;
import codesquad.business.handler.UserRequestHandler;
import codesquad.business.persistence.UserRepository;

public class GlobalBeanContainer {

    private static GlobalBeanContainer instance;

    private final UserRepository userRepository;
    private final UserRequestHandler userRequestHandler;
    private final LoginRequestHandler loginRequestHandler;
    private final LogoutRequestHandler logoutRequestHandler;

    // ----------------------------------------------------- Constructor

    private GlobalBeanContainer() {
        this.userRepository = setUserRepository();
        this.userRequestHandler = setUserProcessor(userRepository);
        this.loginRequestHandler = setLoginProcessor(userRepository);
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

    public UserRequestHandler userRequestHandler() {
        return userRequestHandler;
    }

    public LoginRequestHandler loginRequestHandler() {
        return loginRequestHandler;
    }

    public LogoutRequestHandler logoutRequestHandler() {
        return logoutRequestHandler;
    }

    // ----------------------------------------------------- Setter
    protected UserRepository setUserRepository() {
        return new UserRepository();
    }

    protected UserRequestHandler setUserProcessor(UserRepository userRepository) {
        return new UserRequestHandler(userRepository);
    }

    protected LoginRequestHandler setLoginProcessor(UserRepository userRepository) {
        return new LoginRequestHandler(userRepository);
    }

    protected LogoutRequestHandler setLogoutProcessor() {
        return new LogoutRequestHandler();
    }
}
