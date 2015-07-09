package so.contacts.hub.msgcenter.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mdroid.core.http.IgnitedHttpResponse;

import so.contacts.hub.core.Config;
import so.contacts.hub.db.DatabaseHelper;
import so.contacts.hub.http.OrderEntity;
import so.contacts.hub.msgcenter.MsgCenterConfig;
import so.contacts.hub.net.PTHTTP;
import so.contacts.hub.net.SimpleRequestData;
import so.contacts.hub.service.PlugService;
import so.contacts.hub.thirdparty.tongcheng.bean.HotelOrderPostBean;
import so.contacts.hub.trafficoffence.VehicleInfoShowActivity;
import so.contacts.hub.trafficoffence.bean.Vehicle;
import so.contacts.hub.util.LogUtil;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

/**
 * 消息上报任务,批量执行未上报完成的消息
 * @author change 2015/01/22
 */
public class MsgReportThread extends Thread {
    private static final String TAG = MsgReportThread.class.getSimpleName();
    private MsgReportDB mReportDB;
    private Context mContext;
    private Handler mHandler;
    
    private boolean mExecFinished = false;
    
    private static final int REPORT_STATUS_OK = 0;              // 上报成功
    private static final int REPORT_STATUS_REPEAT = 1;          // 需要重复上报
    private static final int REPORT_STATUS_EXCEPTION = -1;      //上报异常状态 
    
    public MsgReportThread(Context ctx, Handler handler) {
        super("msg_report_thread#");
        this.mContext = ctx;
        this.mReportDB = DatabaseHelper.getInstance(ctx).getMsgNotifyDB();
        this.mHandler = handler;
        this.mExecFinished = false;
    }
    
    public boolean finished() {
        return mExecFinished;
    }
    
    @Override
    public void run() {
        try {
            List<MsgReport> reportList = mReportDB.queryMsgNotifyAll();
            if(reportList == null || reportList.size() == 0) {
                return;
            }
            
            int total = reportList.size();
            for(MsgReport msg : reportList) {
                int type = msg.getType();
                String reportContent = msg.getReportContent();
                
                if(isInterrupted()) {
                    break;
                }                    
                
                int result = 0;
                if(MsgCenterConfig.Product.train.getProductType() == type) {
                    // 火车票上报
                    result = MsgReportUtils.doReportTrain(msg);
                } else if(MsgCenterConfig.Product.traffic_offence.getProductType() == type) {
                    // 车辆信息上报
                    result = MsgReportUtils.doReportVehicle(msg);
                } else if(MsgCenterConfig.Product.hotel.getProductType() == type) {
                    // 酒店上报
                    result = MsgReportUtils.doReportHotel(msg);
                }
                
                if(REPORT_STATUS_OK == result || REPORT_STATUS_EXCEPTION == result) {
                    int ret = doDelete(msg);
                    if(ret > 0) {
                        total--;
                    }
                    LogUtil.d(TAG, "doDelete total="+total+" msg="+msg.toString());
                }
            }
            
            if(isInterrupted()) {
                return;
            }

            if(mHandler != null) {
                /**
                 * 如果处理完后仍然有消息,则发送hander消息继续处理
                 */
                int remain = MsgReportUtils.getUnReportedMsg(mContext);
                if(remain > 0 && remain > total) {  // 有新消息报道,需立即再发一次
                    // do report again
                    mHandler.sendEmptyMessage(PlugService.MSG_REPORT);
                }                
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mExecFinished = true;
        }
    }
    
    private int doDelete(MsgReport msg) {
        return mReportDB.delete(msg);
    }

}
