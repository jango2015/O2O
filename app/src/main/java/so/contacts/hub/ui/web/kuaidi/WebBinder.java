/**
 * 
 */

package so.contacts.hub.ui.web.kuaidi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * @author javafx
 */
public class WebBinder {

	public WebBinder() {

	}

	public static final String TAG = "WebBinder";
	private Activity activity = null;
	private WebView webview = null;
	private ExecutorService workStation = null;
	private Map<String, Class<?>> plugins = new HashMap<String, Class<?>>();
	private Map<String, WebPlugin> pluginBeans = new HashMap<String, WebPlugin>();

	public static final int WHAT_PUBLISH_RESULT = 1001;
	public static final int WHAT_PUBLISH_DESTORY = 1002;

	private AtomicBoolean runAble = new AtomicBoolean(false);

	public void prepare(Activity activity, WebView webview) {
		this.activity = activity;
		this.webview = webview;
		workStation = Executors.newCachedThreadPool();
		runAble.set(true);
	}

	@JavascriptInterface
	public void dispatch(final String eventId, final String args,
			final String funId) {
		if (pluginBeans.containsKey(eventId)) {
			WebPlugin plugin = pluginBeans.get(eventId);
			if (!workStation.isShutdown()) {
				plugin.load(eventId, args, funId);
				workStation.execute(plugin);
			}
			
			if(plugin instanceof Session){
				
			}else{
				pluginBeans.remove(eventId);
			}
			
		} else {
			if (plugins.containsKey(eventId)) {
				Class<?> cls = plugins.get(eventId);
				WebPlugin plugin = null;
				try {
					plugin = (WebPlugin) cls.newInstance();
				} catch (Exception e) {
				}
				plugin.bind(handler);
				plugin.onCreate(activity);
				if (plugin instanceof Session) {
					pluginBeans.put(eventId, plugin);
				}
				if (!workStation.isShutdown()) {
					plugin.load(eventId, args, funId);
					workStation.execute(plugin);
				}
			} else {
				handlerError(eventId, funId);
			}
		}
	}

	public void addPlugin(String name, Class<? extends WebPlugin> plugin) {
		this.plugins.put(name, plugin);
	}

	public void addPlugin(String name, WebPlugin plugin) {
		this.pluginBeans.put(name, plugin);
		plugin.bind(handler);
		plugin.onCreate(activity);
	}

	private void handlerError(String eventId, String funId) {
		Log.d(TAG, eventId + "not find...");
		ResponseEvent data = new ResponseEvent();
		data.setCode(404);
		ResultEvent event = new ResultEvent(eventId, funId, data);
		Message msg = handler.obtainMessage(WHAT_PUBLISH_RESULT);
		msg.obj = event;
		msg.sendToTarget();
	}

	private Handler handler = new Handler(Looper.getMainLooper()) {

		@Override
		public void dispatchMessage(Message msg) {
			if (runAble.get()) {
				super.dispatchMessage(msg);
			}
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_PUBLISH_RESULT: {
				ResultEvent result = (ResultEvent) msg.obj;
				Log.d(TAG, result.getEventId() + ":" + result.getFunId() + ":"
						+ result.getData());
				webview.loadUrl("javascript:JWebBinder.onResponse('"
						+ result.getEventId() + "','" + result.getFunId()
						+ "','" + result.getData() + "')");
			}
				break;
			case WHAT_PUBLISH_DESTORY: {
				WebPlugin plugin = (WebPlugin) msg.obj;
				plugin.onDestory();
			}
				break;
			default:
				break;
			}
		}

	};

	public void release() {
		runAble.set(false);
		for (WebPlugin plugin : pluginBeans.values()) {
			if (plugin instanceof Session) {
				plugin.onDestory();
			}
		}
		workStation.shutdown();
	}

}
