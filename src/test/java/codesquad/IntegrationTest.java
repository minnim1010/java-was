package codesquad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("크롬 기반 통합 테스트")
class IntegrationTest {

    @Test
    void webServerIntegrationTest() throws IOException, InterruptedException {
        new Thread(() -> {
            try {
                Main.main(new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        Thread.sleep(1000);

        ProcessBuilder processBuilder = new ProcessBuilder("/usr/bin/python3", "src/test/java/codesquad/WebTest.py");
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        process.waitFor();

        int len;
        if ((len = process.getErrorStream().available()) > 0) {
            byte[] buf = new byte[len];
            process.getErrorStream().read(buf);
            System.err.println("Command error:\t\"" + new String(buf) + "\"");
        }

        System.out.println(output);
    }
}
