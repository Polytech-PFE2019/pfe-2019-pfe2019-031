package ez.com.inside.business.usagetime;

public class Utils {

    public Utils(){

    }

    public String getDayName(int value){
        switch (value){
            case 1:
                return "Lun.";
            case 2:
                return "Mar.";
            case 3:
                return "Mer.";
            case 4:
                return "Jeu.";
            case 5:
                return "Ven.";
            case 6:
                return "Sam.";
            case 7:
                return "Dim.";

        }
        return "";
    }
}
