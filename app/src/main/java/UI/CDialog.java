package UI;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import game.danielesimone.briscola.R;

public class CDialog extends Dialog implements android.view.View.OnClickListener {
    public Activity c;
    public String title;
    public Runnable callback;

    public CDialog(Activity c, String title, Runnable callback) {
        super(c);
        this.c = c;
        this.title = title;
        this.callback = () -> {
            if(callback != null)
                callback.run();

            this.dismiss();
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog);

        TextView title = this.findViewById(R.id.title);
        Button btnYes = this.findViewById(R.id.btn_yes);
        Button btnNo = this.findViewById(R.id.btn_no);

        title.setText(this.title);
        btnYes.setOnClickListener(v -> this.callback.run());
        btnNo.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                c.finish();
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}