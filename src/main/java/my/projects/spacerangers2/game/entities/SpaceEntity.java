package my.projects.spacerangers2.game.entities;

import javafx.application.Platform;
import my.projects.spacerangers2.game.concurrent.EntitySynchronizable;
import my.projects.spacerangers2.game.geometry.Bounds;
import my.projects.spacerangers2.game.objects.Boundable;
import my.projects.spacerangers2.game.objects.SpaceObject;

public abstract class SpaceEntity<T extends SpaceObject<?>, S extends EntitySynchronizable> implements Runnable,  Boundable,
	Comparable<Object> {
	
	protected S synchronizer;
	protected T object;
	
	public SpaceEntity(S synchronizer, T object) {
		this.synchronizer = synchronizer;
		this.object = object;
	}
	
	public void setInitialPosition(double left, double top) {
		object.setLeftTopPosition(left, top);
	}
	
	@Override
	public void run() {
		initializeObject();
		while(aliveCondition()) {
			if (synchronizer.isScenePause()) {
				synchronizer.waitForSceneResume();
			}
			if (synchronizer.isSceneDisabled()) {
				break;
			}
			Platform.runLater(()->object.perform());
			performLifecycleIteration();
			synchronizer.waitForAnimationTick();
		}
		finalizeObject();
	}

	@Override
	public Bounds getBounds() {
		return object.getBounds();
	}

	@Override
	public javafx.geometry.Bounds getApproximateBounds() {
		return object.getApproximateBounds();
	}

	@Override
	public int compareTo(Object o) {
		if (hashCode() > o.hashCode()) {
			return 1;
		}
		if (hashCode() < o.hashCode()) {
			return -1;
		}
		return 0;
	}

	protected void showObjectOnScene() {
		Platform.runLater(()->object.show());
	}
	
	protected void removeObjectFromScene() {
		Platform.runLater(()->object.hide());
	}

	protected abstract void initializeObject();

	protected abstract boolean aliveCondition();

	protected abstract void performLifecycleIteration();

	protected abstract void finalizeObject();

}
