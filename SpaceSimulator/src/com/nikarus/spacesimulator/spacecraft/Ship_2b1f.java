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

public class Ship_2b1f extends Ship { //SHIP: 2 BACK ENGINES; 1 FRONT ENGINES
	private static BitmapTextureAtlas mBitmapTextureAtlas = null;
	public static Sprite mMissileSprite;
	public Ship_2b1f(SimpleBaseGameActivity context, Engine engine, PhysicsWorld physicsWorld, Scene scene, boolean isHumanControlled, boolean isCameraTraced, float shipX, float shipY, float rotation, int leftEngineForce, int rightEngineForce, int frontEngineForce, float leftEngineStrength, float rightEngineStrength, float frontEngineStrength)
	{
		if (mBitmapTextureAtlas==null) mBitmapTextureAtlas = new BitmapTextureAtlas(context.getTextureManager(), 256, 256/*, TextureOptions.BILINEAR_PREMULTIPLYALPHA*/);
		loadCommonData(mBitmapTextureAtlas, context, engine, scene, physicsWorld, this, isHumanControlled, isCameraTraced);
		//if (!texturesAlreadyLoaded)
		//{
		if (Ship.sEngineFireTextureRegion == null) Ship.sEngineFireTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, context, "EngineFireCropped160.png", 0, 0, 5, 1);
		if (Ship.sShipBodyTextureRegion == null) Ship.sShipBodyTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "Ship/ShipBodySprite.png", 0, 40);
		if (Ship.sLeftEngineTextureRegion == null) Ship.sLeftEngineTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "Ship/LeftEngineSprite.png", 114, 40);
		if (Ship.sRightEngineTextureRegion == null) Ship.sRightEngineTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "Ship/RightEngineSprite.png", 144, 40);
		if (Ship.sFrontEngineTextureRegion == null) Ship.sFrontEngineTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "Ship/FrontEngineSprite.png", 173, 40);
		if (Ship.sRocketTextureRegion == null) Ship.sRocketTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "Missile.png", 0, 117);
		if (Ship.sBulletTextureRegion == null) Ship.sBulletTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "Bullet.png", 9, 117);
		//if (cShip.mChainGunTextureRegion == null) cShip.mChainGunTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "boxPart.png", 26, 117);
		if (Ship.sShip2ChainGunTextureRegion == null) Ship.sShip2ChainGunTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "Ship2/Ship2ChainGun.png", 26, 117);
		if (Ship.sShip2RocketLauncherTextureRegion == null) Ship.sShip2RocketLauncherTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "Ship2/Ship2RocketLauncher.png", 38, 117);
		
		//}
		engine.getTextureManager().loadTexture(mBitmapTextureAtlas);
		this.mShipFixtureDef = PhysicsFactory.createFixtureDef(1f, 0.1f, 0.2f);
		
		float width=Ship.sShipBodyTextureRegion.getWidth() / PIXEL_TO_METER_RATIO_DEFAULT;
		float height=Ship.sShipBodyTextureRegion.getHeight() / PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] ShipBodyVertices = {
				new Vector2(-0.36452f*width, -0.32476f*height),
				new Vector2(-0.11070f*width, -0.51265f*height),
				new Vector2(+0.10250f*width, -0.51265f*height),
				new Vector2(+0.34109f*width, -0.32476f*height),
				new Vector2(+0.49338f*width, +0.06605f*height),
				new Vector2(+0.43754f*width, +0.26897f*height),
				new Vector2(-0.45082f*width, +0.33661f*height),
				new Vector2(-0.50158f*width, +0.05102f*height)
		};
		this.mShipBodySprite = new Sprite(shipX, shipY, Ship.sShipBodyTextureRegion, context.getVertexBufferObjectManager());
		this.mShipBody = PhysicsFactory.createPolygonBody(physicsWorld, this.mShipBodySprite, ShipBodyVertices, BodyType.DynamicBody, this.mShipFixtureDef);
		scene.attachChild(this.mShipBodySprite);
		
		width=Ship.sLeftEngineTextureRegion.getWidth() / PIXEL_TO_METER_RATIO_DEFAULT;
		height=Ship.sLeftEngineTextureRegion.getHeight() / PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] leftEngineVertices = {
				new Vector2(-0.30167f*width, -0.49382f*height),
				new Vector2(+0.43033f*width, +0.03029f*height),
				new Vector2(+0.22633f*width, +0.53324f*height),
				new Vector2(-0.50567f*width, +0.22618f*height)
		};
		width=Ship.sRightEngineTextureRegion.getWidth() / PIXEL_TO_METER_RATIO_DEFAULT;
		height=Ship.sRightEngineTextureRegion.getHeight() / PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] rightEngineVertices = {
				new Vector2(-0.51069f*width, -0.10464f*height),
				new Vector2(+0.44517f*width, -0.56107f*height),
				new Vector2(+0.33345f*width, +0.35179f*height),
				new Vector2(-0.37414f*width, +0.49321f*height)
		};
		width=Ship.sFrontEngineTextureRegion.getWidth() / PIXEL_TO_METER_RATIO_DEFAULT;
		height=Ship.sFrontEngineTextureRegion.getHeight() / PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] frontEngineVertices = {
				new Vector2(-0.54078f*width, -0.30114f*height),
				new Vector2(+0.56734f*width, -0.39371f*height),
				new Vector2(+0.17359f*width, +0.47029f*height),
				new Vector2(-0.24828f*width, +0.46000f*height)
		};
		
		ShipEngine ShipEngine [] = {
				new ShipEngine(context, this, 25, -10, (float)Math.PI, frontEngineForce, frontEngineStrength, this.mShipBody, this.mShipBody, this.mShipBodySprite, frontEngineVertices, scene, physicsWorld, sEngineFireTextureRegion, 0, -Ship.sFrontEngineTextureRegion.getHeight(), 1, Ship.sFrontEngineTextureRegion, this.mShipFixtureDef, true),
				new ShipEngine(context, this, 0,   26, 0.0f,           leftEngineForce, leftEngineStrength, this.mShipBody, this.mShipBody, this.mShipBodySprite, leftEngineVertices,  scene, physicsWorld, sEngineFireTextureRegion, 0, Ship.sLeftEngineTextureRegion.getHeight()-20, 1, Ship.sLeftEngineTextureRegion,  this.mShipFixtureDef, true),
				new ShipEngine(context, this, 85,  43, 0.0f,           rightEngineForce, rightEngineStrength, this.mShipBody, this.mShipBody, this.mShipBodySprite, rightEngineVertices, scene, physicsWorld, sEngineFireTextureRegion, 0, Ship.sRightEngineTextureRegion.getHeight()-20, 1, Ship.sRightEngineTextureRegion, this.mShipFixtureDef, true)
				};
		
		width=Ship.sRocketTextureRegion.getWidth() / PIXEL_TO_METER_RATIO_DEFAULT;
		height=Ship.sRocketTextureRegion.getHeight() / PIXEL_TO_METER_RATIO_DEFAULT;
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

		/*width=cShip.mChainGunTextureRegion.getWidth() / PIXEL_TO_METER_RATIO_DEFAULT;
		height=cShip.mChainGunTextureRegion.getHeight() / PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] ChainGunVertices = {
				new Vector2(-0.50035f*width, -0.49992f*height),
				new Vector2(+0.49806f*width, -0.49992f*height),
				new Vector2(+0.49806f*width, +0.49536f*height),
				new Vector2(-0.50035f*width, +0.49536f*height)
		};*/
		mWeapon.add(new Weapon((Ship)this, 90, 5, 93, -12, 0, 3000, true, 100, 2000, 10f, this.mShipBody, this.mShipBodySprite, rocketLauncherVertices, missileVertices, scene, physicsWorld, sEngineFireTextureRegion, 0, Ship.sRocketTextureRegion.getHeight()-3, 1, sShip2RocketLauncherTextureRegion, Ship.sRocketTextureRegion, this.mShipFixtureDef, this.mShipFixtureDef));
		mWeapon.add(new Weapon((Ship)this, 10, -2, 10, -4, 0, 2000, false, 0.5f, 300, 10f, this.mShipBody, this.mShipBodySprite, chainGunVertices, bulletVertices, scene, physicsWorld, null, 0, 0, 0, sShip2ChainGunTextureRegion, Ship.sBulletTextureRegion, this.mShipFixtureDef, this.mShipFixtureDef));
		
		//weapon.add(new cWeapon((cShip)this, 65, -10, 68, -23, 0, 3000, true, 100, 1000, 10f, this.ShipBody, this.ShipBodySprite, RocketLauncherVertices, MissileVertices, mScene, mPhysicsWorld, mEngineFireTextureRegion, 0, cShip.mMissileTextureRegion.getHeight()-3, 1, mShip2RocketLauncherTextureRegion, cShip.mMissileTextureRegion, this.ShipFixtureDef, this.ShipFixtureDef));
		//weapon.add(new cWeapon((cShip)this, 15, -10, 15, -12, 0, 2000, false, 0.5f, 300, 10f, this.ShipBody, this.ShipBodySprite, ChainGunVertices, BulletVertices, mScene, mPhysicsWorld, null, 0, 0, 0, mShip2ChainGunTextureRegion, cShip.mBulletTextureRegion, this.ShipFixtureDef, this.ShipFixtureDef));
		
		this.mShipEngine=ShipEngine;
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this.mShipBodySprite, this.mShipBody, true, true));
//		this.ShipBody.setTransform(ShipX / PIXEL_TO_METER_RATIO_DEFAULT, ShipY / PIXEL_TO_METER_RATIO_DEFAULT, Rotation*(float)Math.PI/180);
		this.mShipBody.setTransform((mShipBodySprite.getX()+mShipBodySprite.getRotationCenterX())/PIXEL_TO_METER_RATIO_DEFAULT,(mShipBodySprite.getY()+mShipBodySprite.getRotationCenterY())/PIXEL_TO_METER_RATIO_DEFAULT, rotation*(float)Math.PI/180);
		//updateShipFixture(0x00000002, 0xFFFFFFFF);
		

	}
	
	public void control(Touch [] touches) {
		if (!mIsHumanControlled) return;
		
		if (isAreaTouched(0,240,150,480,touches)) {//LEFT-BOTTOM
			mShipEngine[2].engineStart();
		} else {
			mShipEngine[2].engineStop();
		}
		if (isAreaTouched(0,0,150,240,touches) /*LEFT-TOP*/|| isAreaTouched(650,0,800,240,touches)/*RIGHT-TOP*/) {
			mShipEngine[0].engineStart();
		} else {
			mShipEngine[0].engineStop();
		}
		if (isAreaTouched(650,240,800,480,touches)) {//RIGHT-BOTTOM
			mShipEngine[1].engineStart();
		} else {
			mShipEngine[1].engineStop();
		}
		if (isAreaTouched(150,0,300,240,touches) || isAreaTouched(500,0,650,240,touches)) {
			mWeapon.get(0).weaponStartShooting();
		} else {
			mWeapon.get(0).weaponStopShooting();
		}
		if (isAreaTouched(150,240,300,480,touches) || isAreaTouched(500,240,650,480,touches)) {
			mWeapon.get(1).weaponStartShooting();
		} else {
			mWeapon.get(1).weaponStopShooting();
		}
	}
}