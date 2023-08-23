package me.biabani.se.song.tasks.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Function;

public class TokenizeStringTask implements Function<String, List<String>> {

    private static final String delimiters = " \n\r\t\f.,?![]{}()<>:;'\"-_=+/\\&*^%";

    @Override
    public List<String> apply(String content) {
        StringTokenizer stringTokenizer = new StringTokenizer(content, delimiters, true);

        List<String> tokens = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            if (delimiters.contains(token)) {
                stringBuilder.append(token);
            } else {
                if (!stringBuilder.isEmpty()) {
                    tokens.add(stringBuilder.toString());
                    stringBuilder.setLength(0); // clear the builder
                }
                tokens.add(token);
            }
        }

        /* if the last char sequence of file is from delimiters, it must be added to tokens */
        if (!stringBuilder.isEmpty()) {
            tokens.add(stringBuilder.toString());
        }
        return tokens;
    }

    private static TokenizeStringTask singleInstance = null;

    private TokenizeStringTask() {
    }

    public static synchronized TokenizeStringTask getInstance() {
        if (singleInstance == null)
            singleInstance = new TokenizeStringTask();
        return singleInstance;
    }
}
