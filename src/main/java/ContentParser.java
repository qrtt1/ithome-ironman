import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;

public class ContentParser {
    public static void main(String[] args) throws IOException {
        Document document = Jsoup.parse(new File("list.html"), "utf-8");
        for (Element element : document.select("div.border-frame.clearfix > div.contestants-wrapper > div.contestants-list")) {
            System.out.println(element.text());
        }
    }
}
