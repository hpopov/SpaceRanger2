package my.projects.spacerangers2.game.objects;

import javafx.scene.layout.Pane;
import my.projects.spacerangers2.game.common.AbstractAnimation;
import my.projects.spacerangers2.game.geometry.Bounds;
import my.projects.spacerangers2.game.geometry.Point2D;
import my.projects.spacerangers2.game.geometry.Vector2D;

public abstract class SpaceObject<T extends AbstractAnimation> implements Boundable, Visible, Positionable, Performable, Movable {
	
	private Point2D topLeftPosition;
	private T animation;
	private Pane representingStage;
	private boolean isOnStage;
	/**
	 * they have to be calculated according to specified animation and are binded to topLeftPosition as their origin.
	 */
	private Bounds bounds;
	private BoundsInitializer boundsInitializer;
	
	public SpaceObject(T animation, Pane representingStage, BoundsInitializer boundsInitializer) {
		topLeftPosition = new Point2D();
		this.animation = animation;
		this.representingStage = representingStage;
		this.isOnStage = false;
		this.boundsInitializer = boundsInitializer;
		initializeBounds();
	}

	/**
	 * Calculate Bounds (depending on particular images in animation)
	 * @return calculated bounds
	 */
	private void initializeBounds() {
		bounds = boundsInitializer.get();
		bounds.setObjectCoordinatePoint(topLeftPosition);
	}
	
	@Override
	public Point2D getCentrePosition() {
		double x = topLeftPosition.getX() + animation.getWidth()/2;
		double y = topLeftPosition.getY() + animation.getHeight()/2;
		Point2D position = new Point2D(x,y);
		return position;
	}

	@Override
	public void setCentrePosition(Point2D position) {
		topLeftPosition.setX((position.getX() - animation.getWidth()/2));
		topLeftPosition.setY((position.getY() - animation.getHeight()/2));
	}
	
	@Override
	public double getLeft() {
		return topLeftPosition.getX();
	}
	
	@Override
	public double getTop() {
		return topLeftPosition.getY();
	}

	@Override
	public void setLeftTopPosition(Point2D position) {
		topLeftPosition.setNewValues(position);
	}

	/**
	 * Binds the animation to the specified Pane
	 */
	@Override
	public void show() {
		if (!isOnStage) {
			isOnStage = true;
			animation.bindToParent(representingStage);
		}
	}

	/**
	 * Removes the animation from the specified Pane
	 */
	@Override
	public void hide() {
		if (isOnStage) {
			isOnStage = false;
			animation.removeFromParent(representingStage);
		}
	}

	@Override
	public Bounds getBounds() {
		return bounds;
	}
	
	@Override
	public javafx.geometry.Bounds getApproximateBounds() {
		return animation.getBounds();
	}

	@Override
	public void moveByVector(Vector2D delta) {
		topLeftPosition.shiftBy(delta);
	}
	
	
}
