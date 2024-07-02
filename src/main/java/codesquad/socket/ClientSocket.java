package codesquad.socket;

import static codesquad.utils.StringUtils.EMPTY;

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

    public String read() throws IOException {

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

        return EMPTY;
    }

    public void write(String response) throws IOException {
        try (OutputStream outputStream = this.socket.getOutputStream();) {
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            throw e;
        }
    }
}
