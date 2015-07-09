package so.putao.findplug;

import java.util.Comparator;

public class ListComparator implements Comparator<YelloPageItem> {

	@Override
	public int compare(YelloPageItem lhs, YelloPageItem rhs) {
		if (lhs.getDistance() > rhs.getDistance()) {
			return 1;
		} else if (lhs.getDistance() < rhs.getDistance()) {
			return -1;
		}
		return 0;
	}

}
