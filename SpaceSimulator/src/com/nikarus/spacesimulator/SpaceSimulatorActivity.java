package com.nikarus.spacesimulator;


import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.controller.MultiTouch;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.nikarus.spacesimulator.spacecraft.Ship;

public class SpaceSimulatorActivity extends SimpleBaseGameActivity{
	private static final int CAMERA_WIDTH = 800;
	private static final int CAMERA_HEIGHT = 480;
	private Scene mScene;
	private PhysicsWorld mPhysicsWorld;
	private final Touch mTouches [] = {new Touch(),new Touch(),new Touch(),new Touch(),new Touch(),new Touch(),new Touch(),new Touch(),new Touch(),new Touch()};

	@Override
	public EngineOptions onCreateEngineOptions() {
		Log.i("info","onLoadEngine Started");

		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);
		final Engine engine = /*new LimitedFPSEngine(engineOptions,60);*/ new Engine(engineOptions);

		if(!MultiTouch.isSupported(this)) {
			Toast.makeText(this, "Sorry your device does NOT support MultiTouch!", Toast.LENGTH_LONG).show();
			finish();
		}

		return engine.getEngineOptions();
	}

	@Override
	protected void onCreateResources() {
		Log.i("info","onLoadResources Started");
		/* Textures. */
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
	}

	@Override
	protected Scene onCreateScene() {
		Log.i("info","onLoadScene Started");
		this.mScene = new Scene();
		this.mScene.setScale(Ship.mScale, Ship.mScale);
		//this.mScene.setOnSceneTouchListener(this);
		//this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
		this.mPhysicsWorld = new FixedStepPhysicsWorld(100, new Vector2(0f, 0f), false, 1, 1);
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		SpaceSimulatorActivity.this.mScene.registerUpdateHandler(getCollisionUpdateHandler());

		Map map=new Map();
		//map.CreateMetalMap(mEngine, mScene, mPhysicsWorld, this);
		map.CreateAsteroidMap(mEngine, mScene, mPhysicsWorld, SpaceSimulatorActivity.this, 0);
		//map.CreateTrainingBoxesMap(mEngine, mScene, mPhysicsWorld, SpaceSimulatorActivity.this);

		Ship.mShips.get(0).mIsCameraTraced=true;
		Ship.mShips.get(0).mIsHumanControlled=true;

		mScene.setOnSceneTouchListener(new IOnSceneTouchListener() {
			@Override
			public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {

				int id=pSceneTouchEvent.getPointerID();
				if (pSceneTouchEvent.isActionUp()) {
					mTouches[id].Clear();
				} else {
					mTouches[id].Touched(id, pSceneTouchEvent.getX()+CAMERA_WIDTH/2-mEngine.getCamera().getCenterX(), pSceneTouchEvent.getY()+CAMERA_HEIGHT/2-mEngine.getCamera().getCenterY());
				}

				for (Ship ship: Ship.mShips) ship.control(mTouches);
				return true;
			}
		});

		mPhysicsWorld.setContactListener(new ContactListener() {
			@Override
			public void beginContact(final Contact pContact) {
			}
			@Override
			public void endContact(Contact contact) {
			}
			@Override
			public void preSolve(Contact pContact, Manifold oldManifold) {
				for (Ship ship: Ship.mShips) ship.checkContacts(pContact);
			}
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				for (Ship ship: Ship.mShips) ship.checkContactsPostSolve(contact);
			}
		});

		return this.mScene;
	}

	public IUpdateHandler getCollisionUpdateHandler(){
		return new IUpdateHandler(){
			@Override
			public void onUpdate(float pSecondsElapsed) {
				for (Ship ship: Ship.mShips) ship.updateShip();
			}
			@Override
			public void reset() {
			}
		};
	}

	public boolean isAreaTouched(int leftTopX, int leftTopY, int rightBotomX, int rightBottomY) {
		for (int i=0; i<mTouches.length; i++)
			if (mTouches[i].x>leftTopX && mTouches[i].x<rightBotomX && mTouches[i].y>leftTopY && mTouches[i].y<rightBottomY)
				return true;
		return false;
	}

	@Override
	public void onResumeGame() {
		super.onResumeGame();
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();
	}

}
