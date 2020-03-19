package gamepad;

public class PanelGamepad {

    public boolean A = false;
    public boolean B = false;
    public boolean X = false;
    public boolean Y = false;

    public boolean DPAD_UP = false;
    public boolean DPAD_DOWN = false;
    public boolean DPAD_LEFT = false;
    public boolean DPAD_RIGHT = false;

    public boolean START = false;
    public boolean SELECT = false;

    public double LEFT_STICK_X = 0;
    public double LEFT_STICK_Y = 0;

    public double RIGHT_STICK_X = 0;
    public double RIGHT_STICK_Y = 0;

    public PanelGamepad(){ }

    /**
     * WARNING: This is an internal library function, it should not be used in your Team Code
     * The only members of this class you need to use are the UPPER CASE variables.
     */
    public void update(String data){

        String[] split = data.split(";");

        String[] buttons = split[0].split(",");
        String[] axes = split[1].split(",");
        String[] triggers = split[2].split(",");

    }

}
