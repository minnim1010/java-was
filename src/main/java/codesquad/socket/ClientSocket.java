package codesquad.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientSocket implements AutoCloseable {

    private final Socket socket;

    public ClientSocket(Socket socket) {
        this.socket = socket;
    }

    public int getPort() {
        return this.socket.getPort();
    }

    public String read() throws IOException {
        final int buffer_size = 1024;

        InputStream inputStream = this.socket.getInputStream();
        StringBuilder input = new StringBuilder();
        byte[] buffer = new byte[buffer_size];
        int length = 0;

        do {
            length = inputStream.read(buffer);
            input.append(new String(buffer, 0, length));
        } while (length == buffer_size);

        return input.toString();
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
