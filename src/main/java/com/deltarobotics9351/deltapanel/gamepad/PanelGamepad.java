package com.deltarobotics9351.deltapanel.gamepad;

import com.deltarobotics9351.deltapanel.DeltaPanel;

import java.lang.reflect.Field;

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

    public boolean LEFT_BUMPER = false;
    public boolean RIGHT_BUMPER = false;

    public boolean LEFT_STICK_BUTTON = false;
    public boolean RIGHT_STICK_BUTTON = false;

    public double LEFT_STICK_X = 0;
    public double LEFT_STICK_Y = 0;

    public double RIGHT_STICK_X = 0;
    public double RIGHT_STICK_Y = 0;

    public double LEFT_TRIGGER = 0;
    public double RIGHT_TRIGGER = 0;

    public PanelGamepad(){ }

    /**
     * WARNING: This is an internal library function, it should not be used in your Team Code
     * The only members of this class you need to use are the UPPER CASE variables.
     */
    public void update(String data){

        if(data.trim().equalsIgnoreCase("unlinked")){
            try {
                setEverythingtoFalseOrZero();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return;
        }

        //System.out.println(data);

        String[] split = data.split(";");

        String[] buttonsStr = split[0].split(","); //get values as Strings
        String[] axesStr = split[1].split(",");
        String[] triggersStr = split[2].split(",");

        Boolean[] buttons = new Boolean[buttonsStr.length]; //allocate arrays in the type the output should be
        Double[] axes = new Double[axesStr.length];
        Double[] triggers = new Double[triggersStr.length];

        for(int i = 0 ; i < buttonsStr.length ; i++){ //convert buttons strings to booleans
            buttons[i] = Boolean.valueOf(buttonsStr[i]);
        }

        for(int i = 0 ; i < axesStr.length ; i++){ //convert axes strings to doubles
            axes[i] = Double.parseDouble(axesStr[i]);
        }

        for(int i = 0 ; i < triggersStr.length ; i++){ //convert triggers strings to doubles
            triggers[i] = Double.parseDouble(triggersStr[i]);
        }

        //------------ DEFINE PUBLIC VARIABLES ------------

        //the following part of the code defines the booleans and doubles public variables, the location of each value in the arrays were discovered by testing with a real com.deltarobotics9351.deltapanel.gamepad and watching which of the boolean/doubles changed when an specific button was pressed

        A = buttons[0];
        B = buttons[1];
        X = buttons[2];
        Y = buttons[3];

        START = buttons[9];
        SELECT = buttons[8];

        DPAD_UP = buttons[12];
        DPAD_DOWN = buttons[13];
        DPAD_LEFT = buttons[14];
        DPAD_RIGHT = buttons[15];

        LEFT_STICK_BUTTON = buttons[10];
        RIGHT_STICK_BUTTON = buttons[11];

        LEFT_BUMPER = buttons[4];
        RIGHT_BUMPER = buttons[5];

        LEFT_STICK_X = axes[0];
        LEFT_STICK_Y = axes[1];

        RIGHT_STICK_X = axes[2];
        RIGHT_STICK_Y = axes[3];

        LEFT_TRIGGER = triggers[0];
        RIGHT_TRIGGER = triggers[1];

    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append( " PanelGamepad {" );
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();

        //print field names paired with their values
        for ( Field field : fields  ) {
            result.append("  ");
            try {
                result.append( field.getName() );
                result.append(": ");
                //requires access to private field:
                result.append( field.get(this) );
            } catch ( IllegalAccessException ex ) {
                System.out.println(ex);
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }

    private void setEverythingtoFalseOrZero() throws IllegalAccessException {
        Field[] fields = this.getClass().getDeclaredFields();

        for ( Field field : fields ) {
            if(field.getType() == double.class){
                field.set(this, 0);
            }else if(field.getType() == boolean.class){
                field.set(this, false);
            }
        }

    }

}