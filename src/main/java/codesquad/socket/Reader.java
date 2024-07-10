package codesquad.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Reader {

    private final BufferedReader bufferedReader;

    public Reader(InputStream inputStream) {
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public char[] readLine() throws IOException {
        return bufferedReader.readLine().toCharArray();
    }

    public char[] readBytes(int length) throws IOException {
        char[] input = new char[length];
        bufferedReader.read(input);
        return input;
    }
}
