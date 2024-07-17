package codesquad.application.handler;

import static codesquad.http.header.HeaderField.CONTENT_TYPE;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("게시글/댓글 목록 조회 테스트")
class ArticleListHandlerTest extends TestEnvironment {

    private UserRepository userRepository;
    private ArticleRepository articleRepository;
    private CommentRepository commentRepository;
    private ArticleListHandler articleListHandler;

    @BeforeEach
    void setUp() {
        userRepository = ApplicationBeanContainer.getInstance().userRepository();
        articleRepository = ApplicationBeanContainer.getInstance().articleRepository();
        commentRepository = ApplicationBeanContainer.getInstance().commentRepository();
        articleListHandler = new ArticleListHandler(articleRepository, commentRepository);

        commentRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    class 게시글_및_댓글_목록_페이지를_반환한다 {

        @Test
        void 게시글_및_댓글_목록_페이지를_반환할_수_있다() throws URISyntaxException {
            // Given
            userRepository.save(new User("user1", "password", "John Doe", "asdf@dfa.sdf"));
            userRepository.save(new User("user2", "password", "John Doe2", "asdf@dfa.sdfsd"));

            HttpRequest httpRequest = new HttpRequest(
                    HttpMethod.GET,
                    new URI("/"),
                    Collections.emptyMap(),
                    HttpVersion.HTTP_1_1,
                    Collections.emptyMap(),
                    new byte[0]);
            HttpResponse httpResponse = new HttpResponse();

            Article article1 = new Article("Title 1", "Content 1", "user1");
            Article article2 = new Article("Title 2", "Content 2", "user2");
            articleRepository.save(article1);
            articleRepository.save(article2);

            List<Article> articles = articleRepository.findAll();
            Comment comment1 = new Comment("Comment 1", "user1", articles.get(0).getArticleId());
            Comment comment2 = new Comment("Comment 2", "user2", articles.get(0).getArticleId());
            commentRepository.save(comment1);
            commentRepository.save(comment2);

            // When
            articleListHandler.processGet(httpRequest, httpResponse);

            // Then
            assertAll(() -> {
                assertThat(httpResponse.getStatus()).isEqualTo(HttpStatus.OK);
                assertThat(httpResponse.getHeader(CONTENT_TYPE.getFieldName())).isEqualTo("text/html");

                String bodyContent = new String(httpResponse.getBody());
                assertThat(bodyContent).contains("Title 1");
                assertThat(bodyContent).contains("Title 2");
                assertThat(bodyContent).contains("Comment 1");
                assertThat(bodyContent).contains("Comment 2");
                assertThat(bodyContent).contains("user1");
            });
        }
    }
}
