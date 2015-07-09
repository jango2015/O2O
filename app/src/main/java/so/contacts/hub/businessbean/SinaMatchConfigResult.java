/**
 * 
 */
package so.contacts.hub.businessbean;

import java.util.List;

/**
 * @author Acher
 *
 */
    
public class SinaMatchConfigResult extends MatchConfigResult {
    public SinaMatchConfig match_config;//[SinaMatchConfig][not null][该字段继承自MatchConfigResult中的match_config，通过泛型定义为BaseMatchConfig的子类SinaMatchConfig]
    public List<String> access_token_list;//[List<String>][not null][用于匹配的access_token列表]

    public SinaMatchConfigResult() {
        
    }
    
}
