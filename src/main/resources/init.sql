CREATE TABLE MEMBER
(
    userId   VARCHAR(255),
    password VARCHAR(255),
    name     VARCHAR(255),
    email    VARCHAR(255)
);

CREATE TABLE ARTICLE
(
    articleId VARCHAR(255),
    title     VARCHAR(255),
    content   TEXT,
    imagePath VARCHAR(255),
    createdAt TIMESTAMP,
    userId    VARCHAR(255)
);

CREATE TABLE COMMENT
(
    commentId VARCHAR(255),
    content   TEXT,
    createdAt TIMESTAMP,
    userId    VARCHAR(255),
    articleId VARCHAR(255)
);