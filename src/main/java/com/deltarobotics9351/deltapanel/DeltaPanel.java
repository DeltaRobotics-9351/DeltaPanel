package com.deltarobotics9351.deltapanel;

import com.deltarobotics9351.deltapanel.blocks.BlocksManager;
import com.deltarobotics9351.deltapanel.gamepad.PanelGamepad;

import javax.swing.*;
import java.io.IOException;

public class DeltaPanel {

    public static DeltaPanelHttpService httpService;
    public static DeltaPanelWebSocketService webSocketService;
    public static Thread serviceThread;

    public static boolean initialized = false;

    public static final PanelGamepad panelGamepad1 = new PanelGamepad();

    public static final PanelGamepad panelGamepad2 = new PanelGamepad();

    public static JTextArea textfield1 = new JTextArea("bruh");

    public static void main(String[] args){
        final JFrame frame = new JFrame("JTextArea Demo");
        frame.setSize(frame.getWidth() + 40, frame.getHeight() );
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JPanel panel = new JPanel();

        //textfield1.setFont(new Font("Serif", Font.ITALIC, 16));
        textfield1.setLineWrap(true);
        textfield1.setWrapStyleWord(true);
        textfield1.setOpaque(false);
        textfield1.setEditable(false);

        panel.add(textfield1);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

        initialize();
    }

    public static void initialize(){
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
                System.out.println("com.deltarobotics9351.deltapanel.DeltaPanel: Unable to start com.deltarobotics9351.deltapanel.DeltaPanelHttpService");
            }

            try {
                webSocketService = new DeltaPanelWebSocketService();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("com.deltarobotics9351.deltapanel.DeltaPanel: Unable to start com.deltarobotics9351.deltapanel.DeltaPanelHttpService");
            }

            while(true){
                DeltaPanel.textfield1.setText(DeltaPanel.panelGamepad2.toString());
            }

        }
    }

}
