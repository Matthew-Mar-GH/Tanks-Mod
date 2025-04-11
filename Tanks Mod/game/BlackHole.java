package game;

import java.util.*;
import java.awt.*;

public class BlackHole extends GameObject {
    public double x;
    public double y;
    public int durationOfHole = 120;

    public BlackHole(double xCoord, double yCoord) {
        x = xCoord;
        y = yCoord;
    }

    public void update(ArrayList<GameObject> objects) {
        for (GameObject obj : objects) {
            if (obj instanceof Tank) {
                Tank t = (Tank) obj;
            
                Vec2 pos = t.getPos();
                Vec2 vel = t.getVel(); 
            
                double dx = x - pos.x;
                double dy = y - pos.y;
                double dist = Math.sqrt(dx * dx + dy * dy);
            
                if (dist < 20 && dist > 0.5) {
                    double pull = 0.3 * (20 - dist); 
            
                    vel.x += pull * dx / dist;
                    vel.y += pull * dy / dist;
                }
            }
        }
    
        durationOfHole--;
    }

    public boolean isExpired() {return durationOfHole <= 0;}

    public void draw(Graphics2D g) {
        Vec2 pixelPos = Util.toPixels(new Vec2(x, y));
        int radiusPixels = Util.toPixelsLengthInt(1.5);
    
        g.setColor(new Color(0, 0, 0));
        g.fillOval((int)(pixelPos.x - radiusPixels / 2),
                   (int)(pixelPos.y - radiusPixels / 2),
                   radiusPixels, radiusPixels);

                   
    }
}
