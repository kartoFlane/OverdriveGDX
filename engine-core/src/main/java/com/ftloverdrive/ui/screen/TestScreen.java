package com.ftloverdrive.ui.screen;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Scaling;
import com.ftloverdrive.blueprint.incident.ConsequenceDamageBlueprint;
import com.ftloverdrive.blueprint.incident.ConsequenceResourceBlueprint;
import com.ftloverdrive.blueprint.incident.IncidentBlueprint;
import com.ftloverdrive.blueprint.incident.PlotBranchBlueprint;
import com.ftloverdrive.blueprint.ship.TestShipBlueprint;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.game.GamePlayerShipChangeEvent;
import com.ftloverdrive.event.game.GamePlayerShipChangeListener;
import com.ftloverdrive.event.handler.DoorEventHandler;
import com.ftloverdrive.event.handler.EngineEventHandler;
import com.ftloverdrive.event.handler.GameEventHandler;
import com.ftloverdrive.event.handler.IncidentEventHandler;
import com.ftloverdrive.event.handler.LocalEventHandler;
import com.ftloverdrive.event.handler.ShipEventHandler;
import com.ftloverdrive.event.handler.SystemEventHandler;
import com.ftloverdrive.event.handler.TickEventHandler;
import com.ftloverdrive.event.incident.IncidentSelectionEvent;
import com.ftloverdrive.event.ship.ShipPropertyListener;
import com.ftloverdrive.event.system.SystemPropertyListener;
import com.ftloverdrive.event.system.SystemPropertySentinel;
import com.ftloverdrive.model.DefaultPlayerModel;
import com.ftloverdrive.model.PlayerModel;
import com.ftloverdrive.model.incident.requirement.ShipRequirement;
import com.ftloverdrive.ui.ShatteredImage;
import com.ftloverdrive.ui.hud.PlayerScrapMonitor;
import com.ftloverdrive.ui.hud.PlayerShipDoorHighlighter;
import com.ftloverdrive.ui.hud.PlayerShipHullMonitor;
import com.ftloverdrive.ui.hud.PlayerShipReactorUI;
import com.ftloverdrive.ui.hud.PlayerShipShieldMonitor;
import com.ftloverdrive.ui.ship.ShipActor;
import com.ftloverdrive.util.OVDConstants;


public class TestScreen extends BaseScreen {

	private TextureAtlas bgAtlas;

	private PlayerShipHullMonitor playerShipHullMonitor;
	private PlayerShipShieldMonitor playerShipShieldMonitor;
	private PlayerScrapMonitor playerScrapMonitor;
	private PlayerShipReactorUI playerShipReactor;
	private PlayerShipDoorHighlighter doorHighlighter;
	private ShipActor shipActor;


