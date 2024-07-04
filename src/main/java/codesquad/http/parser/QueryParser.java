package codesquad.http.parser;

import codesquad.error.QueryParseException;
import java.util.HashMap;
import java.util.Map;

public class QueryParser {

    public Map<String, String> parseQuery(String query) {
        Map<String, String> queryPairs = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return queryPairs;
        }

        try {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                String key = pair.substring(0, idx);
                String value = pair.substring(idx + 1);
                queryPairs.put(key, value);
            }
            return queryPairs;
        } catch (Exception e) {
            throw new QueryParseException("Invalid query: " + query, e);
        }
    }
}
