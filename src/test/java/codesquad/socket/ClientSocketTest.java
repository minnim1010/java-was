package codesquad.socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("클라이언트 소켓 테스트")
class ClientSocketTest {

    @Nested
    class 소켓에서_데이터를_읽는다 {

        PipedInputStream pipedInputStream;
        PipedOutputStream pipedOutputStream;

        @BeforeEach
        void setUp() throws IOException {
            pipedInputStream = new PipedInputStream();
            pipedOutputStream = new PipedOutputStream(pipedInputStream);
        }

        @AfterEach
        void tearDown() throws IOException {
            pipedOutputStream.close();
            pipedInputStream.close();
        }

        @Test
        void 한_줄씩_읽어온다() throws IOException {
            String expectedLine = "Hello, World!\n";
            String input = "Hello, World!\nWorld!! Hello";
            byte[] inputData = input.getBytes();

            pipedOutputStream.write(inputData);
            pipedOutputStream.flush();

            Socket customSocket = new Socket() {
                @Override
                public PipedInputStream getInputStream() {
                    return pipedInputStream;
                }

                @Override
                public PipedOutputStream getOutputStream() {
                    return new PipedOutputStream();
                }
            };

            ClientSocket clientSocket = new ClientSocket(customSocket);

            byte[] result = clientSocket.readLine();

            assertEquals(expectedLine, new String(result));
        }

        @Test
        void 주어진_바이트_길이만큼_읽어온다() throws IOException {
            String expectedLine = "Hello, World!\n";
            String input = "Hello, World!\nWorld!! Hello";
            int readLen = 14;
            byte[] inputData = input.getBytes();

            pipedOutputStream.write(inputData);
            pipedOutputStream.flush();

            Socket customSocket = new Socket() {
                @Override
                public PipedInputStream getInputStream() {
                    return pipedInputStream;
                }

                @Override
                public PipedOutputStream getOutputStream() {
                    return new PipedOutputStream();
                }
            };

            ClientSocket clientSocket = new ClientSocket(customSocket);

            byte[] result = clientSocket.readBytes(readLen);

            assertEquals(readLen, result.length);
            assertEquals(expectedLine, new String(result));
        }

        @Test
        void 한줄_읽고_주어진_바이트_길이만큼_읽는다() throws IOException {
            String expectedLine = "World!!";
            String input = "Hello, World!\nWorld!! Hello";
            int readLen = 7;
            byte[] inputData = input.getBytes();

            pipedOutputStream.write(inputData);
            pipedOutputStream.flush();

            Socket customSocket = new Socket() {
                @Override
                public PipedInputStream getInputStream() {
                    return pipedInputStream;
                }

                @Override
                public PipedOutputStream getOutputStream() {
                    return new PipedOutputStream();
                }
            };

            ClientSocket clientSocket = new ClientSocket(customSocket);

            byte[] ignore = clientSocket.readLine();
            byte[] result = clientSocket.readBytes(readLen);

            assertEquals(readLen, result.length);
            assertEquals(expectedLine, new String(result));
        }
    }
}