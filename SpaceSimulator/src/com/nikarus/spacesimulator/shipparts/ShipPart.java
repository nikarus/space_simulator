package com.nikarus.spacesimulator.shipparts;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.nikarus.spacesimulator.spacecraft.Ship;

class ShipPart
{
	protected static SimpleBaseGameActivity sContext;
	protected PhysicsWorld mPhysicsWorld;
	protected Scene mScene;
	
	protected TextureRegion mTextureRegion;
	public Sprite mSprite;
	public Body mBody;
	protected Joint mJoint;
	protected WeldJointDef mJointDef = new WeldJointDef();
	public boolean mDestroyJoint=false;
	protected FixtureDef mFixtureDef;
	
	public boolean mCrashed=false;
	protected float mStrength;
	
	protected float mShiftX;
	protected float mShiftY;
	protected float mRotation;
	protected Body mShipBody;
	protected Sprite mShipBodySprite;
	protected Vector2[] mVertices;
	
	//protected float x;
	//protected float y;
	
	public ShipPart(SimpleBaseGameActivity context, Ship ship, float shiftX,float shiftY, float rotation, float strength, Body shipBody, Body bodyToJoinWith, Sprite shipBodySprite, Vector2[] vertices, Scene scene, PhysicsWorld physicsWorld, TextureRegion textureRegion, FixtureDef fixtureDef)
	{
		ShipPart.sContext=context;
		//float ShiftedShipPartShiftX=shiftX-shipBodySprite.getTextureRegion().getWidth()/2f + textureRegion.getWidth()/2f;
		//float ShiftedShipPartShiftY=shiftY-shipBodySprite.getTextureRegion().getHeight()/2f + textureRegion.getHeight()/2f;
		this.mShiftX=shiftX;
		this.mShiftY=shiftY;
		this.mRotation=rotation;
		this.mShipBody=shipBody;
		this.mShipBodySprite=shipBodySprite;
		this.mVertices=vertices;
		
		//float cosA=(float)Math.cos(shipBodySprite.getRotation()*(float)Math.PI/180);
		//float sinA=(float)Math.sin(shipBodySprite.getRotation()*(float)Math.PI/180);
		
		//float ShipPartX=+ShipPartShiftY*sinA+ShipPartShiftX*cosA+ShipBodySprite.getX()+ShipBodySprite.getWidthScaled()/2;
		//float ShipPartY=ShipPartShiftY*cosA+ShipPartShiftX*sinA+ShipBodySprite.getY()+ShipBodySprite.getHeightScaled()/2;
		//float ShipPartX=-ShiftedShipPartShiftY*sinA+ShiftedShipPartShiftX*cosA+shipBodySprite.getX()+shipBodySprite.getTextureRegion().getWidth()/2f - textureRegion.getWidth()/2f;
		//float ShipPartY=ShiftedShipPartShiftY*cosA+ShiftedShipPartShiftX*sinA+shipBodySprite.getY()+shipBodySprite.getTextureRegion().getHeight()/2f - textureRegion.getHeight()/2f;
		//Coping variable references to class private variables
		Vector2 ShiftPart=new Vector2();
		Vector2 shipPartCoordinates=Ship.setCoordinates(shipBodySprite, textureRegion.getWidth(), textureRegion.getHeight(), shiftX, shiftY, ShiftPart);
		//this.x=cShipPartCoordinates.x;
		//this.y=cShipPartCoordinates.y;
		this.mPhysicsWorld=physicsWorld;
		this.mScene=scene;
		this.mTextureRegion=textureRegion;
		this.mFixtureDef=fixtureDef;
		this.mStrength=strength;
		//-----------------------------------------------------
		//Creating ShipPart, rotating it and joining to the BODY
		//Log.i("info","SPRITES: "+ShiftPart.x+" "+ShiftPart.y);
		this.mSprite = new Sprite(ShiftPart.x, ShiftPart.y, this.mTextureRegion, context.getVertexBufferObjectManager());
		this.mBody = PhysicsFactory.createPolygonBody(this.mPhysicsWorld, this.mSprite, vertices, BodyType.DynamicBody, this.mFixtureDef);
		this.mScene.attachChild(this.mSprite);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(this.mSprite, this.mBody, true, true));
		//Log.i("info", "HERE: "+Float.toString(ShipBodySprite.getRotation()));
		//this.body.setTransform((sprite.getX()+sprite.getRotationCenterX())/PIXEL_TO_METER_RATIO_DEFAULT,(sprite.getY()+sprite.getRotationCenterY())/PIXEL_TO_METER_RATIO_DEFAULT, rotation+shipBodySprite.getRotation()*(float)Math.PI/180);
		this.mBody.setTransform(shipPartCoordinates, rotation+shipBodySprite.getRotation()*(float)Math.PI/180);
		
		mSprite.setZIndex(-1);
		scene.sortChildren();
		mJoint=join(bodyToJoinWith,this.mBody);
		
		/*Filter filter=new Filter();
		filter.categoryBits=0x00000001;
		filter.maskBits=    0xFFFFFFFE;
		body.getFixtureList().get(0).setFilterData(filter);*/
		//-----------------------------------------------------
	}
	
	protected void setPosition(float shiftX,float shiftY, float rotation) {
		Vector2 ShiftPart=new Vector2();
		Vector2 shipPartCoordinates=Ship.setCoordinates(mShipBodySprite, mTextureRegion.getWidth(), mTextureRegion.getHeight(), shiftX, shiftY, ShiftPart);
		this.mBody.setTransform(shipPartCoordinates, rotation+mShipBodySprite.getRotation()*(float)Math.PI/180);
		this.mShiftX = shiftX;
		this.mShiftY = shiftY;
		this.mRotation = rotation;
	}
	
	protected Joint join(Body a, Body b) {
		mJointDef.initialize(a, b, a.getWorldCenter());
		mJointDef.collideConnected = false;
		return mPhysicsWorld.createJoint(mJointDef);
	 }
	 protected void unjoin() {
        if (mDestroyJoint)
        {
        	mPhysicsWorld.destroyJoint(mJoint);
        	mDestroyJoint=false;
        }
	 }
	 
	 public void checkJoints(Contact pContact) {
		 if (!mCrashed) {
			 float reactionForce=mJoint.getReactionForce(1).len();
			 //Log.i("info"," "+strength+" "+reactionForce);
         	 if (reactionForce>mStrength)
         	 {
         		mCrashed=true;
         		mDestroyJoint=true;
         	 }
		 }
	 }
	 
	 public void update() {
			if (mCrashed) unjoin();
	 }
}