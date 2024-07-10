package codesquad.config;

import codesquad.business.persistence.UserRepository;
import codesquad.business.processor.LoginProcessor;
import codesquad.business.processor.UserProcessor;
import codesquad.http.handler.RequestHandler;
import codesquad.http.session.SessionIdGenerator;
import codesquad.http.session.SessionManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalBeanContainer {

    private static GlobalBeanContainer instance;
    private final SessionManager sessionManager;

    // ----------------------------------------------------- Session
    private final SessionIdGenerator sessionIdGenerator;
    private final UserRepository userRepository;

    // ----------------------------------------------------- Business
    private final UserProcessor userProcessor;
    private final LoginProcessor loginProcessor;

    private GlobalBeanContainer(GlobalConstants globalConstants) {
        this.sessionIdGenerator = setSessionIdGenerator();
        this.sessionManager = setSessionManager(globalConstants, sessionIdGenerator);

        this.userRepository = setUserRepository();
        this.userProcessor = setUserProcessor(userRepository);
        this.loginProcessor = setLoginProcessor(userRepository);
    }

    // ----------------------------------------------------- Constructor

    public static GlobalBeanContainer getInstance() {
        if (instance == null) {
            instance = new GlobalBeanContainer(GlobalConstants.getInstance());
        }
        return instance;
    }

    // ----------------------------------------------------- Getter

    public SessionManager sessionManager() {
        return sessionManager;
    }

    public SessionIdGenerator sessionIdGenerator() {
        return sessionIdGenerator;
    }

    public UserRepository userRepository() {
        return userRepository;
    }

    public UserProcessor userProcessor() {
        return userProcessor;
    }

    public LoginProcessor loginProcessor() {
        return loginProcessor;
    }

    // ----------------------------------------------------- Setter

    protected SessionManager setSessionManager(GlobalConstants sessionConfig,
                                               SessionIdGenerator sessionIdGenerator) {
        return new SessionManager(sessionConfig, sessionIdGenerator);
    }

    protected SessionIdGenerator setSessionIdGenerator() {
        return new SessionIdGenerator();
    }

    protected UserRepository setUserRepository() {
        return new UserRepository();
    }

    protected UserProcessor setUserProcessor(UserRepository userRepository) {
        return new UserProcessor(userRepository);
    }

    protected LoginProcessor setLoginProcessor(UserRepository userRepository) {
        return new LoginProcessor(userRepository);
    }

    protected Map<String, RequestHandler> setRequestHandlerMap(List<String> path,
                                                               List<RequestHandler> requestHandlers) {
        Map<String, RequestHandler> requestHandlerMap = new HashMap<>();
        for (int i = 0; i < path.size(); ++i) {
            requestHandlerMap.put(path.get(i), requestHandlers.get(i));
        }

        return requestHandlerMap;
    }
}
