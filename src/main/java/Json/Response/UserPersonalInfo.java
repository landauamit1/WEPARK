package Json.Response;

import Json.UserBaseInfo;
import com.google.gson.annotations.SerializedName;

/**
 * UserPersonalInfo
 */
public class UserPersonalInfo extends UserBaseInfo {
    @SerializedName("password")
    private String password;
    @SerializedName("score")
    private int score;

    public UserPersonalInfo(String name, String licensePlate, String carModel, String password, int score) {
        super(name, licensePlate, carModel);
        this.password = password;
        this.score = score;
    }

    public String getPassword() {
        return password;
    }

    public int getScore() {
        return score;
    }
}
