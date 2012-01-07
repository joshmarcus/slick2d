package rakatan.data;

import net.phys2d.raw.Body;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public interface LevelListener {

	public void significantCollision(Body a, Body b);
}
