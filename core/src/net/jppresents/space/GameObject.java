package net.jppresents.space;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface GameObject {
  void render(Batch batch);
  void update();
  float getX();
  float getY();
  void dispose();
  int getSecondarySortAttrib();
}
