package present.binge.com.scrollscaleview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView tv = (TextView) findViewById(R.id.tv);

        List list = new ArrayList(100);
        for (int i = 1; i <= 100; i++) {
            list.add("g" + i);
        }

        ScalePickView s = (ScalePickView) findViewById(R.id.ssv);
        s.setRangeDataList(list);
//        s.setCurrentValue("a3");
//        s.setCurrenValuePosition(3);

//        s.setMaxValue(30);
//        s.setMinValue(10);
//        s.setStepUnit(1);
        s.setMultiple(5);
//        s.setCurrentValue("30");
        s.setCurrentValuePosition(1);

        tv.setText(s.getCurrentValue());
//        s.setCurrenValuePosition(list.size()-1);
//        s.setScaleColor(Color.parseColor("#d3d3d4"));
//        s.setTextColor(Color.parseColor("#d3d3d4"));
        s.setLineMargin(40);
//        s.setLongLineLength(86);

//        s.setMask(new MaskView(this));
        s.setOnScrollListener(new ScrollScaleView.OnScrollListener() {
            @Override
            public void onScrollCompleted(String value) {
                tv.setText(value);
            }
        });
        s.needBottomLine(true);


        final TextView tv2 = (TextView) findViewById(R.id.tv2);
        ScalePickView s2 = (ScalePickView) findViewById(R.id.ssv2);
        s2.setRangeDataList(list);
//        s2.setMaxValue(50);
//        s2.setMinValue(20);
//        s2.setStepUnit(10);
        s2.setMultiple(5);
//        s2.setCurrentValuePosition(3);
        tv2.setText(s2.getCurrentValue());

//        s2.setScaleColor(Color.parseColor("#d3d3d4"));
//        s2.setTextColor(Color.parseColor("#d3d3d4"));
        s2.setLineMargin(40);
        s2.setLongLineLength(86);
        s2.needBottomLine(true);
        s2.setOnScrollListener(new ScrollScaleView.OnScrollListener() {
            @Override
            public void onScrollCompleted(String value) {
                tv2.setText(value);
            }
        });
    }
}
