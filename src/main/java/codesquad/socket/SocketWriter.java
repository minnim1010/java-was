package codesquad.socket;

import java.io.IOException;
import java.io.OutputStream;

public class SocketWriter {

    private final OutputStream outputStream;

    public SocketWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void write(byte[] output) throws IOException {
        outputStream.write(output);
        outputStream.flush();
    }
}
