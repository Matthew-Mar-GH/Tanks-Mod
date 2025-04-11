package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class AmmoSecond extends Ammo {
  public static final int AMMO_SPEED = 12; // 8 faster
  public static final double AMMO_RADIUS = 0.30; // 33% increase
  private static final double AMMO_HEIGHT = 4; // 2.75 to 4
  private static final double AMMO_STROKE_WIDTH = 0.05;
  private static final Color AMMO_COLOR_FILL_0 = new Color(30, 120, 30);
  private static final Color AMMO_COLOR_FILL_1 = new Color(30, 30, 120);
  private static final Color AMMO_COLOR_STROKE = Color.RED;

  private int playerIdx = -1;
  private Vec2 startPos = null;
  private double maxRange = -1.0;
  private Vec2 vel = null;
  private double radius = -1.0;

  public AmmoSecond(int playerIdx, Vec2 pos, Vec2 dir, double maxRange) {
    super(playerIdx, pos, dir, maxRange);

    this.playerIdx = playerIdx;
    this.startPos = pos;
    this.maxRange = maxRange;
    this.vel = Vec2.multiply(dir.unit(), AMMO_SPEED);
    this.radius = AMMO_RADIUS;
  }

  protected void draw(Graphics2D g) {
    // Setup...
    double scale = calcDrawScale();
    double height = calcDrawHeight(AMMO_HEIGHT, scale);
    Color ammoColor = (this.playerIdx == 0) ? AMMO_COLOR_FILL_0 : AMMO_COLOR_FILL_1;
    Color colorFill = Util.colorLerp(World.COLOR_BACKGROUND, ammoColor, timeSinceBorn * 5.0f);
    Color colorStroke = Util.colorLerp(World.COLOR_BACKGROUND, AMMO_COLOR_STROKE, timeSinceBorn * 5.0f);

    // Body...
    Draw.drawRect(g, this.pos, height, new Vec2(AMMO_RADIUS, AMMO_RADIUS), scale, colorFill, colorStroke,
        AMMO_STROKE_WIDTH, AMMO_RADIUS * 2);
  }

  protected void drawShadow(Graphics2D g) {
    // Setup...
    double scale = calcDrawScale();
    Color colorShadow = Util.colorLerp(World.COLOR_BACKGROUND, World.COLOR_SHADOW, timeSinceBorn * 2.0f);

    // Body...
    Draw.drawRectShadow(g, this.pos, calcDrawHeight(AMMO_HEIGHT, scale), new Vec2(AMMO_RADIUS, AMMO_RADIUS), scale,
        colorShadow, AMMO_RADIUS);
  }

  protected void update(double deltaTime) {
    super.update(deltaTime);

    // If dying, continue...
    if (this.timeTillDeath > 0) {
      // Update it...
      this.timeTillDeath = Math.min(this.timeTillDeath + deltaTime * 10.0, 1.0);

      // Scale it down...
      this.radius = AMMO_RADIUS * Math.max(1.0 - this.timeTillDeath, 0.001);
    } else {
      // Update position...
      Vec2 prevPos = this.pos;
      this.pos = Vec2.add(this.pos, Vec2.multiply(this.vel, deltaTime));
      Vec2 motionVec = Vec2.subtract(this.pos, prevPos);

      // Check for hitting things (do swept collision to avoid missing it)...
      ArrayList<GameObject> gameObjects = Simulation.getGameObjects();
      for (int i = 0; i < gameObjects.size(); ++i) {
        GameObject gameObject = gameObjects.get(i);

        // Targets...
        if (gameObject instanceof Target) {
          Target target = (Target) gameObject;
          if (Util.intersectCircleCapsule(target.pos, target.getRadius(), prevPos, motionVec, this.radius)) {
            // Tell the target about it...
            target.onHitByAmmo(this.playerIdx);

            // That's it for us...
            this.timeTillDeath = Math.max(this.timeTillDeath, 0.0001);
          }
        }

        // Tanks...
        if (gameObject instanceof Tank) {
          Tank tank = (Tank) gameObject;
          if ((this.playerIdx != tank.getPlayerIdx()) && // Don't let someone hit themselves
              (Util.intersectCircleCapsule(tank.pos, Tank.BODY_HALFSIZE.x, prevPos, motionVec, this.radius))) {
            // Award points...
            Game.get().awardPoints(Game.POINTS_HIT_OTHER_TWO, this.playerIdx);
            Game.get().removePoints(Game.POINTS_HIT_OTHER_TWO, tank.getPlayerIdx());
            Util.log("(hit tank (alt): score +" + Game.POINTS_HIT_OTHER_TWO + ")");
            Util.log("(hit tank (alt): other score -" + Game.POINTS_CMD_SHOT_TWO + ")");

            // Kick the tank back (if you can)...
            tank.kickBackTank(Vec2.subtract(tank.pos, this.pos).unit());

            // That's it for us...
            this.timeTillDeath = Math.max(this.timeTillDeath, 0.0001);
          }
        }
      }

      // Check max range...
      if ((Vec2.subtract(this.pos, this.startPos)).lengthSqr() >= (this.maxRange * this.maxRange)) {
        // That's it for us...
        this.timeTillDeath = Math.max(this.timeTillDeath, 0.0001);
      }
    }

    // Check max range...
    if ((Vec2.subtract(this.pos, this.startPos)).lengthSqr() >= (this.maxRange * this.maxRange))

    {
      // That's it for us...
      this.timeTillDeath = Math.max(this.timeTillDeath, 0.0001);
    }
  }
}