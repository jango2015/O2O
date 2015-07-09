
package so.contacts.hub.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyQueue {
    private static volatile RequestQueue requestQueue;

    public static void init(Context context) {
        if (null == requestQueue) {
            synchronized (VolleyQueue.class) {
                if (null == requestQueue) {
                    requestQueue = Volley.newRequestQueue(context);
                    requestQueue.start();
                }
            }
        }
    }

    private VolleyQueue() {

    }

    public static RequestQueue getQueue() {
        return requestQueue;
    }
}
