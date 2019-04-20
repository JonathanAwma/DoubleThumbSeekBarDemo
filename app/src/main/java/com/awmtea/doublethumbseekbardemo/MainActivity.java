package com.awmtea.doublethumbseekbardemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.awmtea.rimangseekbar.RimAngSeekBar;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView valueTxt = findViewById(R.id.values);
        final RimAngSeekBar rimAngSeekBar = findViewById(R.id.rimang_seekbar);
        int lowerValue = 60;
        int upperValue = 70;
        rimAngSeekBar.setLowerValue(lowerValue);
        rimAngSeekBar.setUpperValue(upperValue);
        rimAngSeekBar.setOnValueChange(new RimAngSeekBar.OnValueChange() {
            @Override
            public void onValueChanged(int lower, int upper) {
                String text = String.format(Locale.getDefault(), "%1s : %2d - %3d ", "From-To", rimAngSeekBar.getLowerValue(), rimAngSeekBar.getUpperValue());
                valueTxt.setText(text);
            }
        });
        String text = String.format(Locale.getDefault(), "%1s : %2d - %3d ", "From-To", lowerValue, upperValue);
        valueTxt.setText(text);
    }
}
