import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;

public class HelloWorld {
    public static void main(String[] args) throws IOException {
        Document document = Jsoup.connect("https://ithelp.ithome.com.tw/2022ironman/signup/list").get();
        FileUtils.write(new File("list.html"), document.toString(), "utf-8");
        int lastPage = parseLastPage(document);
        System.out.println(lastPage);
    }

    private static int parseLastPage(Document document) {
        String pageListSelector = "div.border-frame.clearfix > div.contestants-wrapper > div.text-center > ul a";
        int maxPage = 0;
        for (Element element : document.select(pageListSelector)) {
            try {
                int page = Integer.parseInt(element.text());
                if (maxPage < page) {
                    maxPage = page;
                }
            } catch (Exception e) {
            }

        }
        return maxPage;
    }
}
