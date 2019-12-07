package ez.com.inside.activities.permissions;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ez.com.inside.R;
import ez.com.inside.business.permission.AppPermissions;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class PermissionsDetailActivity extends AppCompatActivity
{
    private final String HELP_GOSETTINGS = "help.gosettings";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_detail);

        TextView title = findViewById(R.id.AppPermissionDetail_title);
        ImageView logo = findViewById(R.id.AppPermissionDetail_logo);
        ImageView settings = findViewById(R.id.AppPermissionDetail_settings);

        Bundle b = getIntent().getExtras();
        if(b!=null)
        {
            final AppPermissions appPermissions = b.getParcelable("appPermissions");

            if (appPermissions != null)
            {

                title.setText(appPermissions.getAppName());
                Drawable icon = null;
                try
                {
                    icon = getPackageManager().getApplicationIcon(appPermissions.getPackageName());
                }
                catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                logo.setImageDrawable(icon);

                settings.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", appPermissions.getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });

                initializeRecyclerView(appPermissions);
            }
            else {
                title.setText(":(");
            }
        }
        else{
            title.setText(":'(");
        }

        new MaterialShowcaseView.Builder(this)
                .setTarget(settings)
                .setDismissText("OK !")
                .setContentText(getString(R.string.help_permissionsdetails_go_settings))
                .setDelay(500) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse(HELP_GOSETTINGS) // provide a unique ID used to ensure it is only shown once
                .show();
    }

    private void initializeRecyclerView(AppPermissions appPermissions)
    {
        RecyclerView recyclerView = findViewById(R.id.AppPermissionDetail_listview);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new PermissionDetailAdapter(
                this, appPermissions.getPermissionsGroup());
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new DetailItemDecoration());
    }
}
