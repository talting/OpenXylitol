package cc.xylitol.ui.gui.alt.microsoft;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.EnumChatFormatting;
import org.lwjglx.Sys;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;


public final class MicrosoftLogin implements Closeable {
    private static final String CLIENT_ID = "67b74668-ef33-49c3-a75c-18cbb2481e0c";
    //00000000402b5328
    //67b74668-ef33-49c3-a75c-18cbb2481e0c
    private static final String REDIRECT_URI = "http://localhost:3434/sad";
    private static final String SCOPE = "XboxLive.signin%20offline_access";

    private static final String URL = "https://login.live.com/oauth20_authorize.srf?client_id=<client_id>&redirect_uri=<redirect_uri>&response_type=code&display=touch&scope=<scope>"
            .replace("<client_id>", CLIENT_ID)
            .replace("<redirect_uri>", REDIRECT_URI)
            .replace("<scope>", SCOPE);


    public volatile String uuid = null;


    public volatile String userName = null;


    public volatile String accessToken = null;

    public volatile String refreshToken = null;
    @Getter
    @Setter
    public volatile boolean logged = false;

    public volatile String status = EnumChatFormatting.YELLOW + "Login...";

    private final HttpServer httpServer;

    @SuppressWarnings("FieldCanBeLocal")
    private final MicrosoftHttpHandler handler;

    public MicrosoftLogin() throws IOException {
        handler = new MicrosoftHttpHandler();
        httpServer = HttpServer.create(new InetSocketAddress("localhost", 3434), 0);
        httpServer.createContext("/sad", handler);
        httpServer.start();
        Sys.openURL(URL);
    }

    public MicrosoftLogin(String refreshToken) throws IOException {
        this.refreshToken = refreshToken;
        this.httpServer = null;
        this.handler = null;

        final String microsoftTokenAndRefreshToken = getMicrosoftTokenFromRefreshToken(refreshToken);
        final String xBoxLiveToken = getXBoxLiveToken(microsoftTokenAndRefreshToken);
        final String[] xstsTokenAndHash = getXSTSTokenAndUserHash(xBoxLiveToken);
        final String accessToken = getAccessToken(xstsTokenAndHash[0], xstsTokenAndHash[1]);
        final URL url = new URL("https://api.minecraftservices.com/minecraft/profile");
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);

        final String read = read(connection.getInputStream());
        final JsonObject jsonObject = new JsonParser().parse(read).getAsJsonObject();
        final String uuid = jsonObject.get("id").getAsString();
        final String userName = jsonObject.get("name").getAsString();

