
package so.contacts.hub.msgcenter.ui;

import java.util.List;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.adapter.CustomListViewAdapter;
import so.contacts.hub.msgcenter.AbstractMessageBussiness;
import so.contacts.hub.msgcenter.PTOrderCenter;
import so.contacts.hub.msgcenter.bean.PTOrderBean;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.loader.DataLoader;

/**
 * 我的订单的adapter
 * 
 * @author zj 2014-12-18 14:32:37
 */
public class OrderListAdapter extends CustomListViewAdapter {
    private List<PTOrderBean> orders;

    private PTOrderCenter orderCenter;

    private SparseArray<Integer> viewType;

    public OrderListAdapter(List<PTOrderBean> newestOrders, PTOrderCenter orderCenter) {
        this.orders = newestOrders;
        this.orderCenter = orderCenter;
        List<AbstractMessageBussiness> businesses = orderCenter.getAllService();
        viewType = new SparseArray<Integer>();
        for (int i = 0; i < businesses.size(); i++) {
            AbstractMessageBussiness business = businesses.get(i);
            viewType.put(business.getProductType(), i);
        }
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ((ViewGroup)view).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        AbstractMessageBussiness bussiness = orderCenter.getService(orders.get(position));
        if (bussiness != null) {
            View view;
            if (convertView==null||convertView.getTag()==null) {
                view =bussiness.getOrderView(orders.get(position), null);
            }else {
                view =bussiness.getOrderView(orders.get(position), convertView);
            }
            if (view != null) {
                return view;
            }
        }
        /*
         * modify by putao_lhq
         * 去掉移除无用的item
         * delete code:
        orders.remove(position);
        notifyDataSetChanged();*/
        orders.remove(position);
        if (orders.size() == position) {
            notifyDataSetChanged();
            return new View(ContactsApp.getContext());
        } else {
            if (position != 0) {
                position--;
            }
            return getView(position, null, parent);
        }/*@end by putao_lhq*/
    }

    @Override
    public int getViewTypeCount() {
        return viewType.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position>=orders.size()) {
            return 0;
        }
        Integer type=viewType.get(orders.get(position).getProduct_type());
        if (type==null) {
            return 0;
        }
        return viewType.get(orders.get(position).getProduct_type());
    }

    @Override
    public DataLoader getmImageLoader() {
        return null;
    }
}
