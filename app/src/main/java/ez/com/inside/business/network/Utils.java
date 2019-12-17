package ez.com.inside.business.network;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

import ez.com.inside.R;

public class Utils {

    public Utils(){

    }

    public void setWifiText(int level, TextView textView){

        String text = "Pas de donnée";
        int color = Color.parseColor("#000000");

        switch (level){
            case 1:
                text = "Très faible";
                color = Color.parseColor("#990000");
                break;
            case  2:
                text = "Faible";
                color = Color.parseColor("#992600");
                break;
            case 3:
                text = "Correct";
                color = Color.parseColor("#739900");
                break;
            case 4:
                text = "Bonne qualité";
                color = Color.parseColor("#009999");
                break;
            case 5:
                text = "Excellent";
                color = Color.parseColor("#009933");
                break;
        }

        textView.setText(text);
        textView.setTextColor(color);

    }
}
