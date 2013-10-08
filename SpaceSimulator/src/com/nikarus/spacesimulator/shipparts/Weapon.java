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
				if (mShip==(Ship.mShips.get(Ship.sHumanControlledIndex))) {
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