
package so.contacts.hub.thirdparty.cinema.tool;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import so.contacts.hub.core.Config;
import so.contacts.hub.db.MovieDB;
import so.contacts.hub.db.MovieDB.MovieCityTable;
import so.contacts.hub.thirdparty.cinema.bean.MovieCity;
import so.contacts.hub.thirdparty.cinema.utils.GewaApiReqMethod;
import so.contacts.hub.util.CalendarUtil;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.HexUtil;
import so.contacts.hub.util.LogUtil;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author ffh
 * @since 2014/12/18 电影请求URL
 */
public class CinemaApiUtil {

    public static final String TEST_HOST_DOMAIN = Config.MOVIE.TEST_HOST_DOMAIN;

    public static final String DEVELOP_HOST_DOMAIN = Config.MOVIE.DEVELOP_HOST_DOMAIN;

    private static final String appkey = "putao";

    private static final String secretcode = "055019f605272402b2d3d6b1b110b2f2";

    private static final String format = "xml";

    private static final String signmethod = "MD5";

    private static final String v = "1.0";
    
    
    public static boolean isExistCityDBData() {
        MovieDB db = ContactsAppUtils.getInstance().getDatabaseHelper().getMovieDB();
        List<MovieCity> list = db.queryMovieCityAll();
        return list != null && list.size() > 0;
    }
    

    @SuppressWarnings("unchecked")
    public static void initMovieCityDB() {
        String movieCityListUrl = CinemaApiUtil.getMovieCityListUrl();
        List<MovieCity> list = (List<MovieCity>)CinemaApiUtilHelper.doHttpGetObjFromUrl(movieCityListUrl.replace(" ", "%20"), GewaApiReqMethod.OPEN_PARTNER_CITYLIST);
        MovieDB db = ContactsAppUtils.getInstance().getDatabaseHelper().getMovieDB();
        db.clearTable(MovieCityTable.TABLE_NAME);
        db.insertMovieCity(list);
    }
    
