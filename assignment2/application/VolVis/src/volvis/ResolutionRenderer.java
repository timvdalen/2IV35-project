package volvis;

/**
 *
 * @author Tim
 */
public abstract class ResolutionRenderer extends Renderer {
	private int INITIAL_RES = 8;
	protected int resolution = INITIAL_RES;
	
	public void resetResolution(){
		this.resolution = INITIAL_RES;
	}
	
	public void increaseResolution(){
		if(this.resolution <= 1){
			return;
		}
		this.resolution -= 1;
	}
}
