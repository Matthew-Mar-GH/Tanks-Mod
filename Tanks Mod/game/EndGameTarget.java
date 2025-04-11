package game;

import java.awt.Color;
import java.awt.Graphics2D;

public class EndGameTarget extends Target {

    public final double TARGET_RADIUS = 0.4;
    private static final double TARGET_HEIGHT = 0.5;
    private static final double TARGET_STROKE_WIDTH = 0.075;
    private static final Color TARGET_COLOR_FILL_BLUE = Color.BLUE;
    private static final Color TARGET_COLOR_FILL_WHITE = Color.WHITE;
    private static final Color TARGET_COLOR_STROKE = Color.BLACK;

    protected EndGameTarget(Vec2 pos) {
        super(pos);
    }

    protected void onHitByAmmo(int playerIdx) {
        this.timeTillDeath = Math.max(this.timeTillDeath, 0.0001);
        double timeLeft = Game.get().getLevelTimeRemaining();
        int points = Game.POINTS_HIT_TARGET;
        if (timeLeft <= 50.0) {
            points *= 2;
        } else {
            points *= 1;
        }
        Game.get().awardPoints(points, playerIdx);
        Util.log("(hit target: score +" + points + ")");
    }

    protected void draw(Graphics2D g) {
        // Setup...
        double height = calcDrawHeight(TARGET_HEIGHT, calcDrawScale());

        Color colorFillBlue = Util.colorLerp(World.COLOR_BACKGROUND, TARGET_COLOR_FILL_BLUE, timeSinceBorn * 2.0f);
        Color colorFillWhite = Util.colorLerp(World.COLOR_BACKGROUND, TARGET_COLOR_FILL_WHITE, timeSinceBorn * 2.0f);
        Color colorStroke = Util.colorLerp(World.COLOR_BACKGROUND, TARGET_COLOR_STROKE, timeSinceBorn * 2.0f);

        // Body...
        Draw.drawRect(g, this.pos, height, new Vec2(TARGET_RADIUS, TARGET_RADIUS), calcDrawScale(0), colorFillBlue,
                colorStroke, TARGET_STROKE_WIDTH, TARGET_RADIUS * 2);
        Draw.drawRect(g, this.pos, height, (new Vec2(TARGET_RADIUS, TARGET_RADIUS)).multiply(0.66), calcDrawScale(1),
                colorFillWhite, colorStroke, 0, TARGET_RADIUS * 2);
        Draw.drawRect(g, this.pos, height, (new Vec2(TARGET_RADIUS, TARGET_RADIUS)).multiply(0.33), calcDrawScale(2),
                colorFillBlue, colorStroke, 0, TARGET_RADIUS * 2);

    }
}
