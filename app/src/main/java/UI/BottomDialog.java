package UI;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import game.danielesimone.briscola.R;
import gameEngine.Utility;

public class BottomDialog extends BottomSheetDialog{
    public BottomDialog(String title, ArrayList<Button> buttons, @NonNull @NotNull Context context){
        super(context, R.style.SheetDialog);
        this.setContentView(R.layout.bottom_dialog);

        LayoutInflater inflater = LayoutInflater.from(context);

        TextView dialogTitle = this.findViewById(R.id.bottom_dialog_title);
        dialogTitle.setText(title);

        for(Button buttonToAdd : buttons){
            final LinearLayout gallery = this.findViewById(R.id.bottom_dialog_layout);
            int layoutToAdd = R.layout.single_black_button;

            View view = inflater.inflate(layoutToAdd, gallery, false);

            TextView textView = view.findViewById(R.id.bottom_dialog_text_view);
            textView.setText(buttonToAdd.getText());

            View button = view.findViewById(R.id.bottom_dialog_button);
            button.setOnClickListener(v -> {
                buttonToAdd.performClick();
                this.dismiss();
            });

            gallery.addView(view);
        }

        Utility.ridimensionamento((AppCompatActivity) context, findViewById(R.id.bottom_dialog_parent));
        this.show();
    }
}
