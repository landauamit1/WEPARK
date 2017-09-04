package Json.Params;

import Entities.Favorite;
import com.google.gson.annotations.SerializedName;

/**
 * FavoriteParams
 */
public class FavoriteParams {
    @SerializedName("userName")
    private String userName;
    @SerializedName("favorite")
    private Favorite favorite;

    public String getUserName() {
        return userName;
    }

    public Favorite getFavorite() {
        return favorite;
    }


}
