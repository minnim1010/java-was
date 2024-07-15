package codesquad.application.model;

public class Article {
    private int articleId;
    private String title;
    private String content;
    private String userId;

    public Article(int articleId, String title, String content, String userId) {
        this.articleId = articleId;
        this.title = title;
        this.content = content;
        this.userId = userId;
    }

    public Article(String title, String content, String userId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
    }

    // Getters and Setters
    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
