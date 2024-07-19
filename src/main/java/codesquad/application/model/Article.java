package codesquad.application.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Article {

    private String articleId;
    private String title;
    private String content;
    private String imagePath;
    private String userId;
    private LocalDateTime createdAt;

    public Article(String title,
                   String content,
                   String imagePath,
                   String userId) {
        this.articleId = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
        this.userId = userId;
    }

    public Article(String articleId,
                   String title,
                   String content,
                   String imagePath,
                   LocalDateTime createdAt,
                   String userId) {
        this.articleId = articleId;
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
        this.createdAt = createdAt;
        this.userId = userId;
    }

    public String getArticleId() {
        return articleId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
