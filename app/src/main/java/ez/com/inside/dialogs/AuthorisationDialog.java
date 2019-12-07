package ez.com.inside.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import ez.com.inside.R;
import ez.com.inside.activities.StartActivity;

/**
 * Created by Charly on 28/11/2017.
 */

public class AuthorisationDialog extends DialogFragment
{
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogBase);
        builder.setTitle(R.string.dialog_authorisation_title);
        builder.setMessage(R.string.dialog_authorisation_body);
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
                ((StartActivity) getActivity()).requestUsageStatsPermission();
            }
        });

        return builder.create();
    }
}
