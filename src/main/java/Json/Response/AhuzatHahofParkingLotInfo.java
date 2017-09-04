package Json.Response;

/**
 * AhuzatHahofParkingLotInfo
 */
public class AhuzatHahofParkingLotInfo {
    private String totalSpot;
    private String status;
    private String price;
    private String url;

    public AhuzatHahofParkingLotInfo(String totalSpot, String status, String price, String url) {
        this.totalSpot = totalSpot;
        this.status = status;
        this.price = price;
        this.url = url;
    }
}
