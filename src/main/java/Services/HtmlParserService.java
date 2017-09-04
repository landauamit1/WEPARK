package Services;

import Json.Response.AhuzatHahofParkingLotInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Pattern;

/**
 * HtmlParserService
 */
public class HtmlParserService {
    private static final String URL = "http://www.ahuzot.co.il/Parking/ParkingDetails/?ID=";

    public static AhuzatHahofParkingLotInfo getParkingLotInfo(int id) throws Exception {
        String url = URL + id;

        try {
            Document doc = Jsoup.connect(url).timeout(0).get();

            // get status
            String status = getParkingLotStatus(doc);

            // get totalSpot
            String totalSpot = getParkingLotTotalSpot(doc);

            // get price
            String price = getParkingLotPrice(doc);

            return new AhuzatHahofParkingLotInfo(totalSpot, status, price, url);
        } catch (Exception e) {
            throw new Exception("Error in HtmlParser: " + e.getMessage());
        }
    }

    private static String getParkingLotStatus(Document doc) throws Exception {
        try {
            Element statusDivTag = doc.getElementById("ctl06_data1_ctl01_lblAhuzotIDStat");
            Element statusImage = statusDivTag.select("img").first();
            String statusImagePath = null;
            if (statusImage != null) {
                statusImagePath = statusImage.absUrl("src");
                String[] splits = statusImagePath.split("/");
                return splits[splits.length - 1].split(Pattern.quote("."))[0];
            }
            return null;
        } catch (Exception e) {
            throw e;
        }
    }

    private static String getParkingLotTotalSpot(Document doc) throws Exception {
        try {
            Element totalSpotTable = doc.getElementsByClass("ContentTableMiddleLarge").get(2).getElementsByClass("IconText").last();
            return totalSpotTable != null ? totalSpotTable.text() : "N/A";
        } catch (Exception e) {
            throw e;
        }
    }

    private static String getParkingLotPrice(Document doc) throws Exception {
        try {
            Element priceTable = doc.getElementsByClass("ContentTableMiddleLarge").get(2).select("td").first().select("table").first();
            Element priceElem = priceTable.select("p").first();
            return priceElem != null ? priceElem.text() : "N/A";
        } catch (Exception e) {
            throw e;
        }
    }
}
