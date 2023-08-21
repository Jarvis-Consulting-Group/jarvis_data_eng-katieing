package ca.jrvs.apps.grep.practice;

public interface RegexExc {

    /**
     * return true if filename extension is jpeg or jpg (case insensitive)
     */
    public boolean matchJpeg(String filename);

    /**
     * return true if ip is valid
     * IP address range is from 0.0.0.0 to 999.999.999.999
     */
    public boolean matchIp(String ip);

    /**
     * return true if line is empty (empty, whitespace, tabs, etc)
     */
    public boolean isEmptyLine(String line);




}
