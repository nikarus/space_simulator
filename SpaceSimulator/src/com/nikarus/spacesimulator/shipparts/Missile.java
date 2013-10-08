package com.nikarus.spacesimulator.shipparts;

import java.util.Iterator;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.nikarus.spacesimulator.spacecraft.Ship;

class Missile extends ShipEngine {
	public float mMissileMass;
	private boolean mIsMissileLaunched=false;
	public boolean mIsMissileDetonated=false;
	private Weapon mWeapon;
	private boolean mIsRocket;
	private Ship mShip;
	public boolean mIsFiring=false;
	public float mOldMass;
	public float mOldStrength;
	private long mMissileShootTime=0;
	
	public Missile(SimpleBaseGameActivity context, Ship ship, Weapon weapon, float engineShiftX, float engineShiftY, float rotation, int engineForce, boolean isRocket, float MissileMass, int ReloadTime, float strength, Body shipBody, Sprite shipBodySprite, Vector2[] vertices, Scene scene, PhysicsWorld physicsWorld, TiledTextureRegion engineFireTextureRegion, float engineFireShiftX, float engineFireShiftY, float engineFireScale, TextureRegion engineTextureRegion, FixtureDef engineFixtureDef) {
		super(context, ship, engineShiftX, engineShiftY, rotation, engineForce, strength, shipBody, weapon.mBody, shipBodySprite, vertices, scene, physicsWorld, engineFireTextureRegion, engineFireShiftX, engineFireShiftY, engineFireScale, engineTextureRegion, engineFixtureDef, isRocket);
		this.mMissileMass=MissileMass;
		this.mIsRocket=isRocket;
		this.mWeapon=weapon;
		this.mShip=ship;
		mSprite.setZIndex(-2);
		scene.sortChildren();
		if (!isRocket) mSprite.setVisible(false);
		else  mSprite.setVisible(true);
		
		Filter filter=new Filter();
		filter.categoryBits=0x00000001;
		filter.maskBits=    0xFFFFFFFE;
		mBody.getFixtureList().get(0).setFilterData(filter);
		
		mOldMass=mBody.getMassData().mass; //We change the mass of the Missile when we launch it. So we need to save initial value for case when we want to reuse same Missile object
		mOldStrength = this.mStrength;
	}
	public void shoot() {
		this.mBody.setAngularVelocity(0);
		mBody.setFixedRotation(true);
		mBody.setBullet(true);
		this.mStrength=0;
		
		Filter filter=new Filter();
		filter.categoryBits=0x00000001;
		filter.maskBits=    0xFFFFFFFF;
		mBody.getFixtureList().get(0).setFilterData(filter);
		
		//Ship.updateShipFixture(0x00000001, 0xFFFFFFFF);
		mBody.setLinearVelocity(mShipBody.getLinearVelocity());

		//this.unjoin();
		mPhysicsWorld.destroyJoint(mJoint);
		
		if (mIsRocket) {
			mPhysicsWorld.destroyJoint(mEngineFireJoint);
			join(mBody, this.mEngineFireBody);
		}
		engineStart();
		mMissileShootTime = System.currentTimeMillis();
		if (!mIsRocket) {
			mSprite.setVisible(true);
			update();
			engineStop();
		}
		
		MassData missileMassData = new MassData();
		missileMassData.mass=mMissileMass;
		mBody.setMassData(missileMassData);

		mIsMissileLaunched=true;
	 }
	
	public void resetMissileParameters(float x, float y, float rotation){
		mMissileShootTime=0;
		
		Filter filter=new Filter();
		filter.categoryBits=0x00000001;
		filter.maskBits=    0xFFFFFFFE;
		mBody.getFixtureList().get(0).setFilterData(filter);
		mIsMissileDetonated=false;
		mIsMissileLaunched=false;
		mBody.setFixedRotation(false);
		mBody.setBullet(false);
		
		MassData missileMassData = new MassData();
		missileMassData.mass=0;
		mBody.setMassData(missileMassData);
		
		setPosition(x, y, rotation);
		
		mJoint=join(mWeapon.mBody,mBody);
		
		mBody.setActive(true);
		
		missileMassData = new MassData();
		missileMassData.mass=mOldMass;
		mBody.setMassData(missileMassData);
		
		if (!mIsRocket) mSprite.setVisible(false);
		else mSprite.setVisible(true);
		this.mStrength=mOldStrength;
	}
	public void stop() {
		mIsFiring=false;
	}
	
