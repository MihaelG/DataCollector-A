package mihael.galinac.com.datacollectora;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by galin on 09-Apr-18.
 */

public class FirstScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.screen_first);
        TextView textView = findViewById(R.id.enterMail);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstScreen.this, MainActivityJava.class);
                startActivity(intent);
            }
        });
    }
}
