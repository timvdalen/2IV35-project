package volvis;

/**
 *
 * @author Tim
 */
public class RendererSettings {
	private int fmin;
	private int firstMin;
	private int fmax;
	private double omin;
	private double omax;
	
	private boolean useGradient;
	
	private double factor;
	
	public RendererSettings(){
		this.fmin = 180;
		this.fmax = 240;
		this.omin = 0.6;
		this.omax = 0.8;
		this.firstMin = 30;
		this.useGradient = true;
		
		this.factor = 1.0;
	}

	public int getFmin() {
		return fmin;
	}

	public void setFmin(int fmin) {
		this.fmin = fmin;
	}
        
	public int getFirstMin() {
		return firstMin;
	}

	public void setFirstMin(int fmin) {
		this.firstMin = fmin;
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

	public double getFactor() {
		return factor;
	}

	public void setFactor(double factor) {
		this.factor = factor;
	}
	
}
