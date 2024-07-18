package codesquad.socket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SocketReader {

    private final InputStream inputStream;

    public SocketReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public byte[] readLine() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int currentByte;

        while ((currentByte = inputStream.read()) != -1) {
            buffer.write(currentByte);
            if (currentByte == '\n') {
                break;
            }
        }

        return buffer.toByteArray();
    }

    public byte[] readBytes(int length) throws IOException {
        byte[] buffer = new byte[length];

        inputStream.readNBytes(buffer, 0, length);
        return buffer;
    }
}
