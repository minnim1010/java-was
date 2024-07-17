package codesquad.application.model;

import java.time.LocalDateTime;

public class Comment {

    private int commentId;
    private String content;
    private LocalDateTime createdAt;
    private String userId;
    private int articleId;

    public Comment(String content, String userId, int articleId) {
        this.content = content;
        this.userId = userId;
        this.articleId = articleId;
    }

    public Comment(int commentId, String content, LocalDateTime createdAt, String userId, int articleId) {
        this.commentId = commentId;
        this.content = content;
        this.createdAt = createdAt;
        this.userId = userId;
        this.articleId = articleId;
    }

    public int getCommentId() {
        return commentId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public int getArticleId() {
        return articleId;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", userId='" + userId + '\'' +
                ", articleId=" + articleId +
                '}';
    }
}
