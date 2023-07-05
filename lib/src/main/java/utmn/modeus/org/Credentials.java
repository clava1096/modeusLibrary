package utmn.modeus.org;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Credentials {
    private static final AsyncHttpClient client = new DefaultAsyncHttpClient();
    public static final String clientID = "sKir7YQnOUu4G0eCfn3tTxnBfzca";
    private static final String loginUrl = "https://auth.modeus.org/oauth2/authorize";

    public static String login(String email, String password) throws ExecutionException, InterruptedException { //return token
        String state = UUID.randomUUID().toString().replace("-", "");
        String nonce = UUID.randomUUID().toString().replace("-", "");
        // in request of loginUrl need two query param(state, nonce)
        BoundRequestBuilder getReq = client.prepareGet(loginUrl)
                .addQueryParam("client_id", clientID)
                .addQueryParam("redirect_uri", "https://utmn.modeus.org/")
                .addQueryParam("response_type", "id_token")
                .addQueryParam("scope", "openid")
                .addQueryParam("state", state)
                .addQueryParam("nonce", nonce);
        Future<Response> responseFuture = getReq.execute();

        String response = responseFuture.get().getHeaders().get("Location");
        getReq = client.preparePost(response)
                .addFormParam("UserName", email)
                .addFormParam("Password", password)
                .addFormParam("AuthMethod", "FormsAuthentication");
        responseFuture = getReq.execute();

        String FSutmn = responseFuture.get().getHeaders().get("Location");
        getReq = client.prepareGet(FSutmn);
        responseFuture = getReq.execute();
        String HTMLpage = responseFuture.get().getResponseBody();
        Document doc = Jsoup.parse(HTMLpage);
        Elements links = doc.select("input");
        String SAMLResponse = links.get(0).attributes().get("value"); // SAMLResponse
        String RelayState = links.get(1).attributes().get("value"); // RelayState

        String continue_auth_url = "https://auth.modeus.org/commonauth";
        getReq = client.preparePost(continue_auth_url)
                .addFormParam("SAMLResponse", SAMLResponse)
                .addFormParam("RelayState", RelayState);

        responseFuture = getReq.execute();
        ////
        //System.out.println("auth: " + responseFuture.get()); //Success
        String auth = responseFuture.get().getHeader("Location"); // https://auth.modeus.org/oauth2/authorize?sessionDataKey=812ccdd5-ea91-4101-8b72-16270c6c565b
        ////
        getReq = client.prepareGet(auth);
        responseFuture = getReq.execute();
        String token = responseFuture.get().getHeader("Location");
        token = token.substring(token.indexOf("=") + 1);
        token = token.substring(0, token.indexOf("&state"));
        return token;
    }
}
