package so.contacts.hub.msgcenter.ui;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.msgcenter.AbstractMessageBussiness;
import so.contacts.hub.msgcenter.IMessageBusiness;
import so.contacts.hub.msgcenter.IPTMessageCenter;
import so.contacts.hub.msgcenter.PTMessageCenterFactory;
import so.contacts.hub.msgcenter.bean.PTMessageBean;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yulong.android.contacts.discover.R;

/**
 * 提醒中心页
 * @author zj 2014-12-18 14:30:57
 *
 */
public class YellowPageMessageCenterActivity extends BaseRemindActivity implements OnClickListener{

    protected ListView listView;
    protected BaseAdapter adapter;
    private List<PTMessageBean> messageList=new ArrayList<PTMessageBean>();
    private IPTMessageCenter messageCenter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.putao_message_center_activity);
        initView();
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        initData();
        
        refresh();
    }

    private void refresh() {
        if (messageList == null || messageList.size() <= 0) {
            //显示无提醒
            findViewById(R.id.my_nodata_layout).setVisibility(View.VISIBLE);
            /*
             * modify by putao_lhq
             * 控制list显隐
             * @start
             */
            listView.setVisibility(View.GONE);
            /*@end by putao_lhq*/
        }else {
            findViewById(R.id.my_nodata_layout).setVisibility(View.GONE);
            /*
             * modify by putao_lhq
             * 控制list显隐
             * @start
             */
            listView.setVisibility(View.VISIBLE);
            /*@end by putao_lhq*/
            adapter.notifyDataSetChanged();
        }
    }
    

    private void initData() {
        messageCenter = PTMessageCenterFactory.getPTMessageCenter();       
        messageList = messageCenter.loadMessage();
        if (messageList==null) {
            messageList=new ArrayList<PTMessageBean>();
        }
        
        if (messageList != null && messageList.size() > 0) {
            java.util.Iterator<PTMessageBean> it = messageList.iterator();
            while(it.hasNext()) {
                PTMessageBean bean = it.next();
                IMessageBusiness business = messageCenter.getService(bean);
                if (null==business) {
                    it.remove();//保证每一条数据业务都能正常识别,去掉错误数据
                }
            }
        }
        LogUtil.d(LogUtil.TAG, "message center init data-messagelist size: " + messageList.size());
        
        //以下模拟数据
        /*messageList=new ArrayList<PTMessageBean>();
        for (int i = 0; i < 300; i++) {
            PTMessageBean bean=new PTMessageBean();
            bean.setAction_type("film_all");
            bean.setTime(System.currentTimeMillis()-i*1000000);
            bean.setDigest("电影-星级穿越 3D票2张");
            bean.setSubject("订单等待支付中");
            
            messageList.add(bean);
        }*/
        
    }

    private void initView() {
        findViewById(R.id.next_setp_layout).setOnClickListener(this);
        
        setTitle(R.string.message_center);
        
        TextView tv=(TextView)findViewById(R.id.exception_desc);
        tv.setText(R.string.putao_msg_none);
        
        ImageView  next_step_img= (ImageView) findViewById(R.id.next_step_img);
        next_step_img.setImageResource(R.drawable.putao_icon_title_set);
        next_step_img.setVisibility(View.VISIBLE);

        if (null!=messageList) {
            listView = (ListView)findViewById(R.id.list);

            adapter = new RemindListAdapter();
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    PTMessageBean bean=messageList.get(position);
                    LogUtil.d(LogUtil.TAG, "message center onItemClick: " + bean + "position: " + position);
                    messageCenter.getService(bean).click(bean, YellowPageMessageCenterActivity.this);
                    MobclickAgentUtil.onEvent(YellowPageMessageCenterActivity.this, UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_CLICK_LIST_ITEM);
                    //add by zj 模拟跳转
//                    Intent intent= new Intent(getApplicationContext(), YellowPageJumpH5Activity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                    intent.putExtra("targetActivityName", YellowPageLotteryOrderDetailActivity.class.getName());
//                    intent.putExtra("url", "http://121.41.60.51:7899/_plugin/head/train.html");
//                    intent.putExtra("title", "彩票订单");//哈哈黑河
//                    mYellowParams  = new YellowParams();
//                    mYellowParams.setUrl("http://121.41.60.51:7899/_plugin/head/train.html");
//                    mYellowParams.setTitle("彩票订单");
//                    intent.putExtra(YellowUtil.TargetIntentParams, mYellowParams);
//                    startActivity(intent);
                    //add by zj end
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_setp_layout:
                Intent intent=new Intent(this,YellowPageMessageCenterSettingActivity.class);
                startActivity(intent);
                MobclickAgentUtil.onEvent(YellowPageMessageCenterActivity.this,
                        UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_SETTING_IN);
                break;

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
        return YellowPageMessageCenterActivity.class.getName();
    }

    @Override
    public boolean needMatchExpandParam() {
        return false;
    }

    @Override
    public Integer remindCode() {
        return null;
    }

    class RemindListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return messageList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LogUtil.d(LogUtil.TAG, "message center getView: " + position);
            PTMessageBean bean= messageList.get(position);
            AbstractMessageBussiness bussiness = messageCenter.getService(bean);
            if (bussiness != null) {
                View view;
                if (convertView==null||convertView.getTag()==null) {
                    view =bussiness.getNotifyView(bean, null);
                }else {
                    view =bussiness.getNotifyView(bean, convertView);
                }
                if (view!=null) {
                    LogUtil.d(LogUtil.TAG, "message center display view: " + position);
                    return view;
                }
            }
            
            messageList.remove(position);
            LogUtil.d(LogUtil.TAG, "message center display size: " + messageList.size());
            if (messageList.size() == position) {
                refresh();
                notifyDataSetChanged();
                return new View(YellowPageMessageCenterActivity.this);
            } else {
                if (position != 0) {
                    position--;
                }
                return getView(position, convertView, parent);
            }
        }
    }
    
}