	public TestScreen( OverdriveContext srcContext ) {
		super( srcContext );

		// These layers are mainly notes. Many will probably be moved inside actors.
		Array<String> mainLayerNames = new Array<String>();
		mainLayerNames.add( "Background" );
		mainLayerNames.add( "BackgroundAccent" ); // Planet.
		mainLayerNames.add( "BackgroundDetail" ); // Asteroids, fleet, etc.
		mainLayerNames.add( "ShipShield" ); // Done: inside ShipActor
		mainLayerNames.add( "ShipWeapon" );
		mainLayerNames.add( "ShipGib" );
		mainLayerNames.add( "ShipBase" ); // Done: inside ShipActor
		mainLayerNames.add( "ShipFloor" ); // Done: inside ShipActor
		mainLayerNames.add( "ShipFloorSheen" ); // Oxygen stripes.
		mainLayerNames.add( "ShipRoomDecor" ); // Done: inside ShipActor
		mainLayerNames.add( "ShipRoomAccent" ); // Terminal.
		mainLayerNames.add( "ShipBreach" );
		mainLayerNames.add( "ShipFire" );
		mainLayerNames.add( "ShipSystemIcon" );
		mainLayerNames.add( "ShipPersonnelDot" ); // Sensor blip or walk target.
		mainLayerNames.add( "ShipPersonnel" );
		mainLayerNames.add( "ShipWall" ); // Done: inside ShipActor
		mainLayerNames.add( "ShipDoor" ); // Done: inside ShipActor
		mainLayerNames.add( "Satellite" ); // Flying drones.
		mainLayerNames.add( "Debris" ); // Crystal lockdown chunks. Explosions.
		for ( String layerName : mainLayerNames ) {
			Group tmpGroup = new Group();
			tmpGroup.setName( layerName );
			mainStage.getRoot().addActor( tmpGroup );
		}

		Array<String> hudLayerNames = new Array<String>();
		hudLayerNames.add( "Warning" ); // "Danger", "Intruders Detected" and "O2 Low".
		hudLayerNames.add( "UnitStatus" ); // Health bars over crew.
		hudLayerNames.add( "ShipDoorHighlight" ); // Done: PlayerShipDoorHighlighter
		hudLayerNames.add( "FloatyDamageNumber" ); // When hit.
		hudLayerNames.add( "Beam" );
		hudLayerNames.add( "Reticle" );
		hudLayerNames.add( "CtrlPanel" );
		for ( String layerName : hudLayerNames ) {
			Group tmpGroup = new Group();
			tmpGroup.setName( layerName );
			hudStage.getRoot().addActor( tmpGroup );
		}

		// Layers can be looked up with stage.getRoot().findActor( layerName );
		// A layer can be inserted like this...
		// Group stageRoot = stage.getRoot();
		// Group lowerLayer = (Group)stageRoot.findActor( layerName );
		// stageRoot.addActorAfter( lowerLayer, newGroup );

		context.getAssetManager().load( OVDConstants.BKG_ATLAS, TextureAtlas.class );
		context.getAssetManager().load( OVDConstants.ROOT_ATLAS, TextureAtlas.class );
		context.getAssetManager().load( OVDConstants.MISC_ATLAS, TextureAtlas.class );
		context.getAssetManager().load( OVDConstants.STATUSUI_ATLAS, TextureAtlas.class );
		context.getAssetManager().load( OVDConstants.EFFECTS_ATLAS, TextureAtlas.class );
		context.getAssetManager().load( OVDConstants.PEOPLE_ATLAS, TextureAtlas.class );
		context.getAssetManager().load( OVDConstants.PLOT_FONT, BitmapFont.class );

		context.getAssetManager().finishLoading();

		bgAtlas = context.getAssetManager().get( OVDConstants.BKG_ATLAS, TextureAtlas.class );
		ShatteredImage bgImage = new ShatteredImage( bgAtlas.findRegions( "bg-blueStarcluster" ), 5 );
		bgImage.setFillParent( true );
		Group bg = mainStage.getRoot().findActor( "Background" );
		bg.setSize( bgImage.getCompleteWidth(), bgImage.getCompleteHeight() );
		bg.addActor( bgImage );

		// TODO: Encapsulate HUD actors in a PlayerHUD class.
		playerShipHullMonitor = new PlayerShipHullMonitor( context );
		playerShipHullMonitor.setPosition( 0, hudStage.getHeight() - playerShipHullMonitor.getHeight() );
		hudStage.addActor( playerShipHullMonitor );

		playerShipShieldMonitor = new PlayerShipShieldMonitor( context );
		playerShipShieldMonitor.setPosition( 0,
				hudStage.getHeight() - playerShipHullMonitor.getHeight() - playerShipShieldMonitor.getHeight() );
		hudStage.addActor( playerShipShieldMonitor );

		playerScrapMonitor = new PlayerScrapMonitor( context );
		playerScrapMonitor.setPosition( playerShipHullMonitor.getWidth(),
				hudStage.getHeight() - playerScrapMonitor.getHeight() );
		hudStage.addActor( playerScrapMonitor );

		playerShipReactor = new PlayerShipReactorUI( context );
		playerShipReactor.setPosition( 45, 5 );
		hudStage.addActor( playerShipReactor );

		doorHighlighter = new PlayerShipDoorHighlighter( context );
		doorHighlighter.setVisible( false );
		hudStage.addActor( doorHighlighter );
		mainStage.addListener( doorHighlighter );

		shipActor = new ShipActor( context );
		// Ship's offset from the window's top left corner in FTL: X + 350, Y + 170
		// At this point, shipActor's height is 0...
		// shipActor.setPosition( 350, mainStage.getHeight() - shipActor.getHeight() - 170 );
		mainStage.addActor( shipActor );
		mainStage.addListener( shipActor );

		// Wire up the event manager...
		TickEventHandler tickHandler = new TickEventHandler();
		for ( Class c : tickHandler.getEventClasses() )
			eventManager.setEventHandler( c, tickHandler );

		EngineEventHandler engineHandler = new EngineEventHandler();
		for ( Class c : engineHandler.getEventClasses() )
			eventManager.setEventHandler( c, engineHandler );

		LocalEventHandler localHandler = new LocalEventHandler();
		for ( Class c : localHandler.getEventClasses() )
			eventManager.setEventHandler( c, localHandler );

		GameEventHandler gameHandler = new GameEventHandler();
		for ( Class c : gameHandler.getEventClasses() )
			eventManager.setEventHandler( c, gameHandler );

		ShipEventHandler shipHandler = new ShipEventHandler();
		for ( Class c : shipHandler.getEventClasses() )
			eventManager.setEventHandler( c, shipHandler );

		DoorEventHandler doorHandler = new DoorEventHandler();
		for ( Class c : doorHandler.getEventClasses() )
			eventManager.setEventHandler( c, doorHandler );

		SystemEventHandler systemHandler = new SystemEventHandler();
		for ( Class c : systemHandler.getEventClasses() )
			eventManager.setEventHandler( c, systemHandler );

		IncidentEventHandler incHandler = new IncidentEventHandler();
		for ( Class c : incHandler.getEventClasses() )
			eventManager.setEventHandler( c, incHandler );

		eventManager.addEventListener( playerShipHullMonitor, GamePlayerShipChangeListener.class );
		eventManager.addEventListener( playerShipHullMonitor, ShipPropertyListener.class );
		eventManager.addEventListener( playerShipShieldMonitor, GamePlayerShipChangeListener.class );
		eventManager.addEventListener( playerShipShieldMonitor, ShipPropertyListener.class );
		eventManager.addEventListener( playerScrapMonitor, GamePlayerShipChangeListener.class );
		eventManager.addEventListener( playerScrapMonitor, ShipPropertyListener.class );
		eventManager.addEventListener( playerShipReactor, GamePlayerShipChangeListener.class );
		eventManager.addEventListener( playerShipReactor, ShipPropertyListener.class );
		eventManager.addEventListener( playerShipReactor, SystemPropertyListener.class );
		eventManager.addEventListener( playerShipReactor, SystemPropertySentinel.class );

		eventManager.addEventListener( shipActor, GamePlayerShipChangeListener.class );
		eventManager.addEventListener( shipActor, ShipPropertyListener.class );
		eventManager.addEventListener( shipActor, SystemPropertyListener.class );
		eventManager.addTickListener( 1, shipActor );

		// XXX: Periodic events can be registered like this...
		// eventManager.addTickListener( numberOfTicks, tickListener );

		int playerRefId = context.getNetManager().requestNewRefId();
		PlayerModel playerModel = new DefaultPlayerModel();
		context.getReferenceManager().addObject( playerModel, playerRefId );
		context.getNetManager().setLocalPlayerRefId( playerRefId );

		// Create a test ship.

		int shipRefId = new TestShipBlueprint( null ).construct( context );

		int gameRefId = context.getGameModelRefId();
		// Set it as the player's ship.
		GamePlayerShipChangeEvent shipChangeEvent = Pools.get( GamePlayerShipChangeEvent.class ).obtain();
		shipChangeEvent.init( gameRefId, playerRefId, shipRefId );
		eventManager.postDelayedEvent( shipChangeEvent );

		incidentWindowDemo();

		resize( getScreenWidth(), getScreenHeight() );
	}

