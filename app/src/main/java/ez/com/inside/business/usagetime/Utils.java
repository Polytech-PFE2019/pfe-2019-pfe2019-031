package ez.com.inside.business.usagetime;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public Utils(){

    }

    public List<Integer> setDayOrder(int firstDay){
        List<Integer> values = new ArrayList<>();
        int temp = 1;
        Boolean restart = false;
        for(int index = 1; index < 7; index ++){
            if(firstDay+index == 7){
                values.add(firstDay+index);
                restart = true;

            } else if (firstDay == 7) {
                restart = true;
            }


            if(restart){
                values.add(temp);
                temp++;
            }
            else{
                values.add(firstDay+index);
            }
        }

        if(values.size() == 6){
            values.add(7);
        }
        return values;
    }

    public String getDayName(int value){
        switch (value){
            case 1:
                return "Dim.";
            case 2:
                return "Lun.";
            case 3:
                return "Mar.";
            case 4:
                return "Mer.";
            case 5:
                return "Jeu.";
            case 6:
                return "Ven.";
            case 7:
                return "Sam.";

        }
        return "";
    }
}
