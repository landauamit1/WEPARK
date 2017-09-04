package Json.Params;

import com.google.gson.annotations.SerializedName;

/**
 * LoginUserParams
 */
public class LoginUserParams {
    @SerializedName("userName")
    private String userName;
    @SerializedName("password")
    private String password;
    @SerializedName("token")
    private String token;

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }
}
