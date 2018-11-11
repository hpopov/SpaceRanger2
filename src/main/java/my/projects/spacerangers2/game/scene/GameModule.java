package my.projects.spacerangers2.game.scene;

import java.util.concurrent.Semaphore;
import javafx.scene.layout.Pane;
import my.projects.spacerangers2.game.concurrent.AimableModifiableList;
import my.projects.spacerangers2.game.concurrent.GameLevelSynchronizer;
import my.projects.spacerangers2.game.entities.SpaceEntityCreator;
import my.projects.spacerangers2.game.geometry.Vector2D;
import my.projects.spacerangers2.game.objects.SpaceObjectBuilder;

public abstract class GameModule {
	private Semaphore buildSynchronizer;
	private volatile BuildState buildState;
	protected SpaceEntityCreator entityBuilder;
	protected Vector2D sceneSize;
	
	public GameModule(Vector2D sceneSize, SpaceEntityCreator entityBuilder) {
		buildSynchronizer = new Semaphore(0);
		buildState = BuildState.NOT_STARTED;
		this.entityBuilder = entityBuilder;
		this.sceneSize = sceneSize;
	}
	
	public final void buildInBackground() {
		buildState = BuildState.IN_PROCESS;
		Thread t = new Thread(() -> build());
		t.setDaemon(true);
		t.start();
	}
	
	public final void build() {
		buildLogic();
		buildState = BuildState.ACCOMPLISHED;
		buildSynchronizer.release();
	}
	
	protected abstract void buildLogic();
	
	public final void execute() {
		switch(buildState) {

		case NOT_STARTED:
			buildLogic();
			executeLogic();
			break;
		case IN_PROCESS:
		case ACCOMPLISHED:
			try {
				buildSynchronizer.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			executeLogic();
			buildState = BuildState.NOT_STARTED;
			break;
		default:
			break;
		}
	}

	public boolean isShipAlive() {
		return entityBuilder.getModuleSynchronizationManager().isShipAlive();
	}
	
	protected abstract void executeLogic();

	private enum BuildState {
		IN_PROCESS, ACCOMPLISHED, NOT_STARTED;
	}

}