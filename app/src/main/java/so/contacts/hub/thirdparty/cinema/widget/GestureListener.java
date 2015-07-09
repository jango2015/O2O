package so.contacts.hub.thirdparty.cinema.widget;

import java.util.ArrayList;

import so.contacts.hub.thirdparty.cinema.bean.Seat;
import so.contacts.hub.thirdparty.cinema.bean.SeatInfo;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;


class GestureListener extends GestureDetector.SimpleOnGestureListener {
    private SelectSeatView mSsView;
    private int maxSeats=0;
    private int selectedSeats=0;

    GestureListener(SelectSeatView paramSSView) {
        mSsView = paramSSView;
    }

    public boolean onDoubleTap(MotionEvent paramMotionEvent) {
        return super.onDoubleTap(paramMotionEvent);
    }

    public boolean onDoubleTapEvent(MotionEvent paramMotionEvent) {
        return super.onDoubleTapEvent(paramMotionEvent);
    }

    public boolean onDown(MotionEvent paramMotionEvent) {
        return false;
    }

    public boolean onFling(MotionEvent paramMotionEvent1,
                           MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
        return false;
    }

    public void onLongPress(MotionEvent paramMotionEvent) {
    }

    public boolean onScroll(MotionEvent paramMotionEvent1,
                            MotionEvent paramMotionEvent2, float x_scroll_distance, float y_scroll_distance) {
        //是否可以移动和点击
        if(!SelectSeatView.a(mSsView)){
            return false;
        }
        //显示缩略图
        SelectSeatView.a(mSsView,true);
        boolean bool1 = true;
        boolean bool2 = true;
        if ((SelectSeatView.s(mSsView) < mSsView.getMeasuredWidth())
                && (0.0F == SelectSeatView.v(mSsView))){
            bool1 = false;
        }

        if ((SelectSeatView.u(mSsView) < mSsView.getMeasuredHeight())
                && (0.0F == SelectSeatView.w(mSsView))){
            bool2  = false;
        }

        if(bool1){
            int k = Math.round(x_scroll_distance);
            //修改排数x轴的偏移量
            SelectSeatView.c(mSsView, (float)k);
//			Log.i("TAG", SSView.v(mSsView)+"");
            //修改座位距离排数的横向距离
            SelectSeatView.k(mSsView, k);
//			Log.i("TAG", SSView.r(mSsView)+"");
            if (SelectSeatView.r(mSsView) < 0) {
                //滑到最左
                SelectSeatView.i(mSsView, 0);
                SelectSeatView.a(mSsView, 0.0F);
            }

            if(SelectSeatView.r(mSsView) + mSsView.getMeasuredWidth() > SelectSeatView.s(mSsView)){
                //滑到最右
                SelectSeatView.i(mSsView, SelectSeatView.s(mSsView) - mSsView.getMeasuredWidth());
                SelectSeatView.a(mSsView, (float)(mSsView.getMeasuredWidth() - SelectSeatView.s(mSsView)));
            }
        }

        if(bool2){
            //上负下正- 往下滑则减
            int j = Math.round(y_scroll_distance);
            //修改排数y轴的偏移量
            SelectSeatView.d(mSsView, (float)j);
            //修改可视座位距离顶端的距离
            SelectSeatView.l(mSsView, j);
            Log.i("TAG", SelectSeatView.t(mSsView)+"");
            if (SelectSeatView.t(mSsView) < 0){
                //滑到顶
                SelectSeatView.j(mSsView, 0);
                SelectSeatView.b(mSsView, 0.0F);
            }

            if (SelectSeatView.t(mSsView) + mSsView.getMeasuredHeight() > SelectSeatView
                    .u(mSsView)){
                //滑到底
                SelectSeatView.j(mSsView, SelectSeatView.u(mSsView) - mSsView.getMeasuredHeight());
                SelectSeatView.b(mSsView, (float)(mSsView.getMeasuredHeight() - SelectSeatView.u(mSsView)));
            }
        }

        mSsView.invalidate();

//		Log.i("GestureDetector", "onScroll----------------------");
        return false;
    }

    public void onShowPress(MotionEvent paramMotionEvent) {
    }

    public boolean onSingleTapConfirmed(MotionEvent paramMotionEvent) {
        return false;
    }

    public boolean onSingleTapUp(MotionEvent paramMotionEvent) {
//		Log.i("GestureDetector", "onSingleTapUp");
//		if(!SSView.a(mSsView)){
//			return false;
//		}
        //列数
        int i = SelectSeatView.a(mSsView, (int)paramMotionEvent.getX());
        //排数
        int j = SelectSeatView.b(mSsView, (int) paramMotionEvent.getY());
        ArrayList<SeatInfo> infos=SelectSeatView.c(mSsView);
        if((j>=0 && j< infos.size())){
            if(i>=0 && i<infos.get(j).getSeatList().size()){
                Log.i("TAG", "排数："+ j + "列数："+i);
                Seat localSeat = infos.get(j).getSeat(i);
                switch (localSeat.getCondition()) {
                    case 3://已选中
                        localSeat.setCondition(1);
                        if(SelectSeatView.d(mSsView)!=null){
                            SelectSeatView.d(mSsView).cancel(i, j, false);
                            selectedSeats--;
                        }



                        break;
                    case 1://可选
                        localSeat.setCondition(3);
                        if(SelectSeatView.d(mSsView)!=null){
                            if (maxSeats==0) {
                                SelectSeatView.d(mSsView).choose(i, j, false);
                                selectedSeats++;
                            }else if (selectedSeats>=maxSeats) {
                                SelectSeatView.d(mSsView).selectSeatMax();
                                localSeat.setCondition(1);
                            }else {
                                SelectSeatView.d(mSsView).choose(i, j, false);
                                selectedSeats++;
                            }
                        }
                        break;
                    default:
                        break;
                }

            }
        }

        //显示缩略图
        SelectSeatView.a(mSsView,true);
        mSsView.invalidate();
        return false;
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    public void setMaxSeats(int maxSeats) {
        this.maxSeats = maxSeats;
        selectedSeats=0;
    }
}
