package codesquad.socket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Reader {

    private final InputStream inputStream;

    public Reader(InputStream inputStream) {
        this.inputStream = inputStream;
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
}
