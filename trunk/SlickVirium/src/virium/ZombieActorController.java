package virium;

/**
 * TODO: Document this class
 *
 * @author kevin
 */
public class ZombieActorController implements ActorController {

	/**
	 * @see virium.ActorController#update(virium.GameContext, virium.Actor, int)
	 */
	public void update(GameContext context, Actor actor, int delta) {
		Actor target = null;
		
		target = context.getPlayer1();
		if (context.getPlayer2() != null) {
			if (target == null) {
				target = context.getPlayer2();
			} else {
				float disx1 = actor.getX() - context.getPlayer1().getX();
				float disy1 = actor.getY() - context.getPlayer1().getY();
				float disx2 = actor.getX() - context.getPlayer2().getX();
				float disy2 = actor.getY() - context.getPlayer2().getY();
				
				if ((disx1*disx1)+(disy1*disy1) < (disx2*disx2)+(disy2*disy2)) {
					target = context.getPlayer1();
				} else {
					target = context.getPlayer2();
				}
			}
		}
		
		if (target != null) {
			float dx = target.getX() - actor.getX();
			float dy = target.getY() - actor.getY();
			
			int tolerance = 3;
			
			if (dx < -tolerance) {
				dx = -1;
			}
			if (dx > tolerance) {
				dx = 1;
			}
			if (dy < -tolerance) {
				dy = -1;
			}
			if (dy > tolerance) {
				dy = 1;
			}
			
			actor.applyDirection((int) dx, (int) dy);
		}
	}
	/**
	 * @see virium.ActorController#init(virium.Actor)
	 */
	public void init(Actor actor) {
		actor.setSpeed(0.05f);
	}

}
