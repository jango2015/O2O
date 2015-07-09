package so.contacts.hub.ui.yellowpage;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.ad.AdCode;
import so.contacts.hub.adapter.ServerManagerAddedAdapter;
import so.contacts.hub.adapter.ServerManagerAllAdapter;
import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.ui.yellowpage.bean.CategoryBean;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.MyCenterConstant;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.Utils;
import so.contacts.hub.util.YellowPagePlugUtil;
import so.contacts.hub.util.YellowUtil;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;
import so.contacts.hub.widget.CustomGridView;
import so.contacts.hub.widget.DragGridView;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yulong.android.contacts.discover.R;

public class YellowPageServerManagerActivity extends BaseRemindActivity implements OnClickListener, OnItemClickListener {

	private static final String TAG = "YellowPageServerManagerActivity";
	
	
	//add ljq 2014-10-10 start
    /**
     * 底色图片名称数组 只包含normal状态
     * @return
     */
    private static final String[] BASE_COLOR = new String[]{"putao_icon_logo_base_blue","putao_icon_logo_base_cyan","putao_icon_logo_base_green"
        ,"putao_icon_logo_base_navy","putao_icon_logo_base_orange","putao_icon_logo_base_pink","putao_icon_logo_base_purple","putao_icon_logo_base_sky"
        ,"putao_icon_logo_base_yolk"};
    /**
     * 底色图片名称加入此后缀得到 按下状态图片 名称
     */
    private static final String BASE_COLOR_SUFFIX = "_d";
    //add ljq 2014-10-10 end
	
	private DragGridView mAddedGridView = null;
	
	private CustomGridView mAllGridView = null;
	
	private ServerManagerAddedAdapter mAddedAdapter = null;
	
	private ServerManagerAllAdapter mAllAdapter = null;
	
	private List<CategoryBean> mAddedCategoryList = new ArrayList<CategoryBean>();
	
	private List<CategoryBean> mAllCategoryList = new ArrayList<CategoryBean>();
	
	private YellowPageDB mYellowPageDB = null;
	
	//自定义快速搜索起始ID号
	private static final int QUICK_SRARCH_CATEGORYBEAN_BASIC_ID = 1800;
	//自定义快速搜索ID范围
    private static final int QUICK_SRARCH_CATEGORYBEAN_ID_AREA = 100;
	
	//添加自定义搜索按钮
	private Button mAddedQuickSearchButton = null;
	
	/**
	 * "常用" 中"我的"，在编辑服务中不需要显示，但是在主界面中需要显示
	 * 因此进入时，需要将其过滤，退出时需要将其保存
	 */
	private CategoryBean mMyCategoryBean = null;
	
