package com.twa.flights.api.clusters.serializer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringSerializerTest {

    @Test
    public void testCompressionDecompression() {
        String toCompress = "string to compress";
        byte[] compressed = StringSerializer.gzip(toCompress);
        String uncompressed = StringSerializer.ungzip(compressed);

        assertEquals(uncompressed, toCompress);
    }
}