package virium;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public interface ActorController {

	public void init(Actor actor);
	
	public void update(GameContext context, Actor actor, int delta);
}
