package codesquad.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public final class ArticleCommentResponse {

    private final String articleId;
    private final String title;
    private final String content;
    private final String userId;
    private final LocalDateTime createdAt;
    private final String imagePath;
    private final List<CommentResponse> comments;

    public ArticleCommentResponse(String articleId,
                                  String title,
                                  String content,
                                  String userId,
                                  LocalDateTime createdAt,
                                  String imagePath,
                                  List<CommentResponse> comments) {
        this.articleId = articleId;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.createdAt = createdAt;
        this.imagePath = imagePath;
        this.comments = comments;
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

    public String getUserId() {
        return userId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<CommentResponse> getComments() {
        return comments;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (ArticleCommentResponse) obj;
        return this.articleId == that.articleId &&
                Objects.equals(this.title, that.title) &&
                Objects.equals(this.content, that.content) &&
                Objects.equals(this.userId, that.userId) &&
                Objects.equals(this.createdAt, that.createdAt) &&
                Objects.equals(this.comments, that.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleId, title, content, userId, createdAt, comments);
    }

    @Override
    public String toString() {
        return "ArticleComment[" +
                "articleId=" + articleId + ", " +
                "title=" + title + ", " +
                "content=" + content + ", " +
                "userId=" + userId + ", " +
                "createdAt=" + createdAt + ", " +
                "comments=" + comments + ']';
    }
}
