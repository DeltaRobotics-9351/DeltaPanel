import fi.iki.elonen.NanoHTTPD;

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
        System.out.println("DeltaPanel: Started the DeltaPanelHttpService");
    }

    public int requestNo = 0;

    @Override
    public Response serve(IHTTPSession session){

        requestNo++;

        String currRequestNo = String.valueOf(requestNo);

        long startMillis = System.currentTimeMillis();

        Map<String, List<String>> params = session.getParameters();

        System.out.println("DeltaPanel: Incoming request from " + session.getRemoteIpAddress() + " (#" + currRequestNo + ")");

        String uri = session.getUri();
        String response = "";

        System.out.println("DeltaPanel: Remote requested " + uri + " (#" + currRequestNo + ")");


        InputStream requestedResource = null;
        if(uri.trim().equals("/")){

            requestedResource = DeltaPanelHttpService.class.getResourceAsStream("/webpage/home.html");
        } else {
            requestedResource = DeltaPanelHttpService.class.getResourceAsStream("/webpage" +uri);
        }

        if(requestedResource == null && (uri.endsWith(".js") || uri.endsWith(".css"))){
            System.out.println("DeltaPanel: Connection closed. Took " + String.valueOf(System.currentTimeMillis() - startMillis) + " ms" + " (#" + currRequestNo + ")");
            return newFixedLengthResponse("");
        }

        if(requestedResource == null ){
            System.out.println("DeltaPanel: Connection closed. Took " + String.valueOf(System.currentTimeMillis() - startMillis) + " ms" + " (#" + currRequestNo + ")");
            return newFixedLengthResponse("<meta http-equiv = \"refresh\" content = \"2; /home.html\" /> 404 Not Found. Redirecting to home page...");
        }

        if(uri.endsWith(".jpg")){
            return newChunkedResponse(Response.Status.OK, "image/jpeg", requestedResource);
        }

        if(uri.endsWith(".mp3")){
            return newChunkedResponse(Response.Status.OK, "audio/mpeg", requestedResource);
        }

        if(uri.endsWith(".png")){
            return newChunkedResponse(Response.Status.OK, "image/png", requestedResource);
        }

        if(uri.endsWith(".ico")){
            return newChunkedResponse(Response.Status.OK, "image/ico", requestedResource);
        }

        response = streamToString(requestedResource);

        if(uri.endsWith(".js")){
            return newFixedLengthResponse(Response.Status.OK, "text/javascript", response);
        }

        if(uri.endsWith(".css")){
            return newFixedLengthResponse(Response.Status.OK, "text/css", response);
        }

        System.out.println("DeltaPanel: Connection closed. Took " + String.valueOf(System.currentTimeMillis() - startMillis) + " ms" + " (#" + currRequestNo + ")");

        return newFixedLengthResponse(response);
    }

    private String streamToString(InputStream is) {
       Scanner s = new Scanner(is).useDelimiter("\\A");
       return s.hasNext() ? s.next() : "";
    }

}
