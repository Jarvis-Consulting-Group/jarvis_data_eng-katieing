package ca.jrvs.apps.grep.practice;

import java.util.regex.Pattern;

public class RegexExcImp implements RegexExc{
    @Override
    public boolean matchJpeg(String filename) {
        return Pattern.matches("(?i).+\\.jpe?g", filename);
    }

    @Override
    public boolean matchIp(String ip) {
        return Pattern.matches("\\d{0,3}\\.\\d{0,3}\\.\\d{0,3}\\.\\d{0,3}", ip);
    }

    @Override
    public boolean isEmptyLine(String line) {
        return Pattern.matches("\\s*", line);
    }
}
