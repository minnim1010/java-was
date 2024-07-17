package codesquad.application.dto;

import java.util.Objects;

public final class CommentResponse {

    private final int commentId;
    private final String content;
    private final String userId;

    public CommentResponse(int commentId,
                           String content,
                           String userId) {
        this.commentId = commentId;
        this.content = content;
        this.userId = userId;
    }

    public int getCommentId() {
        return commentId;
    }

    public String getContent() {
        return content;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (CommentResponse) obj;
        return Objects.equals(this.content, that.content) &&
                Objects.equals(this.userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, userId);
    }

    @Override
    public String toString() {
        return "CommentResponse[" +
                "content=" + content + ", " +
                "userId=" + userId + ']';
    }
}
