package ez.com.inside.activities.permissions;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ez.com.inside.R;
import ez.com.inside.activities.listeners.OnClickListener;
import ez.com.inside.business.permission.AppPermissions;

/**
 * Created by Charly on 11/12/2017.
 */

public class FragmentListPermissions extends Fragment
{
    private List<AppPermissions> apps;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_permissionslist, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if(args != null)
            apps = args.getParcelableArrayList(PermissionsActivity.EXTRA_APPPERMISSIONS);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        initializeRecyclerView();
    }

    private void initializeRecyclerView()
    {
        RecyclerView recyclerView = getView().findViewById(R.id.gridview_apps_autho);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new PermissionsAdapter(getContext(), apps, new OnClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getContext(), PermissionsDetailActivity.class);
                Bundle b = new Bundle();
                b.putParcelable("appPermissions",apps.get(position));
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new ItemDecoration());
    }
}