	private void incidentWindowDemo() {
		String loremIpsum = "This window is draggable.\n\n" +
				"This window intercepts all user input while it is visible. " +
				"Click on the choice below (or press corresponding number key) to dismiss the window.";

		// TODO: Blueprints would normally be created and registered by parsing the data files
		IncidentBlueprint incBlueprint = new IncidentBlueprint( "TEST_INCIDENT_1" );
		incBlueprint.setTextTemplate( loremIpsum );

		PlotBranchBlueprint branchBlueprint = new PlotBranchBlueprint( "TEST_INCIDENT_2",
				"Go to event 2. Also a branch with a very long selection text that hopefully will wrap correctly." );
		incBlueprint.addPlotBranch( branchBlueprint );
		branchBlueprint = new PlotBranchBlueprint( "TEST_INCIDENT_3", "(Test Ship) Need moar scrap!" );
		branchBlueprint.addRequirement( new ShipRequirement( TestShipBlueprint.class.getSimpleName() ) );
		incBlueprint.addPlotBranch( branchBlueprint );
		incBlueprint.addPlotBranch( new PlotBranchBlueprint() );

		context.getBlueprintManager().storeBlueprint( "TEST_INCIDENT_1", incBlueprint );
		int incRefId = incBlueprint.construct( context );

		incBlueprint = new IncidentBlueprint( "TEST_INCIDENT_2" );
		incBlueprint.setTextTemplate( "This is a looped event!" );

		incBlueprint.addConsequence( new ConsequenceResourceBlueprint( OVDConstants.FUEL, 0, 3 ) );
		incBlueprint.addConsequence( new ConsequenceResourceBlueprint( OVDConstants.MISSILES, 0, 1 ) );
		incBlueprint.addConsequence( new ConsequenceResourceBlueprint( OVDConstants.DRONES, 0, 1 ) );
		incBlueprint.addConsequence( new ConsequenceResourceBlueprint( OVDConstants.SCRAP, 15, 30 ) );
		incBlueprint.addConsequence( new ConsequenceDamageBlueprint( 1, 5 ) );

		branchBlueprint = new PlotBranchBlueprint( "TEST_INCIDENT_1", "Go to event 1" );
		incBlueprint.addPlotBranch( branchBlueprint );

		context.getBlueprintManager().storeBlueprint( "TEST_INCIDENT_2", incBlueprint );

		incBlueprint = new IncidentBlueprint( "TEST_INCIDENT_3" );
		incBlueprint.setTextTemplate( "Requirement test" );
		incBlueprint.addConsequence( new ConsequenceResourceBlueprint( OVDConstants.FUEL, 1, 3 ) );
		incBlueprint.addConsequence( new ConsequenceResourceBlueprint( OVDConstants.SCRAP, -50, -60 ) );
		incBlueprint.addPlotBranch( branchBlueprint );
		context.getBlueprintManager().storeBlueprint( "TEST_INCIDENT_3", incBlueprint );

		if ( context.getGame().isServer() ) {
			IncidentSelectionEvent incSelectionE = Pools.get( IncidentSelectionEvent.class ).obtain();
			incSelectionE.init( context, incRefId );
			context.getScreenEventManager().postDelayedEvent( incSelectionE );
		}
	}

