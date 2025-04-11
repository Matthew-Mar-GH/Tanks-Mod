package ai;

import game.PowerUp;
import game.TankAIBase;
import game.Target;
import game.Vec2;

public class TankAI extends TankAIBase {
    public String getPlayerName() {
        return "Modded AI";
    }

    public int getPlayerPeriod() {
        return 3;
    }

    public boolean updateAI() {
        boolean isTarget = isClosestTarget();
        Vec2 closestTarget = getClosestTarget();
        Vec2 closestPowerUp = getClosestPowerUp();
        Vec2 typePowerUp = getTypePowerUp("R", "S");
        Vec2 otherTankLocation = getOther().getPos().minus(getTankPos());
        double distanceToTarget = getTankPos().distance(closestTarget);
        Vec2 otherMoveSpeed = getOther().getVel();
        if (getLevelTimeRemaining() >= 30) {
            if (hostile(3)) {
                shoot(otherTankLocation.x, otherTankLocation.y);
            }
            if (distanceToTarget <= 0.1) {
                moveToTarget(new Vec2(closestTarget.x + 1, closestTarget.y));
            }
            if (isTarget) {
                shoot(closestTarget.x, closestTarget.y);
            } else if (getTankMoveSpeed() <= 2.7 || getTankShotRange() <= 6.5) {
                if (typePowerUp != null) {
                    moveToTarget(typePowerUp);
                } else {
                    moveToTarget(closestPowerUp);
                }
            } else {
                moveToTarget(closestPowerUp);
            }
        } else if (getLevelTimeRemaining() < 30) {
            if (hostile(5) && (otherMoveSpeed.x <= 1.0 || otherMoveSpeed.y <= 1.0)) {
                shoot(otherTankLocation.x, otherTankLocation.y);
            }
            if (isTarget) {
                shoot(closestTarget.x, closestTarget.y);
            } else {
                moveToTarget(closestPowerUp);
            }
        }
        return true;
    }

    public void moveToTarget(Vec2 target) {
        double distX = target.x;
        double distY = target.y;
        double moveThreshold = 0.1;
        if (Math.abs(distX) > Math.abs(distY)) {
            if (Math.abs(distX) > moveThreshold) {
                queueCmd("move", new Vec2(distX, 0));
            }
        } else {
            if (Math.abs(distY) > moveThreshold) {
                queueCmd("move", new Vec2(0, distY));
            }
        }
    }

    public void shoot(double x, double y) {
        queueCmd("shoot2", new Vec2(x, y));
    }

    public void turn(double x, double y) {
        queueCmd("turn", new Vec2(x, y));
    }

    public Vec2 getClosestPowerUp() {
        Vec2 shortestVec = new Vec2(0, 0);

        PowerUp[] powerups = getPowerUps();
        double shortest = 1000;
        int shortestIndex = -1;

        for (int i = 0; i < powerups.length; i++) {
            double distance = powerups[i].getPos().distance(getTankPos());
            if (distance < shortest) {
                shortest = distance;
                shortestIndex = i;
                shortestVec = powerups[shortestIndex].getPos().minus(getTankPos());
            }
        }
        return shortestVec;
    }

    public Vec2 getTypePowerUp(String type, String secondChoice) {
        PowerUp[] powerUps = getPowerUps();
        Vec2 selectedPowerUp = null;
        for (int i = 0; i < powerUps.length; i++) {
            if (powerUps[i].getType().equals(type)) {
                selectedPowerUp = powerUps[i].getPos().minus(getTankPos());
                break;
            }
        }
        if (selectedPowerUp == null) {
            for (int i = 0; i < powerUps.length; i++) {
                if (powerUps[i].getType().equals(secondChoice)) {
                    selectedPowerUp = powerUps[i].getPos().minus(getTankPos());
                    break;
                }
            }
        }
        if (selectedPowerUp == null && powerUps.length > 0) {
            return null;
        }
        return selectedPowerUp;
    }

    public boolean isClosestTarget() {
        boolean isTarget = false;

        Target[] targets = getTargets();

        for (int i = 0; i < targets.length; i++) {
            double distance = targets[i].getPos().distance(getTankPos());
            if (distance <= getTankShotRange()) {
                isTarget = true;
            }

        }
        return isTarget;
    }

    public Vec2 getClosestTarget() {
        Vec2 shortestVec = new Vec2(0, 0);

        Target[] targets = getTargets();
        double shortest = 1000;
        int shortestIndex = -1;

        for (int i = 0; i < targets.length; i++) {
            if (getTankShotRange() >= targets[i].getPos().distance(getTankPos())) {
                double distance = targets[i].getPos().distance(getTankPos());
                if (distance < shortest) {
                    shortest = distance;
                    shortestIndex = i;
                    shortestVec = targets[shortestIndex].getPos().minus(getTankPos());
                }
            }
        }
        return shortestVec;
    }

    public boolean hostile(double aggroRange) {
        if (getTankPos().distance(getOther().getPos()) <= aggroRange) {
            return true;
        }
        return false;
    }
}
