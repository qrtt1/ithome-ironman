import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;

public class HelloWorld {
    public static void main(String[] args) throws IOException {
        Document document = Jsoup.connect("https://ithelp.ithome.com.tw/2021ironman/signup/list").get();
        FileUtils.write(new File("list.html"), document.toString(), "utf-8");

        String pageListSelector = "div.border-frame.clearfix > div.contestants-wrapper > div.text-center > ul a";
        for (Element element : document.select(pageListSelector)) {
            System.out.println(element);
        }
    }
}
