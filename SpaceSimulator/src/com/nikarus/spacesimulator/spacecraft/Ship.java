package com.nikarus.spacesimulator.spacecraft;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.nikarus.spacesimulator.Map;
import com.nikarus.spacesimulator.Touch;
import com.nikarus.spacesimulator.shipparts.ShipEngine;
import com.nikarus.spacesimulator.shipparts.Weapon;

public abstract class Ship {
	public double mFitness=0;
	public int mTrainingBoxID=0;

	protected static SimpleBaseGameActivity sContext;
	protected static Engine sEngine;
	protected static Scene sScene;
	protected static PhysicsWorld sPhysicsWorld;
	public static float mScale=0.7f;
	public Body mShipBody;
	protected Sprite mShipBodySprite;
	public ShipEngine mShipEngine [];
	protected FixtureDef mShipFixtureDef;
	public boolean mIsHumanControlled=false;
	public boolean mIsCameraTraced=false;
	public static int sHumanControlledIndex;

	public List<Body> mShipBodyList = new ArrayList<Body>();
	public List<Weapon> mWeapon = new ArrayList<Weapon>();

	public static ArrayList<Ship> mShips = new ArrayList<Ship>();

	protected static TiledTextureRegion sEngineFireTextureRegion;

	protected static TextureRegion sShipBodyTextureRegion;
	protected static TextureRegion sFrontEngineTextureRegion;
	protected static TextureRegion sLeftEngineTextureRegion;
	protected static TextureRegion sRightEngineTextureRegion;

	protected static TextureRegion sBoxPartTextureRegion;
	protected static TextureRegion sCirclePartTextureRegion;
	protected static TextureRegion sTrianglePartTextureRegion;
	protected static TextureRegion sRocketTextureRegion;
	protected static TextureRegion sBulletTextureRegion;

	protected static TextureRegion sChainGunTextureRegion;

	protected static TextureRegion sShip2ShipBodyTextureRegion;
	protected static TextureRegion sShip2FrontEngineTextureRegion;
	protected static TextureRegion sShip2BackEngineTextureRegion;
	protected static TextureRegion sShip2FrontLeftEngineTextureRegion;
	protected static TextureRegion sShip2FrontRightEngineTextureRegion;
	protected static TextureRegion sShip2BackLeftEngineTextureRegion;
	protected static TextureRegion sShip2BackRightEngineTextureRegion;

	protected static TextureRegion sShip2ChainGunTextureRegion;
	protected static TextureRegion sShip2RocketLauncherTextureRegion;

	protected void loadCommonData(BitmapTextureAtlas mBitmapTextureAtlas, SimpleBaseGameActivity context, Engine engine, Scene scene, PhysicsWorld physicsWorld, Object theShip, boolean isHumanControlled, boolean isCameraTraced) {
		sContext=context;
		mShips.add((Ship) theShip);
		if (isHumanControlled) Ship.sHumanControlledIndex=mShips.size()-1;
		this.mIsHumanControlled=isHumanControlled;
		this.mIsCameraTraced=isCameraTraced;
		Ship.sEngine=engine;
		Ship.sPhysicsWorld=physicsWorld;
		Ship.sScene=scene;
	}

	public void checkContacts(Contact pContact) {
		for (int i=0;i<mShipEngine.length; i++) mShipEngine[i].checkJoints(pContact);
		for (int i=0;i < mWeapon.size();i++) mWeapon.get(i).checkJoints(pContact);
	}

	public void checkContactsPostSolve(Contact contact) {
		for (int i=0;i < mWeapon.size();i++) mWeapon.get(i).checkShellDetonation(contact);
	}

	public void updateShip() {
		//Log.i("info", "Updating SHIP");
		for (int i=0;i<mShipEngine.length; i++) mShipEngine[i].update();

		for (int i=0;i < mWeapon.size();i++) mWeapon.get(i).update();

		if (mIsCameraTraced) {
			sEngine.getCamera().setCenter(mShipBodySprite.getX()*mScale, mShipBodySprite.getY()*Ship.mScale);
			//Map.ParallaxBackground.setParallaxValue(-ShipBodySprite.getX()/100, -ShipBodySprite.getY()/100);
			Map.ParallaxBackground.setParallaxValue(1f);
		}
	}

	public void updateShipFixture(int cat, int mask) {

		Filter filter = new Filter();
		filter.categoryBits=(short)cat;
		filter.maskBits=(short)mask;
		mShipBody.getFixtureList().get(0).setFilterData(filter);

		for (int i=0;i<mShipEngine.length; i++) mShipEngine[i].mBody.getFixtureList().get(0).setFilterData(filter);
	}

	public boolean checkSpriteCollision(Sprite sprite) {
		boolean result = false;
		result=mShipBodySprite.collidesWith(sprite);
		for (int i=0;i<mShipEngine.length; i++) if (!result) result=mShipEngine[i].mSprite.collidesWith(sprite);
		return result;
	}

	protected static boolean isAreaTouched(int leftTopX, int leftTopY, int rightBotomX, int rightBottomY, Touch [] Touches) {
		for (int i=0; i<Touches.length; i++)
			if (Touches[i].x>leftTopX && Touches[i].x<rightBotomX && Touches[i].y>leftTopY && Touches[i].y<rightBottomY)
				return true;
		return false;
	}

	public static Vector2 setCoordinates(Sprite relativeSprite, float width, float height, float shiftX, float shiftY, Vector2 ShiftPart) {
		if (ShiftPart==null) ShiftPart=new Vector2();

		float ShiftedShipPartShiftX=shiftX-relativeSprite.getWidth()/2f + width/2f;
		float ShiftedShipPartShiftY=shiftY-relativeSprite.getHeight()/2f + height/2f;

		float cosA=(float)Math.cos(relativeSprite.getRotation()*(float)Math.PI/180);
		float sinA=(float)Math.sin(relativeSprite.getRotation()*(float)Math.PI/180);
		//sprite.getTextureRegion()
		//Log.i("info",relativeSprite.getX()+" "+relativeSprite.getY());
		ShiftPart.x=-ShiftedShipPartShiftY*sinA+ShiftedShipPartShiftX*cosA+relativeSprite.getX()+relativeSprite.getWidth()/2f - width/2f;
		ShiftPart.y=ShiftedShipPartShiftY*cosA+ShiftedShipPartShiftX*sinA+relativeSprite.getY()+relativeSprite.getHeight()/2f - height/2f;
		return new Vector2((ShiftPart.x+width/2f)/PIXEL_TO_METER_RATIO_DEFAULT, (ShiftPart.y+height/2f)/PIXEL_TO_METER_RATIO_DEFAULT);
		//this.body.setTransform(,, rotation+shipBodySprite.getRotation()*(float)Math.PI/180);
	}

	public abstract void control(Touch[] touches);
}