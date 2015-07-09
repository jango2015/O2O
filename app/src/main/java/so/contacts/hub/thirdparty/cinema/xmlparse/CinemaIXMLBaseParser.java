package so.contacts.hub.thirdparty.cinema.xmlparse;

import java.io.InputStream;

public interface CinemaIXMLBaseParser {
	
	/** 
     * 解析输入流 得到Book对象集合 
     * @param is 
     * @return 
     * @throws Exception 
     */  
    public Object parse(InputStream is) throws Exception;  
      
}
