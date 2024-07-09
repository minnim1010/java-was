package codesquad.socket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientSocket implements AutoCloseable {

    private final Socket nativeSocket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public ClientSocket(Socket nativeSocket) throws IOException {
        this.nativeSocket = nativeSocket;
        this.inputStream = nativeSocket.getInputStream();
        this.outputStream = nativeSocket.getOutputStream();
    }

    public int getPort() {
        return this.nativeSocket.getPort();
    }

    public String read() throws IOException {
        final int buffer_size = 1024;

        StringBuilder input = new StringBuilder();
        byte[] buffer = new byte[buffer_size];
        int length = 0;

        do {
            length = inputStream.read(buffer);
            input.append(new String(buffer, 0, length));
        } while (length == buffer_size);

        return input.toString();
    }

    public byte[] readLine() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nextByte;
        while ((nextByte = inputStream.read()) != -1) {
            buffer.write(nextByte);
            if (nextByte == '\n') {
                break;
            }
        }

        if (buffer.size() == 0) {
            return new byte[0];
        }
        return buffer.toByteArray();
    }

    public byte[] readBytes(int length) throws IOException {
        return inputStream.readNBytes(length);
    }

    public void write(byte[] output) throws IOException {
        outputStream.write(output);
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        this.nativeSocket.close();
    }
}
