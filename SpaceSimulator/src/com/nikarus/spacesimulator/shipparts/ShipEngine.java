package com.nikarus.spacesimulator.shipparts;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.nikarus.spacesimulator.spacecraft.Ship;

public class ShipEngine extends ShipPart
{
	protected final int mEngineFireAnimationDelay=80;
	
	protected TiledTextureRegion mEngineFireTextureRegion;
	protected AnimatedSprite mEngineFireSprite;
	public boolean mEngineStarted=false;
	protected int mEngineForce;
	
	protected float mEngineFireShiftX;
	protected float mEngineFireShiftY;
	protected Body mEngineFireBody;
	protected Joint mEngineFireJoint;
	private boolean mHasEngineFire;
	protected float mRotation;
	//protected float xRotationCenterShift;
	//protected float yRotationCenterShift;
	//protected float xPositionShift;
	//protected float yPositionShift;
	
	public ShipEngine(SimpleBaseGameActivity context, Ship ship, float engineShiftX,float engineShiftY, float rotation, int engineForce, float strength, Body shipBody, Body bodyToJoinWith, Sprite shipBodySprite, Vector2[] vertices, Scene scene, PhysicsWorld physicsWorld, TiledTextureRegion engineFireTextureRegion, float engineFireShiftX, float engineFireShiftY, float engineFireScale, TextureRegion engineTextureRegion, FixtureDef engineFixtureDef, boolean hasEngineFire)
	{
		super(context, ship, engineShiftX, engineShiftY, 0, strength, shipBody, bodyToJoinWith, shipBodySprite, vertices, scene, physicsWorld, engineTextureRegion, engineFixtureDef);
		
		this.mEngineForce=engineForce;
		this.mEngineFireTextureRegion=engineFireTextureRegion;
		this.mEngineFireShiftX=engineFireShiftX;
		this.mEngineFireShiftY=engineFireShiftY;
		this.mHasEngineFire=hasEngineFire;
		this.mRotation=rotation;
		//-----------------------------------------------------
		/*//Creating FIRE sprite
		if (this.EngineFireTextureRegion != null) {
			this.EngineFireSprite = new AnimatedSprite(x, y, this.EngineFireTextureRegion);
			this.EngineFireSprite.animate(this.EngineFireAnimationDelay);
			this.mScene.attachChild(this.EngineFireSprite);
			this.EngineFireSprite.setZIndex(-100);
			this.EngineFireSprite.setVisible(false);
		}
		//-----------------------------------------------------
		//Setting transform parameters for engine FIRE
		if (this.EngineFireTextureRegion != null) {
			this.xRotationCenterShift=this.EngineFireSprite.getWidthScaled()/2;
			this.yRotationCenterShift=-this.sprite.getHeightScaled()/2+this.EngineFireOverlap;
			this.xPositionShift=-this.EngineFireSprite.getWidthScaled()/2+this.sprite.getWidthScaled()/2;
			this.yPositionShift=this.sprite.getHeightScaled()-this.EngineFireOverlap;
			this.EngineFireSprite.setRotationCenter(this.xRotationCenterShift, this.yRotationCenterShift);
		}*/
		//-----------------------------------------------------
		
		
		if (hasEngineFire) {
			float width=this.mEngineFireTextureRegion.getWidth() / PIXEL_TO_METER_RATIO_DEFAULT;
			float height=this.mEngineFireTextureRegion.getHeight() / PIXEL_TO_METER_RATIO_DEFAULT;
			this.mEngineFireSprite = new AnimatedSprite(mSprite.getX(), mSprite.getY(), this.mEngineFireTextureRegion, context.getVertexBufferObjectManager());
			this.mEngineFireSprite.animate(this.mEngineFireAnimationDelay);
			mEngineFireSprite.setScale(engineFireScale);
			this.mScene.attachChild(this.mEngineFireSprite);
			mEngineFireSprite.setZIndex(-100);
			scene.sortChildren();
			
			final Vector2[] engineFireVertices = {
					new Vector2(-0.42972f*width, -0.48825f*height),
					new Vector2(-0.36867f*width, -0.48825f*height),
					new Vector2(-0.40064f*width, +0.34899f*height)
			};
			this.mEngineFireBody = PhysicsFactory.createPolygonBody(this.mPhysicsWorld, this.mEngineFireSprite, engineFireVertices, BodyType.DynamicBody, this.mFixtureDef);
			
			
			this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(this.mEngineFireSprite, mEngineFireBody, true, true));
		
			//EngineFireBody.setTransform((sprite.getX()+sprite.getRotationCenterX())/PIXEL_TO_METER_RATIO_DEFAULT,(sprite.getY()+sprite.getRotationCenterY()+(float)Math.cos(rotation)*(this.sprite.getHeightScaled()-this.EngineFireOverlap))/PIXEL_TO_METER_RATIO_DEFAULT, body.getAngle());
			//Log.i("info", "INFO: "+sprite.getX()+" "+sprite.getY());
			mEngineFireBody.setTransform(Ship.setCoordinates(mShipBodySprite, mTextureRegion.getWidth(), mTextureRegion.getHeight(), engineShiftX+this.mEngineFireShiftX, engineShiftY+this.mEngineFireShiftY, null), mBody.getAngle()+rotation);
			//EngineFireSprite.setPosition(sprite.getX()+-this.EngineFireSprite.getWidthScaled()/2+this.sprite.getWidthScaled()/2, sprite.getY()+this.sprite.getHeightScaled()-this.EngineFireOverlap);
			//EngineFireSprite.setRotation(sprite.getRotation());
			
			/*WeldJointDef jointDef = new WeldJointDef();
			jointDef.initialize(EngineFireBody, body, EngineFireBody.getWorldCenter());
			jointDef.collideConnected = false;
			mPhysicsWorld.createJoint(jointDef);*/
			//if (!isMissile)
			mEngineFireJoint=join(mShipBody,mEngineFireBody);
			
			Filter filter=new Filter();
			filter.categoryBits=0x00001000;
			filter.maskBits=    0x00000000;
			mEngineFireBody.getFixtureList().get(0).setFilterData(filter);
		}
			engineStop();
			
	}
	 public void engineStart() {
		if (!mCrashed) {
			mEngineStarted=true;
			if (mHasEngineFire) mEngineFireSprite.setVisible(true);
		}
	 }
	 public void engineStop() {
		mEngineStarted=false;
		if (mHasEngineFire) mEngineFireSprite.setVisible(false);
	 }
	 @Override
	 public void update() {
		super.update();
		
		if (mEngineStarted)
     	{
			/*if (this.EngineFireTextureRegion != null) {
				EngineFireSprite.setPosition(sprite.getX()+xPositionShift, sprite.getY()+yPositionShift);
	    		EngineFireSprite.setRotation(sprite.getRotation());
			}*/
			mBody.applyForce(new Vector2(mEngineForce*(float)Math.sin(mBody.getAngle()+mRotation), -mEngineForce*(float)Math.cos(mBody.getAngle()+mRotation)), mBody.getWorldCenter());
     	}
	 }
	 @Override
	 public void checkJoints(Contact pContact) {
		 super.checkJoints(pContact);
		 if (mCrashed){
			 mEngineStarted=false;
      		if (this.mEngineFireTextureRegion != null) mEngineFireSprite.setVisible(false);
		 }
	 }
}