package net.jppresents.lifeInSpace;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;

public class UserInterface implements Disposable{
  private final TextureAtlas sprites;
  private final TextureAtlas.AtlasRegion marker;
  private final TextureAtlas.AtlasRegion markerError;
  private final TextureAtlas.AtlasRegion selector;
  private final TextureAtlas.AtlasRegion markerTarget;

  private boolean error = false;
  private boolean target = false;


  private int selectorX, selectorY;

  public UserInterface() {
    sprites = new TextureAtlas("sprites.atlas");
    marker = sprites.findRegion("marker");
    markerError = sprites.findRegion("markerError");
    markerTarget = sprites.findRegion("markerTarget");
    selector = sprites.findRegion("selector");
    selectorX = -1;
    selectorY = -1;
  }

  @Override
  public void dispose() {
    sprites.dispose();
  }

  public void render(SpriteBatch batch) {
    if (selectorX != -1 && selectorY != -1) {

      if (!target) {
        batch.setColor(0.5f, 0.8f, 0.9f, 1);
        batch.draw(selector, selectorX - selector.getRegionWidth() / 2, selectorY - selector.getRegionHeight() / 2);
      }

      if (error) {
        batch.setColor(1, 0, 0, 1);
        batch.draw(markerError, selectorX - markerError.getRegionWidth() / 2, selectorY - markerError.getRegionHeight() / 2);
      }

      if (target) {
        batch.setColor(1, 0, 0, 1);
        batch.draw(markerTarget, selectorX - markerTarget.getRegionWidth() / 2, selectorY - markerTarget.getRegionHeight() / 2);
      }

    }
  }

  public void setSelectorPos(int x, int y) {
    selectorX = x;
    selectorY = y;
  }

  public void hideSelector() {
    selectorX = -1;
  }

  public void setError(boolean error) {
    this.error = error;
  }

  public void setTarget(boolean target) {
    this.target = target;
  }
}