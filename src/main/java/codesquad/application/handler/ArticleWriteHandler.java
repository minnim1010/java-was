package codesquad.application.handler;

import codesquad.application.error.ApplicationException;
import codesquad.application.model.Article;
import codesquad.application.persistence.ArticleRepository;
import codesquad.config.WasConfiguration;
import codesquad.http.handler.AbstractDynamicRequestHandler;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.parser.MultipartFormDataParser.Part;
import codesquad.utils.FileUtils;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ArticleWriteHandler extends AbstractDynamicRequestHandler {

    private final ArticleRepository articleRepository;

    public ArticleWriteHandler(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public void processPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            Part title = httpRequest.getMultipartParts("title");
            Part content = httpRequest.getMultipartParts("content");
            Part image = httpRequest.getMultipartParts("image");
            String imagePath = saveImage(image);

            String userId = httpRequest.getSession().getAttribute("userId");
            Article article = new Article(new String(title.getBody()), new String(content.getBody()), imagePath,
                    userId);
            articleRepository.save(article);

            httpResponse.sendRedirect("/index.html");
        } catch (IOException e) {
            throw new ApplicationException("IO exception occured :", e);
        }
    }

    private String saveImage(Part image) throws IOException {
        if (image == null) {
            return "";
        }

        String filename = image.getFilename();
        String fileExtension = FileUtils.getFileExtension(filename);
        String newFileName =
                WasConfiguration.getInstance().getImagePath() + "/" + UUID.randomUUID() + "." + fileExtension;

        try (FileOutputStream fos = new FileOutputStream(newFileName)) {
            fos.write(image.getBody());
        }

        return newFileName;
    }
}
