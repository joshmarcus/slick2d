package virium;

import java.util.ArrayList;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class Bag extends ArrayList {

	public boolean remove(Object object) {
		int i = indexOf(object);
		if (i >= 0) {
			set(i, get(size()-1));
			remove(size()-1);
			return true;
		} else {
			return false;
		}
	}
}
