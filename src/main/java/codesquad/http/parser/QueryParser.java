package codesquad.http.parser;

import codesquad.http.error.QueryParseException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class QueryParser {

    private QueryParser() {
    }

    public static Map<String, String> parse(String query) {
        Map<String, String> queryPairs = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return queryPairs;
        }

        try {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String decodedPair = URLDecoder.decode(pair, "UTF-8");
                int idx = decodedPair.indexOf("=");
                String key = decodedPair.substring(0, idx);
                String value = decodedPair.substring(idx + 1);
                queryPairs.put(key, value);
            }
            return queryPairs;
        } catch (Exception e) {
            throw new QueryParseException("Invalid query: " + query, e);
        }
    }
}