	public void missileUpdate(int missileIndex) {
		Log.i("info", ""+missileIndex+" mIsMissileDetonated "+mIsMissileDetonated);
		if (mIsMissileDetonated) {
			//Log.i("info", ""+missileIndex);
			engineStop();
			//mScene.setVisible(false);
			mSprite.setVisible(false);
			mBody.setActive(false);
    		//mScene.detachChild(sprite);
    		//this.mPhysicsWorld.destroyBody(body);
    		//weapon.Shells.remove(missileIndex);
    		//System.gc();
    		//Ship.updateShipFixture(0x00000002, 0xFFFFFFFF);
			return;
		}
		update();
		//if (!Ship.checkSpriteCollision(sprite)) {
		//}
	 }
	
	public void missileCheckDetonation(Contact pContact) {
		
		//EngineCheckJoints(pContact);
		if (mIsMissileDetonated) return;
		if (mMissileShootTime!=0 && System.currentTimeMillis()-mMissileShootTime>20000) mIsMissileDetonated=true;//If a missile flies out for a very long distance without hitting anything, we detondate it.
		if (mIsMissileLaunched && (pContact.getFixtureA()==mBody.getFixtureList().get(0) || pContact.getFixtureB()==mBody.getFixtureList().get(0))) {
			//Increase fitness
			/*for (int i=0;i<Ship.mShip.length; i++) {
				//if (((cShip)cShip.Ship[i]) != Ship) {
					for (int j=0;j<((Ship)Ship.mShip[i]).mShipBodyList.size();j++) {
						Body b = ((Ship)Ship.mShip[i]).mShipBodyList.get(j);
						if (pContact.getFixtureA()==b.getFixtureList().get(0) || pContact.getFixtureB()==b.getFixtureList().get(0)) {
							mShip.mFitness = mShip.mFitness+1;
							//Log.i("info","CONTACT"+Ship.fitness);
						}
					}
				//}
			}*/
			
			if (mIsRocket) explode(mBody.getPosition(), 8, 10,50);
			mIsMissileDetonated = true;
			Log.i("info", "Missile detonated");
		}
	 }
	
	private void explode(Vector2 explosionPosition, float radius, float explosionPower, float damping) {
		//Log.i("info","EXPLOSION: ");
		
		
		Body body;
		/*for (int i=0;i < cShip.Ship.length;i++) {
			body=((cShip) cShip.Ship[i]).ShipBody;
			float d=explosionPosition.dst(body.getPosition());
			if (d<=r) {
				Vector2 explosionDirection=body.getPosition().sub(explosionPosition).cpy();
				explosionDirection.set(explosionDirection.mul((explosionPower/d)/(explosionDirection.len()/100)));
				//Log.i("info", ""+i+" "+body.getPosition().x + " "+explosionDirection.len()+" "+explosionDirection.x+" "+explosionDirection.y);
				//body.applyForce(explosionDirection, body.getPosition());
				body.applyAngularImpulse((explosionPower/d)/(explosionDirection.len()/100));
				body.applyLinearImpulse(explosionDirection, body.getPosition());
			}
		}*/
		Iterator<Body> iterator = mPhysicsWorld.getBodies();
		
		while (iterator.hasNext()) {
			body=iterator.next();
			float d=explosionPosition.dst(body.getPosition());
			if (d<=radius) {
				float a = (float) ((Math.sqrt(Math.pow(explosionPower, 2)*Math.pow(radius, 2)+4*explosionPower*radius*damping)-explosionPower*radius)/(2*radius));
				float b = damping/(a+radius);
				float appliedExplosionForce = damping/(d+a)-b;
				Vector2 explosionDirection=body.getPosition().sub(explosionPosition).cpy();
				explosionDirection.set(explosionDirection.mul(appliedExplosionForce));
				//Log.i("info", ""+i+" "+body.getPosition().x + " "+explosionDirection.len()+" "+explosionDirection.x+" "+explosionDirection.y);
				//body.applyForce(explosionDirection, body.getPosition());
				body.applyAngularImpulse(appliedExplosionForce);
				body.applyLinearImpulse(explosionDirection, body.getPosition());
			}
		}
		
	}
}