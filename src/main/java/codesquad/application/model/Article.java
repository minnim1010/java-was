package codesquad.application.model;

import java.time.LocalDateTime;

public class Article {

    private int articleId;
    private String title;
    private String content;
    private String imagePath;
    private String userId;
    private LocalDateTime createdAt;

    public Article(String title, String content, String imagePath, String userId) {
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
        this.userId = userId;
    }

    public Article(int articleId, String title, String content, String imagePath, String userId,
                   LocalDateTime createdAt) {
        this.articleId = articleId;
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public int getArticleId() {
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
