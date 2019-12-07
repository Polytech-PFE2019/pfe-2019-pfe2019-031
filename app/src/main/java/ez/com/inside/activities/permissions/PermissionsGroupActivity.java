package ez.com.inside.activities.permissions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import ez.com.inside.R;
import ez.com.inside.activities.listeners.OnClickListener;
import ez.com.inside.business.permission.PermissionGroupInfo;
import ez.com.inside.business.permission.PermissionsFinder;

public class PermissionsGroupActivity extends AppCompatActivity {

    private List<PermissionGroupInfo> permissionGroupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions_group);

        setTitle(getString(R.string.permissiongroup_title));

        initializeRecyclerView();
    }

    private void initializeRecyclerView(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.PermissionsGroupActivity_RecyclerView);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        permissionGroupList = PermissionsFinder.getPermissionsGroupsInfo(this.getPackageManager());

        RecyclerView.Adapter adapter = new PermissionsGroupAdapter(this, permissionGroupList, new OnClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(PermissionsGroupActivity.this, PermissionsActivity.class);
                Bundle b = new Bundle();
                b.putParcelable("info",permissionGroupList.get(position));
                intent.putExtras(b);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
