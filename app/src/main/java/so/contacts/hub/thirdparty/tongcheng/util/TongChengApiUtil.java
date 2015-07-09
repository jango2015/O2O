package so.contacts.hub.thirdparty.tongcheng.util;

import so.contacts.hub.util.CalendarUtil;

import so.contacts.hub.thirdparty.tongcheng.bean.TCRequestData;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Request_HotelList;

import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_HotelList;


public class TongChengApiUtil {

    public static TC_Response_HotelList getHotelList(String keyword, String cityId, double latitude, double longitude, int radius, int pageSize, int page){
        TC_Request_HotelList hotelRequestBody = new TC_Request_HotelList();
        hotelRequestBody.setKeyword(keyword);
        hotelRequestBody.setCityId(cityId);
        hotelRequestBody.setComeDate(CalendarUtil.getNowDateStr());
        hotelRequestBody.setLeaveDate(CalendarUtil.getTomorrowDateStr());
        hotelRequestBody.setLatitude(latitude);
        hotelRequestBody.setLongitude(longitude);
        hotelRequestBody.setPage(page);
        hotelRequestBody.setPageSize(pageSize);
        hotelRequestBody.setClientIp("192.168.1.108");//修复同程酒店搜索不出来的bug add by ls 2015-03-10;
        if(latitude != 0 && longitude != 0 ){
        	//有经纬度时按照距离最近排序
        	hotelRequestBody.setSortType(TC_Common.TC_SEARCH_SORT_TYPE_DISTANCE);
        }else{
        	//否则按照默认排序
        	hotelRequestBody.setSortType(TC_Common.TC_SEARCH_SORT_TYPE_DEFAULT);
        }
        String requestBody = hotelRequestBody.getBody();
        String requestHead = TC_Request_DataFactory.getRequestHead("GetHotelList");
        String url = TC_Common.TC_URL_SEARCH_HOTEL;
        TCRequestData requestData = new TCRequestData(requestHead, requestBody);
        Object object = TC_Http.getDataByPost(url, requestData.getReqeustData(), TC_Response_HotelList.class);
        if( object == null ){
            return null;
        }
        return (TC_Response_HotelList)object;
    }
}
