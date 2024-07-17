package codesquad.application.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import codesquad.application.model.Article;
import codesquad.application.model.Comment;
import codesquad.application.model.User;
import codesquad.application.persistence.ArticleRepository;
import codesquad.application.persistence.CommentRepository;
import codesquad.application.persistence.UserRepository;
import codesquad.config.ApplicationBeanContainer;
import codesquad.environment.TestEnvironment;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpMethod;
import codesquad.http.property.HttpStatus;
import codesquad.http.property.HttpVersion;
import codesquad.http.session.Session;
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
@DisplayName("댓글 작성 테스트")
class CommentWriteHandlerTest extends TestEnvironment {

    private CommentRepository commentRepository;
    private CommentWriteHandler commentWriteHandler;
    private UserRepository userRepository;
    private ArticleRepository articleRepository;

    @BeforeEach
    void setUp() {
        commentRepository = ApplicationBeanContainer.getInstance().commentRepository();
        userRepository = ApplicationBeanContainer.getInstance().userRepository();
        articleRepository = ApplicationBeanContainer.getInstance().articleRepository();
        commentWriteHandler = new CommentWriteHandler(commentRepository);

        commentRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    class 댓글을_작성한다 {

        @Test
        void 댓글을_성공적으로_저장하고_리다이렉트한다() throws URISyntaxException {
            // Given
            userRepository.save(new User("user1", "password", "John Doe", "email@email.com"));
            articleRepository.save(new Article("Sample Title", "Sample Content", null, "user1"));
            Article article = articleRepository.findAll().get(0);

            HttpRequest httpRequest = new HttpRequest(
                    HttpMethod.POST,
                    new URI("/comment/write"),
                    Map.of("content", "Sample Comment Content", "articleId", String.valueOf(article.getArticleId())),
                    HttpVersion.HTTP_1_1,
                    Collections.emptyMap(),
                    new byte[0]);
            HttpResponse httpResponse = new HttpResponse();
            Session session = new Session(3600L, "session-id");
            session.setAttribute("userId", "user1");
            httpRequest.setSession(session);

            // When
            commentWriteHandler.processPost(httpRequest, httpResponse);

            // Then
            assertAll(() -> {
                assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.FOUND);
                assertThat(httpResponse.getHeader("Location")).isEqualTo("/index.html");

                List<Comment> comments = commentRepository.findAllByArticleId(article.getArticleId());
                assertThat(comments).hasSize(1);
                Comment savedComment = comments.get(0);
                assertThat(savedComment).isNotNull();
                assertThat(savedComment.getContent()).isEqualTo("Sample Comment Content");
                assertThat(savedComment.getUserId()).isEqualTo("user1");
            });
        }
    }
}
