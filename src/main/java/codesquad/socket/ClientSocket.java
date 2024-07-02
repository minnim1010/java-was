package codesquad.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientSocket implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(ClientSocket.class);

    private final Socket socket;

    public ClientSocket(Socket socket) {
        this.socket = socket;
    }

    public int getPort() {
        return this.socket.getPort();
    }

    public String read() throws IOException {
        InputStream inputStream = this.socket.getInputStream();
        byte[] buffer = new byte[1024];

        int length = inputStream.read(buffer);
        if (length == -1) {
            throw new IOException("Connection closed");
        }
        String result = new String(buffer);
        log.debug(result);
        return result;
    }

    public void write(String response) throws IOException {
        OutputStream outputStream = this.socket.getOutputStream();
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        this.socket.close();
    }
}
