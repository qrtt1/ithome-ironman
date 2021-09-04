import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

public class ContentParser {
    public static void main(String[] args) throws IOException {
        Document document = Jsoup.parse(new File("list.html"), "utf-8");
        System.out.println(document);
    }
}
