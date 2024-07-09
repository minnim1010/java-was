package codesquad;

import codesquad.config.GlobalConfig;
import codesquad.http.HttpProcessor;
import codesquad.http.HttpRequestProcessor;
import codesquad.http.handler.RequestHandlerResolver;
import codesquad.http.handler.StaticResourceRequestHandler;
import codesquad.http.parser.HttpParser;
import codesquad.socket.ClientSocket;
import codesquad.socket.Reader;
import codesquad.socket.ServerSocket;
import codesquad.socket.Writer;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {

    private static final Logger log = LoggerFactory.getLogger(WebServer.class);

    private final HttpParser httpParser;
    private final RequestHandlerResolver requestHandlerResolver;
    private final StaticResourceRequestHandler staticResourceRequestHandler;
    private final HttpRequestProcessor httpRequestProcessor;
    private final HttpProcessor httpProcessor;

    public WebServer() {
        this.httpParser = new HttpParser();
        this.requestHandlerResolver = new RequestHandlerResolver(GlobalConfig.REQUEST_HANDLER);
        Set<String> staticResourcePaths = initializeStaticResourcePaths();
        this.staticResourceRequestHandler = new StaticResourceRequestHandler(staticResourcePaths);
        this.httpRequestProcessor = new HttpRequestProcessor(requestHandlerResolver, staticResourceRequestHandler);
        this.httpProcessor = new HttpProcessor(httpParser, httpRequestProcessor);
    }

    private Set<String> initializeStaticResourcePaths() {
        return loadAllFilePathsFromDirectory("static");
    }

    public Set<String> loadAllFilePathsFromDirectory(String directoryPath) {
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
            for (File file : directory.listFiles()) {
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

    public void run() {
        ExecutorService threadPool = Executors.newFixedThreadPool(GlobalConfig.REQUEST_THREADS);
        boolean isRunning = true;

        try (ServerSocket serverSocket = new ServerSocket()) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdownServer(threadPool, serverSocket)));

            while (isRunning) {
                ClientSocket clientSocket = serverSocket.acceptClient();
                threadPool.submit(() -> processClientRequest(clientSocket));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            threadPool.shutdown();
        }
    }

    private void processClientRequest(ClientSocket clientSocket) {
        try (clientSocket) {
            log.debug("Client connected: port " + clientSocket.getPort());

            Reader reader = clientSocket.getReader();
            Writer writer = clientSocket.getWriter();

            String input = reader.read();
            byte[] output = httpProcessor.process(input);
            writer.write(output);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void shutdownServer(ExecutorService threadPool, ServerSocket serverSocket) {
        log.info("Shutting down server...");
        threadPool.shutdown();

        try {
            if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
                if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                    log.error("Thread pool did not terminate");
                }
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        } finally {
            try {
                if (!serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                log.error("Error closing server socket", e);
            }
        }
        log.info("Server shutdown complete");
    }
}
