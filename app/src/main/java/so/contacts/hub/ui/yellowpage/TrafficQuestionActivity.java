package so.contacts.hub.ui.yellowpage;

import com.yulong.android.contacts.discover.R;

import so.contacts.hub.remind.BaseRemindActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class TrafficQuestionActivity extends BaseRemindActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putao_traffic_question_layout);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(R.string.putao_charge_question);
        
        findViewById(R.id.back_layout).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public String getServiceNameByUrl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getServiceName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean needMatchExpandParam() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_layout:
                finish();
                break;

            default:
                break;
        }

    }

    @Override
    public Integer remindCode() {
        // TODO Auto-generated method stub
        return null;
    }

}
