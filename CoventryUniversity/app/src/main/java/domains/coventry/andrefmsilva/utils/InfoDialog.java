/*:::::::::::::::::::::::::::::::::::::::::::::::::::
 : Copyright 2018 Andre Silva. All rights reserved. :
 : Contact: mateussa@uni.coventry.ac.uk             :
 :                                                  :
 : Check my work at,                                :
 : https://github.coventry.ac.uk/mateussa           :
 : https://andrefmsilva.coventry.domains            :
 :                                                  :
 : InfoDialog.java                                  :
 : Last modified 06 Dec 2018                        :
 :::::::::::::::::::::::::::::::::::::::::::::::::::*/

package domains.coventry.andrefmsilva.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
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
        return newIntance(type, title, message, -1);
    }

    /**
     * Creates a new instance of the dialog with the given input, with message
     *
     * @param type     Type of the dialog
     * @param title    String to show as the title
     * @param message  String to show as the dialog message
     * @param drawable Resource ID for the drawable to add to the message
     * @return A dialog that can be started and kept as reference
     */
    public static InfoDialog newIntance(@NonNull InfoDialog.Type type, String title, String message, @DrawableRes int drawable)
    {
        InfoDialog infoDialog = new InfoDialog();
        Bundle bundle = new Bundle();

        bundle.putSerializable("type", type);
        bundle.putSerializable("title", title);

        if (message != null)
            bundle.putSerializable("message", message);

        if (drawable != -1)
            bundle.putSerializable("drawable", drawable);

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

            String message = getArguments().getString("message", null);
            if (message != null)
                ((TextView) dialog.findViewById(R.id.dialog_message)).setText(message);

            int drawableID = getArguments().getInt("drawable", -1);
            if (drawableID != -1)
                ((TextView) dialog.findViewById(R.id.dialog_message)).setCompoundDrawablesRelativeWithIntrinsicBounds(0, drawableID, 0, 0);

            switch ((Type) Objects.requireNonNull(getArguments().getSerializable("type")))
            {
                case OK:
                    dialog.findViewById(R.id.dialog_confirm).setOnClickListener(v -> dialog.dismiss());

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
