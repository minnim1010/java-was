package codesquad.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SocketReader {

    private final BufferedReader bufferedReader;

    public SocketReader(InputStream inputStream) {
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public char[] readLine() throws IOException {
        String input = bufferedReader.readLine();
        if (input == null) {
            return new char[0];
        }

        return input.toCharArray();
    }

    public char[] readBytes(int length) throws IOException {
        char[] input = new char[length];
        bufferedReader.read(input);
        return input;
    }
}
