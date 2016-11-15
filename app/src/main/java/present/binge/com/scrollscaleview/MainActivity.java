package present.binge.com.scrollscaleview;

import android.graphics.Color;
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
            list.add("a" + i);
        }

        ScalePickView s = (ScalePickView) findViewById(R.id.ssv);
        s.setRangeDataList(list);
        s.setCurrentValue("a3");
//        s.setCurrenValuePosition(3);
        tv.setText(s.getCurrentValue());
//        s.setCurrenValuePosition(list.size()-1);
        s.setScaleColor(Color.parseColor("#d3d3d4"));
        s.setTextColor(Color.parseColor("#d3d3d4"));
        s.setLineMargin(40);
        s.setLongLineLength(86);
//        s.setMask(new MaskView(this));
        s.setOnScrollListener(new ScrollScaleView.OnScrollListener() {
            @Override
            public void onScrollCompleted(String value) {
                tv.setText(value);
            }
        });
//        s.needBottomLine(false);
//


        TextView tv2 = (TextView) findViewById(R.id.tv2);
        ScrollScaleView s2 = (ScrollScaleView) findViewById(R.id.ssv2);
        s2.setRangeDataList(list);

        s.setScaleColor(Color.parseColor("#d3d3d4"));
        s.setTextColor(Color.parseColor("#d3d3d4"));
        s.setLineMargin(40);
        s.setLongLineLength(86);
    }
}
