package so.contacts.hub.thirdparty.tongcheng.util;

/**
 * 酒店订单提交时状态验证逻辑
 * @author Michael
 *
 */
public class TC_Hotel_OrderState_Util {
	
	public static final int SUBMIT_ORDER_TYPE_NONE = 0;		//提交订单类型：无担保
	public static final int SUBMIT_ORDER_TYPE_DANBAO = 1;	//提交订单类型：担保冻结 或者 担保预付
	public static final int SUBMIT_ORDER_TYPE_YUFU = 2;		//提交订单类型：代收代付

	/**
	 * 判断是否需要担保、预付等状态
	 */
	public static int checkOrderStateData(int guaranteeType, int danBaoType, int overTime, int userArriveTime){
		int submitOrderType = SUBMIT_ORDER_TYPE_NONE;
		//担保类型(0-无担保；1-担保冻结；2-担保预付；3-代收代付)(用来判断是否需要支付再提交订单)
        if( guaranteeType == 0 ){
        	//提交订单(0-无担保)
        	submitOrderType = SUBMIT_ORDER_TYPE_NONE;
        }else if( guaranteeType == 1 || guaranteeType == 2){
        	//担保(1-担保冻结；2-担保预付)
        	if( danBaoType == 0 ){
        		//担保政策类型等于0时，则不需要担保
        		submitOrderType = SUBMIT_ORDER_TYPE_NONE;
        	}else{
        		// 超时点钟（表示>mOverTime要担保，不包含=mOverTime）
        		if( overTime == -1 ){
        			//如果同城没有返回这个节点，则一定要担保的
        			submitOrderType = SUBMIT_ORDER_TYPE_DANBAO;
        		}else{
        			if( userArriveTime > overTime ){
        				//担保
        				submitOrderType = SUBMIT_ORDER_TYPE_DANBAO;
        			}else{
        				//不需要担保
        				submitOrderType = SUBMIT_ORDER_TYPE_NONE;
        			}
        		}
        	}
        }else{
        	//信用卡预付(3-代收代付)
        	submitOrderType = SUBMIT_ORDER_TYPE_YUFU;
        }
        return submitOrderType;
	}
	
}
