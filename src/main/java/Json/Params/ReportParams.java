package Json.Params;

import Entities.Address;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * ReportParams
 */
public class ReportParams{
    @SerializedName("address")
    private Address address;
    @SerializedName("availableFrom")
    private Date availableFrom;
    @SerializedName("reporter")
    private String reporter;

    public Address getAddress() {
        return address;
    }

    public Date getAvailableFrom() {
        return availableFrom;
    }

    public String getReporter() {
        return reporter;
    }
}