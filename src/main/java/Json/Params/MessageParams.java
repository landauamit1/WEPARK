package Json.Params;

import com.google.gson.annotations.SerializedName;

/**
 * MessageParams
 */
public class MessageParams {
    @SerializedName("from")
    private String fromUser;
    @SerializedName("to")
    private String toUser;
    @SerializedName("text")
    private String text;

    public String getFromUser() {
        return fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public String getText() {
        return text;
    }
}
