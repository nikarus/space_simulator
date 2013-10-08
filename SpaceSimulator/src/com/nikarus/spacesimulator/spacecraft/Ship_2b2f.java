package com.nikarus.spacesimulator.spacecraft;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.nikarus.spacesimulator.Touch;
import com.nikarus.spacesimulator.shipparts.ShipEngine;

public class Ship_2b2f extends Ship { //SHIP: 2 BACK ENGINES; 2 FRONT ENGINES
	private static BitmapTextureAtlas mBitmapTextureAtlas = new BitmapTextureAtlas(sContext.getTextureManager(), 256, 64/*, TextureOptions.BILINEAR_PREMULTIPLYALPHA*/);
	public Ship_2b2f(SimpleBaseGameActivity context, Engine engine, PhysicsWorld physicsWorld, Scene scene, boolean isHumanControlled, boolean isCameraTraced, float shipX, float shipY, float rotation, int frontLeftEngineForce, int frontRightEngineForce, int backLeftEngineForce, int backRightEngineForce, float frontLeftEngineStrength, float frontRightEngineStrength, float backLeftEngineStrength, float backRightEngineStrength)
	{
		loadCommonData(mBitmapTextureAtlas, context, engine, scene, physicsWorld, this, isHumanControlled, isCameraTraced);
		/*if (cShip.mEngineFireTextureRegion == null)*/ Ship.sEngineFireTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, context, "EngineFireCropped160.png", 0, 0, 5, 1);
		/*if (cShip.mBoxPartTextureRegion == null)*/ Ship.sBoxPartTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "boxPart.png", 160, 0);
		/*if (cShip.mCirclePartTextureRegion == null)*/ Ship.sCirclePartTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "circlePart.png", 192, 0);
		/*if (cShip.mTrianglePartTextureRegion == null)*/ Ship.sTrianglePartTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "trianglePart.png", 224, 0);
		//EACH SHIP SHOULD HAVE HIS OWN mBitmapTextureAtlas!!! FIX IT!
		engine.getTextureManager().loadTexture(mBitmapTextureAtlas);
		
		this.mShipFixtureDef = PhysicsFactory.createFixtureDef(1f, 0.1f, 0.2f);
		
		this.mShipBodySprite = new Sprite(shipX, shipY, Ship.sCirclePartTextureRegion, context.getVertexBufferObjectManager());
		this.mShipBody = PhysicsFactory.createCircleBody(physicsWorld, this.mShipBodySprite, BodyType.DynamicBody, this.mShipFixtureDef);
		
		float width=Ship.sBoxPartTextureRegion.getWidth() / PIXEL_TO_METER_RATIO_DEFAULT;
		float height=Ship.sBoxPartTextureRegion.getHeight() / PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] boxPartVertices = {
				new Vector2(-0.50035f*width, -0.49992f*height),
				new Vector2(+0.49806f*width, -0.49992f*height),
				new Vector2(+0.49806f*width, +0.49536f*height),
				new Vector2(-0.50035f*width, +0.49536f*height)
		};
		width=Ship.sTrianglePartTextureRegion.getWidth() / PIXEL_TO_METER_RATIO_DEFAULT;
		height=Ship.sTrianglePartTextureRegion.getHeight() / PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] trianglePartVertices = {
				new Vector2(-0.49997f*width, -0.50155f*height),
				new Vector2(+0.49467f*width, -0.50155f*height),
				new Vector2(-0.00000f*width, +0.49762f*height)
		};
		
		ShipEngine ShipEngine [] = {
				new ShipEngine(context, this, -20, -40, (float)Math.PI, frontLeftEngineForce, frontLeftEngineStrength, this.mShipBody, this.mShipBody, this.mShipBodySprite, trianglePartVertices, scene, physicsWorld, sEngineFireTextureRegion, 0, 0, 1, Ship.sTrianglePartTextureRegion, this.mShipFixtureDef, true),
				new ShipEngine(context, this, 20,  -40, (float)Math.PI, frontRightEngineForce, frontRightEngineStrength, this.mShipBody, this.mShipBody, this.mShipBodySprite, trianglePartVertices, scene, physicsWorld, sEngineFireTextureRegion, 0, 0, 1, Ship.sTrianglePartTextureRegion, this.mShipFixtureDef, true),
				new ShipEngine(context, this, -20,  40, 0.0f,           backLeftEngineForce, backLeftEngineStrength, this.mShipBody, this.mShipBody, this.mShipBodySprite, boxPartVertices,      scene, physicsWorld, sEngineFireTextureRegion, 0, 0, 1, Ship.sBoxPartTextureRegion,  this.mShipFixtureDef, true),
				new ShipEngine(context, this, 20,   40, 0.0f,           backRightEngineForce, backRightEngineStrength, this.mShipBody, this.mShipBody, this.mShipBodySprite, boxPartVertices,      scene, physicsWorld, sEngineFireTextureRegion, 0, 0, 1, Ship.sBoxPartTextureRegion, this.mShipFixtureDef, true)
				};
		this.mShipEngine=ShipEngine;
		scene.attachChild(this.mShipBodySprite);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this.mShipBodySprite, this.mShipBody, true, true));
		this.mShipBody.setTransform(shipX / PIXEL_TO_METER_RATIO_DEFAULT, shipY / PIXEL_TO_METER_RATIO_DEFAULT, rotation*(float)Math.PI/180);
	}
	
	public void control(Touch [] touches) {
		if (!mIsHumanControlled) return;
		
		if (isAreaTouched(0,240,240,480,touches)) {//LEFT-BOTTOM
			mShipEngine[2].engineStart();
		} else {
			mShipEngine[2].engineStop();
		}
		if (isAreaTouched(0,0,240,240,touches)) {//LEFT-TOP
			mShipEngine[0].engineStart();
		} else {
			mShipEngine[0].engineStop();
		}
		if (isAreaTouched(560,240,800,480,touches)) {//RIGHT-BOTTOM
			mShipEngine[3].engineStart();
		} else {
			mShipEngine[3].engineStop();
		}
		if (isAreaTouched(560,0,800,240,touches)) {//RIGHT-TOP
			mShipEngine[1].engineStart();
		}
		else {
			mShipEngine[1].engineStop();
		}
	}
}