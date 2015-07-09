
package so.contacts.hub.ui.yellowpage.oldtag;

import so.contacts.hub.remind.BaseRemindActivity;

import com.yulong.android.contacts.discover.R;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class YellowPageThreeTagActivity extends BaseRemindActivity implements
        OnClickListener {

    private TextView mTagTextOne;

    private TextView mTagTextTwo;

    private TextView mTagTextThree;

    private RelativeLayout mTagOneRelativeLayout;

    private RelativeLayout mTagTwoRelativeLayout;

    private RelativeLayout mTagThreeRelativeLayout;

    private ImageView mTagImageOne;

    private ImageView mTagImageTwo;

    private ImageView mTagImageThree;

    private FragmentManager mFragmentManager;

    private Fragment mFragmentOne;

    private Fragment mFragmentTwo;

    private Fragment mFragmentThree;

    ColorStateList mTheme_csl;

    ColorStateList mGray_csl;

    /**
     * 定义界面LayoutId
     */
    public abstract int getLayoutId();

    /**
     * 定义第一个Fragment
     */
    public abstract Fragment getFragmentOne();

    /**
     * 定义第二个Fragment
     */
    public abstract Fragment getFragmentTwo();

    /**
     * 定义第三个Fragment
     */
    public abstract Fragment getFragmentThree();

    /**
     * 定义Fragment插入节点
     */
    public abstract int getShowViewId();

    /**
     * 定义第一个TAG文本
     */
    public abstract String getTagOneTextViewText();

    /**
     * 定义第二个TAG文本
     */
    public abstract String getTagTwoTextViewText();

    /**
     * 定义第三个TAG文本
     */
    public abstract String getTagThreeTextViewText();

    /**
     * 定义第一个TAG图片
     */
    public abstract int getTagOneImageViewCode();

    /**
     * 定义第二个TAG图片
     */
    public abstract int getTagTwoImageViewCode();

    /**
     * 定义第三个TAG图片
     */
    public abstract int getTagThreeImageViewCode();

    /**
     * 定义第一个TAG图片(press状态)
     */
    public abstract int getTagOneImagePressViewCode();

    /**
     * 定义第二个TAG图片(press状态)
     */
    public abstract int getTagTwoImagePressViewCode();

    /**
     * 定义第三个TAG图片(press状态)
     */
    public abstract int getTagThreeImagePressViewCode();

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tag_view_one) {
            setTabSelection(0);
        } else if (id == R.id.tag_view_two) {
            setTabSelection(1);
        } else if (id == R.id.tag_view_three) {
            setTabSelection(2);
        } else if (id == R.id.back_layout) {
            finish();
        }
    }

    private void initView() {
        findViewById(R.id.back_layout).setOnClickListener(this);

        mTagTextOne = (TextView)findViewById(R.id.tag_text_one);
        mTagTextTwo = (TextView)findViewById(R.id.tag_text_two);
        mTagTextThree = (TextView)findViewById(R.id.tag_text_three);

        mTagImageOne = (ImageView)findViewById(R.id.tag_img_one);
        mTagImageTwo = (ImageView)findViewById(R.id.tag_img_two);
        mTagImageThree = (ImageView)findViewById(R.id.tag_img_three);

        mTagOneRelativeLayout = (RelativeLayout)findViewById(R.id.tag_view_one);
        mTagTwoRelativeLayout = (RelativeLayout)findViewById(R.id.tag_view_two);
        mTagThreeRelativeLayout = (RelativeLayout)findViewById(R.id.tag_view_three);

        mTagTextOne.setText(getTagOneTextViewText());
        mTagTextTwo.setText(getTagTwoTextViewText());
        mTagTextThree.setText(getTagThreeTextViewText());

        mTagImageOne.setImageResource(getTagOneImageViewCode());
        mTagImageTwo.setImageResource(getTagTwoImageViewCode());
        mTagImageThree.setImageResource(getTagThreeImageViewCode());

        mTagOneRelativeLayout.setOnClickListener(this);
        mTagTwoRelativeLayout.setOnClickListener(this);
        mTagThreeRelativeLayout.setOnClickListener(this);

        Resources resource = (Resources)getBaseContext().getResources();
        mTheme_csl = (ColorStateList)resource.getColorStateList(R.color.putao_theme);
        mGray_csl = (ColorStateList)resource.getColorStateList(R.color.putao_pt_deep_gray);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mFragmentManager = getSupportFragmentManager();
        initView();
        setTabSelection(0);

    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     */
    @SuppressLint({
            "NewApi", "ResourceAsColor"
    })
    private void setTabSelection(int index) {
        // 重置按钮
        resetBtn();
        // 开启一个Fragment事务
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case 0:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                mTagTextOne.setTextColor(mTheme_csl);
                mTagTextTwo.setTextColor(mGray_csl);
                mTagTextThree.setTextColor(mGray_csl);

                mTagImageOne.setImageResource(getTagOneImagePressViewCode());
                mTagImageTwo.setImageResource(getTagTwoImageViewCode());
                mTagImageThree.setImageResource(getTagThreeImageViewCode());

                if (mFragmentOne == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    mFragmentOne = getFragmentOne();
                    transaction.add(getShowViewId(), mFragmentOne);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(mFragmentOne);
                }
                break;
            case 1:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                mTagTextOne.setTextColor(mGray_csl);
                mTagTextTwo.setTextColor(mTheme_csl);
                mTagTextThree.setTextColor(mGray_csl);

                mTagImageOne.setImageResource(getTagOneImageViewCode());
                mTagImageTwo.setImageResource(getTagTwoImagePressViewCode());
                mTagImageThree.setImageResource(getTagThreeImageViewCode());

                if (mFragmentTwo == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    mFragmentTwo = getFragmentTwo();
                    transaction.add(getShowViewId(), mFragmentTwo);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(mFragmentTwo);
                }
                break;
            case 2:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                mTagTextOne.setTextColor(mGray_csl);
                mTagTextTwo.setTextColor(mGray_csl);
                mTagTextThree.setTextColor(mTheme_csl);

                mTagImageOne.setImageResource(getTagOneImageViewCode());
                mTagImageTwo.setImageResource(getTagTwoImageViewCode());
                mTagImageThree.setImageResource(getTagThreeImagePressViewCode());

                if (mFragmentThree == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    mFragmentThree = getFragmentThree();
                    transaction.add(getShowViewId(), mFragmentThree);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(mFragmentThree);
                }
                break;
        }
        transaction.commit();
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     * 
     * @param transaction 用于对Fragment执行操作的事务
     */
    @SuppressLint("NewApi")
    private void hideFragments(FragmentTransaction transaction) {
        if (mFragmentOne != null) {
            transaction.hide(mFragmentOne);
        }
        if (mFragmentTwo != null) {
            transaction.hide(mFragmentTwo);
        }
        if (mFragmentThree != null) {
            transaction.hide(mFragmentThree);
        }
    }

    /**
     * 清除掉所有的选中状态。
     */
    private void resetBtn() {

    }

}
