package codesquad.socket;

import java.io.IOException;
import java.io.OutputStream;

public class Writer {

    private final OutputStream outputStream;

    public Writer(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void write(byte[] output) throws IOException {
        outputStream.write(output);
        outputStream.flush();
    }
}
