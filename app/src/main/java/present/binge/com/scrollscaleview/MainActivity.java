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

        TextView tv = (TextView) findViewById(R.id.tv);

        List list = new ArrayList(10);
        for (int i = 1; i <= 10; i++) {
            list.add("a" + i);
        }

        ScalePickView s = (ScalePickView) findViewById(R.id.ssv);
        s.setRangeDataList(list);
//        s.setCurrenValuePosition(list.size()-1);
//        ScrollScaleView s = (ScrollScaleView) findViewById(R.id.ssv);
//        s.setRangeDataList(list);
//
//        ScrollScaleView s2 = (ScrollScaleView) findViewById(R.id.ssv2);
//        s2.setRangeDataList(list);
    }
}
