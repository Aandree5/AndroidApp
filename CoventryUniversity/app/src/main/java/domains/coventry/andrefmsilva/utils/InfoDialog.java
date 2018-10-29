package domains.coventry.andrefmsilva.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

import domains.coventry.andrefmsilva.coventryuniversity.R;

public class InfoDialog extends DialogFragment
{
    public enum Type
    {
        NO_ACTION,
        OK,
        RETRY
    }

    /**
     * Creates a new instance of the dialog with the given input
     *
     * @param type  Type of the dialog
     * @param title String to show as the title
     * @return A dialog that can be started and kept as reference
     */
    public static InfoDialog newIntance(@NonNull InfoDialog.Type type, String title)
    {
        return newIntance(type, title, null);
    }

    /**
     * Creates a new instance of the dialog with the given input, with message
     *
     * @param type    Type of the dialog
     * @param title   String to show as the title
     * @param message String to show as the dialog message
     * @return A dialog that can be started and kept as reference
     */
    public static InfoDialog newIntance(@NonNull InfoDialog.Type type, String title, String message)
    {
        InfoDialog infoDialog = new InfoDialog();
        Bundle bundle = new Bundle();

        bundle.putSerializable("type", type);
        bundle.putSerializable("title", title);

        if (message != null)
            bundle.putSerializable("message", message);

        infoDialog.setArguments(bundle);

        return infoDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Dialog dialog = new Dialog(Objects.requireNonNull(getContext()));

        if (getArguments() != null)
        {
            dialog.setContentView(R.layout.layout_infodialog);
            ((TextView) dialog.findViewById(R.id.dialog_title)).setText(getArguments().getString("title"));

            String message = getArguments().getString("message");
            if (message != null)
                ((TextView) dialog.findViewById(R.id.dialog_message)).setText(message);

            switch ((Type) Objects.requireNonNull(getArguments().getSerializable("type")))
            {
                case OK:
                    dialog.findViewById(R.id.dialog_progressbar).setVisibility(View.GONE);
                    dialog.findViewById(R.id.dialog_cancel).setVisibility(View.GONE);
                    break;

                case RETRY:
                    ((Button) dialog.findViewById(R.id.dialog_confirm)).setText(getString(R.string.infodialog_retry));

                    dialog.findViewById(R.id.dialog_progressbar).setVisibility(View.GONE);
                    dialog.findViewById(R.id.dialog_cancel).setOnClickListener(v -> dialog.cancel());
                    break;

                case NO_ACTION:
                    dialog.findViewById(R.id.dialog_message).setVisibility(View.GONE);
                    dialog.findViewById(R.id.dialog_confirm).setVisibility(View.GONE);
                    dialog.findViewById(R.id.divider).setVisibility(View.GONE);
                    dialog.findViewById(R.id.dialog_cancel).setVisibility(View.GONE);
                    dialog.findViewById(R.id.dialog_progressbar).setVisibility(View.VISIBLE);
                    break;
            }
        }

        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog)
    {
        super.onCancel(dialog);

        Objects.requireNonNull(getActivity()).onBackPressed();
    }
}
