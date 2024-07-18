package codesquad.application.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Comment {

    private String commentId;
    private String content;
    private LocalDateTime createdAt;
    private String userId;
    private String articleId;

    public Comment(String content,
                   String userId,
                   String articleId) {
        this.commentId = UUID.randomUUID().toString();
        this.content = content;
        this.userId = userId;
        this.articleId = articleId;
    }

    public Comment(String commentId,
                   String content,
                   LocalDateTime createdAt,
                   String userId,
                   String articleId) {
        this.commentId = commentId;
        this.content = content;
        this.createdAt = createdAt;
        this.userId = userId;
        this.articleId = articleId;
    }

    public String getCommentId() {
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

    public String getArticleId() {
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
