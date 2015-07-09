package so.contacts.hub.ui.yellowpage;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import so.contacts.hub.ad.AdCode;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.ui.expresscheck.zxing.ScanCaptureActivity;
import so.contacts.hub.ui.yellowpage.bean.CategoryBean;
import so.contacts.hub.ui.yellowpage.bean.Express;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.SmsAnalysisUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.YellowUtil;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import so.contacts.hub.util.MobclickAgentUtil;

import com.yulong.android.contacts.discover.R;

public class YellowPageExpressSelectHome extends BaseRemindActivity implements OnClickListener {
	
	private static final int REQUEST_CODE_SCAN_CODE = 0x1000;

	private static final int REQUEST_CODE_SELECT_EXPRESS_COM = 0x1001;
	
	private RelativeLayout titleBackLayout;
	private TextView sendExpressLayout;
	private EditText expNumEditText;
	private Button selectBtn;
	private ImageView clearContentImageView;
	private ImageButton scanImgBtn = null;
	
    private TextView clipText;
    private RelativeLayout clipLayout;
    private Button quickSeatchBtn;
	
	
	private String expComId;
	private String expComName;
	private String expNum;
	//add ljq start 2015/01/09 增加剪切板查询单号字段
	private String expNum_quick;
	//add ljq end 2015/01/09 
	private SharedPreferences spHistory;
	public static final String EXPRESS_HISTORY = "express_history";
	public static final String EXPRESS_HISTORY_KEY = "express_history_key";
	public static final String EXPRESS_HISTORY_EXPRESS_COMPANY_INFO_KEY = "express_history_express_company_info_key";
	public static final String CLIPBOARD_BODY_HISTORY = "clipboard_body_history";
	
	private CategoryBean sendExpressCategoryBean;
	private long staticExpressId;
	