        MicrosoftLogin.this.uuid = uuid;
        MicrosoftLogin.this.userName = userName;
        MicrosoftLogin.this.accessToken = accessToken;
        MicrosoftLogin.this.logged = true;
    }

    @Override
    public void close() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }

    public void show() throws Exception {
        Desktop.getDesktop().browse(new URI(URL));
    }

    private String getAccessToken(String xstsToken, String uhs) throws IOException {
        status = EnumChatFormatting.YELLOW + "Getting access token";
        System.out.println("Getting access token");
        final URL url = new URL("https://api.minecraftservices.com/authentication/login_with_xbox");
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoInput(true);
        connection.setDoOutput(true);

        final JsonObject input = new JsonObject();
        input.addProperty("identityToken", "XBL3.0 x=" + uhs + ";" + xstsToken);

        write(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), new Gson().toJson(input));

        final JsonObject jsonObject = new JsonParser().parse(read(connection.getInputStream())).getAsJsonObject();

        return jsonObject.get("access_token").getAsString();
    }

    public String getMicrosoftTokenFromRefreshToken(String refreshToken) throws IOException {
        status = EnumChatFormatting.YELLOW + "Getting microsoft token from refresh token";
        System.out.println("Getting microsoft token from refresh token");

        final URL url = new URL("https://login.live.com/oauth20_token.srf");
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        final String param = "client_id=" + CLIENT_ID +
                //"&client_secret=" + "" +
                "&refresh_token=" + refreshToken +
                "&grant_type=refresh_token" +
                "&redirect_uri=" + REDIRECT_URI;

        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        write(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), param);

        final JsonObject response_obj = new JsonParser().parse(read(connection.getInputStream())).getAsJsonObject();
        return response_obj.get("access_token").getAsString();
    }

    public String[] getMicrosoftTokenAndRefreshToken(String code) throws IOException {
        status = EnumChatFormatting.YELLOW + "Getting microsoft token";
        System.out.println("Getting microsoft token");
        final URL url = new URL("https://login.live.com/oauth20_token.srf");
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        final String param = "client_id=" + CLIENT_ID +
                "&code=" + code +
                "&grant_type=authorization_code" +
                "&redirect_uri=" + REDIRECT_URI +
                "&scope=" + SCOPE;

        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        write(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), param);

        final JsonObject response_obj = new JsonParser().parse(read(connection.getInputStream())).getAsJsonObject();
        return new String[]{response_obj.get("access_token").getAsString(), response_obj.get("refresh_token").getAsString()};
    }

    @SuppressWarnings("HttpUrlsUsage")
    public String getXBoxLiveToken(String microsoftToken) throws IOException {
        status = EnumChatFormatting.YELLOW + "Getting xbox live token";
        System.out.println("Getting xbox live token");
        final URL connectUrl = new URL("https://user.auth.xboxlive.com/user/authenticate");
        final String param;
        final JsonObject xbl_param = new JsonObject();
        final JsonObject xbl_properties = new JsonObject();
        xbl_properties.addProperty("AuthMethod", "RPS");
        xbl_properties.addProperty("SiteName", "user.auth.xboxlive.com");
        xbl_properties.addProperty("RpsTicket", "d=" + microsoftToken);
        xbl_param.add("Properties", xbl_properties);
        xbl_param.addProperty("RelyingParty", "http://auth.xboxlive.com");
        xbl_param.addProperty("TokenType", "JWT");
        param = new Gson().toJson(xbl_param);

        final HttpURLConnection connection = (HttpURLConnection) connectUrl.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        write(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), param);

        final JsonObject response_obj = new JsonParser().parse(read(connection.getInputStream())).getAsJsonObject();
        return response_obj.get("Token").getAsString();
    }

    public String[] getXSTSTokenAndUserHash(String xboxLiveToken) throws IOException {
        status = EnumChatFormatting.YELLOW + "Getting xsts token and user hash";
        System.out.println("Getting xsts token and user hash");
        final URL ConnectUrl = new URL("https://xsts.auth.xboxlive.com/xsts/authorize");
        final String param;
        final ArrayList<String> tokens = new ArrayList<>();
        tokens.add(xboxLiveToken);
        final JsonObject xbl_param = new JsonObject();
        final JsonObject xbl_properties = new JsonObject();
        xbl_properties.addProperty("SandboxId", "RETAIL");
        xbl_properties.add("UserTokens", new JsonParser().parse(new Gson().toJson(tokens)).getAsJsonArray());

        xbl_param.add("Properties", xbl_properties);
        xbl_param.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
        xbl_param.addProperty("TokenType", "JWT");
        param = new Gson().toJson(xbl_param);

        final HttpURLConnection connection = (HttpURLConnection) ConnectUrl.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        write(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), param);
        final JsonObject response_obj = new JsonParser().parse(read(connection.getInputStream())).getAsJsonObject();

        final String token = response_obj.get("Token").getAsString();
        final String uhs = response_obj.getAsJsonObject("DisplayClaims").getAsJsonArray("xui").get(0).getAsJsonObject().get("uhs").getAsString();
        return new String[]{token, uhs};
    }

    private void write(BufferedWriter writer, String s) throws IOException {
        writer.write(s);
        writer.close();
    }

    private String read(InputStream stream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        final StringBuilder stringBuilder = new StringBuilder();
        String s;
        while ((s = reader.readLine()) != null) {
            stringBuilder.append(s);
        }

        stream.close();
        reader.close();

        return stringBuilder.toString();
    }

    public boolean isLogged() {
        return logged;
    }

    public String getUserName() {
        return userName;
    }

    public String getUuid() {
        return uuid;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private class MicrosoftHttpHandler implements HttpHandler {
        private boolean got = false;

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            if (!got) {
                final String query = httpExchange.getRequestURI().getQuery();
                if (query.contains("code")) {
                    got = true;
                    final String code = query.split("code=")[1];

                    final String[] microsoftTokenAndRefreshToken = getMicrosoftTokenAndRefreshToken(code);
                    final String xBoxLiveToken = getXBoxLiveToken(microsoftTokenAndRefreshToken[0]);
                    final String[] xstsTokenAndHash = getXSTSTokenAndUserHash(xBoxLiveToken);
                    final String accessToken = getAccessToken(xstsTokenAndHash[0], xstsTokenAndHash[1]);
                    final URL url = new URL("https://api.minecraftservices.com/minecraft/profile");
                    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Authorization", "Bearer " + accessToken);

                    final String read = read(connection.getInputStream());
                    final JsonObject jsonObject = new JsonParser().parse(read).getAsJsonObject();
                    final String uuid = jsonObject.get("id").getAsString();
                    final String userName = jsonObject.get("name").getAsString();

                    MicrosoftLogin.this.uuid = uuid;
                    MicrosoftLogin.this.userName = userName;
                    MicrosoftLogin.this.accessToken = accessToken;
                    MicrosoftLogin.this.refreshToken = microsoftTokenAndRefreshToken[1];
                    MicrosoftLogin.this.logged = true;
                }
            }
        }
    }
}
