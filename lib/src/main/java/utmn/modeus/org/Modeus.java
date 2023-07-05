package utmn.modeus.org;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Modeus {
    private String personID;
    private String bearerToken;

    public Modeus(String bearerToken) throws ParseException {
        this.bearerToken = bearerToken;
        setPersonID(bearerToken);
    }

    private void setPersonID(String bearerToken) throws ParseException {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] chunks = bearerToken.split("\\.");
        String payload = new String(decoder.decode(chunks[1])); // we take only payload
        Object obj = new JSONParser().parse(payload); // payload to json-object for find "person_id"
        JSONObject jo = (JSONObject) obj;
        personID =((String) jo.get("person_id"));
    }

    public JSONObject getMyTimetable(LocalDateTime timeMin, LocalDateTime timeMax, Boolean thisWeek)
            throws ExecutionException, InterruptedException, ParseException { // this week, or date begin - date end
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime nextWeek = today.plusWeeks(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        JSONObject takeTimetable = new JSONObject();
        JSONArray attendeePersonId = new JSONArray();
        attendeePersonId.add(0, personID); // "array":["cringe"];

        takeTimetable.put("size", "500");
        takeTimetable.put("attendeePersonId", attendeePersonId);

        if(thisWeek) {
            takeTimetable.put("timeMin", today.format(formatter) + "Z"); // yyyy-MM-dd'T'HH:mm:ssZ java doesn't have Z
            takeTimetable.put("timeMax", nextWeek.format(formatter) + "Z");
        }
        else {
            takeTimetable.put("timeMin", timeMin.format(formatter) + "Z"); // yyyy-MM-dd'T'HH:mm:ssZ java doesn't have Z
            takeTimetable.put("timeMax", timeMax.format(formatter) + "Z");

        }
        //take request for timetable;
        AsyncHttpClient client = new DefaultAsyncHttpClient();
        BoundRequestBuilder getReq = client.preparePost(ModeusAdresses.TIMETABLE.getAddress())
                .addQueryParam("tz","Asia/Tyumen")
                .addQueryParam("authAction","")
                .addHeader("Authorization","Bearer " + bearerToken)
                .addHeader("Content-Type", "application/json")
                .setBody(takeTimetable.toString());
        Future<Response> responseFuture = getReq.execute();
        //if bearer isn't right, so we use again Credentials.login()


        JSONParser jsonParser = new JSONParser();
        Object json = jsonParser.parse(responseFuture.get().getResponseBody());
        return (JSONObject) json;
    }
}

enum ModeusAdresses{
    TIMETABLE("https://utmn.modeus.org/schedule-calendar-v2/api/calendar/events/search");
    private final String address;
    ModeusAdresses(String address){
        this.address = address;
    }
    public String getAddress(){
        return address;
    }
}

