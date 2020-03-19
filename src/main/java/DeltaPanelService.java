import blocks.BlocksManager;

import java.io.IOException;

public class DeltaPanelService {

    public static DeltaPanelHttpService httpService;
    public static DeltaPanelWebSocketService webSocketService;
    public static Thread serviceThread;

    private static boolean initialized = false;

    public static void main(String[] args){
        if(initialized) return;

        initialized = true;
        serviceThread = new Thread(new ServiceRunnable());
        serviceThread.start();
        BlocksManager.update();
    }

    static class ServiceRunnable implements Runnable {

        @Override
        public void run() {
            try {
                httpService = new DeltaPanelHttpService();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("DeltaPanel: Unable to start DeltaPanelHttpService");
            }

            try {
                webSocketService = new DeltaPanelWebSocketService();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("DeltaPanel: Unable to start DeltaPanelHttpService");
            }

        }
    }

}
