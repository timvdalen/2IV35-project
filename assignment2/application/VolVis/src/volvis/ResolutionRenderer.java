package volvis;

/**
 *
 * @author Tim
 */
public abstract class ResolutionRenderer extends Renderer {
	private int INITIAL_RES = 16;
	protected int resolution = INITIAL_RES;
	
	protected abstract boolean shouldIncreaseResolution();
	protected abstract boolean shouldDecreaseResolution();
	
	public void increaseResolution(){
		if(this.resolution <= 1 || !shouldIncreaseResolution()){
			return;
		}
		this.resolution -= 1;
	}
	
	public void decreaseResolution(){
		if(this.resolution*2 >= INITIAL_RES || !shouldDecreaseResolution()){
			return;
		}
		this.resolution *= 2;
	}
	
	public void resetResolution(){
		this.resolution = INITIAL_RES;
	}
}
