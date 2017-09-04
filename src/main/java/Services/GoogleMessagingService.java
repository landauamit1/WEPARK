package Services;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import groovy.lang.Tuple2;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * GoogleMessagingService
 */
public class GoogleMessagingService {
    private static final String SERVER_KEY = "AAAAF5Rikag:APA91bGcE4gDoTyiLpm81ojOlePOmGe0z64fJAzddS3ncPtmyPTjFli_wxRMY3Pz0WA-HeNbFk74pHojCH5At51fiWE6G5_jsdADmU62RtuYXfzdBrHcpNVskI3fLchkhR9F24zmnzko";
    private static final String PROJECT_ID = "101273735592";
    private static final String SEND_URL = "https://gcm-http.googleapis.com/gcm/send";
    private static final String NOTIFICATION_URL = "https://android.googleapis.com/gcm/notification";
    private static final String OPERATION = "operation";
    private static final String NOTIFICATION_KEY_NAME = "notification_key_name";
    private static final String REGISTRATION_IDS = "registration_ids";
    private static final String NOTIFICATION_KEY = "notification_key";
    private static final String APPLICATION_JSON = "application/json";

    public static void sendMessage(String to, int code, JSONObject msgObject) throws Exception{
        OkHttpClient client = new OkHttpClient();

        // create json for message body
        JSONObject obj = new JSONObject();
        msgObject.put("code", code);
        obj.put("to", to);
        obj.put("data", msgObject);

        RequestBody body = RequestBody.create(MediaType.parse(APPLICATION_JSON), obj.toString());
        Request request = new Request.Builder().url(SEND_URL).post(body)
                .addHeader("content-type", APPLICATION_JSON)
                .addHeader("authorization", "key=" + SERVER_KEY).build();

        try {
            com.squareup.okhttp.Response response = client.newCall(request).execute();
            System.out.println("Message was sent successfully to: " + to);
        } catch (Exception e) {
            throw new Exception("שליחה ההודעה נכשלה. אנא נסה שנית");
        }
    }

    public static void sendGroupMessage(List<String> tokens, int code, JSONObject msgObject) {
        try {
            Tuple2<String, String> groupInfo = createGroup(tokens);
            sendMessage(groupInfo.getFirst(), code, msgObject);
            deleteGroup(groupInfo.getFirst(), groupInfo.getSecond(), tokens);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static Tuple2<String, String> createGroup(List<String> tokens) throws Exception {
        URL url = new URL(NOTIFICATION_URL);
        BufferedReader br = null;
        String groupId = UUID.randomUUID().toString();

        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);

            // HTTP request header
            urlConnection.setRequestProperty("project_id", PROJECT_ID);
            urlConnection.setRequestProperty("Content-Type", APPLICATION_JSON);
            urlConnection.setRequestProperty("Accept", APPLICATION_JSON);
            urlConnection.setRequestProperty("authorization", "key=" + SERVER_KEY);
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            // HTTP request
            JSONObject data = new JSONObject()
                    .put(OPERATION, "create")
                    .put(NOTIFICATION_KEY_NAME, groupId)
                    .put(REGISTRATION_IDS, tokens);

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.toString().getBytes("UTF-8"));
            os.close();

            // Read the response into a string
            InputStream is = urlConnection.getInputStream();
            String responseString = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
            is.close();

            // Parse the JSON string and return the notification key + group id
            JSONObject response = new JSONObject(responseString);
            System.out.println(String.format("group '%s' was created successfully", groupId));
            return new Tuple2(response.getString("notification_key"), groupId);
        } catch (Exception e) {
            throw new Exception("Error creating device group: " + e.getMessage());
        }
    }

    public static void deleteGroup(String groupToken, String groupId, List<String> tokens) throws Exception {
        URL url = new URL(NOTIFICATION_URL);
        BufferedReader br = null;

        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);

            // HTTP request header
            urlConnection.setRequestProperty("project_id", PROJECT_ID);
            urlConnection.setRequestProperty("Content-Type", APPLICATION_JSON);
            urlConnection.setRequestProperty("Accept", APPLICATION_JSON);
            urlConnection.setRequestProperty("authorization", "key=" + SERVER_KEY);
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            // HTTP request
            JSONObject data = new JSONObject()
                    .put(OPERATION, "remove")
                    .put(NOTIFICATION_KEY_NAME, groupId)
                    .put(REGISTRATION_IDS, tokens)
                    .put(NOTIFICATION_KEY, groupToken);

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.toString().getBytes("UTF-8"));
            os.close();

            // Read the response into a string
            InputStream is = urlConnection.getInputStream();
            String responseString = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
            is.close();

        } catch (Exception e) {
            throw new Exception("Error deleting device group: " + e.getMessage());
        }
    }
}
