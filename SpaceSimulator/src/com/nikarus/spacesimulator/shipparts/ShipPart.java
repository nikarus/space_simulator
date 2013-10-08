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
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.nikarus.spacesimulator.spacecraft.Ship;

class ShipPart {
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

	public ShipPart(SimpleBaseGameActivity context, Ship ship, float shiftX,float shiftY, float rotation, float strength, Body shipBody, Body bodyToJoinWith, Sprite shipBodySprite, Vector2[] vertices, Scene scene, PhysicsWorld physicsWorld, TextureRegion textureRegion, FixtureDef fixtureDef) {
		ShipPart.sContext=context;
		this.mShiftX=shiftX;
		this.mShiftY=shiftY;
		this.mRotation=rotation;
		this.mShipBody=shipBody;
		this.mShipBodySprite=shipBodySprite;
		this.mVertices=vertices;
		Vector2 ShiftPart=new Vector2();
		Vector2 shipPartCoordinates=Ship.setCoordinates(shipBodySprite, textureRegion.getWidth(), textureRegion.getHeight(), shiftX, shiftY, ShiftPart);
		this.mPhysicsWorld=physicsWorld;
		this.mScene=scene;
		this.mTextureRegion=textureRegion;
		this.mFixtureDef=fixtureDef;
		this.mStrength=strength;

		this.mSprite = new Sprite(ShiftPart.x, ShiftPart.y, this.mTextureRegion, context.getVertexBufferObjectManager());
		this.mBody = PhysicsFactory.createPolygonBody(this.mPhysicsWorld, this.mSprite, vertices, BodyType.DynamicBody, this.mFixtureDef);
		this.mScene.attachChild(this.mSprite);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(this.mSprite, this.mBody, true, true));
		this.mBody.setTransform(shipPartCoordinates, rotation+shipBodySprite.getRotation()*(float)Math.PI/180);

		mSprite.setZIndex(-1);
		scene.sortChildren();
		mJoint=join(bodyToJoinWith,this.mBody);
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