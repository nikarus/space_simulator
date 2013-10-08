package com.nikarus.spacesimulator.shipparts;

import java.util.ArrayList;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.nikarus.spacesimulator.spacecraft.Ship;

public class Weapon extends ShipPart {

	public boolean mWeaponIsShooting=false;

	private final Ship mShip;
	private final int mReloadTime;
	private final float mShellMass;
	private boolean mIsLoaded=false;
	private long mShootTime=0;
	private final ArrayList<Missile> mShells = new ArrayList<Missile>();
	private final boolean mIsRocketLauncher;

	private final float mShellShiftX;
	private final float mShellShiftY;
	private final int mWeaponForce;

	private final Vector2[] mShellVertices;

	private final TiledTextureRegion mShellFireTextureRegion;
	private final TextureRegion mShellTextureRegion;
	private final FixtureDef mShellFixtureDef;
	private final float mRotation;
	private final float mShellFireShiftX;
	private final float mShellFireShiftY;
	private final float mShellFireScale;

	private Missile mLoadedShell;
	public Weapon(Ship ship, float weaponShiftX, float weaponShiftY, float shellShiftX, float shellShiftY, float rotation, int weaponForce, boolean isRocketLauncher, float shellMass, int reloadTime, float strength, Body shipBody, Sprite shipBodySprite, Vector2[] weaponVertices, Vector2[] shellVertices, Scene scene, PhysicsWorld physicsWorld, TiledTextureRegion shellFireTextureRegion, float shellFireShiftX, float shellFireShiftY, float shellFireScale, TextureRegion weaponTextureRegion, TextureRegion shellTextureRegion, FixtureDef weaponFixtureDef, FixtureDef shellFixtureDef)
	{
		super(sContext, ship, weaponShiftX, weaponShiftY, rotation, strength, shipBody, shipBody, shipBodySprite, weaponVertices, scene, physicsWorld, weaponTextureRegion, weaponFixtureDef);

		this.mReloadTime=reloadTime;
		this.mShellMass=shellMass;
		this.mIsRocketLauncher=isRocketLauncher;
		this.mShip=ship;

		this.mShellFireTextureRegion=shellFireTextureRegion;
		this.mShellTextureRegion=shellTextureRegion;
		this.mShellFixtureDef=shellFixtureDef;
		this.mWeaponForce=weaponForce;
		this.mShellShiftX=shellShiftX;
		this.mShellShiftY=shellShiftY;
		this.mShellVertices=shellVertices;
		this.mRotation=rotation;
		this.mShellFireShiftX=shellFireShiftX;
		this.mShellFireShiftY=shellFireShiftY;
		this.mShellFireScale=shellFireScale;

		mLoadedShell = new Missile(sContext, ship, this, shellShiftX, shellShiftY, rotation, weaponForce, isRocketLauncher, shellMass, reloadTime, mStrength, mShipBody, mShipBodySprite, shellVertices, scene, physicsWorld, shellFireTextureRegion, shellFireShiftX, shellFireShiftY, shellFireScale, shellTextureRegion, shellFixtureDef);
		mShells.add(mLoadedShell);
		mIsLoaded=true;

	}

	public Missile obtainShell() {
		/*for (Missile m:mShells) {// Couldn't do this optimization for rocket luncher. Got problems with setting its position. Left it for now.
			if (m.mIsMissileDetonated) {
				m.resetMissileParameters(mShellShiftX, mShellShiftY, mRotation);
				return m;
			}
		}*/
		Missile m = new Missile(sContext, mShip, this, mShellShiftX, mShellShiftY, mRotation, mWeaponForce, mIsRocketLauncher, mShellMass, mReloadTime, mStrength, mShipBody, mShipBodySprite, mShellVertices, mScene, mPhysicsWorld, mShellFireTextureRegion, mShellFireShiftX, mShellFireShiftY, mShellFireScale, mShellTextureRegion, mShellFixtureDef);
		mShells.add(m);
		Log.i("info", "SHELL ADDED. SIZE: "+mShells.size());
		return m;
	}

