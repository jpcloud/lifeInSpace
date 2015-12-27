package net.jppresents.lifeInSpace;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;

import java.util.List;

public class World implements Disposable {

  private OrthogonalTiledMapRenderer mapRenderer;
  private TiledMap map;
  private TmxMapLoader mapLoader;
  private TiledMapTileLayer mainLayer;
  private MapLayer objLayer;


  public World() {
    mapLoader = new TmxMapLoader();
    load("world");
  }

  private void load(String mapName) {
    map = mapLoader.load("world/" + mapName + ".tmx");
    mainLayer = (TiledMapTileLayer)(map.getLayers().get("world"));
    objLayer = map.getLayers().get("objects");
    mapRenderer = new OrthogonalTiledMapRenderer(map, 1);
  }

  public void resetPosition(SetPosition guy, String name) {
    for (MapObject object: objLayer.getObjects()) {
      if (object.getName().equals(name)) {
        guy.setPosition( ((RectangleMapObject)object).getRectangle().getX(), ((RectangleMapObject)object).getRectangle().getY());
      }
    }
  }

  public int getCount(String name, String typ) {
    int i = 0;
    for (MapObject object: objLayer.getObjects()) {
      if (object.getName().equals(name) && object.getProperties().get("type").equals(typ)) {
        i++;
      }
    }
    return i;
  }

  public void resetPositions(List guys, String name, String typ) {
    int i = 0;
    for (MapObject object: objLayer.getObjects()) {
      if (object.getName().equals(name) && object.getProperties().get("type").equals(typ)) {
        SetPosition guy = (SetPosition)guys.get(i);
        guy.setPosition( ((RectangleMapObject)object).getRectangle().getX(), ((RectangleMapObject)object).getRectangle().getY());
        i++;
      }
    }
  }


  public boolean isBlocking(float x, float y) {
    TiledMapTileLayer.Cell cell = mainLayer.getCell((int) Math.floor(x / mainLayer.getTileWidth()), (int) Math.floor(y / mainLayer.getTileHeight()));
    return cell == null || cell.getTile().getProperties().containsKey("b");
  }

  @Override
  public void dispose() {
    map.dispose();
    mapRenderer.dispose();
  }

  public void render(OrthographicCamera camera) {
    mapRenderer.setView(camera);
    mapRenderer.render();
  }

  public void restrictCamera(OrthographicCamera camera) {
    if (camera.position.x < camera.viewportWidth/2) {
      camera.position.x = camera.viewportWidth/2;
    }
    if (camera.position.y < camera.viewportHeight/2) {
      camera.position.y = camera.viewportHeight/2;
    }
    if (camera.position.x > mainLayer.getWidth() * mainLayer.getTileWidth() - camera.viewportWidth/2) {
      camera.position.x = mainLayer.getWidth() * mainLayer.getTileWidth() - camera.viewportWidth/2;
    }
    if (camera.position.y > mainLayer.getHeight() * mainLayer.getTileHeight() - camera.viewportHeight/2) {
      camera.position.y = mainLayer.getHeight() * mainLayer.getTileHeight() - camera.viewportHeight/2;
    }
  }
}
