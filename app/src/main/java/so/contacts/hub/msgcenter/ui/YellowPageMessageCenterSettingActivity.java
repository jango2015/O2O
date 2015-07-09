
package so.contacts.hub.msgcenter.ui;

import java.util.List;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.msgcenter.AbstractMessageBussiness;
import so.contacts.hub.msgcenter.IPTMessageCenter;
import so.contacts.hub.msgcenter.PTMessageCenterFactory;
import so.contacts.hub.msgcenter.PTMessageCenterSettings;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yulong.android.contacts.discover.R;

/**
 * 提醒 中心-设置
 * @author zj 2014-12-18 14:15:06
 *
 */
public class YellowPageMessageCenterSettingActivity extends BaseRemindActivity implements
        OnClickListener {

    protected ListView listView;

    private int expandPosion=0;
    
    private int[] mTitles = {
            R.string.notify_type, R.string.vibrate_notify, R.string.sound_notify,R.string.notify_event
    };

    private ExpandableListAdapter adapter;

    private List<AbstractMessageBussiness> bussinesses;

    private IPTMessageCenter messageCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putao_message_center_activity);

    }

    @Override
    protected void onResume() {
        super.onResume();

        initData();
        initView();
    }

    private void initData() {
        messageCenter = PTMessageCenterFactory.getPTMessageCenter();
        bussinesses = messageCenter.getAllService();
    }

    private void initView() {
        setTitle(R.string.settings);

        listView = (ListView)findViewById(R.id.list);

        adapter = new ExpandableListAdapter(getApplicationContext());
        listView.setAdapter(adapter);
        listView.setDividerHeight(0);
        
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (expandPosion==position) {
                    expandPosion=0;
                }else {
                    expandPosion=position;
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    private class ExpandableListAdapter extends BaseAdapter {
        private Context mContext;

        public ExpandableListAdapter(Context context) {
            mContext = context;
        }

        public int getCount() {
            return bussinesses.size()+4;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            switch (getItemViewType(position)) {
                case 0:
                    View titleView = View.inflate(mContext,
                            R.layout.putao_tag_view, null);
                    TextView text=(TextView)titleView.findViewById(R.id.tvTag);
                    text.setText(mTitles[position]);
                    return titleView;
                case 1:
                    View foreView = View.inflate(mContext,
                            R.layout.putao_message_center_settings_list_item, null);

                    ImageView logo = (ImageView)foreView.findViewById(R.id.logo);
                    logo.setVisibility(View.GONE);
                    TextView mTitle = (TextView)foreView.findViewById(R.id.title);
                    mTitle.setText(mTitles[position]);
                    SwitchButton set_switch = (SwitchButton)foreView.findViewById(R.id.set_switch);
                    if (position==1) {//震动
                        set_switch.setChecked(messageCenter.getEnableVibrate());//总开关
                        set_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                            
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                PTMessageCenterSettings.setVibrateEnable(isChecked);
                                if(isChecked){
                                    MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(), UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_SETTING_VIBRATE_OPEN);
                                }else{
                                    MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(), UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_SETTING_VIBRATE_CLOSE);
                                }
                            }
                        });
                    }else {//声音
                        foreView.findViewById(R.id.divider).setVisibility(View.INVISIBLE);
                        set_switch.setChecked(messageCenter.getEnableSound());//总开关
                        set_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                            
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                PTMessageCenterSettings.setSoundEnable(isChecked);
                                if(isChecked){
                                    MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(), UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_SETTING_SOUND_OPEN);
                                }else{
                                    MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(), UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_SETTING_SOUND_CLOSE);
                                }
                            }
                        });
                    }
                    return foreView;
                case 2:
                    final AbstractMessageBussiness business = bussinesses.get(position-4);
                    return business.getSettingView(convertView,expandPosion==position,YellowPageMessageCenterSettingActivity.this);
            }
            return new View(mContext);
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            if (position==0||position==3) {
                return 0;
            }else if (position==1||position==2) {
                return 1;
            }else {
                return 2;
            }
        }

    }

    @Override
    public String getServiceNameByUrl() {
        return null;
    }

    @Override
    public String getServiceName() {
        return YellowPageMessageCenterSettingActivity.class.getName();
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
