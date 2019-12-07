package ez.com.inside.activities.settings;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import ez.com.inside.R;

/**
 * Created by Charly on 28/11/2017.
 */
public class SettingsActivity extends AppCompatActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();
    }
}
