package net.jppresents.space;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.brashmonkey.spriter.*;

import java.util.ArrayList;
import java.util.List;

public class AnimatedGameObject implements SetPosition, Player.PlayerListener, SetPath, GameObject {
  private int actionPoints = 3;
  private int maxActionPoints = 3;
  private boolean combat;
  private boolean currentMovecostsActinPoints;
  private int damage = 1;
  private boolean faceRight = false;

  public void setFaceRight(boolean faceRight) {
    this.faceRight = faceRight;
  }

  public int getActionPoints() {
    return actionPoints;
  }

  public void resetActionPoints() {
    actionPoints = maxActionPoints;
  }

  public void decActionPoints(int value) {
    actionPoints -= value;
  }

  public void setDamage(int damage) {
    this.damage = damage;
  }

  public float calcDistance(AnimatedGameObject object) {
    return (float) Math.sqrt(Math.pow(getTilePosition().x - object.getTilePosition().x, 2) + Math.pow(getTilePosition().y - object.getTilePosition().y, 2));
  }

  public int getMaxActionPoints() {
    return maxActionPoints;
  }

  public int getDamage() {
    return damage;
  }

  public int getHealth() {
    return health;
  }

  protected enum Movement {NONE, LEFT, RIGHT, UP, DOWN}

  protected Player spriterPlayer;

  private Drawer drawer;
  protected Vector3 worldPosition = new Vector3();
  private Vector3 tilePosition = new Vector3();
  private List<Vector2> path = new ArrayList<Vector2>(20);
  private int pathLength = 0;
  private int currentPathTarget = 0;
  private List<Light> attachedLights = new ArrayList<Light>(1);
  private Movement movement = Movement.NONE;
  private int maxHealth = 10;
  protected int health = 10;
  private int idleTick;

  public int getSecondarySortAttrib() {
    return health;
  }

  public int getMaxHealth() {
    return maxHealth;
  }

  public void setMaxHealth(int maxHealth) {
    this.maxHealth = maxHealth;
  }

  public void setHealth(int health) {
    this.health = Math.min(health, maxHealth);
  }

  public void setMaxActionPoints(int maxActionPoints) {
    this.actionPoints = maxActionPoints;
    this.maxActionPoints = maxActionPoints;
  }

  protected void setIdleIn(int timeInMs, int currentTick) {
    idleTick = (int) ((float) timeInMs / 1000 * 60) + currentTick;
  }

  public boolean isIdle(int tick) {
    return (pathLength == 0 || currentPathTarget == pathLength) && tick > idleTick;
  }

  public void cancelMove(boolean instant) {
    if (instant) {
      currentPathTarget = 0;
      pathLength = 0;
    } else {
      if (currentPathTarget < pathLength - 1) {
        pathLength = currentPathTarget + 1;
      }
    }
    currentMovecostsActinPoints = combat;
  }

  public void hit(int dmg) {
    if (health <= 0)
      return;
    health -= dmg;
    if (health <= 0) {
      health = 0;
      die();
    } else {
      hurt();
    }
  }

  protected void die() {

  }

  protected void hurt() {

  }

  public void attachLight(Light light) {
    attachedLights.add(light);
  }

  public void fadeAllLights(float percent) {
    for (Light light : attachedLights) {
      light.setFade(percent);
    }
  }

  public AnimatedGameObject(Entity entity, Drawer drawer) {
    spriterPlayer = new Player(entity);
    spriterPlayer.addListener(this);
    this.drawer = drawer;
    spriterPlayer.setPosition(100, 100);
    spriterPlayer.setAnimation("front_idle");
    spriterPlayer.setTime(MathUtils.random(800)); //so not all idles are synchronized
  }

  protected void updateAnimation() {
    //override
  }

  private float toWorld(float pos) {
    return pos * SpaceMain.tileSize;
  }

  public void setCurrentMovecostsActinPoints(boolean currentMovecostsActinPoints) {
    this.currentMovecostsActinPoints = currentMovecostsActinPoints;
  }

