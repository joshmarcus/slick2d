package rakatan;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.opengl.Texture;

/**
 * @author Mark Bernard
 *
 * Use this class to render shpaes directly to OpenGL.  Allows you to bypass the Graphics class.
 */
public final class ShapeRenderer {
    public static final void fill(Shape shape, Image image, float scale) {
        float points[] = shape.getPoints();
        
        Texture store = Texture.getLastBind();
        image.getTexture().bind();
        
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        float center[] = shape.getCenter();
        GL11.glTexCoord2f(center[0] * scale, center[1] * scale);
        GL11.glVertex2f(center[0], center[1]);

        for(int i=0;i<points.length;i+=2) {
            GL11.glTexCoord2f(points[i] * scale, points[i + 1] * scale);
            GL11.glVertex2f(points[i], points[i + 1]);
        }
        GL11.glTexCoord2f(points[0] * scale, points[1] * scale);
        GL11.glVertex2f(points[0], points[1]);
        GL11.glEnd();
        
        if (store == null) {
        	Texture.bindNone();
        } else {
        	store.bind();
        }
    }

}
