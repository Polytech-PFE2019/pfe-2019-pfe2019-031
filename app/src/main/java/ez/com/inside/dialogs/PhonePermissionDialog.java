package ez.com.inside.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;

import ez.com.inside.R;
import ez.com.inside.activities.network.NetworkActivity;

/**
 * Created by Monz on 11/12/2017.
 */

public class PhonePermissionDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogBase);
        builder.setTitle(R.string.dialog_phone_permission_title);
        builder.setMessage(R.string.dialog_phone_permission_body);
        setCancelable(false);

        builder.setNegativeButton("Quitter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getActivity().finish();
            }
        });

        builder.setPositiveButton("Accepter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((NetworkActivity) getActivity()).requestUsageStatsPermission();
            }
        });

        return builder.create();
    }
}
