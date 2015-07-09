package so.contacts.hub.thirdparty.dianping;

import java.util.Map;

import android.os.AsyncTask;
import android.util.Log;

/**
 * 获取大众点评信息
 * @author Michael
 *
 */
public class DianpingAsyncTask extends AsyncTask<Void, Void, String> {
	
	private static final String TAG = "CustomAsyncTask";
	
	private IAsyncCallback mIAsyncCallback = null;
	
	private String mRequestUrl = "";
	
	private Map<String, String> mParamMap = null;
	
	public DianpingAsyncTask(String requestUrl, Map<String, String> paramMap, IAsyncCallback iAsyncCallback){
		mRequestUrl = requestUrl;
		mIAsyncCallback = iAsyncCallback;
		mParamMap = paramMap;
	}

	@Override
	protected String doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		return DianPingApiTool.requestApi(mRequestUrl, mParamMap);
	}
	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		Log.i(TAG, "CustomAsyncTask result: " + result);
		mIAsyncCallback.onPostExecute(result);
	}
	
	public interface IAsyncCallback{
		void onPostExecute(String result);
	}

}
