package Json.Params;

import Json.Response.UserPersonalInfo;
import com.google.gson.annotations.SerializedName;

/**
 * PersonalInfoParams
 */
public class PersonalInfoParams {
    @SerializedName("personalInfo")
    private UserPersonalInfo personalInfo;

    public UserPersonalInfo getPersonalInfo() {
        return personalInfo;
    }
}