	@Override
	public void resize( int width, int height ) {
		hudStage.getViewport().update( width, height, true );
		mainStage.getViewport().update( width, height, true );
		popupStage.getViewport().update( width, height, true );

		Group bg = mainStage.getRoot().findActor( "Background" );
		Vector2 scaled = Scaling.fill.apply( bg.getWidth(), bg.getHeight(), width, height );
		bg.setScale( Math.min( scaled.x / bg.getWidth(), scaled.y / bg.getHeight() ) );

		// TODO: Re-layout Stages.

		// HUD
		playerShipHullMonitor.setPosition( 0, hudStage.getHeight() - playerShipHullMonitor.getHeight() );
		playerScrapMonitor.setPosition( playerShipHullMonitor.getWidth(),
				hudStage.getHeight() - playerScrapMonitor.getHeight() );
		playerShipShieldMonitor.setPosition( 0,
				hudStage.getHeight() - playerShipHullMonitor.getHeight() - playerShipShieldMonitor.getHeight() + 22 );
		// Main
		shipActor.setPosition( 350, mainStage.getHeight() - shipActor.getHeight() - 170 );

		// SpriteBatches get resized to match the new aspect ratio,
		// need to counteract this.
		// http://stackoverflow.com/questions/14085212/libgdx-framebuffer-scaling
		// projMatrix = new Matrix4();
		// projMatrix.setToOrtho2D( 0, 0, width, height );
		// batch.setProjectionMatrix( projMatrix );
	}

	@Override
	public void render( float delta ) {
		super.render( delta );

		mainStage.act( delta );
		mainStage.draw();

		hudStage.act( delta );
		hudStage.draw();

		popupStage.act( delta );
		popupStage.draw();

		renderedPreviousFrame = true;
	}

	@Override
	public void dispose() {
		hudStage.dispose();
		playerShipHullMonitor.dispose();
		playerScrapMonitor.dispose();
		shipActor.dispose();
		context.getAssetManager().unload( OVDConstants.BKG_ATLAS );
		context.getAssetManager().unload( OVDConstants.ROOT_ATLAS );
		context.getAssetManager().unload( OVDConstants.MISC_ATLAS );
		context.getAssetManager().unload( OVDConstants.PEOPLE_ATLAS );
		context.getAssetManager().unload( OVDConstants.PLOT_FONT );
		Pools.get( OverdriveContext.class ).free( context );
	}

	@Override
	public int getScreenWidth() {
		return (int)mainStage.getWidth();
	}

	@Override
	public int getScreenHeight() {
		return (int)mainStage.getHeight();
	}
}
