package mihael.galinac.com.datacollectora;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by galin on 09-Apr-18.
 */

public class FirstScreen extends AppCompatActivity {
    private Button daljeBut;
    private CheckBox cb1, cb2, cb3;
    private TextView tipBiciklaText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.screen_first);



//        tipBiciklaText = findViewById(R.id.tipBicikla);
        daljeBut = findViewById(R.id.daljeBut);

//        cb1 = findViewById(R.id.CB1);
//        cb2 = findViewById(R.id.CB2);
//        cb3 = findViewById(R.id.CB3);

        daljeBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstScreen.this, MainActivityJava.class);
                startActivity(intent);
            }

        });
//    public void checkone(View v){
//        if (cb1.isChecked()){
//            tipBiciklaText.setText("Odabrani tip bicikla je cestovni bicikl");
//        }
//        else if (cb2.isChecked()){
//            tipBiciklaText.setText("Odabrani tip bicikla je trekking bicikl");
//        }
//        else if (cb3.isChecked()){
//            tipBiciklaText.setText("Odabrani tip bicikla je MTB");
//        };
    }
}
