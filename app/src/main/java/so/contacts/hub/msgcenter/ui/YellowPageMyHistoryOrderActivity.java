
package so.contacts.hub.msgcenter.ui;

import java.util.List;

import so.contacts.hub.msgcenter.PTMessageCenterFactory;
import so.contacts.hub.msgcenter.PTOrderCenter;
import so.contacts.hub.msgcenter.bean.PTOrderBean;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.util.LogUtil;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.yulong.android.contacts.discover.R;

/**
 * 历史订单页
 * @author zj 2014-12-18 14:31:18
 *
 */
public class YellowPageMyHistoryOrderActivity extends BaseRemindActivity implements OnClickListener {

    protected ListView listView;

    protected OrderListAdapter adapter;

    private PTOrderCenter orderCenter;

    private List<PTOrderBean> expiredOrders;

    public static final String HISTORY_ORDERS = "history_orders";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.putao_message_center_activity);
        initData();
        initView();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
    }

    private void initData() {
        orderCenter = PTMessageCenterFactory.getPTOrderCenter();
        if (getIntent()!=null) {
            expiredOrders = (List<PTOrderBean>)getIntent().getSerializableExtra(
                    YellowPageMyHistoryOrderActivity.HISTORY_ORDERS);
        }

    }

    private void initView() {
        setTitle(R.string.order_history);
        
        TextView tv=(TextView)findViewById(R.id.exception_desc);
        tv.setText(R.string.putao_order_history_none);
        
        if (expiredOrders == null||expiredOrders.size()<=0) {
            // 无历史订单
        	findViewById(R.id.list).setVisibility(View.GONE);//add by ls 2015-01-23
            findViewById(R.id.my_nodata_layout).setVisibility(View.VISIBLE);
        } else {
            listView = (ListView)findViewById(R.id.list);

            adapter = new OrderListAdapter(expiredOrders, orderCenter);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    PTOrderBean bean = expiredOrders.get(position);
                    orderCenter.getService(bean).click(bean, YellowPageMyHistoryOrderActivity.this);

                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }

    }

    @Override
    public String getServiceNameByUrl() {
        return null;
    }

    @Override
    public String getServiceName() {
        return YellowPageMyHistoryOrderActivity.class.getName();
    }

    @Override
    public boolean needMatchExpandParam() {
        return false;
    }

    @Override
    public Integer remindCode() {
        return null;
    }

}