	public void weaponStartShooting() {
		if (!mCrashed) {
			mWeaponIsShooting=true;
		}
	}
	public void weaponStopShooting() {
		mWeaponIsShooting=false;
	}
	@Override
	public void update() {
		super.update();
		if (!mIsLoaded && !mCrashed) {
			if (System.currentTimeMillis()-mShootTime>mReloadTime) {
				mLoadedShell = obtainShell();
				//mShells.add(new Missile(context, Ship, this, ShellShiftX, ShellShiftY, Rotation, WeaponForce, isMissile, ShellMass, ReloadTime, strength, shipBody, shipBodySprite, shellVertices, mScene, mPhysicsWorld, shellFireTextureRegion, shellFireShiftX, shellFireShiftY, shellFireScale, shellTextureRegion, shellFixtureDef));
				mIsLoaded=true;
			}
		}

		if (mWeaponIsShooting) {
			if (mIsLoaded) {
				double k = Math.tan(mShip.mWeapon.get(0).mBody.getAngle()+Math.PI/2);

				Vector2 weaponPosition = mShip.mWeapon.get(0).mBody.getWorldCenter();
				//EQUATION ONE:
				//kX + (-1)Y + k*weaponPosition.x+weaponPosition.y = 0
				Vector2 enemyPosition = null;
				Vector2 enemySpeed = null;
				Vector2 enemySpeedLocal = null;
				for (Ship ship: Ship.mShips) {
					if (ship.mTrainingBoxID==mShip.mTrainingBoxID && ship!=mShip) {
						enemyPosition = ship.mShipBody.getWorldCenter();
						enemySpeedLocal = ship.mShipBody.getLinearVelocity();
						enemySpeed = new Vector2();
						enemySpeed.x = enemyPosition.x+enemySpeedLocal.x;
						enemySpeed.y = enemyPosition.y+enemySpeedLocal.y;
					}
				}



				//EQUATION TWO:
				//(enemySpeed.y-enemyPosition.y)X+(enemyPosition.x-enemySpeed.x)Y+(enemySpeed.x*enemyPosition.y-enemyPosition.x*enemySpeed.y) = 0

				//Intersection equation:
				//x = (b1*c2-b2*c1)/(a1*b2-a2*b2)
				//y = (a2*c1-a1*c2)/(a1*b2-a2*b1)

				double a1 = k;
				double a2 = (enemySpeed.y-enemyPosition.y);
				double b1 = -1;
				double b2 = (enemyPosition.x-enemySpeed.x);
				double c1 = -k*weaponPosition.x+weaponPosition.y;
				double c2 = (enemySpeed.x*enemyPosition.y-enemyPosition.x*enemySpeed.y);

				Vector2 intersection = new Vector2();
				intersection.x = (float) ((b1*c2-b2*c1)/(a1*b2-a2*b2));
				intersection.y = (float) ((a2*c1-a1*c2)/(a1*b2-a2*b1));

				/*Log.i("info", "k: "+k+" angle: "+Ship.weapon.get(0).body.getAngle()+Math.PI/2);
				Log.i("info", "wX = " + weaponPosition.x + " wY = "+ weaponPosition.y);
				Log.i("info", "eX = " + enemyPosition.x + " eY = "+ enemyPosition.y);
				Log.i("info", "esX = " + enemySpeed.x + " esY = "+ enemySpeed.y);
				Log.i("info", "iX = " + intersection.x + " iY = "+ intersection.y);*/

				double current_intersection_distance = mShip.mWeapon.get(0).mBody.getWorldCenter().dst(intersection);
				double enemy_intersection_distance = enemyPosition.dst(intersection);

				double missileSpeed = (mLoadedShell.mEngineForce/mLoadedShell.mMissileMass)/15;
				double enemyScalarSpeed = enemySpeed.len();

				double missileTime = current_intersection_distance/missileSpeed;
				double enemyTime = enemy_intersection_distance/enemyScalarSpeed;

				if (mShip==(Ship.mShips.get(Ship.sHumanControlledIndex)))
						{
					//Log.i("info", "current_intersection_distance = " + current_intersection_distance + " enemy_intersection_distance = "+ enemy_intersection_distance);
					//Log.i("info", "missileSpeed = " + missileSpeed + " enemyScalarSpeed = "+ enemyScalarSpeed);
					//Log.i("info", "missileTime = " + missileTime + " enemyTime = "+ enemyTime);

					if (Math.abs(missileTime-enemyTime)<0.5) {
						//Log.i("info", "HIT HIT HIT!!!");
						//Ship.fitness = Ship.fitness+1;
					}

					mLoadedShell.shoot();
						}
				mShootTime=System.currentTimeMillis();
				mIsLoaded=false;
			}
		}

		for (int i=0;i < mShells.size();i++)  mShells.get(i).missileUpdate(i);
	}

	public void checkShellDetonation(Contact pContact) {
		for (int i=0;i < mShells.size();i++)  mShells.get(i).missileCheckDetonation(pContact);
	}
}