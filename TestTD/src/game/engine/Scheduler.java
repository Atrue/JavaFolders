package game.engine;
import game.engine.characters.Monster;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
public class Scheduler{
	private GameManager parent;
	private Timeline timeline;
	private double gametime = 5;
	public Scheduler(GameManager manager){
		this.parent = manager;
		timeline = new Timeline(new KeyFrame(
    			Duration.millis(1000 / getFPS()),
    			tick -> tick()
    			));
		timeline.setCycleCount(Animation.INDEFINITE);
	}
	public int getFPS(){
		return parent.getFPS();
	}
	private void tick(){
		 // Times each second
		gametime -= 1./getFPS();
		parent.update();
		if (gametime <= 0){
			parent.levelUp();
			gametime = 30;
		}
	}
	public int getGameTime(){
		return (int)gametime;
	}
	public void start(){
		timeline.play();
	}
	public void stop(){
		timeline.stop();
	}
	
	
}