    /**
     * 获取电影详情URL
     * 
     * @param movieId
     * @param fields
     * @param picWidth
     * @param picHeight
     * @return
     */
    public static String getMovieDetailUrl(long movieId, String fields, int picWidth, int picHeight) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("method", GewaApiReqMethod.MOVIE_DETAIL.toString());
        paramMap.put("movieid", String.valueOf(movieId));
        if (!TextUtils.isEmpty(fields)) {
            paramMap.put("fields", fields);
        }
        if (picWidth != 0) {
            paramMap.put("picwidth", String.valueOf(picWidth));
        }
        if (picHeight != 0) {
            paramMap.put("picheight", String.valueOf(picHeight));
        }
        TreeMap<String, String> params = reSetMap(paramMap);
        return getApiUrl(params);
    }
    
    
    /**
     * 获取正在热映的电影
     * @param cinemaid
     * @param playdate
     * @param cityCode
     * @param fields
     * @param picWidth
     * @param picHeight
     * @return
     */
    public static String getOpenMovieListUrl(long cinemaid,String playdate,String cityCode,String fields,int picWidth,int picHeight){
        Map<String, String> paramMap = new HashMap<String,String>();
        paramMap.put("method", GewaApiReqMethod.OPEN_MOVIE_LIST.toString());
        if (cinemaid != 0) {
            paramMap.put("cinemaid", String.valueOf(cinemaid));
        }
        if (!TextUtils.isEmpty(playdate)) {
            paramMap.put("playdate", String.valueOf(playdate));
        }
        if (!TextUtils.isEmpty(cityCode)) {
            paramMap.put("citycode", String.valueOf(cityCode));
        }
        if (!TextUtils.isEmpty(fields)) {
            paramMap.put("fields", fields);
        }
        if (picWidth != 0) {
            paramMap.put("picwidth", String.valueOf(picWidth));
        }
        if (picHeight != 0) {
            paramMap.put("picheight", String.valueOf(picHeight));
        }
        TreeMap<String, String> params = reSetMap(paramMap);
        return getApiUrl(params);
    }
    
    /**
     * 获取电影城市
     */
    public static String getMovieCityListUrl(){
        Map<String, String> paramMap = new HashMap<String,String>();
        paramMap.put("method", GewaApiReqMethod.OPEN_PARTNER_CITYLIST.toString());
        TreeMap<String,String> params = reSetMap(paramMap);
        return getApiUrl(params);
    }
    
    /**
     * 获取即将上映的电影
     * @param cinemaid
     * @param playdate
     * @param cityCode
     * @param fields
     * @param picWidth
     * @param picHeight
     * @return
     */
    public static String getFutureMovieListUrl(int from,Integer maxnum,String fields,int picWidth,int picHeight){
        Map<String, String> paramMap = new HashMap<String,String>();
        paramMap.put("method", GewaApiReqMethod.FUTURE_MOVIE_LIST.toString());
        if (from != 0) {
            paramMap.put("from", String.valueOf(from));
        }
        if (maxnum != 0) {
            paramMap.put("maxnum", String.valueOf(maxnum));
        }
        if (!TextUtils.isEmpty(fields)) {
            paramMap.put("fields", fields);
        }
        if (picWidth != 0) {
            paramMap.put("picwidth", String.valueOf(picWidth));
        }
        if (picHeight != 0) {
            paramMap.put("picheight", String.valueOf(picHeight));
        }
        TreeMap<String, String> params = reSetMap(paramMap);
        return getApiUrl(params);
    }

    /**
     * 获取影院列表URL
     * 
     * @param playDate
     * @param movieId
     * @param cityCode
     * @param fields
     * @param picWidth
     * @param picHeight
     * @return
     */
    public static String getMovieCinemaListUrl(String playDate, long movieId, String cityCode,
            String fields, int picWidth, int picHeight) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("method", GewaApiReqMethod.OPEN_CINEMA_LIST_BY_PLAYDATE.toString());
        if (!TextUtils.isEmpty(playDate)) {
            paramMap.put("playdate", playDate);
        }
        if (movieId != 0) {
            paramMap.put("movieid", String.valueOf(movieId));
        }
        if (!TextUtils.isEmpty(cityCode)) {
            paramMap.put("citycode", String.valueOf(cityCode));
        }
        if (TextUtils.isEmpty(fields)) {
            paramMap.put("fields", fields);
        }
        if (picWidth != 0) {
            paramMap.put("picwidth", String.valueOf(picWidth));
        }
        if (picHeight != 0) {
            paramMap.put("picheight", String.valueOf(picHeight));
        }
        TreeMap<String, String> params = reSetMap(paramMap);
        return getApiUrl(params);
    }

    /**
     * @author peku
     * @param movieId 电影Id
     * @return 取票机位置接口url
     */

    public static String getTicketHelpUrl(long movieId) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("method", GewaApiReqMethod.TICKET_HELP.toString());
        if (movieId != 0) {
            paramMap.put("cinemaid", String.valueOf(movieId));
        }
        TreeMap<String, String> params = reSetMap(paramMap);
        return getApiUrl(params);
    }
    
    /**
     * 根据订单号查询订单详情
     * @param tradeno
     * @return
     */
    public static String getMovieOrderDetailsUrl(String tradeno) {
    	Map<String, String> paramMap = new HashMap<String, String>();
    	paramMap.put("method", GewaApiReqMethod.TICKETORDER_DETAIL.toString());
    	if (null!=tradeno) {
    		paramMap.put("tradeno", String.valueOf(tradeno));
    	}
    	TreeMap<String, String> params = reSetMap(paramMap);
    	return getApiUrl(params);
    }
    
    /**
     * 取消订单
     * @param tradeno
     * @param ukey
     * @return
     */
    public static String getCancelOrderUrl(String tradeno,String ukey) {
    	Map<String, String> paramMap = new HashMap<String, String>();
    	paramMap.put("method", GewaApiReqMethod.CANCEL_OEDER.toString());
    	if (null!=tradeno) {
    		paramMap.put("tradeNo", tradeno);
    		paramMap.put("ukey", ukey);
    	}
    	TreeMap<String, String> params = reSetMap(paramMap);
    	Log.i("L", ""+getApiUrl(params));
    	return getApiUrl(params);
    }
    
    

    /**
     * 获取播放日期列表URL
     * 
     * @param cinemaId
     * @param movieId
     * @return
     */
    public static String getPlayListUrl(long cinemaId, long movieId) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("method", GewaApiReqMethod.PLAYDATE_LIST.toString());
        paramMap.put("movieid", String.valueOf(movieId));
        paramMap.put("cinemaid", String.valueOf(cinemaId));
        TreeMap<String, String> params = reSetMap(paramMap);
        return getApiUrl(params);
    }

    /**
     * 获取场次列表URL
     * 
     * @param playDate
     * @param cinemaId
     * @param movieId
     * @return
     */
    public static String getOpiListUrl(String playDate, long cinemaId, long movieId) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("method", GewaApiReqMethod.OPI_LIST.toString());
        paramMap.put("cinemaid", String.valueOf(cinemaId));
        if (!TextUtils.isEmpty(playDate)) {
            paramMap.put("playdate", playDate);
        }
        if (movieId != 0) {
            paramMap.put("movieid", String.valueOf(movieId));
        }
        TreeMap<String, String> params = reSetMap(paramMap);
        return getApiUrl(params);
    }

    /**
     * 获取该场次影厅的座位信息URL
     * 
     * @param mpid
     * @return
     */
    public static String getOPISeatInfoUrl(long mpid) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("method", GewaApiReqMethod.OPI_SEAT_INFO.toString());
        if (mpid != 0) {
            paramMap.put("mpid", String.valueOf(mpid));
        }
        TreeMap<String, String> params = reSetMap(paramMap);
        return getApiUrl(params);
    }

    /**
     * 下订单并锁定座位接口的URL
     * 
     * @param mpid
     * @param mobile
     * @param language
     * @param edition
     * @param seatLabel
     * @param ukey
     * @return
     */
    public static String getAddTicketOrderUrl(long mpid, String mobile, String language,
            String edition, String seatLabel, String ukey) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("method", GewaApiReqMethod.TICKETORDER_ADD.toString());
        if (mpid != 0) {
            paramMap.put("mpid", String.valueOf(mpid));
        }
        if (!TextUtils.isEmpty(mobile)) {
            paramMap.put("mobile", mobile);
        }
        if (!TextUtils.isEmpty(language)) {
            paramMap.put("language", language);
        }
        if (!TextUtils.isEmpty(edition)) {
            paramMap.put("edition", edition);
        }
        if (!TextUtils.isEmpty(seatLabel)) {
            paramMap.put("seatLabel", seatLabel);
        }
        if (!TextUtils.isEmpty(ukey)) {
            paramMap.put("ukey", ukey);
        }
        TreeMap<String, String> params = reSetMap(paramMap);
        return getApiUrl(params);
    }

    /**
     * 获取URL
     * 
     * @param params
     * @param method
     * @return
     */
    public static String getApiUrl(TreeMap<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(DEVELOP_HOST_DOMAIN).append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                sb.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    /**
     * 获取参数map
     * 
     * @param paramMap
     * @return
     */
    protected static TreeMap<String, String> reSetMap(Map<String, String> paramMap) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("appkey", appkey);
        params.put("v", v);
        params.put("format", format);
        params.put("timestamp", CalendarUtil.getNowDateStr(new Date()));
        if (paramMap != null) {
            Set<Map.Entry<String, String>> entrySet = paramMap.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                if (entry.getValue() != null && !entry.getValue().equals("")) {
                    params.put(entry.getKey().toString(), entry.getValue());
                }
            }
        }
        String sign = signMD5(params, secretcode);
        params.put("signmethod", signmethod);
        params.put("sign", sign);
        return params;
    }

    /**
     * 获取签名
     * 
     * @param params
     * @param secretcode
     * @return
     */
    public static String signMD5(TreeMap<String, String> params, String secretcode) {
        StringBuffer sb = new StringBuffer(512);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }   
        sb.deleteCharAt(sb.length() - 1).append(secretcode);
//        System.out.println("MD5-sb:"+ sb.toString());
        byte[] utf8Bytes = sb.toString().getBytes(Charset.forName("UTF-8"));
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(utf8Bytes);
            byte[] digests = digest.digest();
            return HexUtil.bytes2HexStr(digests, false).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            LogUtil.e("CinemaApiUtil", e.getMessage(), e);
            return null;
        }
    }
}
