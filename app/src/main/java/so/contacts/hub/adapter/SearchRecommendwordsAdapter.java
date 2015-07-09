package so.contacts.hub.adapter;


import java.util.List;

import so.contacts.hub.util.LogUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yulong.android.contacts.discover.R;

public class SearchRecommendwordsAdapter extends BaseAdapter {  
    private Context context;                        
    private List<String> mListItems;    
    private LayoutInflater mListContainer;
    private final static int COLUMN_NUM = 3;
    public final class ViewHodle{               
        TextView content;

        ImageView rightline;

        ImageView bottomline;
    }    

      
    public  SearchRecommendwordsAdapter(Context context, List<String> listItems) {  
        this.context = context;           
        mListContainer = LayoutInflater.from(context);   
        this.mListItems = listItems;  
    }  

    public int getCount() {  
        // TODO Auto-generated method stub  
        return mListItems.size();  
    }  

    public Object getItem(int arg0) {  
        // TODO Auto-generated method stub  
        return mListItems.get(arg0);  
    }  

    public long getItemId(int arg0) {  
        // TODO Auto-generated method stub  
        return arg0;  
    }  
         
    public View getView(int position, View convertView, ViewGroup parent) {  
        // TODO Auto-generated method stub  
        ViewHodle  listItemView = null;  
        if (convertView == null) {  
            listItemView = new ViewHodle();   
            convertView = mListContainer.inflate(R.layout.putao_recommend_word_item, null);  
            listItemView.content = (TextView)convertView.findViewById(R.id.hotword_title);
            listItemView.rightline = (ImageView)convertView.findViewById(R.id.hotword_right_line);
            listItemView.bottomline = (ImageView)convertView.findViewById(R.id.hotword_bottom_line);
            
            
            boolean isColumnEnd = ((position+1)%COLUMN_NUM == 0);
            boolean isLastColumn = ((position+1) > (getCount()-COLUMN_NUM));
                String text = mListItems.get(position);
                
//                LogUtil.d("ljq", "position " + position);
//                LogUtil.d("ljq", "isColumnEnd " + isColumnEnd);
//                LogUtil.d("ljq", "isLastColumn " + isLastColumn);
//                LogUtil.d("ljq", "text " + text);
                if(text != null && text.length()>0){
                    listItemView.content.setText(text);
                    if(position==0){
                        listItemView.content.setTextColor(context.getResources().getColor(R.color.putao_red));
                    }
                }
                if(isColumnEnd){
                    listItemView.rightline.setVisibility(View.GONE);
                }
                if(isLastColumn){
                    listItemView.bottomline.setVisibility(View.GONE);
                }
                convertView.setTag(listItemView);    
        }else {  
            listItemView = (ViewHodle)convertView.getTag();  
        }  

            
        return convertView;  
    }
} 