  public void update() {
    if (!combat) {
      resetActionPoints();
    }

    for (Light light : attachedLights) {
      light.setPosition(worldPosition.x + SpaceMain.tileSize / 2, worldPosition.y);
    }

    Vector2 target = null;
    if (pathLength > 0 && currentPathTarget < pathLength) {
      target = path.get(currentPathTarget);
    }

    if (target != null) {
      if (Math.abs(worldPosition.x - toWorld(target.x)) <= 10 && Math.abs(worldPosition.y - toWorld(target.y)) <= 10) {
        worldPosition.x = toWorld(target.x);
        worldPosition.y = toWorld(target.y);
        currentPathTarget++;

        if (currentMovecostsActinPoints) {
          if (combat) {
            actionPoints--;
          }
        } else {
          currentMovecostsActinPoints = true;
        }
        if (actionPoints <= 0) {
          cancelMove(true);
        }

        if (currentPathTarget == pathLength) {
          this.movement = Movement.NONE;
        }
      } else {
        if (worldPosition.x - toWorld(target.x) < -5) {
          worldPosition.x += 5;
          movement = Movement.RIGHT;
        } else if (worldPosition.x - toWorld(target.x) > 5) {
          worldPosition.x -= 5;
          movement = Movement.LEFT;
        } else if (worldPosition.y - toWorld(target.y) < -5) {
          worldPosition.y += 5;
          movement = Movement.UP;
        } else if (worldPosition.y - toWorld(target.y) > 5) {
          worldPosition.y -= 5;
          movement = Movement.DOWN;
        }
      }
    }
    if (!faceRight && spriterPlayer.flippedX() == -1) {
      spriterPlayer.flipX();
    }

    if (faceRight && spriterPlayer.flippedX() != -1) {
      spriterPlayer.flipX();
    }
    updateAnimation();
    spriterPlayer.setPosition(worldPosition.x + SpaceMain.tileSize / 2, worldPosition.y);
    tilePosition.x = Math.round(worldPosition.x / SpaceMain.tileSize);
    tilePosition.y = Math.round(worldPosition.y / SpaceMain.tileSize);
    spriterPlayer.update();
  }

  public void centerCamera(OrthographicCamera camera) {
    camera.position.x = spriterPlayer.getX();
    camera.position.y = spriterPlayer.getY();
  }


  private Vector2 target = new Vector2();

  private void getCameraTarget(Vector2 target, OrthographicCamera camera) {
    float width = camera.viewportWidth / 2;
    float height = camera.viewportHeight / 2;

    target.set(camera.position.x, camera.position.y);
    if (spriterPlayer.getX() - (SpaceMain.tileSize * 0.5f) < camera.position.x - width) {
      target.x = spriterPlayer.getX() + width - (SpaceMain.tileSize * 0.5f);
    }

    if (spriterPlayer.getX() + (SpaceMain.tileSize * 0.5f) > camera.position.x + width) {
      target.x = spriterPlayer.getX() - width + (SpaceMain.tileSize * 0.5f);
    }

    if (spriterPlayer.getY() < camera.position.y - height) {
      target.y = spriterPlayer.getY() + height;
    }

    if (spriterPlayer.getY() + (SpaceMain.tileSize * 1.6f) > camera.position.y + height) {
      target.y = spriterPlayer.getY() - height + (SpaceMain.tileSize * 1.6f);
    }
  }

  public void restrictCamera(OrthographicCamera camera) {
    getCameraTarget(target, camera);
    camera.position.x = target.x;
    camera.position.y = target.y;
  }

  private final int camSpeed = 5;


  public void moveCamera(OrthographicCamera camera) {
    target.x = spriterPlayer.getX();
    target.y = spriterPlayer.getY();

    if (camera.position.x < target.x) {
      camera.position.x += camSpeed;
    }
    if (camera.position.x > target.x) {
      camera.position.x -= camSpeed;
    }
    if (camera.position.y < target.y) {
      camera.position.y += camSpeed;
    }
    if (camera.position.y > target.y) {
      camera.position.y -= camSpeed;
    }

    if (Math.abs(camera.position.y - target.y) < camSpeed) {
      camera.position.y = target.y;
    }

    if (Math.abs(camera.position.x - target.x) < camSpeed) {
      camera.position.x = target.x;
    }
  }


  public void render(Batch batch) {
    drawer.draw(spriterPlayer);
  }

  public void setPosition(float x, float y) {
    worldPosition.set(x, y, 0);
  }

  public float getY() {
    return worldPosition.y;
  }

  public float getX() {
    return worldPosition.x;
  }

  public Vector3 getTilePosition() {
    return this.tilePosition;
  }

  protected Movement getMovement() {
    return movement;
  }

  @Override
  public void animationFinished(Animation animation) {
  }

  @Override
  public void animationChanged(Animation oldAnim, Animation newAnim) {
  }

  @Override
  public void preProcess(Player player) {
  }

  @Override
  public void postProcess(Player player) {
  }

  @Override
  public void mainlineKeyChanged(Mainline.Key prevKey, Mainline.Key newKey) {
  }

  @Override
  public List<Vector2> getPath() {
    return path;
  }

  @Override
  public void setPathLength(int pathLength) {
    this.pathLength = pathLength;
    currentPathTarget = 0;
  }

  public void setCombat(boolean combat) {
    this.combat = combat;
  }

  public boolean inCombat() {
    return combat;
  }

  @Override
  public void dispose() {
    for (Light light : attachedLights) {
      light.setOn(false);
    }
  }
}
