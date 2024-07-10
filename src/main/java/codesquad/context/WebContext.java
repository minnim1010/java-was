package codesquad.context;

import codesquad.config.GlobalBeanContainer;
import codesquad.http.handler.RequestHandler;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebContext {

    private static final Logger log = LoggerFactory.getLogger(WebContext.class);

    private final Map<String, RequestHandler> requestHandlerMap;
    private final Set<String> staticResourcePaths;
    private final Set<String> defaultPages;

    public WebContext() {
        GlobalBeanContainer globalBeanContainer = GlobalBeanContainer.getInstance();
        this.requestHandlerMap = setRequestHandlerMap(
                List.of("/user/create", "/user/login"),
                List.of(globalBeanContainer.userRequestHandler(), globalBeanContainer.loginRequestHandler()));
        this.staticResourcePaths = setStaticResourcePaths("static");
        this.defaultPages = setDefaultPages();
    }

    protected Map<String, RequestHandler> setRequestHandlerMap(List<String> path,
                                                               List<RequestHandler> requestHandlers) {
        Map<String, RequestHandler> mapper = new HashMap<>();
        for (int i = 0; i < path.size(); ++i) {
            mapper.put(path.get(i), requestHandlers.get(i));
        }

        return mapper;
    }

    protected Set<String> setStaticResourcePaths(String directoryPath) {
        return loadAllFilePathsFromDirectory(directoryPath);
    }

    private Set<String> loadAllFilePathsFromDirectory(String directoryPath) {
        try {
            Set<String> filePaths = new HashSet<>();

            ClassLoader classLoader = getClass().getClassLoader();
            URL directoryURL = classLoader.getResource(directoryPath);

            if (directoryURL == null) {
                throw new IOException("Directory not found: " + directoryPath);
            }

            if (directoryURL.getProtocol().equals("jar")) {
                JarURLConnection jarConnection = (JarURLConnection) directoryURL.openConnection();
                try (JarFile jarFile = jarConnection.getJarFile()) {
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String entryName = entry.getName();
                        if (entryName.startsWith(directoryPath) && !entry.isDirectory()) {
                            String entryPath = entryName.substring(directoryPath.length());
                            filePaths.add(entryPath);
                        }
                    }
                }
            } else if (directoryURL.getProtocol().equals("file")) {
                File directory = new File(directoryURL.toURI());
                filePaths = loadFilesFromDirectory(directory, directoryPath);
            }
            return filePaths;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(-1);
        }
        throw new UnsupportedOperationException("Unsupported protocol");
    }

    private Set<String> loadFilesFromDirectory(File directory, String basePath) {
        Set<String> filePaths = new HashSet<>();
        if (directory.isDirectory()) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (file.isFile()) {
                    String filePath = "/" + basePath + "/" + file.getName();
                    filePaths.add(filePath.substring("/static".length()));
                } else if (file.isDirectory()) {
                    filePaths.addAll(loadFilesFromDirectory(file, basePath + "/" + file.getName()));
                }
            }
        }
        return filePaths;
    }

    protected Set<String> setDefaultPages() {
        return Set.of("index.html");
    }

    public Map<String, RequestHandler> getRequestHandlerMap() {
        return requestHandlerMap;
    }

    public Set<String> getStaticResourcePaths() {
        return staticResourcePaths;
    }

    public Set<String> getDefaultPages() {
        return defaultPages;
    }
}
