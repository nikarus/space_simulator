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
import com.nikarus.spacesimulator.shipparts.Weapon;

public class Ship_1b1f2l2r extends Ship { //SHIP: 1 BACK ENGINE; 1 FRONT ENGINE; 2 LEFT ENGINES; 2 RIGHT ENGINES
	private static BitmapTextureAtlas mBitmapTextureAtlas = null;
	public static Sprite mMissileSprite;
	public Ship_1b1f2l2r(SimpleBaseGameActivity context, Engine engine, PhysicsWorld physicsWorld, Scene scene, boolean isHumanControlled, boolean isCameraTraced, float shipX, float shipY, float rotation, int frontLeftEngineForce, int frontRightEngineForce, int backLeftEngineForce, int backRightEngineForce, int frontEngineForce, int backEngineForce, float frontLeftEngineStrength, float frontRightEngineStrength, float backLeftEngineStrength, float backRightEngineStrength, float frontEngineStrength, float backEngineStrength)
	{
		if (mBitmapTextureAtlas==null) mBitmapTextureAtlas = new BitmapTextureAtlas(context.getTextureManager(), 512, 128/*, TextureOptions.BILINEAR_PREMULTIPLYALPHA*/);
		loadCommonData(mBitmapTextureAtlas, context, engine, scene, physicsWorld, this, isHumanControlled, isCameraTraced);
		//if (!texturesAlreadyLoaded)
		//{
		
		if (Ship.sEngineFireTextureRegion == null) Ship.sEngineFireTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, context, "EngineFireCropped160.png", 110, 2, 5, 1);
		if (Ship.sShip2ShipBodyTextureRegion == null) Ship.sShip2ShipBodyTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "Ship2/Ship2Body.png", 2, 2);
		if (Ship.sShip2FrontLeftEngineTextureRegion == null) Ship.sShip2FrontLeftEngineTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "Ship2/Ship2FrontLeftEngine.png", 19, 104);
		if (Ship.sShip2FrontRightEngineTextureRegion == null) Ship.sShip2FrontRightEngineTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "Ship2/Ship2FrontRightEngine.png", 129, 76);
		if (Ship.sShip2BackLeftEngineTextureRegion == null) Ship.sShip2BackLeftEngineTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "Ship2/Ship2BackLeftEngine.png", 129, 44);
		if (Ship.sShip2BackRightEngineTextureRegion == null) Ship.sShip2BackRightEngineTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "Ship2/Ship2BackRightEngine.png", 164, 44);
		if (Ship.sShip2FrontEngineTextureRegion == null) Ship.sShip2FrontEngineTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "Ship2/Ship2FrontEngine.png", 41, 104);
		if (Ship.sShip2BackEngineTextureRegion == null) Ship.sShip2BackEngineTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "Ship2/Ship2BackEngine.png", 61, 104);
		if (sRocketTextureRegion == null) sRocketTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "Missile.png", 99, 2);
		if (Ship.sBulletTextureRegion == null) Ship.sBulletTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "Bullet.png", 110, 44);
		if (Ship.sShip2ChainGunTextureRegion == null) Ship.sShip2ChainGunTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "Ship2/Ship2ChainGun.png", 110, 81);
		if (Ship.sShip2RocketLauncherTextureRegion == null) Ship.sShip2RocketLauncherTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "Ship2/Ship2RocketLauncher.png", 2, 104);
		//}
		engine.getTextureManager().loadTexture(mBitmapTextureAtlas);
		this.mShipFixtureDef = PhysicsFactory.createFixtureDef(1f, 0.1f, 0.2f);
		float width=Ship.sShip2ShipBodyTextureRegion.getWidth() / PIXEL_TO_METER_RATIO_DEFAULT;
		float height=Ship.sShip2ShipBodyTextureRegion.getHeight() / PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] Ship2BodyVertices = {
				new Vector2(-0.46061f*width, -0.46694f*height),
				new Vector2(+0.42876f*width, -0.46116f*height),
				new Vector2(+0.39221f*width, +0.42426f*height),
				new Vector2(-0.39361f*width, +0.41269f*height)
		};
		this.mShipBodySprite = new Sprite(shipX, shipY, Ship.sShip2ShipBodyTextureRegion, context.getVertexBufferObjectManager());
		this.mShipBody = PhysicsFactory.createPolygonBody(physicsWorld, this.mShipBodySprite, Ship2BodyVertices, BodyType.DynamicBody, this.mShipFixtureDef);
		
		scene.attachChild(this.mShipBodySprite);
		
		width=Ship.sShip2FrontLeftEngineTextureRegion.getWidth() / PIXEL_TO_METER_RATIO_DEFAULT;
		height=Ship.sShip2FrontLeftEngineTextureRegion.getHeight() / PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] frontLeftEngineVertices = {
				new Vector2(-0.47936f*width, -0.23495f*height),
				new Vector2(+0.41584f*width, -0.48414f*height),
				new Vector2(+0.37892f*width, +0.61410f*height)
		};
		width=Ship.sShip2FrontRightEngineTextureRegion.getWidth() / PIXEL_TO_METER_RATIO_DEFAULT;
		height=Ship.sShip2FrontRightEngineTextureRegion.getHeight() / PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] frontRightEngineVertices = {
				new Vector2(-0.38707f*width, -0.49092f*height),
				new Vector2(+0.41584f*width, -0.35058f*height),
				new Vector2(-0.36862f*width, +0.52489f*height)
		};
		width=Ship.sShip2BackLeftEngineTextureRegion.getWidth() / PIXEL_TO_METER_RATIO_DEFAULT;
		height=Ship.sShip2BackLeftEngineTextureRegion.getHeight() / PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] backLeftEngineVertices = {
				new Vector2(-0.06059f*width, -0.48729f*height),
				new Vector2(+0.45992f*width, +0.47568f*height),
				new Vector2(-0.49804f*width, +0.31821f*height)
		};
		width=Ship.sShip2BackRightEngineTextureRegion.getWidth() / PIXEL_TO_METER_RATIO_DEFAULT;
		height=Ship.sShip2BackRightEngineTextureRegion.getHeight() / PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] backRightEngineVertices = {
				new Vector2(+0.09459f*width, -0.48769f*height),
				new Vector2(+0.46649f*width, +0.34291f*height),
				new Vector2(-0.52350f*width, +0.35675f*height)
		};
		width=Ship.sShip2FrontEngineTextureRegion.getWidth() / PIXEL_TO_METER_RATIO_DEFAULT;
		height=Ship.sShip2FrontEngineTextureRegion.getHeight() / PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] frontEngineVertices = {
				new Vector2(-0.46862f*width, -0.25951f*height),
				new Vector2(+0.32701f*width, -0.26832f*height),
				new Vector2(-0.06060f*width, +0.51571f*height)
		};
		width=Ship.sShip2BackEngineTextureRegion.getWidth() / PIXEL_TO_METER_RATIO_DEFAULT;
		height=Ship.sShip2BackEngineTextureRegion.getHeight() / PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] backEngineVertices = {
				new Vector2(-0.11583f*width, -0.58586f*height),
				new Vector2(+0.16325f*width, +0.31534f*height),
				new Vector2(-0.45693f*width, +0.33472f*height)
		};
		
		ShipEngine ShipEngine [] = {
				new ShipEngine(context, this, -7, 0, (float)Math.PI/2, frontLeftEngineForce, frontLeftEngineStrength, this.mShipBody, this.mShipBody, this.mShipBodySprite, frontLeftEngineVertices, scene, physicsWorld, sEngineFireTextureRegion, -18, -3, 0.5f, Ship.sShip2FrontLeftEngineTextureRegion, this.mShipFixtureDef, true),
				new ShipEngine(context, this, 80,   0, (float)-Math.PI/2, frontRightEngineForce, frontRightEngineStrength, this.mShipBody, this.mShipBody, this.mShipBodySprite, frontRightEngineVertices,  scene, physicsWorld, sEngineFireTextureRegion, 18, -7, 0.5f, Ship.sShip2FrontRightEngineTextureRegion, this.mShipFixtureDef, true),
				new ShipEngine(context, this, -7,  67, (float)Math.PI/2, backLeftEngineForce, backLeftEngineStrength, this.mShipBody, this.mShipBody, this.mShipBodySprite, backLeftEngineVertices, scene, physicsWorld, sEngineFireTextureRegion, -24, 8, 0.5f, Ship.sShip2BackLeftEngineTextureRegion, this.mShipFixtureDef, true),
				new ShipEngine(context, this, 67, 73, (float)-Math.PI/2, backRightEngineForce, backRightEngineStrength, this.mShipBody, this.mShipBody, this.mShipBodySprite, backRightEngineVertices, scene, physicsWorld, sEngineFireTextureRegion, 24, 8, 0.5f, Ship.sShip2BackRightEngineTextureRegion, this.mShipFixtureDef, true),
				new ShipEngine(context, this, 38, -5, (float)Math.PI, frontEngineForce, frontEngineStrength, this.mShipBody, this.mShipBody, this.mShipBodySprite, frontEngineVertices,  scene, physicsWorld, sEngineFireTextureRegion, 0, -20, 1, Ship.sShip2FrontEngineTextureRegion,  this.mShipFixtureDef, true),
				new ShipEngine(context, this, 38,  83, 0.0f, backEngineForce, backEngineStrength, this.mShipBody, this.mShipBody, this.mShipBodySprite, backEngineVertices, scene, physicsWorld, sEngineFireTextureRegion, -3, 20, 1, Ship.sShip2BackEngineTextureRegion, this.mShipFixtureDef, true)
				};
		
		
		width=sRocketTextureRegion.getWidth() / PIXEL_TO_METER_RATIO_DEFAULT;
		height=sRocketTextureRegion.getHeight() / PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] missileVertices = {
				new Vector2(-0.59465f*width, -0.47054f*height),
				new Vector2(-0.02973f*width, -0.50988f*height),
				new Vector2(+0.50514f*width, -0.47153f*height)
		};
		width=Ship.sBulletTextureRegion.getWidth() / PIXEL_TO_METER_RATIO_DEFAULT;
		height=Ship.sBulletTextureRegion.getHeight() / PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] bulletVertices = {
				new Vector2(-0.23070f*width, -0.48555f*height),
				new Vector2(-0.02071f*width, -0.50780f*height),
				new Vector2(+0.18164f*width, -0.48555f*height)
		};

		width=Ship.sShip2ChainGunTextureRegion.getWidth() / PIXEL_TO_METER_RATIO_DEFAULT;
		height=Ship.sShip2ChainGunTextureRegion.getHeight() / PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] chainGunVertices = {
				new Vector2(-0.38667f*width, -0.51030f*height),
				new Vector2(+0.45316f*width, -0.50284f*height),
				new Vector2(+0.01710f*width, +0.49601f*height)
		};
		width=Ship.sShip2RocketLauncherTextureRegion.getWidth() / PIXEL_TO_METER_RATIO_DEFAULT;
		height=Ship.sShip2RocketLauncherTextureRegion.getHeight() / PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] rocketLauncherVertices = {
				new Vector2(-0.39978f*width, -0.46213f*height),
				new Vector2(+0.25917f*width, -0.46213f*height),
				new Vector2(-0.08969f*width, +0.45405f*height)
		};
		mWeapon.add(new Weapon((Ship)this, 65, -10, 68, -23, 0, 3000, true, 100, 1000, 5f, this.mShipBody, this.mShipBodySprite, rocketLauncherVertices, missileVertices, scene, physicsWorld, sEngineFireTextureRegion, 0, sRocketTextureRegion.getHeight()-3, 1, sShip2RocketLauncherTextureRegion, sRocketTextureRegion, this.mShipFixtureDef, this.mShipFixtureDef));
		mWeapon.add(new Weapon((Ship)this, 15, -10, 15, -12, 0, 2000, false, 0.5f, 300, 5f, this.mShipBody, this.mShipBodySprite, chainGunVertices, bulletVertices, scene, physicsWorld, null, 0, 0, 0, sShip2ChainGunTextureRegion, Ship.sBulletTextureRegion, this.mShipFixtureDef, this.mShipFixtureDef));
		
		mShipBodyList.add(mShipBody);
		for (int i=0; i<ShipEngine.length; i++) {
			mShipBodyList.add(ShipEngine[i].mBody);
		}
		for (int i=0; i<mWeapon.size(); i++) {
			mShipBodyList.add(mWeapon.get(i).mBody);
		}
		
		this.mShipEngine=ShipEngine;
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this.mShipBodySprite, this.mShipBody, true, true));
//		this.ShipBody.setTransform(ShipX / PIXEL_TO_METER_RATIO_DEFAULT, ShipY / PIXEL_TO_METER_RATIO_DEFAULT, Rotation*(float)Math.PI/180);
		this.mShipBody.setTransform((mShipBodySprite.getX()+mShipBodySprite.getRotationCenterX())/PIXEL_TO_METER_RATIO_DEFAULT,(mShipBodySprite.getY()+mShipBodySprite.getRotationCenterY())/PIXEL_TO_METER_RATIO_DEFAULT, rotation*(float)Math.PI/180);
		//updateShipFixture(0x00000002, 0xFFFFFFFF);
		

	}
	
	@Override
	public void control(Touch [] touches) {
		if (!mIsHumanControlled) return;
		//LEFT-BOTTOM
		if (isAreaTouched(0,320,150,480,touches)) mShipEngine[2].engineStart(); else mShipEngine[2].engineStop();
		//LEFT-TOP
		if (isAreaTouched(0,0,150,160,touches)) mShipEngine[0].engineStart(); else mShipEngine[0].engineStop();
		//RIGHT-TOP
		if (isAreaTouched(650,0,800,160,touches)) mShipEngine[1].engineStart(); else mShipEngine[1].engineStop();
		//RIGHT-BOTTOM
		if (isAreaTouched(650,320,800,480,touches)) mShipEngine[3].engineStart(); else mShipEngine[3].engineStop();
		//LEFT-BOTTOM-RIGHT
		if (isAreaTouched(150,240,300,480,touches)) mShipEngine[5].engineStart(); else mShipEngine[5].engineStop();
		//RIGHT-BOTTOM-LEFT
		if (isAreaTouched(500,240,650,480,touches)) mWeapon.get(1).weaponStartShooting(); else mWeapon.get(1).weaponStopShooting();
		
		//MIDDLE_LEFT
		if (isAreaTouched(0,160,150,320,touches)) {mShipEngine[0].engineStart();mShipEngine[2].engineStart();} else {if (!isAreaTouched(0,0,150,180,touches))mShipEngine[0].engineStop();if (!isAreaTouched(0,300,150,480,touches))mShipEngine[2].engineStop();}
		//MIDDLE_RIGHT
		if (isAreaTouched(650,160,800,320,touches)) {mShipEngine[1].engineStart();mShipEngine[3].engineStart();} else {if (!isAreaTouched(650,0,800,180,touches))mShipEngine[1].engineStop();if (!isAreaTouched(650,300,800,480,touches))mShipEngine[3].engineStop();}
		
		//LEFT-TOP-RIGHT
		if (isAreaTouched(150,0,300,240,touches)) mShipEngine[4].engineStart(); else mShipEngine[4].engineStop();
		//RIGHT-TOP-LEFT
		if (isAreaTouched(500, 0, 650, 240, touches)) mWeapon.get(0).weaponStartShooting(); else mWeapon.get(0).weaponStopShooting();
		
	}
}