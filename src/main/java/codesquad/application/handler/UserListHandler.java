package codesquad.application.handler;

import static codesquad.http.header.HeaderField.CONTENT_TYPE;

import codesquad.application.model.User;
import codesquad.application.persistence.UserRepository;
import codesquad.http.handler.AbstractDynamicRequestHandler;
import codesquad.http.message.HttpRequest;
import codesquad.http.message.HttpResponse;
import codesquad.http.property.HttpStatus;
import codesquad.template.TemplateContext;
import codesquad.template.TemplateEngine;
import java.util.List;

public class UserListHandler extends AbstractDynamicRequestHandler {

    private final UserRepository userRepository;

    public UserListHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void processGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        List<User> users = userRepository.findAll();

        TemplateContext templateContext = new TemplateContext();
        templateContext.setValue("user", httpRequest.getSession().getAttribute("userId"));
        templateContext.setValue("userList", users);

        String templatedFileContent = TemplateEngine.getInstance().render("/user_list.html", templateContext);

        httpResponse.setBody(templatedFileContent.getBytes());
        httpResponse.setHeader(CONTENT_TYPE.getFieldName(), "text/html");
        httpResponse.setStatus(HttpStatus.OK);
    }
}
