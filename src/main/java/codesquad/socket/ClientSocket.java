package codesquad.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientSocket {

    private static final Logger logger = LoggerFactory.getLogger(ClientSocket.class);

    private final Socket socket;

    public ClientSocket(Socket socket) {
        this.socket = socket;
    }

    public String read() {
        try {
            InputStream inputStream = this.socket.getInputStream();
            byte[] buffer = new byte[1024];

            int length = inputStream.read(buffer);
            if (length == -1) {
                throw new IOException("Connection closed");
            }
            String result = new String(buffer);
            logger.debug(result);
            return result;
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return "";
    }

    public void write(String message) {
        try (OutputStream outputStream = this.socket.getOutputStream();) {
            outputStream.write("HTTP/1.1 200 OK\r\n".getBytes());
            outputStream.write("Content-Type: text/html\r\n".getBytes());
            outputStream.write("\r\n".getBytes());
            outputStream.write("<h1>Hello</h1>\r\n".getBytes());
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
