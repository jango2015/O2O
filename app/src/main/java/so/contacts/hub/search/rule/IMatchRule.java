package so.contacts.hub.search.rule;

import so.putao.findplug.YelloPageItem;

public interface IMatchRule {
	
	public double match(String word, String matchWord);
	
}
