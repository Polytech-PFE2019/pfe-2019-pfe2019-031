package ez.com.inside.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import ez.com.inside.R;

/**
 * Created by Charly on 05/12/2017.
 */

public class PointInformationDialog extends DialogFragment
{
    private String value;

    public PointInformationDialog() {}

    public void setValue(String value) {this.value = value;}

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogBase);
        builder.setTitle("Informations");
        builder.setMessage("Temps passé : " + value);

        return builder.create();
    }
}
