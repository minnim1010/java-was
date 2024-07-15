package codesquad.config;

import codesquad.application.handler.ArticleWriteHandler;
import codesquad.application.handler.LoginHandler;
import codesquad.application.handler.LogoutHandler;
import codesquad.application.handler.UserCreateHandler;
import codesquad.application.handler.UserListHandler;
import codesquad.application.infrastructure.JdbcArticleRepository;
import codesquad.application.infrastructure.JdbcUserRepository;
import codesquad.application.persistence.ArticleRepository;
import codesquad.application.persistence.UserRepository;

public class GlobalBeanContainer {

    private static GlobalBeanContainer instance;

    private final UserRepository userRepository;
    private final UserCreateHandler userRequestHandler;
    private final LoginHandler loginHandler;
    private final LogoutHandler logoutRequestHandler;
    private final UserListHandler userListRequestHandler;

    private final ArticleRepository articleRepository;
    private final ArticleWriteHandler articleWriteHandler;

    // ----------------------------------------------------- Constructor

    private GlobalBeanContainer() {
        this.userRepository = setUserRepository();
        this.userRequestHandler = setUserProcessor(userRepository);
        this.loginHandler = setLoginProcessor(userRepository);
        this.logoutRequestHandler = setLogoutProcessor();
        this.userListRequestHandler = setUserListHandler(userRepository);
        this.articleRepository = setArticleRepository();
        this.articleWriteHandler = setArticleWriteHandler(articleRepository);
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

    public ArticleRepository articleRepository() {
        return articleRepository;
    }

    public ArticleWriteHandler articleWriteHandler() {
        return articleWriteHandler;
    }

    // ----------------------------------------------------- Setter
    protected UserRepository setUserRepository() {
        return new JdbcUserRepository();
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

    protected ArticleRepository setArticleRepository() {
        return new JdbcArticleRepository();
    }

    protected ArticleWriteHandler setArticleWriteHandler(ArticleRepository articleRepository) {
        return new ArticleWriteHandler(articleRepository);
    }
}