package net.jppresents.lifeInSpace;

import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Drawer;
import com.brashmonkey.spriter.Entity;

public class Enemy extends AnimatedGameObject {

  private Light light;
  private boolean aggro = false;
  private float aggroRange = 6;
  private float deAggroRange = 8;

  public Enemy(Entity entity, Drawer drawer, int tileSize) {
    super(entity, drawer, tileSize);
    light = new Light(0, 0, 0, 40, 300, LifeInSpaceMain.lights);
    light.setColor(0.2f, 0.5f, 0.5f, 1);
    attachLight(light);
  }

  @Override
  protected void hurt() {
    LifeInSpaceMain.assets.playSound(Assets.SoundEffect.ALIEN_HURT);
    spriterPlayer.setAnimation("front_hurt");
  }

  @Override
  protected void die() {
    LifeInSpaceMain.assets.playSound(Assets.SoundEffect.ALIEN_DIE);
    spriterPlayer.setAnimation("front_die");
    fadeAllLights(0.1f);
  }

  @Override
  public void animationFinished(Animation animation) {
    if (animation.name.equals("front_hurt")) {
      spriterPlayer.setAnimation("front_idle");
    }

    if (animation.name.equals("front_die")) {
      spriterPlayer.speed = 0;
      spriterPlayer.setTime(animation.length - 1);
    }
  }

  public void planTurn(World world, Guy guy) {
    if (getHealth() <= 0)
      return;

    if (isAggro()) {
      world.calcPath(this, this.getTilePosition(), guy.getTilePosition());
    }
  }

  public boolean updateAggro(AnimatedGameObject obj) {
    if (aggro) {
      if (obj.calcDistance(this) >= deAggroRange) {
        aggro = false;
        light.setColor(0.2f, 0.5f, 0.5f, 1);
      }
    } else {
      if (obj.calcDistance(this) <= aggroRange) {
        aggro = true;
        light.setColor(0.5f, 0.2f, 0.2f, 1);
      }
    }
    return aggro;
  }

  public boolean isAggro() {
    return aggro;
  }

}