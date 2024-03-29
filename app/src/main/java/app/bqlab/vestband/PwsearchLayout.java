package app.bqlab.vestband;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Objects;

public class PwsearchLayout extends LinearLayout {

    public PwsearchLayout(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_pwsearch, this);
        init();
    }

    private void init() {
        findViewById(R.id.pwsearch_close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        findViewById(R.id.pwsearch_send).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = ((EditText) findViewById(R.id.pwsearch_email)).getText().toString();
                if (Objects.equals(getContext().getSharedPreferences("idpw", Context.MODE_PRIVATE).getString(id, "none"), "none"))
                    Toast.makeText(getContext(), "이메일을 다시 확인하세요.", Toast.LENGTH_LONG).show();
                else {
                    new AlertDialog.Builder(getContext())
                            .setMessage("안내 메일을 전송할 수 없습니다.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    close();
                }
            }
        });
    }

    private void close() {
        final FrameLayout p = ((FrameLayout) getParent());
        for (int i = 0; i < p.getChildCount(); i++) {
            p.getChildAt(i).setClickable(true);
            p.getChildAt(i).setFocusable(true);
        }
        p.removeView(PwsearchLayout.this);
    }
}
