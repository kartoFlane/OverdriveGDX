package com.ftloverdrive.core;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.utils.Pool.Poolable;

import com.ftloverdrive.core.OverdriveGame;
import com.ftloverdrive.event.OVDEventManager;
import com.ftloverdrive.model.GameModel;
import com.ftloverdrive.script.OVDScriptManager;
import com.ftloverdrive.ui.screen.OVDScreenManager;
import com.ftloverdrive.ui.screen.OVDStageManager;


/**
 * A means to pass the numerous managers around, especially to callbacks.
 *
 * TODO: Decide how best to use this thing.
 *
 * This should be preferred over globals.
 * It's not necessary to set every value.
 *
 * It might make sense for an object of limited scope to take context as an
 * arg in its constructor and remember it. But if possible, individual
 * methods should never cache an old context passed to them.
 *
 * Context objects can be created by a Pool for reuse, but it will not be
 * safe to free() them until they're definitely forgotten.
 *
 * @see com.badlogic.gdx.utils.Pool
 * @see com.badlogic.gdx.utils.Pool.Poolable
 */
public class OverdriveContext implements Poolable {

	protected OverdriveGame game = null;
	protected FileHandleResolver resolver = null;
	protected AssetManager assetManager = null;
	protected OVDScreenManager screenManager = null;

	protected OVDEventManager screenEventManager = null;
	protected OVDScriptManager screenScriptManager = null;
	protected OVDStageManager screenStageManager = null;

	protected GameModel gameModel = null;


	/**
	 * This constructor must take no args, and only set up variables.
	 * Call init() afterward, as if that were the normal constructor.
	 *
	 * @see com.badlogic.gdx.utils.ReflectionPool
	 */
	public OverdriveContext() {
	}

	/**
	 * Pseudo constructor for new and reused objects.
	 * This is capable of accepting args to set initial values.
	 *
	 * Always call this after obtaining a reused object from a Pool.
	 * Subclasses overriding this must call super.init();
	 */
	public void init() {
	}

	/**
	 * Sets the OverdriveGame and related values.
	 * Resolver, AssetManager, and ScreenManager.
	 */
	public void init( OverdriveGame game ) {
		this.game = game;
		this.resolver = game.getFileHandleResolver();
		this.assetManager = game.getAssetManager();
		this.screenManager = game.getScreenManager();
	}

	/**
	 * Copies values from an existing context.
	 * If anything later decides to modify this context, the original won't
	 * be affected.
	 */
	public void init( OverdriveContext context ) {
		this.game = context.getGame();
		this.resolver = context.getFileHandleResolver();
		this.assetManager = context.getAssetManager();
		this.screenManager = context.getScreenManager();

		this.screenEventManager = null;
		this.screenScriptManager = null;
		this.screenStageManager = null;

		this.gameModel = null;
	}


	public void setGame( OverdriveGame game ) {
		this.game = game;
	}

	public OverdriveGame getGame() {
		return game;
	}


	public void setFileHandleResolver( FileHandleResolver resolver ) {
		this.resolver = resolver;
	}

	public FileHandleResolver getFileHandleResolver() {
		return resolver;
	}


	public void setAssetManager( AssetManager assetManager ) {
		this.assetManager = assetManager;
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}


	public void setScreenManager( OVDScreenManager screenManager ) {
		this.screenManager = screenManager;
	}

	public OVDScreenManager getScreenManager() {
		return screenManager;
	}


	public void setScreenEventManager( OVDEventManager eventManager ) {
		this.screenEventManager = eventManager;
	}

	public OVDEventManager getScreenEventManager() {
		return screenEventManager;
	}


	public void setScreenScriptManager( OVDScriptManager scriptManager ) {
		this.screenScriptManager = scriptManager;
	}

	public OVDScriptManager getScreenScriptManager() {
		return screenScriptManager;
	}


	public void setScreenStageManager( OVDStageManager stageManager ) {
		this.screenStageManager = stageManager;
	}

	public OVDStageManager getScreenStageManager() {
		return screenStageManager;
	}


	public void setGameModel( GameModel gameModel ) {
		this.gameModel = gameModel;
	}

	public GameModel getGameModel() {
		return gameModel;
	}


	/**
	 * Resets this object for Pool reuse, setting all variables to defaults.
	 * This is automatically called by the Pool's free() method.
	 *
	 * @see com.badlogic.gdx.utils.Pool
	 * @see com.badlogic.gdx.utils.Pool.Poolable
	 */
	@Override
	public void reset() {
		this.game = null;
		this.resolver = null;
		this.assetManager = null;
		this.screenManager = null;

		this.screenEventManager = null;
		this.screenScriptManager = null;
		this.screenStageManager = null;

		this.gameModel = null;
	}
}