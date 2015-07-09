package so.contacts.hub.thirdparty.express;
import so.contacts.hub.ContactsApp;
import so.contacts.hub.msgcenter.AbstractMessageBussiness;
import so.contacts.hub.msgcenter.MsgCenterConfig;
import so.contacts.hub.msgcenter.PTMessageCenterSettings;
import so.contacts.hub.msgcenter.bean.PTMessageBean;
import so.contacts.hub.msgcenter.bean.PTOrderBean;
import so.contacts.hub.ui.yellowpage.YellowPageExpressSelectReslutPage;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yulong.android.contacts.discover.R;

public class ExpressMessageBusiness extends AbstractMessageBussiness {

	private Context context;
	private static final String CAN_EXPRESS_SHOW = "can_express_show";
	private static ExpressMessageBusiness mInstance = null;

	private ExpressMessageBusiness(Context context) {
		super(context);
		this.context = context;
		super.smallLogoId=R.drawable.putao_icon_order_kd_s;
		super.logoId = R.drawable.putao_icon_order_kd;
		super.title = R.string.express;
		super.productType=MsgCenterConfig.Product.express.getProductType();
		umengInsertDataEventId=UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_EXPRESS_ITEM_NUM;
	}

	public static ExpressMessageBusiness getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new ExpressMessageBusiness(context);
		}
		return mInstance;
	}

	@Override
	public View getOrderView(PTOrderBean bean, View convertView) {
		// TODO Auto-generated method stub
		return null; // 快递无订单...
	}
	
	@Override
	public void click(PTMessageBean bean, Activity context) {
	    super.click(bean, context);
        if (bean == null) {
            return;
        }
        LogUtil.i("ExpressMessageBusiness", "bean=" + bean.toString());
        String expand = bean.getExpand_param();
        if (expand == null) {
            return;
        }
        
        ExpressMsgBean info = null;
        try {
            info = new Gson().fromJson(expand, ExpressMsgBean.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        
        onClick(info, context);
        // add 2014-12-31 xcx start 统计埋点
        MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_EXPRESS_ITEM_CLICK);
        // add 2014-12-31 xcx end 统计埋点
	}
	
    @Override
    public void click(PTOrderBean bean, Activity context) {
        if (bean == null) {
            return;
        }
        LogUtil.i("ExpressMessageBusiness", "bean=" + bean.toString());
        String expand = bean.getExpand();
        if (expand == null) {
            return;
        }
        
        ExpressMsgBean info = null;
        try {
            info = new Gson().fromJson(expand, ExpressMsgBean.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        
        onClick(info, context);
    }
    
    private void onClick(ExpressMsgBean info, Activity context) {
        if (info != null) {
            LogUtil.i("ExpressMessageBusiness", info.toString());
            Intent intent = new Intent(context, YellowPageExpressSelectReslutPage.class);
            // intent.putExtra(
            // YellowPageExpressSelectReslutPage.EXTRA_EXP_COM_ID,
            // info.getCompany_id());
            // intent.putExtra(
            // YellowPageExpressSelectReslutPage.EXTRA_EXP_COM_NAME,
            // info.getCompany_name());
            intent.putExtra(YellowPageExpressSelectReslutPage.EXTRA_EXP_NUM,
                    info.getOrder_no());

            context.startActivity(intent);
        }
        
    }

	@Override
	public void handleBusiness(PTMessageBean message) {
		if (message == null) {
			return;
		}
		if (getEnable()&& message.getIs_notify()!=0) {
			
			LogUtil.i("ExpressMessageBusiness", "message=" + message.toString());
			String expand = message.getExpand_param();
			if (expand == null) {
				return;
			}
			Intent intent = null;
			try {
				ExpressMsgBean info = new Gson().fromJson(expand,
						ExpressMsgBean.class);
				if (info == null) {
					return;
				}
				LogUtil.i("LotteryMessageBusiness", info.toString());
				intent = new Intent(context,
						YellowPageExpressSelectReslutPage.class);
				intent.putExtra(
						YellowPageExpressSelectReslutPage.EXTRA_EXP_COM_ID,
						info.getCompany_code());
				intent.putExtra(
						YellowPageExpressSelectReslutPage.EXTRA_EXP_COM_NAME,
						info.getCompany());
				intent.putExtra(
						YellowPageExpressSelectReslutPage.EXTRA_EXP_NUM,
						info.getOrder_no());
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
			// TODO 具体内容需改进;
			Notification notification = new Notification.Builder(context)
					.setContentTitle(message.getSubject())
					.setContentText(message.getDigest())
					.setWhen(System.currentTimeMillis())
					.setSmallIcon(R.drawable.putao_ic_launcher)// TODO
					.build();
			if (intent != null) {
				sendNotification(notification, intent);
			}
		}

	}

	@Override
	public boolean isOrderExpire(PTOrderBean orderContent) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void setEnable(boolean enable) {
		SharedPreferences sp = ContactsApp.getInstance()
				.getSharedPreferences(PTMessageCenterSettings.SHARED_NAME,
						Context.MODE_MULTI_PROCESS);
		sp.edit().putBoolean(CAN_EXPRESS_SHOW, enable).commit();
	}

	@Override
	public boolean getEnable() {
		SharedPreferences sp = ContactsApp.getInstance()
				.getSharedPreferences(PTMessageCenterSettings.SHARED_NAME,
						Context.MODE_MULTI_PROCESS);
		return sp.getBoolean(CAN_EXPRESS_SHOW, true);
	}

	@Override
	public View getConfigView(Activity context) {
		View view = View.inflate(context,
				R.layout.putao_express_remind_setting_layout, null);
		return view;
	}

}
