package com.deltarobotics9351.deltapanel;

import fi.iki.elonen.NanoHTTPD;

import static com.deltarobotics9351.deltapanel.DeltaLogger.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DeltaPanelHttpService extends NanoHTTPD {

    public static String RESOURCE_NOT_FOUND = "<h1>404 Not Found</h1> <h4>The requested resource was not found on the server.</h4>";

    public DeltaPanelHttpService() throws IOException {
        super(93);
        start(8000, false);
        logInfo("Started the DeltaPanelHttpService");
    }

    public int requestNo = 0;

    @Override
    public Response serve(IHTTPSession session){

        requestNo++;

        String currRequestNo = String.valueOf(requestNo);

        long startMillis = System.currentTimeMillis();

        Map<String, List<String>> params = session.getParameters();

        logInfo("Incoming request from " + session.getRemoteIpAddress() + " (#" + currRequestNo + ")");

        String uri = session.getUri();
        String response = "";

        logInfo("Remote requested " + uri + " (#" + currRequestNo + ")");

        InputStream requestedResource = null;
        if(uri.trim().equals("/")){
            requestedResource = DeltaPanelHttpService.class.getResourceAsStream("/webpage/home.html");
        } else {
            requestedResource = DeltaPanelHttpService.class.getResourceAsStream("/webpage" +uri);
        }

        if(requestedResource == null && (uri.endsWith(".js") || uri.endsWith(".css"))){
            logInfo("Connection closed. Took " + String.valueOf(System.currentTimeMillis() - startMillis) + " ms" + " (#" + currRequestNo + ")");
            return newFixedLengthResponse("");
        }

        if(requestedResource == null ){
            logInfo("Connection closed. Took " + String.valueOf(System.currentTimeMillis() - startMillis) + " ms" + " (#" + currRequestNo + ")");
            return newFixedLengthResponse("<meta http-equiv = \"refresh\" content = \"2; /home.html\" /> 404 Not Found. Redirecting to home page...");
        }

        if(uri.endsWith(".jpg")){
            logInfo("Connection closed. Took " + String.valueOf(System.currentTimeMillis() - startMillis) + " ms" + " (#" + currRequestNo + ")");
            return newChunkedResponse(Response.Status.OK, "image/jpeg", requestedResource);
        }

        if(uri.endsWith(".mp3")){
            logInfo("Connection closed. Took " + String.valueOf(System.currentTimeMillis() - startMillis) + " ms" + " (#" + currRequestNo + ")");
            return newChunkedResponse(Response.Status.OK, "audio/mpeg", requestedResource);
        }

        if(uri.endsWith(".png")){
            logInfo("Connection closed. Took " + String.valueOf(System.currentTimeMillis() - startMillis) + " ms" + " (#" + currRequestNo + ")");
            return newChunkedResponse(Response.Status.OK, "image/png", requestedResource);
        }

        if(uri.endsWith(".ico")){
            logInfo("Connection closed. Took " + String.valueOf(System.currentTimeMillis() - startMillis) + " ms" + " (#" + currRequestNo + ")");
            return newChunkedResponse(Response.Status.OK, "image/ico", requestedResource);
        }

        response = streamToString(requestedResource);

        if(uri.endsWith(".js")){
            logInfo("Connection closed. Took " + String.valueOf(System.currentTimeMillis() - startMillis) + " ms" + " (#" + currRequestNo + ")");
            return newFixedLengthResponse(Response.Status.OK, "text/javascript", response);
        }

        if(uri.endsWith(".css")){
            logInfo("Connection closed. Took " + String.valueOf(System.currentTimeMillis() - startMillis) + " ms" + " (#" + currRequestNo + ")");
            return newFixedLengthResponse(Response.Status.OK, "text/css", response);
        }

        logInfo("Connection closed. Took " + String.valueOf(System.currentTimeMillis() - startMillis) + " ms" + " (#" + currRequestNo + ")");

        return newFixedLengthResponse(response);
    }

    private String streamToString(InputStream is) {
       Scanner s = new Scanner(is).useDelimiter("\\A");
       return s.hasNext() ? s.next() : "";
    }

}
