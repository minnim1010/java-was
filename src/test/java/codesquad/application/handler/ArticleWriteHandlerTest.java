package codesquad.application.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import codesquad.application.model.Article;
import codesquad.application.model.User;
import codesquad.application.persistence.ArticleRepository;
import codesquad.application.persistence.UserRepository;
import codesquad.config.ApplicationBeanContainer;
import codesquad.environment.TestEnvironment;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.parser.MultipartFormDataParser.Part;
import codesquad.http.property.HttpMethod;
import codesquad.http.property.HttpStatus;
import codesquad.http.property.HttpVersion;
import codesquad.http.session.Session;
import codesquad.http.session.SessionManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("게시글 작성 테스트")
class ArticleWriteHandlerTest extends TestEnvironment {

    private UserRepository userRepository;
    private ArticleRepository articleRepository;
    private ArticleWriteHandler articleWriteHandler;

    @BeforeEach
    void setUp() {
        userRepository = ApplicationBeanContainer.getInstance().userRepository();
        articleRepository = ApplicationBeanContainer.getInstance().articleRepository();
        articleWriteHandler = ApplicationBeanContainer.getInstance().articleWriteHandler();

        articleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    class 게시글을_저장한다 {

        @Test
        void 게시글을_성공적으로_저장하고_리다이렉트한다() throws URISyntaxException {
            // Given
            userRepository.save(new User("user1", "password", "John Doe", "asdf@dfa.sdf"));

            HttpRequest httpRequest = new HttpRequest(
                    HttpMethod.POST,
                    new URI("/article/write"),
                    Collections.emptyMap(),
                    HttpVersion.HTTP_1_1,
                    Map.of("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW"),
                    new byte[0],
                    Map.of("title", new Part("title", null, "Sample Title".getBytes(), null),
                            "content", new Part("content", null, "Sample Content".getBytes(), null)));
            HttpResponse httpResponse = new HttpResponse();
            Session session = SessionManager.getInstance().createSession();
            session.setAttribute("userId", "user1");
            httpRequest.setSession(session);

            // When
            articleWriteHandler.processPost(httpRequest, httpResponse);

            // Then
            assertAll(() -> {
                assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.FOUND);

                List<Article> all = articleRepository.findAll();
                assertThat(all).hasSize(1);
                Article article = all.get(0);
                assertThat(article).isNotNull();
                assertThat(article.getTitle()).isEqualTo("Sample Title");
                assertThat(article.getContent()).isEqualTo("Sample Content");
                assertThat(article.getUserId()).isEqualTo("user1");
            });
        }
    }
}