	/** 是否在移动，由于这边是动画结束后才进行的数据更替，设置这个限制为了避免操作太频繁造成的数据错乱。 */	
	private boolean isMove = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_yellow_page_server_manager);
		initView();
		initData();
		MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_MY_SERVER_MANAGER);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgentUtil.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgentUtil.onPause(this);
	}
	
	private void initView(){
		((TextView) findViewById(R.id.title)).setText(getResources().getString(R.string.putao_servermanager_title));
		findViewById(R.id.back_layout).setOnClickListener(this);
		
		mAddedGridView = (DragGridView) findViewById(R.id.added_gridView);
		mAddedGridView.setOnItemClickListener(this);
		mAllGridView = (CustomGridView) findViewById(R.id.all_gridview);
		mAllGridView.setOnItemClickListener(this);
		mAddedQuickSearchButton = (Button) findViewById(R.id.add_quick_search_button);
		mAddedQuickSearchButton.setOnClickListener(this);
		
	}
	
	private void initData(){
		mYellowPageDB = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper();
		List<CategoryBean> categorys = mYellowPageDB.queryCategoryByParentId(YellowUtil.YELLOW_PAGE_PARENTID_ALL);
        if(categorys == null || categorys.size() == 0){
        	return;
        }
        
        // 常用 部分
        CategoryBean addedbean = categorys.get(1);
        if( addedbean != null ){
        	mAddedCategoryList = mYellowPageDB.queryCategoryByParentId(addedbean.getCategory_id(), false);
        	filterItemWithMy();
    		mAddedAdapter = new ServerManagerAddedAdapter(this, mAddedCategoryList);
    		mAddedGridView.setAdapter(mAddedAdapter);
        }
        
        // 全部 部分
        CategoryBean allbean = categorys.get(2);
        if( allbean != null ){
        	mAllCategoryList = mYellowPageDB.queryCategoryByParentId(allbean.getCategory_id());
        	
        	mAllAdapter = new ServerManagerAllAdapter(this, mAllCategoryList);
        	mAllGridView.setAdapter(mAllAdapter);
        }
        
	}
	
	/**
	 * 过滤"我的"
	 */
	private void filterItemWithMy(){
		if( mAddedCategoryList == null ){
			return;
		}
		int addedSize = mAddedCategoryList.size();
		for(int i = 0; i < addedSize; i++){
			String targetActivity = mAddedCategoryList.get(i).getTarget_activity();
			if( MyCenterConstant.MY_NODE.equals(targetActivity) ){
				mMyCategoryBean = mAddedCategoryList.remove(i);
				return;
			}
		}
	}

	/**
	 * 上下跳动规则：
	 * 1、"常用" 添加到 "全部"
	 *    1.1、排序规则：按照last_sort进行排序
	 *    1.2、CategoryBean的editType = 1，则是不可删除
	 * 2、"全部" 添加到 "常用"，添加到"常用"末尾
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
		// TODO Auto-generated method stub
		//如果点击的时候，之前动画还没结束，那么就让点击事件无效
		if(isMove){
			return;
		}
		isMove = true;
		//获取点击的类别内容
		final CategoryBean categoryBean = (CategoryBean) parent.getAdapter().getItem(position);
		if( categoryBean != null && categoryBean.getEditType() == YellowUtil.YELLOW_CATEGORY_EDITTYPE_NOT_DEL ){
			Utils.showToast(YellowPageServerManagerActivity.this, R.string.putao_servermanager_not_del_hint, false);
			isMove = false;
			return;
		}
		int parentId = parent.getId();
		if (parentId == R.id.added_gridView) {
			//如果是用户自己添加类型 目前只有快速搜索 则做移除动作
		    if (categoryBean != null&& categoryBean.getEditType() == YellowUtil.YELLOW_CATEGORY_EDITTYPE_USER_ADD) {
		        mAddedAdapter.setRemove(position);
		        mAddedAdapter.remove();
		        Utils.showToast(YellowPageServerManagerActivity.this, R.string.putao_servermanager_del_quick_search, false);
		        isMove = false;
		        return;
            } else {
                MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_MY_SERVER_MANAGER_REMOVE);
                final ImageView addedMoveImageView = getView(view);
                if (addedMoveImageView != null) {
                    TextView newTextView = (TextView) view.findViewById(R.id.added_item_name);
                    final int[] startLocation = new int[2];
                    // 获取起点的坐标
                    newTextView.getLocationInWindow(startLocation);
                    // 获取点击的类别内容
                    
                    mAllAdapter.setVisible(false);
                    
                    int lastSort = categoryBean.getLastSort();
                    int index = -1; 
                    if( lastSort != YellowUtil.YELLOW_CATEGORY_DEFAULT_LASTSORT ){
                        // 则按之前保留的顺序添加
                        index = mAllAdapter.getCurrentIndex(lastSort);
                    }
                    mAllAdapter.addItem(categoryBean, index);
                    //add ljq start 2014/10/13 标记用户改变行为
                    categoryBean.setChange_type(YellowUtil.YELLOW_CATEGORY_CHANGE_TYPE_USER_MODITY);
                    //add ljq end
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                int[] endLocation = new int[2];
                                //获取终点的坐标
                                int addChildPos = mAllAdapter.getAddedPostion(); 
                                if( addChildPos == -1 ){
                                    mAllGridView.getLastVisiblePosition();
                                }
                                mAllGridView.getChildAt(addChildPos).getLocationInWindow(endLocation);
                                MoveAnim(addedMoveImageView, startLocation , endLocation, categoryBean, mAddedGridView);
                                mAddedAdapter.setRemove(position);
                            } catch (Exception localException) {
                            }
                        }
                    }, 50L);
                }
            }
		} else if (parentId == R.id.all_gridview) {
			MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_MY_SERVER_MANAGER_ADD);
			final ImageView allMoveImageView = getView(view);
			if (allMoveImageView != null){
				TextView newTextView = (TextView) view.findViewById(R.id.allitem_name);
				final int[] startLocation = new int[2];
				// 获取起点的坐标
				newTextView.getLocationInWindow(startLocation);
				mAddedAdapter.setVisible(false);
				//添加到最后一个
				mAddedAdapter.addItem(categoryBean, true);
                //add ljq start 2014/10/13 标记用户改变行为
                categoryBean.setChange_type(YellowUtil.YELLOW_CATEGORY_CHANGE_TYPE_USER_MODITY);
                //add ljq end
				new Handler().postDelayed(new Runnable() {
					@Override
                    public void run() {
						try {
							int[] endLocation = new int[2];
							//获取终点的坐标
							mAddedGridView.getChildAt(mAddedGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
							MoveAnim(allMoveImageView, startLocation , endLocation, categoryBean, mAllGridView);
							mAllAdapter.setRemove(position);
						} catch (Exception localException) {
						}
					}
				}, 50L);
			}
		} else {
			isMove = false;
		}
	}
	
	/**
	 * 点击ITEM移动动画
	 */
	private void MoveAnim(View moveView, int[] startLocation,int[] endLocation, final CategoryBean moveCategory,
			final GridView clickGridView) {
		int[] initLocation = new int[2];
		//获取传递过来的VIEW的坐标
		moveView.getLocationInWindow(initLocation);
		//得到要移动的VIEW,并放入对应的容器中
		final ViewGroup moveViewGroup = getMoveViewGroup();
		final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
		//创建移动动画
		TranslateAnimation moveAnimation = new TranslateAnimation(
				startLocation[0], endLocation[0], startLocation[1],
				endLocation[1]);
		moveAnimation.setDuration(300L);//动画时间
		//动画配置
		AnimationSet moveAnimationSet = new AnimationSet(true);
		moveAnimationSet.setFillAfter(false);//动画效果执行完毕后，View对象不保留在终止的位置
		moveAnimationSet.addAnimation(moveAnimation);
		mMoveView.startAnimation(moveAnimationSet);
		moveAnimationSet.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				moveViewGroup.removeView(mMoveView);
				// 判断点击的是DragGridView还是CustomGridView
				if (clickGridView instanceof DragGridView) {
					mAllAdapter.setVisible(true);
					if( mAddedAdapter.remove() ){
						mAllAdapter.notifyDataSetChanged();
					}
				}else{
					mAddedAdapter.setVisible(true);
					if( mAllAdapter.remove() ){
						mAddedAdapter.notifyDataSetChanged();
					}
				}
				isMove = false;
			}
		});
	}
	
	/**
	 * 获取移动的VIEW，放入对应ViewGroup布局容器
	 */
	private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
		int x = initLocation[0];
		int y = initLocation[1];
		viewGroup.addView(view);
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mLayoutParams.leftMargin = x;
		mLayoutParams.topMargin = y;
		view.setLayoutParams(mLayoutParams);
		return view;
	}
	
	/**
	 * 创建移动的ITEM对应的ViewGroup布局容器
	 */
	private ViewGroup getMoveViewGroup() {
		ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
		LinearLayout moveLinearLayout = new LinearLayout(this);
		moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		moveViewGroup.addView(moveLinearLayout);
		return moveLinearLayout;
	}
	
	/**
	 * 获取点击的Item的对应View，
	 */
	private ImageView getView(View view) {
		view.destroyDrawingCache();
		view.setDrawingCacheEnabled(true);
		Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
		view.setDrawingCacheEnabled(false);
		ImageView iv = new ImageView(this);
		iv.setImageBitmap(cache);
		return iv;
	}
	
	/**
	 * 保存数据
	 */
	private void saveModeifyData(){
		if( mMyCategoryBean != null ){
			// 将"我的"添加到数据库中
			mAddedAdapter.addItem(mMyCategoryBean, false);
		}
		List<CategoryBean> offenList = mAddedAdapter.getCategoryLst();
		List<CategoryBean> allList = mAllAdapter.getCategoryLst();
		for(int i = 0; i < allList.size(); i++){
			// "全部" 里面以 last_sort为默认排序
			allList.get(i).setSort(allList.get(i).getLastSort());
		}
		mYellowPageDB.updateCategoryData(offenList, allList);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		/** 再次检验移动的动画是否结束（结束了会删除数据），没结束则删除数据 */
		mAddedAdapter.remove();
		mAllAdapter.remove();
		/** */
		
		if(mAddedAdapter.isListChanged()){
			LogUtil.i(TAG, "onBackPressed home data has changed. need update home-layout.");
			saveModeifyData();
			// 有数据更新， 更新黄页数据
			YellowPagePlugUtil.getInstance().setRefreshPlugViewState(YellowPagePlugUtil.STATE_REFRESH_ALL_VIEW);
			finish();
		}else{
			super.onBackPressed();
		}
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.back_layout) {
			onBackPressed();
		} else if (id == R.id.add_quick_search_button) {
			//获取对话框
			final CommonDialog commonDialog = CommonDialogFactory.getEditTextCommonDialog(this);
			commonDialog.setTitle(R.string.putao_servermanager_dialog_title);
			commonDialog.getInputEditView().setHint(R.string.putao_servermanager_dialog_edittext_hint);
			commonDialog.setOkButtonClickListener(new OnClickListener() {
			    @Override
			    public void onClick(View arg0) {
			        String content = commonDialog.getInputEditView().getText().toString();
			        if(content != null && content.length()>0){
			            commonDialog.dismiss();
			            //截取信息 插入数据 
			            CategoryBean cb = createQuickSearchServerManagerBean(content);
			            mAddedAdapter.addItem(cb, true);
			            Utils.showToast(YellowPageServerManagerActivity.this, R.string.putao_servermanager_add_quick_search, false);
			        }else{
			            Utils.showToast(YellowPageServerManagerActivity.this, R.string.putao_servermanager_dialog_null_edittext_content, false);
			        }
			    }
			});
			commonDialog.setCancelButtonClickListener(new OnClickListener() {
			    @Override
			    public void onClick(View arg0) {
			        commonDialog.dismiss();
			    }
			});
			commonDialog.show();
		} else {
		}
	}
	
	/**
     * modify by ljq at 2014-10-09
     * 添加 "自定义搜索" 项
     * 注意：放在"常用"的后面
     */
    private CategoryBean createQuickSearchServerManagerBean(String name){
        CategoryBean categoryBean = new CategoryBean();
        //获取随机的ID号 可配置范围
        categoryBean.setCategory_id(getRandomBaseCategoryId());
        categoryBean.setName(name);
        // modify by putao_lhq 2014年10月30日 start
        //categoryBean.setShow_name("zh_CN:" + name);
        String showName = addLanguage(name);
		categoryBean.setShow_name(showName);
        // modify by putao_lhq 2014年10月30日 end
        categoryBean.setSort(0); 
        categoryBean.setLastSort(YellowUtil.YELLOW_CATEGORY_DEFAULT_LASTSORT);
        //获取随机的颜色背景
        String icon_str = getRandomBaseColorId();
        categoryBean.setIcon(icon_str);
        categoryBean.setPressIcon(icon_str+BASE_COLOR_SUFFIX);
        categoryBean.setTarget_activity(YellowPageSearchNumberActivity.class.getName());
        //配置传参
        YellowParams parames = new YellowParams();
        parames.setWords(name);
        Gson gson = new Gson();
        String parames_str = gson.toJson(parames);
        categoryBean.setTarget_params(parames_str);
        categoryBean.setParent_id(YellowUtil.YELLOW_PAGE_CATEGORY_ID_OFFEN); //属于常用
        categoryBean.setEditType(YellowUtil.YELLOW_CATEGORY_EDITTYPE_USER_ADD);//注意类型! 是属于用户添加
        categoryBean.setRemind_code(YellowUtil.YELLOW_CATEGORY_DEFAULT_REMIND_CODE);
        categoryBean.setKey_tag(name); // 关键字标签（自定义的设为自己）
        return categoryBean;
    }

    /**
     * 支持语言组装
     * @param name
     * @return
     */
	private String addLanguage(String name) {
		StringBuilder sb = new StringBuilder();
        sb.append("zh_CN:");
        sb.append(name);
        sb.append(";");
        sb.append("en_US:");
        sb.append(name);
        sb.append(";");
        sb.append("zh_TW:");
        sb.append(name);
        return sb.toString();
	}
    /**
     * 生成随机的ID号 如果数据库中有相同则一直随机 直到不同 可配置范围
     * QUICK_SRARCH_CATEGORYBEAN_BASIC_ID 开始ID
     * QUICK_SRARCH_CATEGORYBEAN_ID_AREA  范围
     * 目前是1000-1999
     */
    private int getRandomBaseCategoryId(){
        
        int i = QUICK_SRARCH_CATEGORYBEAN_BASIC_ID;
        while(true){
            i = (int)(Math.random()*(QUICK_SRARCH_CATEGORYBEAN_ID_AREA))+QUICK_SRARCH_CATEGORYBEAN_BASIC_ID;
            CategoryBean categoryBean = mYellowPageDB.queryCategoryByCategoryId(i);
            if(categoryBean !=null){
                continue;
            }else{
                break;
            }
        }
        return i;
    }
    /**
     * 随机底色图片
     * @return
     */
    private String getRandomBaseColorId(){
        //生成随机数  范围在颜色数组大小内
        int i = (int)(Math.random()*(BASE_COLOR.length));
        return BASE_COLOR[i];
    }

	@Override
	public String getServiceNameByUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServiceName() {
		return this.getClass().getName();
	}

	@Override
	public boolean needMatchExpandParam() {
		return false;
	}

	@Override
	public Integer remindCode() {
		return mRemindCode;
	}

    @Override
    public Integer getAdId() {
        return AdCode.ADCODE_YellowPageServerManagerActivity;
    }


	
}
