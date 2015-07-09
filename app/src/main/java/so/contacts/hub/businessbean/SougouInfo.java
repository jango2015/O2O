
package so.contacts.hub.businessbean;

import android.graphics.Bitmap;

/**
 * 搜狗号码标记的返回结果
 * @author hyl 2014-5-14
 */
public class SougouInfo {

    /** 信息类型
        LOCAL_MARK = 1; 本地号码库信息
        NET_ MARK = 2;  云端信息
        USER_MARK = 3;  用户标记信息
    **/
    private int type;
    /**电话号码**/
    private String number;
    /**信息内容**/
    private String markContent;
    /**信息内容的来源**/
    private String markSource;
    /**举报该信息的人数**/
    private int markNumber;
    /**号码logo**/
    private Bitmap iconBitmap;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMarkContent() {
        return markContent;
    }

    public void setMarkContent(String markContent) {
        this.markContent = markContent;
    }

    public String getMarkSource() {
        return markSource;
    }

    public void setMarkSource(String markSource) {
        this.markSource = markSource;
    }

    public int getMarkNumber() {
        return markNumber;
    }

    public void setMarkNumber(int markNumber) {
        this.markNumber = markNumber;
    }

    public Bitmap getIconBitmap() {
        return iconBitmap;
    }

    public void setIconBitmap(Bitmap iconBitmap) {
        this.iconBitmap = iconBitmap;
    }

}
