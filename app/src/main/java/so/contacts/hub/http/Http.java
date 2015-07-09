package so.contacts.hub.http;

import java.net.URLEncoder;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.core.PTUser;
import so.putao.findplug.LBSServiceGaode;
import android.content.Context;

import com.mdroid.core.http.IgnitedHttp;
import com.mdroid.core.util.SystemUtil;

public class Http extends IgnitedHttp {

	public Http(Context context) {
		super(context);
		init();
	}

	private void init() {
//		enableResponseCache(25, 1440, 8);
		setDefaultHeader("Accept-Encoding", "gzip");
		setDefaultHeader("Content-Type", "application/json");
		initDefaultHeaderToken();
	}
	
	public void initDefaultHeaderToken(){
		PTUser ptUsr = PutaoAccount.getInstance().getPtUser();
		StringBuffer cookieBuf = new StringBuffer();
        cookieBuf.append("app_id=").append(SystemUtil.getAppid(ContactsApp.getContext()))
                 .append(";channel=").append(SystemUtil.getChannelNo(ContactsApp.getContext()))
                 .append(";version=").append(SystemUtil.getAppVersion(ContactsApp.getContext()))
                 .append(";dev_no=").append(SystemUtil.getDeviceId(ContactsApp.getContext()))
                 .append(";band=").append(SystemUtil.getMachine())
                 .append(";city=").append(URLEncoder.encode(LBSServiceGaode.getLocCity()))
                 .append(";loc=").append(String.valueOf(LBSServiceGaode.getLocLatitude()))
                                 .append(",")
                                 .append(String.valueOf(LBSServiceGaode.getLocLongitude()));
        
        if(ptUsr != null) {
            cookieBuf.append(";pt_token=").append(ptUsr.getPt_token());
        }
        
        setDefaultHeader("Cookie", cookieBuf.toString());
	}

}
