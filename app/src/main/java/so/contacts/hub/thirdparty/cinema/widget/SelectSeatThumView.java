
package so.contacts.hub.thirdparty.cinema.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class SelectSeatThumView extends View {
    private Bitmap a = null;

    private Paint b = null;

    public SelectSeatThumView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public void initSize(int height) {
        getLayoutParams().height = height;
        requestLayout();
    }

    public void a(Bitmap paramBitmap) {
        this.a = paramBitmap;
    }

    protected void onDraw(Canvas paramCanvas) {
        super.onDraw(paramCanvas);
        // Log.i("TAG", "onDraw()...");
        if (this.a != null) {
            paramCanvas.drawBitmap(this.a, 0.0F, 0.0F, this.b);
        }

    }
}
