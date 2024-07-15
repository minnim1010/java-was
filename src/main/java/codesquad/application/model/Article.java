package codesquad.application.model;

import java.time.LocalDateTime;

public class Article {

    private int articleId;
    private String title;
    private String content;
    private String userId;
    private LocalDateTime createdAt;

    public Article(int articleId, String title, String content, String userId, LocalDateTime createdAt) {
        this.articleId = articleId;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public Article(String title, String content, String userId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
