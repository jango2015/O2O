package com.yulong.android.contacts.ui.yellowpage;

import android.content.Intent;

interface DoubleCardInterface {

	/**获取当前有效的sim卡数目*/
	int getValidCardNumber();
	
	/**根据接收到短信的广播的intent判断是哪张sim卡接收到的。
	 *返回值的对应关系如下：
	 *-1:获取失败
	 *1:卡1
	 *2:卡2
	 */
	int getCardSlot(in Intent intent);
	
	/**根据卡槽id(卡1为1、卡2为2)获取sim卡的显示名称*/
	String getCardDisplayNameFromSlotId(int slotId);
	
	/**根据phone id获取对应的卡槽id*/
	int getSlotIdByPhoneId(int phoneId);
	
	/**获取当前sim卡所在卡槽的id(在getValidCardNumber方法返回1时调用才正确，否则返回-1)*/
	int getValidSlotId();
}