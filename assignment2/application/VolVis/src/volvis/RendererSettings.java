package volvis;

/**
 *
 * @author Tim
 */
public class RendererSettings {
	private int fmin;
	private int fmax;
	private double omin;
	private double omax;
	
	private boolean useGradient;
	
	public RendererSettings(){
		this.fmin = 180;
		this.fmax = 240;
		this.omin = 0.6;
		this.omax = 0.8;
		
		this.useGradient = true;
	}

	public int getFmin() {
		return fmin;
	}

	public void setFmin(int fmin) {
		this.fmin = fmin;
	}

	public int getFmax() {
		return fmax;
	}

	public void setFmax(int fmax) {
		this.fmax = fmax;
	}

	public double getOmin() {
		return omin;
	}

	public void setOmin(double omin) {
		this.omin = omin;
	}

	public double getOmax() {
		return omax;
	}

	public void setOmax(double omax) {
		this.omax = omax;
	}

	public boolean isUseGradient() {
		return useGradient;
	}

	public void setUseGradient(boolean useGradient) {
		this.useGradient = useGradient;
	}
}
