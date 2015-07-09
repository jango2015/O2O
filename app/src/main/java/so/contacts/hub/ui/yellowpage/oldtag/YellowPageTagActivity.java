
package so.contacts.hub.ui.yellowpage.oldtag;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.remind.BaseRemindActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.yulong.android.contacts.discover.R;

public abstract class YellowPageTagActivity extends BaseRemindActivity implements OnClickListener,OnPageChangeListener{

    private LinearLayout tagViewContainer;

    private FragmentManager fragmentManager;

    protected int mCurTagid;// 当前选择标签
    
    private ViewPager mTagViewPager;
    
    private ViewPagerAdapter mViewPagerAdapter = null;
    
    private List<View> lineViewList = new ArrayList<View>();

    public abstract String[] getTagItemViewTexts();
    
    public abstract Fragment[] getFragments();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putao_yellow_page_tag_layout);
        initView();
        initTagViewItems(getTagItemViewTexts());
        fragmentManager = getSupportFragmentManager();
        mViewPagerAdapter = new ViewPagerAdapter(fragmentManager, getFragments());
        mTagViewPager.setAdapter(mViewPagerAdapter);
        mTagViewPager.setCurrentItem(0);
    }
    
    private void initView() {
        mTagViewPager = (ViewPager)findViewById(R.id.tag_viewpager);
        tagViewContainer = (LinearLayout)findViewById(R.id.tag_view_container);
        findViewById(R.id.back_layout).setOnClickListener(this);
        mTagViewPager.setOnPageChangeListener(this);
    }
    
    private void initTagViewItems(String[] itemTexts){
        if(itemTexts == null || itemTexts.length == 0){
            return;
        }
        tagViewContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
        for(int i = 0;i<itemTexts.length;i++){
           LinearLayout itemLayout =  (LinearLayout)inflater.inflate(R.layout.putao_yellow_page_tag_item, null);
           TextView itemText = (TextView)itemLayout.findViewById(R.id.tag_text);
           TextView itemLine = (TextView)itemLayout.findViewById(R.id.tag_line);
           itemText.setText(itemTexts[i]);
           itemLine.setTag(i);
           if(i == 0){
               itemLine.setVisibility(View.VISIBLE);
           }else{
               itemLine.setVisibility(View.INVISIBLE);
           }
           lineViewList.add(itemLine);
           itemLayout.setTag(i);
           itemLayout.setOnClickListener(this);
           tagViewContainer.addView(itemLayout,params);
        }
    }
    
    private void onItemSelected(int index){
        for(View view : lineViewList){
            int tag = (Integer)view.getTag();
            if(tag == index){
                view.setVisibility(View.VISIBLE);
            }else{
                view.setVisibility(View.INVISIBLE);
            }
        }
    }
    
    @Override
    public void onClick(View view) {
        int id = 0;
        if(view.getTag() != null && view.getTag() instanceof Integer){
            id = (Integer)view.getTag();
            mTagViewPager.setCurrentItem(id);
        }else{
            id = view.getId();
            switch (id) {
                case R.id.back_layout:
                    finish();
                    break;

                default:
                    break;
            }
        }
    }
    
    class ViewPagerAdapter extends FragmentPagerAdapter {

        private Fragment[] mFragments;

        public ViewPagerAdapter(FragmentManager fm, Fragment[] fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int arg0) {
            return (mFragments == null || mFragments.length == 0) ? null
                    : mFragments[arg0];
        }

        @Override
        public int getCount() {
            return mFragments == null ? 0 : mFragments.length;
        }

    }


    @Override
    public void onPageScrollStateChanged(int arg0) {
        
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        
    }

    @Override
    public void onPageSelected(int arg0) {
        mCurTagid = arg0;
        onItemSelected(arg0);
    }
}