	private int mRemindCode = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_express_check_main);
		
		spHistory = getSharedPreferences(EXPRESS_HISTORY, Context.MODE_MULTI_PROCESS);
		
		parseIntent();
		initView();
		loadCompanyInfo();
		showClipboard();
	}
	
	// 友盟统计：进入时间
	private long startTime = 0L;
	
	@Override
	protected void onResume() {
		MobclickAgentUtil.onResume(this);
		startTime = System.currentTimeMillis();
		super.onResume();
	}

	@Override
	protected void onPause() {
		MobclickAgentUtil.onPause(this);
		try {
			int time = ((int) ((System.currentTimeMillis() - startTime) / 1000));
			Map<String, String> map_value = new HashMap<String, String>();
			map_value.put("type", this.getClass().getName());
//	        com.putao.analytics.MobclickAgentUtil.onEventValue(this,
//	                    UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_50, map_value, time);
			MobclickAgentUtil.onEventValue(this,
					UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_50, map_value, time);
		} catch (Exception e) {
		}
		super.onPause();
	}

	private void initView() {
		((TextView) findViewById(R.id.next_step_btn)).setText(R.string.putao_express_send_express);
        if( TextUtils.isEmpty(mTitleContent) ){
            mTitleContent = getResources().getString(R.string.putao_express_check_title);
        }
        ((TextView)findViewById(R.id.title)).setText(mTitleContent);
		
		titleBackLayout = (RelativeLayout) findViewById(R.id.back_layout);
		titleBackLayout.setOnClickListener(this);

		sendExpressLayout = (TextView) findViewById(R.id.next_step_btn);
		sendExpressLayout.setVisibility(View.VISIBLE);
		sendExpressLayout.setText(getResources().getString(R.string.putao_express_send_express));
		findViewById(R.id.next_setp_layout).setOnClickListener(this);
		
		findViewById(R.id.express_select_history_btn).setOnClickListener(this);
		
		selectBtn = (Button) findViewById(R.id.express_select_btn);
		selectBtn.setOnClickListener(this);
		selectBtn.setEnabled(false);
		
		clearContentImageView = (ImageView) findViewById(R.id.clear_number_content_btn);
		clearContentImageView.setOnClickListener(this);
		scanImgBtn = (ImageButton) findViewById(R.id.scan_btn);
		scanImgBtn.setOnClickListener(this);

        clipLayout = (RelativeLayout)findViewById(R.id.clipboard_layout);
        clipText = (TextView)findViewById(R.id.clipboard_text);
        quickSeatchBtn = (Button)findViewById(R.id.express_quick_search_btn);
        quickSeatchBtn.setOnClickListener(this);

		expNumEditText = (EditText) findViewById(R.id.express_delivery_num_edittext);
		expNumEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		expNumEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				expNum = expNumEditText.getText().toString();
				if (null == expNum || "".equals(expNum.trim())) {
					selectBtn.setEnabled(false);
					clearContentImageView.setVisibility(View.GONE);
				} else {
					selectBtn.setEnabled(true);
					clearContentImageView.setVisibility(View.VISIBLE);
					/*
			         * added by ffh 2015-01-13 start for bug 2798
			         * 输入框有输入的时候隐藏粘贴板
			         */
					clipLayout.setVisibility(View.GONE);
				}
			}
		});
		//lihq add for plug-1.3 快递查询API start
		if (!TextUtils.isEmpty(expNum)) {
			expNumEditText.setText(expNum);
		}
		//lihq add for plug-1.3 快递查询API end
	}
	
	private void parseIntent() {
		sendExpressCategoryBean = new CategoryBean();
		Intent intent = getIntent();
		if( intent != null ){
			if (mYellowParams == null) {
				expNum = intent.getStringExtra("exp_num");
				sendExpressCategoryBean.setCategory_id(6);
				sendExpressCategoryBean.setShow_name(getResources().getString(R.string.putao_express_check_title));
				return;
			}else{
				long tid = mYellowParams.getCategory_id();
				sendExpressCategoryBean.setCategory_id(tid);
				sendExpressCategoryBean.setName(mYellowParams.getCategory_name());
				staticExpressId = intent.getLongExtra("static_express_id", 6);
				sendExpressCategoryBean.setCategory_id(staticExpressId);
				mRemindCode = mYellowParams.getRemindCode();
			}
			sendExpressCategoryBean.setShow_name(mTitleContent);
		}
	}
	
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private String readClipboard() {
        String clipText = "";
        /**
         * add by zjh 2014-12-20 start
         * 解决BUG #2211 查快递，粘贴板号码快速查询，查询后粘贴板上的内容被剪切，应该保留，用户可继续粘贴至其他位置
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            android.content.ClipboardManager c = (android.content.ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
            if (c.hasPrimaryClip()) {
            	ClipData clipData = c.getPrimaryClip();
            	/*
                 * modify by putao_lhq at 2015年1月14日 @start
                 * old code:
            	if( clipData != null && clipData.getItemAt(0) != null){
            		clipText = clipData.getItemAt(0).getText().toString();
            	}*/
            	if( clipData != null && clipData.getItemAt(0) != null && clipData.getItemAt(0).getText() != null ){
                    clipText = clipData.getItemAt(0).getText().toString();
                }/* end by putao_lhq */
            	
                //c.setPrimaryClip(ClipData.newPlainText("", ""));
            }
        } else {
            android.text.ClipboardManager c = (android.text.ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
            if (c.hasText()) {
                clipText = c.getText().toString();
                //c.setText("");
            }
        }
        return clipText;
        /** add by zjh 2014-12-20 end */
        
    }

    private void showClipboard() {
        String body = readClipboard();
        String expressNum = SmsAnalysisUtil.getExpressNumWithDigitAndChar(body);
        /*
         * modified by ffh 2015-01-13 start for bug 2798
         * 增加条件 !body.equals(spHistory.getString(CLIPBOARD_BODY_HISTORY, ""))
         */
        if (!body.equals(spHistory.getString(CLIPBOARD_BODY_HISTORY, "")) && !TextUtils.isEmpty(expressNum)) {
            expNum_quick = expressNum;
            clipLayout.setVisibility(View.VISIBLE);
            clipText.setText(expressNum);
        } else {
            clipLayout.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(body)){
            spHistory.edit().putString(CLIPBOARD_BODY_HISTORY, body).commit();
        }else{
            spHistory.edit().putString(CLIPBOARD_BODY_HISTORY, "").commit();
        }
    }
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.express_quick_search_btn) {
		    String body = readClipboard();
		    /*
		     * modified by ffh 2015-01-13 start for bug 2798
		     * old code:
		       String expressNum = SmsAnalysisUtil.getExpressNumWithDigitAndChar(body);
                if (!TextUtils.isEmpty(expressNum)) {
                    expNum = expressNum;
                }
		     */
//            spHistory.edit().putString(CLIPBOARD_BODY_HISTORY, body).commit();
		    //modified by ffh 2015-01-13 end for bug 2798
            clipLayout.setVisibility(View.GONE);
			if (null == expNum_quick || expNum_quick.length() < 5 || isSame(expNum_quick)) {
				showToast(R.string.putao_express_select_error_num);
				return;
			}
			startSelectActivity(expComId, expComName, expNum_quick);

		} else if (id == R.id.express_select_btn) {
			if (null == expNum || expNum.length() < 5 || isSame(expNum)) {
				showToast(R.string.putao_express_select_error_num);
				return;
			}
			startSelectActivity(expComId, expComName, expNum);
		} else if (id == R.id.back_layout) {
			this.finish();
		} else if (id == R.id.clear_number_content_btn) {
			expNumEditText.setText("");
			clearContentImageView.setVisibility(View.GONE);
		} else if (id == R.id.scan_btn) {
			Intent intent = new Intent(this, ScanCaptureActivity.class);
			startActivityForResult(intent, REQUEST_CODE_SCAN_CODE);
		} else if (id == R.id.next_setp_layout) {
			startSendExpressActivity(sendExpressCategoryBean);
		} else if (id == R.id.express_select_history_btn) {
			Intent i = new Intent(this, YellowPageExpressSelectHistoryPage.class);
			startActivity(i);
		} else {
		}
	}
	
	private void startSendExpressActivity(CategoryBean bean) {

		if (null == bean) {
			return;
		}

		YellowParams params = new YellowParams();
		try {
			Intent intent = new Intent(YellowPageExpressSelectHome.this,
					Class.forName(YellowUtil.DefCategoryActivity));
			params.setCategory_id(bean.getCategory_id());
			params.setCategory_name(bean.getName());
//			params.setTitle(bean.getShow_name());
			params.setTitle(getString(R.string.putao_express_send_express));

			intent.putExtra(YellowUtil.TargetIntentParams, params);

			startActivity(intent);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void startSelectActivity(String expComIdStr, String expComNameStr, String expNumStr) {
	
		Intent i = new Intent(YellowPageExpressSelectHome.this,
				YellowPageExpressSelectReslutPage.class);
		i.putExtra(YellowPageExpressSelectReslutPage.EXTRA_EXP_COM_ID, expComIdStr);
		i.putExtra(YellowPageExpressSelectReslutPage.EXTRA_EXP_COM_NAME,
				expComNameStr);
		i.putExtra(YellowPageExpressSelectReslutPage.EXTRA_EXP_NUM, expNumStr);

		startActivity(i);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(REQUEST_CODE_SELECT_EXPRESS_COM == requestCode){
			if(resultCode == RESULT_OK){
				Express e = (Express) data.getSerializableExtra("express");
				if(null != e){
					String name = e.getName();
					expComId = e.getPinyin();
					expComName = name;
					saveCompanyInfo(expComId, expComName);
					
					if(!TextUtils.isEmpty(expComId) && !TextUtils.isEmpty(expNum) 
							&& expNum.length() >= 5 && !isSame(expNum)){
						startSelectActivity(expComId, expComName, expNum);
					}
				}
			}
		}else if( REQUEST_CODE_SCAN_CODE == requestCode ){
            if(resultCode == RESULT_OK){
                String contents = data.getStringExtra("SCAN_RESULT");
                if (null == contents || contents.length() < 5 || isSame(contents)) {
                    showToast(R.string.putao_express_select_error_num);
                    return;
                }
                if (checkInputContent(contents)) {
                    expNumEditText.setText(contents);
                    startSelectActivity(expComId, expComName, contents);
                }else {
                    showToast(R.string.putao_express_select_error_num);
                }
                
            }
        }
    }
    
    private boolean checkInputContent(String contents){
//      StringBuilder temp = new StringBuilder(contents);
        try {
            char[] temC = contents.toCharArray();
            for (int i=0;i<temC.length;i++) {
                char mid = temC[i];
                if(mid>=48&&mid<=57){//数字
                    continue;
                }
                if(mid>=65&&mid<=90){//大写字母
                    continue ;
                }
                if(mid>=97&&mid<=122){//小写字母
                    continue ;
                }
//              temp.replace(i, i+1, " ");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    
	
	private void loadCompanyInfo() {
		String lastCompanyInfo = spHistory.getString(
				EXPRESS_HISTORY_EXPRESS_COMPANY_INFO_KEY, null);
		if (null != lastCompanyInfo && !"".equals(lastCompanyInfo)) {
			String[] infos = lastCompanyInfo.split(",");
			String comId = infos[0];
			String comName = infos[1];
			if (null != comId && null != comName && !"".equals(comId)
					&& !"".equals(comName)) {
				expComId = comId;
				expComName = comName;
			}
		}
	}
	
	private void saveCompanyInfo(String comId, String comName){
		spHistory
				.edit()
				.putString(EXPRESS_HISTORY_EXPRESS_COMPANY_INFO_KEY,
						comId + "," + comName).commit();
	}
	
	// 顺序表
	static String orderStr = "";
	static {
		for (int i = 33; i < 127; i++) {
			orderStr += Character.toChars(i)[0];
		}
	}
    
	// 判断是否有顺序
	public static boolean isOrder(String str) {
		if (!str.matches("((\\d)|([a-z])|([A-Z]))+")) {
			return false;
		}
		return orderStr.contains(str);
	}
	
	// 判断是否相同
	public static boolean isSame(String str) {
		String regex = str.substring(0, 1) + "{" + str.length() + "}";
		return str.matches(regex);
	}
	
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
	
	private Toast mToast;
	private void showToast(int stringId) {
		if (null == mToast) {
			mToast = Toast.makeText(this, stringId, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(stringId);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

    @Override
    public Integer remindCode() {
        return mRemindCode;
    }
    
    @Override
    public String getServiceNameByUrl() {
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
	public Integer getAdId() {
	    return AdCode.ADCODE_YellowPageExpressSelectHome;
	}
}
