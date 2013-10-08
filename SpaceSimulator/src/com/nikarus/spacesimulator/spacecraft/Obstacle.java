package com.nikarus.spacesimulator.spacecraft;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;

public class Obstacle {
	final public Body mObstacleBody;

	public static List<Obstacle> mObstacles = new ArrayList<Obstacle>();
	public static BitmapTextureAtlas mBitmapTextureAtlas = null;
	public static TextureRegion mAsteroidTextureRegion;
	public Obstacle(SimpleBaseGameActivity context, Engine engine, PhysicsWorld physicsWorld, Scene scene, float obstacleX, float obstacleY, float rotation)
	{
		mObstacles.add(this);
		if (mBitmapTextureAtlas==null) mBitmapTextureAtlas = new BitmapTextureAtlas(context.getTextureManager(), 256, 256/*, TextureOptions.BILINEAR_PREMULTIPLYALPHA*/);
		if (mAsteroidTextureRegion==null) mAsteroidTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "asteroid5.png", 0, 0);

		engine.getTextureManager().loadTexture(mBitmapTextureAtlas);

		final Sprite asteroidSprite;

		final FixtureDef asteroidFixture = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		asteroidSprite = new Sprite(obstacleX, obstacleY,mAsteroidTextureRegion, context.getVertexBufferObjectManager());

		final float width= asteroidSprite.getWidthScaled() / PIXEL_TO_METER_RATIO_DEFAULT;
		final float height = asteroidSprite.getHeightScaled() / PIXEL_TO_METER_RATIO_DEFAULT;

		final Vector2[] vertices = {
				new Vector2(-0.47206f*width, -0.20466f*height),
				new Vector2(-0.22987f*width, -0.47810f*height),
				new Vector2(+0.09044f*width, -0.46248f*height),
				new Vector2(+0.25451f*width, -0.32185f*height),
				new Vector2(+0.47326f*width, +0.10002f*height),
				new Vector2(+0.23107f*width, +0.43596f*height),
				new Vector2(-0.25274f*width, +0.42923f*height)
		};

		mObstacleBody = PhysicsFactory.createPolygonBody(physicsWorld, asteroidSprite, vertices, BodyType.DynamicBody, asteroidFixture);
		MassData asteroidMassData = new MassData();
		asteroidMassData.mass=150;
		mObstacleBody.setMassData(asteroidMassData);

		scene.attachChild(asteroidSprite);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(asteroidSprite, mObstacleBody, true, true));
		mObstacleBody.setTransform(obstacleX / PIXEL_TO_METER_RATIO_DEFAULT, obstacleY / PIXEL_TO_METER_RATIO_DEFAULT, rotation*(float)Math.PI/180);

		asteroidSprite.setZIndex(-100);
		scene.sortChildren();
	}
}