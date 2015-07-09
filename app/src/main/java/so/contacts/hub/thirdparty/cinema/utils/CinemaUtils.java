package so.contacts.hub.thirdparty.cinema.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.client.methods.HttpGet;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.widget.ImageView;

import so.contacts.hub.thirdparty.cinema.bean.DetailMovieOrder;

public class CinemaUtils {
	
	
	public static String inputsteam2Str(InputStream is)
	{
		int i = -1;  
		byte[] b = new byte[1024];  
		StringBuffer sb = new StringBuffer();  
		try {
			while ((i = is.read(b)) != -1) {  
			    sb.append(new String(b, 0, i));  
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return sb.toString();
	}
	
	/**
	 * 订单状态枚举类，调用getStatus方法传入状态值得到状态枚举对象，toString转成状态字符形式
	 * @author peku
	 *
	 */
	public enum OrderStatus{
		
		
		ORDER_CANCEL("订单取消",0),
		WAIT_BUYER_PAY ("订单创建 未支付",1),
		PAY_FAIL("支付失败",2),
		TRADE_PROCESS("处理中",3),
		TRADE_SUCCESS("交易成功",4),
		REFUND_PROCESS("退款中",5),
		REFUND_SUCCESS("退款成功",6); 
		private String strStatus;
		private int intStatus;
		private  OrderStatus(String strStatus,int intStatus)
		{
			this.strStatus=strStatus;
			this.intStatus=intStatus;
		}
		public String getStatusStr()
		{
			return this.strStatus;
		}
		public int getStatusInt()
		{
			return this.intStatus;
		}
		public static OrderStatus getStatusBeen(int status)
		{
			for(OrderStatus been:OrderStatus.values())
			{
				if(been.intStatus==status)
					return been;
			}
			return null;
		}
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return this.strStatus;
		}
	}
	
	public static long timeStr2Long(String time){
	    if(TextUtils.isEmpty(time)){
	        return -1;
	    }
	    if(timeStr2Timestamp(time) == null){
	        return -1;
	    }
		return timeStr2Timestamp(time).getTime();
	}
	
	public static Timestamp timeStr2Timestamp(String time){
		if(!TextUtils.isEmpty(time)){
			return Timestamp.valueOf(time);
		}
		return null;
	}
	
	public static String getNeedFormatTime(Timestamp time,String formatType){
		 if(null!=time&&null!=formatType)
		 {
			 Date date=new Date(time.getTime());
			 SimpleDateFormat dateFm = new SimpleDateFormat(formatType);
			 String needTime = dateFm.format(date);
			 return needTime;
		 }
		 return null;
	}
	
	public  static boolean isNetworkConnected(Context context) {  
	    if (context != null) {  
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
	                .getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
	        if (mNetworkInfo != null) {  
	            return mNetworkInfo.isAvailable();  
	        }  
	    }  
	    return false;  
	}
	
	public static void setMovieLogo(Context context,int movieId,ImageView logo)
	{
		
	}
	
	public static String formatOrderItemSeat(String waitFormatSeat)
	{
		String[] seats=new String[]{};
		seats=waitFormatSeat.split(",");
		for(int i=0;i<seats.length;i++)
		{
			int index=seats[i].indexOf('座');
			seats[i]=seats[i].substring(0, index+1);
			if(i!=seats.length-1)
				seats[i]+=",";
		}
		StringBuilder sb=new StringBuilder();
		for(String seat:seats)
		{
			sb.append(seat);
		}
		return sb.toString();
	}
	
	public static String hidePhone(String waitHidePhone)
	{
		char[] phoneNo=waitHidePhone.toCharArray();
		
		for(int i=3;i<7;i++)
		{
			phoneNo[i]='*';
		}
		return String.valueOf(phoneNo);
	}
	
	public static String getDouble2(double number)
	{
		DecimalFormat df=new DecimalFormat("0.00");
		return df.format(number);
		
	}
}
