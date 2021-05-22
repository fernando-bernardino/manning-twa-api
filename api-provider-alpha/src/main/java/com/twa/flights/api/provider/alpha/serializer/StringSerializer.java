package com.twa.flights.api.provider.alpha.serializer;

import org.slf4j.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.*;

public class StringSerializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringSerializer.class);

    private StringSerializer() {
        // just to avoid create instances
    }

    public static String ungzip(byte[] bytes) {
        try {
            InputStreamReader isr = new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(bytes)),
                    StandardCharsets.UTF_8);
            StringWriter sw = new StringWriter();
            char[] chars = new char[1024];
            for (int len; (len = isr.read(chars)) > 0;) {
                sw.write(chars, 0, len);
            }
            return sw.toString();
        } catch (IOException e) {
            LOGGER.error("Error uncompressing object: {}", e.getMessage());
            return null;
        }
    }

    public static byte[] gzip(String toCompress) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            OutputStreamWriter osw = new OutputStreamWriter(gzip, StandardCharsets.UTF_8);
            osw.write(toCompress);
            osw.close();
            return bos.toByteArray();
        } catch (IOException e) {
            LOGGER.error("Error compressing object: {}", e.getMessage());
            return null;
        }
    }
}
