package so.contacts.hub.smartscene;

import com.yulong.android.contacts.discover.R;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import so.contacts.hub.msgcenter.MessageDeleteListener;
import so.contacts.hub.msgcenter.PTMessageCenterFactory;
import so.contacts.hub.msgcenter.bean.PTOrderBean;
import so.contacts.hub.remind.BaseRemindActivity;

public class BaseDetailAcitvity extends BaseRemindActivity {
    public final static String ENTRY="entry";
    public final static String ORDER_NO="order_no";
    public final static int ENTRY_NOTIFICATION=2;
    public final static int ENTRY_REMIND_CENTER=1;
    private int entry;
    private String order_no;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent=getIntent();
        if (intent!=null) {
            entry = intent.getIntExtra(ENTRY, 0);
            order_no = intent.getStringExtra(ORDER_NO);
        }
        
        if (!TextUtils.isEmpty(order_no)) {
            PTOrderBean bean=PTMessageCenterFactory.getPTOrderCenter().getOrderByOrderNumber(order_no);
            if (bean!=null&&bean.getView_status() == 0) {
                bean.setView_status(1);
                PTMessageCenterFactory.getPTOrderCenter().updateOrderData(bean);
            }
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        if ((entry==ENTRY_REMIND_CENTER||entry==ENTRY_NOTIFICATION)&&!TextUtils.isEmpty(order_no)) {
            TextView delete=(TextView)findViewById(R.id.next_step_btn);
            delete.setVisibility(View.VISIBLE);
            delete.setText(R.string.message_close);
            findViewById(R.id.next_setp_layout).setOnClickListener(new MessageDeleteListener(order_no,this));
        }
    }
    
}
