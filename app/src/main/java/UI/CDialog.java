package UI;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import game.danielesimone.briscolav10.R;

public class CDialog extends Dialog implements android.view.View.OnClickListener {
    public Activity c;
    public String title;
    public Runnable callback;

    public CDialog(Activity c, String title, Runnable callback) {
        super(c);
        this.c = c;
        this.title = title;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog);
        ((TextView)findViewById(R.id.title)).setText(CDialog.this.title);
        findViewById(R.id.btn_yes).setOnClickListener(v -> CDialog.this.callback.run());
        findViewById(R.id.btn_no).setOnClickListener(v -> dismiss());
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