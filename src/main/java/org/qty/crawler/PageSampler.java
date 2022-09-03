package org.qty.crawler;

import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class PageSampler {

    public static boolean CREATE_SAMPLE = false;

    public static void save(Document doc, String filename) {
        if (!CREATE_SAMPLE) {
            return;
        }
        try {
            Files.writeString(new File(filename).toPath(), doc.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
