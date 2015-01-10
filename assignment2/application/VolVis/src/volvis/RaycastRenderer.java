/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package volvis;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import gui.RaycastRendererPanel;
import gui.RendererSettingsPanel;
import gui.TransferFunctionEditor;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL2;
import javax.swing.SwingWorker;
import util.TFChangeListener;
import util.VectorMath;
import volume.Volume;

/**
 *
 * @author michel
 */
public class RaycastRenderer extends ResolutionRenderer implements TFChangeListener, QualityForcible, BackgroundRunnable, CanInterpolate {
	public enum RaycastMethod{
		Slicer,
		MIP,
		CTF,
                First
	}
	
	public RendererSettings settings;
	
    private Volume volume = null;
    RaycastRendererPanel panel;
	RendererSettingsPanel settingsPanel;
    TransferFunction tFunc;
    TransferFunctionEditor tfEditor;
    double maximum;
	double lastRenderTime;
	private boolean forceQuality = false;
	private boolean runInBackground = false;
	private boolean interpolate = true;
	private RaycastMethod method = RaycastMethod.MIP;
	
	private RaycastWorker raycastWorker;

    public RaycastRenderer() {
        panel = new RaycastRendererPanel(this);
        panel.setSpeedLabel("0");
		
		this.settings = new RendererSettings();
		settingsPanel = new RendererSettingsPanel(this.settings);
    }

    public void setVolume(Volume vol) {
        volume = vol;
        maximum = vol.getMaximum();
        // set up image for storing the resulting rendering
        // the image width and height are equal to the length of the volume diagonal
        int imageSize = (int) Math.floor(Math.sqrt(vol.getDimX() * vol.getDimX() + vol.getDimY() * vol.getDimY()
                + vol.getDimZ() * vol.getDimZ()));
        if (imageSize % 2 != 0) {
            imageSize = imageSize + 1;
        }
        image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
        tFunc = new TransferFunction(volume.getMinimum(), volume.getMaximum());
        tFunc.addTFChangeListener(this);
        tfEditor = new TransferFunctionEditor(tFunc, volume.getHistogram());
        panel.setTransferFunctionEditor(tfEditor);

		this.settingsPanel.setVolume(vol);
		
		this.resetResolution();
    }

    @Override
    public void changed() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).changed();
        }
    }

    public RaycastRendererPanel getPanel() {
        return panel;
    }
	
	public RendererSettingsPanel getSettingsPanel() {
		return settingsPanel;
	}

    // get a voxel from the volume data by nearest neighbor interpolation
    short getVoxel(double[] coord) {

        int x = (int) Math.round(coord[0]);
        int y = (int) Math.round(coord[1]);
        int z = (int) Math.round(coord[2]);

        if ((x >= 0) && (x < volume.getDimX()) && (y >= 0) && (y < volume.getDimY())
                && (z >= 0) && (z < volume.getDimZ())) {
            return volume.getVoxel(x, y, z);
        } else {
            return 0;
        }
    }
      
    
    BufferedImage slicer(double[] viewMatrix) {
		BufferedImage image = new BufferedImage(this.image.getWidth(), this.image.getWidth(), BufferedImage.TYPE_INT_ARGB);
        // clear image
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                image.setRGB(i, j, 0);
            }
        }

        // vector uVec and vVec define a plane through the origin, 
        // perpendicular to the view vector viewVec
        double[] viewVec = new double[3];
        double[] uVec = new double[3];
        double[] vVec = new double[3];
        VectorMath.setVector(viewVec, viewMatrix[2], viewMatrix[6], viewMatrix[10]);
        VectorMath.setVector(uVec, viewMatrix[0], viewMatrix[4], viewMatrix[8]);
        VectorMath.setVector(vVec, viewMatrix[1], viewMatrix[5], viewMatrix[9]);
        

        // image is square
        int imageCenter = image.getWidth() / 2;

        double[] pixelCoord = new double[3];
        double[] volumeCenter = new double[3];
        VectorMath.setVector(volumeCenter, volume.getDimX() / 2, volume.getDimY() / 2, volume.getDimZ() / 2);

        // sample on a plane through the origin of the volume data
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                pixelCoord[0] = uVec[0] * (i - imageCenter) + vVec[0] * (j - imageCenter)
                        + volumeCenter[0];
                pixelCoord[1] = uVec[1] * (i - imageCenter) + vVec[1] * (j - imageCenter)
                        + volumeCenter[1];
                pixelCoord[2] = uVec[2] * (i - imageCenter) + vVec[2] * (j - imageCenter)
                        + volumeCenter[2];

                int val;
				if (this.interpolate){
					val = (int) getTriVoxel(pixelCoord, 1);
				}else{
					val = getVoxel(pixelCoord);
				}
				if (val < 0){
					val = 0;
				}
                // Apply the transfer function to obtain a color
                TFColor voxelColor = tFunc.getColor(val);
                
                // BufferedImage expects a pixel color packed as ARGB in an int
                int c_alpha = voxelColor.a <= 1.0 ? (int) Math.floor(voxelColor.a * 255) : 255;
                int c_red = voxelColor.r <= 1.0 ? (int) Math.floor(voxelColor.r * 255) : 255;
                int c_green = voxelColor.g <= 1.0 ? (int) Math.floor(voxelColor.g * 255) : 255;
                int c_blue = voxelColor.b <= 1.0 ? (int) Math.floor(voxelColor.b * 255) : 255;
                int pixelColor = (c_alpha << 24) | (c_red << 16) | (c_green << 8) | c_blue;
                image.setRGB(i, j, pixelColor);
            }
        }

		return image;
    }
    
    double getTriVoxel(double[] pixelCoord,int i ){
        double[] xyz0 = new double[3];
        double[] xyz1 = new double[3];
        VectorMath.setVector(xyz0,(int)pixelCoord[0], (int)pixelCoord[1], (int)pixelCoord[2]);
        VectorMath.setVector(xyz1,(int)xyz0[0]+i,(int)xyz0[1]+i,(int)xyz0[2]+i);
        //On a periodic and cubic lattice, let x_d, y_d, 
        //and z_d be the differences between each of x, y, z and the smaller coordinate related, that is:
        double xd= (pixelCoord[0]-xyz0[0])/(xyz1[0]-xyz0[0]);
        double yd= (pixelCoord[1]-xyz0[1])/(xyz1[1]-xyz0[1]);
        double zd= (pixelCoord[2]-xyz0[2])/(xyz1[2]-xyz0[2]);
        
        double[] valuesCube = new double[8];
        double[][] coords = new double[8][3];
        coords[0] = new double[]{xyz0[0],xyz0[1],xyz0[2]};
        coords[1] = new double[]{xyz0[0],xyz0[1],xyz1[2]};
        coords[2] = new double[]{xyz0[0],xyz1[1],xyz0[2]};
        coords[3] = new double[]{xyz0[0],xyz1[1],xyz1[2]};
        coords[4] = new double[]{xyz1[0],xyz0[1],xyz0[2]};
        coords[5] = new double[]{xyz1[0],xyz0[1],xyz1[2]};
        coords[6] = new double[]{xyz1[0],xyz1[1],xyz0[2]};
        coords[7] = new double[]{xyz1[0],xyz1[1],xyz1[2]};
        for(int x =0;x<8;x++){
            valuesCube[x]= getVoxel(coords[x]);
        }
        //where  x_0  indicates the lattice point below  x , and   x_1  indicates 
        //the lattice point above  x  and similarly for y_0, y_1, z_0 and z_1.
        //First we interpolate along x (imagine we are pushing the front face of the cube to the back), giving:
        double c00 = valuesCube[0]*(1-xd)+valuesCube[4]*xd;
        double c01 = valuesCube[1]*(1-xd)+valuesCube[5]*xd;
        double c10 = valuesCube[2]*(1-xd)+valuesCube[6]*xd;
        double c11 = valuesCube[3]*(1-xd)+valuesCube[7]*xd;
        
        double c0 = c00*(1-yd)+c10*yd;
        double c1 = c01*(1-yd)+c11*yd;
        return c0*(1-zd)+c1*zd;
    }
    
    TFColor getColor(double[]colr,double[]colg,double[]colb,double[]cola){
         int length = colr.length;
         double mult=1;
         double r=0;
         double b=0;
         double g=0;
         double a=0;
         for(int i=0;i<length;i++ ){
             mult =1;
             for(int j=i+1;j<length;j++ ){
                 mult *=(1-cola[j]);
             }

            r+= colr[i]*mult;
            b+= colb[i]*mult;
            a+= cola[i]*mult;
            g+= colg[i]*mult;
         }
        return new TFColor(r,g,b,a);
    }
    
     BufferedImage ctf(double[] viewMatrix,int res) {
		BufferedImage image = new BufferedImage(this.image.getWidth(), this.image.getWidth(), BufferedImage.TYPE_INT_ARGB);
        // clear image
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                image.setRGB(i, j, 0);
            }
        }

        // vector uVec and vVec define a plane through the origin, 
        // perpendicular to the view vector viewVec
        double[] viewVec = new double[3];
        double[] uVec = new double[3];
        double[] vVec = new double[3];
        VectorMath.setVector(viewVec, viewMatrix[2], viewMatrix[6], viewMatrix[10]);
        VectorMath.setVector(uVec, viewMatrix[0], viewMatrix[4], viewMatrix[8]);
        VectorMath.setVector(vVec, viewMatrix[1], viewMatrix[5], viewMatrix[9]);
        
        // image is square
        int imageCenter = image.getWidth() / 2;
        
        double[] pixelCoord = new double[3];
        double[] volumeCenter = new double[3];
        VectorMath.setVector(volumeCenter, volume.getDimX() / 2, volume.getDimY() / 2, volume.getDimZ() / 2);
        int val = 0;
        // sample on a plane through the origin of the volume data
        
        double max = Math.abs(viewVec[0]*volume.getDimX())
                + Math.abs(viewVec[1]*volume.getDimY()) 
                + Math.abs(viewVec[2]*volume.getDimZ());
        for (int j = 0; j < (image.getHeight()/res); j++) {
            for (int i = 0; i < (image.getWidth()/res); i++) {
                val = 0;
                double[] voxValuesa = new double[200];
                double[] voxValuesb = new double[200];
                double[] voxValuesr = new double[200];
                double[] voxValuesg = new double[200];
                for(int k = 0; k<200;k++){
                    pixelCoord[0] = uVec[0] * (res*i - imageCenter) + vVec[0] * (res*j - imageCenter)
                        + (viewVec[0])*k +volumeCenter[0];
                    pixelCoord[1] = uVec[1] * (res*i - imageCenter) + vVec[1] * (res*j - imageCenter)
                        + (viewVec[1])*k +volumeCenter[1];
                    pixelCoord[2] = uVec[2] * (res*i - imageCenter) + vVec[2] * (res*j - imageCenter)
                        + (viewVec[2])*k +volumeCenter[2];
					if (this.interpolate){
						val = (int) getTriVoxel(pixelCoord, 1);
					}else{
						val = getVoxel(pixelCoord);
					}
                    TFColor voxelColor = tFunc.getColor(val);
                    voxValuesa[k] = voxelColor.a;
                    voxValuesb[k] = voxelColor.b;
                    voxValuesr[k] = voxelColor.r;
                    voxValuesg[k] = voxelColor.g;
                }
                // Apply the transfer function to obtain a color
                TFColor voxelColor = getColor(voxValuesr,voxValuesg,voxValuesb,voxValuesa);
                
                // BufferedImage expects a pixel color packed as ARGB in an int
                int c_alpha = voxelColor.a <= 1.0 ? (int) Math.floor(voxelColor.a * 255) : 255;
                int c_red = voxelColor.r <= 1.0 ? (int) Math.floor(voxelColor.r * 255) : 255;
                int c_green = voxelColor.g <= 1.0 ? (int) Math.floor(voxelColor.g * 255) : 255;
                int c_blue = voxelColor.b <= 1.0 ? (int) Math.floor(voxelColor.b * 255) : 255;
                int pixelColor = (c_alpha << 24) | (c_red << 16) | (c_green << 8) | c_blue;
                
                for (int x = 0; x < res; x++) {
                    for (int y = 0; y < res; y++) {
                        image.setRGB((i*res)+x,(j*res)+y, pixelColor);
                        }
                }
            }
        }
      
		return image;
    }
     
    TFColor getOpacityWeighting(double[] pixelCoord,int fmin, int fmax, double omin, double omax){
        int val = getVoxel(pixelCoord);
        TFColor voxelColor = tFunc.getColor(val);
        double a = 0;
        if(val<fmin){
            a = 0;
        }
        else if(val>fmax){
            a = 0;
         }
        else{
        int x1 = getVoxel(new double[]{pixelCoord[0]+1,pixelCoord[1],pixelCoord[2]});
        int x2 = getVoxel(new double[]{pixelCoord[0]-1,pixelCoord[1],pixelCoord[2]});
        int y1 = getVoxel(new double[]{pixelCoord[0],pixelCoord[1]+1,pixelCoord[2]});
        int y2 = getVoxel(new double[]{pixelCoord[0],pixelCoord[1]-1,pixelCoord[2]});
        int z1 = getVoxel(new double[]{pixelCoord[0],pixelCoord[1],pixelCoord[2]+1});
        int z2 = getVoxel(new double[]{pixelCoord[0],pixelCoord[1],pixelCoord[2]-1});
        double[] gradientV = new double[3];
        VectorMath.setVector(gradientV, 0.5*(x1-x2),0.5*(y1-y2),0.5*(z1-z2));
        double gradient = VectorMath.length(gradientV);
        
        double max = ((val-fmin));
        double min = ((fmax-val));
        double dif = fmax - min;
        max /= dif;
        min /= dif;
        max *= omax;
        min *= omin;
        a = gradient * (max+min);
        if(a>1){
            System.out.println("Nooosee" + a + "grad" + gradient);
        }
        }
        return(new TFColor(voxelColor.r,voxelColor.g,voxelColor.b,a));
    }
    
    BufferedImage mip(double[] viewMatrix,int res,int samples) {
		BufferedImage image = new BufferedImage(this.image.getWidth(), this.image.getWidth(), BufferedImage.TYPE_INT_ARGB);
        // clear image
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                image.setRGB(i, j, 0);
            }
        }

        // vector uVec and vVec define a plane through the origin, 
        // perpendicular to the view vector viewVec
        double[] viewVec = new double[3];
        double[] uVec = new double[3];
        double[] vVec = new double[3];
        VectorMath.setVector(viewVec, viewMatrix[2], viewMatrix[6], viewMatrix[10]);
        VectorMath.setVector(uVec, viewMatrix[0], viewMatrix[4], viewMatrix[8]);
        VectorMath.setVector(vVec, viewMatrix[1], viewMatrix[5], viewMatrix[9]);
        
        // image is square
        int imageCenter = image.getWidth() / 2;
        
        double[] pixelCoord = new double[3];
        double[] pixelCoordMax = new double[3];
        double[] volumeCenter = new double[3];
        VectorMath.setVector(volumeCenter, volume.getDimX() / 2, volume.getDimY() / 2, volume.getDimZ() / 2);
        int val = 0;
        // sample on a plane through the origin of the volume data
        double max = Math.abs(viewVec[0]*volume.getDimX())
                + Math.abs(viewVec[1]*volume.getDimY()) 
                + Math.abs(viewVec[2]*volume.getDimZ());
        for (int j = 0; j < (image.getHeight()/res); j++) {
            for (int i = 0; i < (image.getWidth()/res); i++) {
                val = 0;
                for(int k = -(int)(max/2); k<(max/2);k=k+samples){
                pixelCoord[0] = uVec[0] * (res*i - imageCenter) + vVec[0] * (res*j - imageCenter)
                        + (viewVec[0])*k +volumeCenter[0];
                pixelCoord[1] = uVec[1] * (res*i - imageCenter) + vVec[1] * (res*j - imageCenter)
                        + (viewVec[1])*k +volumeCenter[1];
                pixelCoord[2] = uVec[2] * (res*i - imageCenter) + vVec[2] * (res*j - imageCenter)
                        + (viewVec[2])*k +volumeCenter[2];
					int x;
					if (this.interpolate){
					  x = (int) getTriVoxel(pixelCoord, 1);
					}else{
					  x = getVoxel(pixelCoord);
					}
                                        System.out.println(x);   
                    if(x > val){
                        val = x;
                        VectorMath.setVector(pixelCoordMax, (int)pixelCoord[0],(int)pixelCoord[1],(int)pixelCoord[2]); 
                        }
                }
                // Apply the transfer function to obtain a color
                TFColor voxelColor;
                if(settings.isUseGradient()){
                    voxelColor = getOpacityWeighting(pixelCoordMax,
                                                     settings.getFmin(),
                                                     settings.getFmax(),
                                                     settings.getOmin(),
                                                     settings.getOmax());
                }
                else{
                    voxelColor = tFunc.getColor(val);
                }
                
                // BufferedImage expects a pixel color packed as ARGB in an int
                int c_alpha = voxelColor.a <= 1.0 ? (int) Math.floor(voxelColor.a * 255) : 255;
                int c_red = voxelColor.r <= 1.0 ? (int) Math.floor(voxelColor.r * 255) : 255;
                int c_green = voxelColor.g <= 1.0 ? (int) Math.floor(voxelColor.g * 255) : 255;
                int c_blue = voxelColor.b <= 1.0 ? (int) Math.floor(voxelColor.b * 255) : 255;
                int pixelColor = (c_alpha << 24) | (c_red << 16) | (c_green << 8) | c_blue;
                
                for (int x = 0; x < res; x++) {
                    for (int y = 0; y < res; y++) {
                        image.setRGB((i*res)+x,(j*res)+y, pixelColor);
                        }
                }
            }
        }
      
		return image;
    }
    
       BufferedImage first(double[] viewMatrix,int res,int samples) {
		BufferedImage image = new BufferedImage(this.image.getWidth(), this.image.getWidth(), BufferedImage.TYPE_INT_ARGB);
        // clear image
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                image.setRGB(i, j, 0);
            }
        }
        // vector uVec and vVec define a plane through the origin, 
        // perpendicular to the view vector viewVec
        double[] viewVec = new double[3];
        double[] uVec = new double[3];
        double[] vVec = new double[3];
        VectorMath.setVector(viewVec, viewMatrix[2], viewMatrix[6], viewMatrix[10]);
        VectorMath.setVector(uVec, viewMatrix[0], viewMatrix[4], viewMatrix[8]);
        VectorMath.setVector(vVec, viewMatrix[1], viewMatrix[5], viewMatrix[9]);
        
        // image is square
        int imageCenter = image.getWidth() / 2;
        
        double[] pixelCoord = new double[3];
        double[] pixelCoordMax = new double[3];
        double[] volumeCenter = new double[3];
        VectorMath.setVector(volumeCenter, volume.getDimX() / 2, volume.getDimY() / 2, volume.getDimZ() / 2);
        int val = 0;
        // sample on a plane through the origin of the volume data
        double max = Math.abs(viewVec[0]*volume.getDimX())
                + Math.abs(viewVec[1]*volume.getDimY()) 
                + Math.abs(viewVec[2]*volume.getDimZ());
        for (int j = 0; j < (image.getHeight()/res); j++) {
            for (int i = 0; i < (image.getWidth()/res); i++) {
                val = 0;
                for(int k = -(int)(max/2); k<(max/2);k=k+samples){
                pixelCoord[0] = uVec[0] * (res*i - imageCenter) + vVec[0] * (res*j - imageCenter)
                        + (viewVec[0])*k +volumeCenter[0];
                pixelCoord[1] = uVec[1] * (res*i - imageCenter) + vVec[1] * (res*j - imageCenter)
                        + (viewVec[1])*k +volumeCenter[1];
                pixelCoord[2] = uVec[2] * (res*i - imageCenter) + vVec[2] * (res*j - imageCenter)
                        + (viewVec[2])*k +volumeCenter[2];
					int x;
					if (this.interpolate){
					  x = (int) getTriVoxel(pixelCoord, 1);
					}else{
					  x = getVoxel(pixelCoord);
					}
                    if(x > settings.getFirstMin()){
                        val = x;
                        k = (int)max;
                        }
                }
                // Apply the transfer function to obtain a color
                TFColor voxelColor;
                voxelColor = tFunc.getColor(val);
                
                // BufferedImage expects a pixel color packed as ARGB in an int
                int c_alpha = voxelColor.a <= 1.0 ? (int) Math.floor(voxelColor.a * 255) : 255;
                int c_red = voxelColor.r <= 1.0 ? (int) Math.floor(voxelColor.r * 255) : 255;
                int c_green = voxelColor.g <= 1.0 ? (int) Math.floor(voxelColor.g * 255) : 255;
                int c_blue = voxelColor.b <= 1.0 ? (int) Math.floor(voxelColor.b * 255) : 255;
                int pixelColor = (c_alpha << 24) | (c_red << 16) | (c_green << 8) | c_blue;
                
                for (int x = 0; x < res; x++) {
                    for (int y = 0; y < res; y++) {
                        image.setRGB((i*res)+x,(j*res)+y, pixelColor);
                        }
                }
            }
        }
      
		return image;
    }
    
   

    private void drawBoundingBox(GL2 gl) {
        gl.glPushAttrib(GL2.GL_CURRENT_BIT);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glColor4d(1.0, 1.0, 1.0, 1.0);
        gl.glLineWidth(1.5f);
        gl.glEnable(GL2.GL_LINE_SMOOTH);
        gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glDisable(GL2.GL_LINE_SMOOTH);
        gl.glDisable(GL2.GL_BLEND);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glPopAttrib();

    }

    @Override
    public void visualize(GL2 gl) {


        if (volume == null) {
            return;
        }

        drawBoundingBox(gl);

        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, viewMatrix, 0);
		
		if (reset && raycastWorker != null) {
			raycastWorker.cancel(true);
			raycastWorker = null;
			reset = false;
		}
		
		if (raycastWorker == null || raycastWorker.isDone()) {
			raycastWorker = new RaycastWorker();
			raycastWorker.execute();
			if(!this.runInBackground){
				raycastWorker.done();
			}
		}

        Texture texture = AWTTextureIO.newTexture(gl.getGLProfile(), image, false);

        gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

        // draw rendered image as a billboard texture
        texture.enable(gl);
        texture.bind(gl);
        double halfWidth = image.getWidth() / 2.0;
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glTexCoord2d(0.0, 0.0);
        gl.glVertex3d(-halfWidth, -halfWidth, 0.0);
        gl.glTexCoord2d(0.0, 1.0);
        gl.glVertex3d(-halfWidth, halfWidth, 0.0);
        gl.glTexCoord2d(1.0, 1.0);
        gl.glVertex3d(halfWidth, halfWidth, 0.0);
        gl.glTexCoord2d(1.0, 0.0);
        gl.glVertex3d(halfWidth, -halfWidth, 0.0);
        gl.glEnd();
        texture.disable(gl);
        texture.destroy(gl);
        gl.glPopMatrix();

        gl.glPopAttrib();


        if (gl.glGetError() > 0) {
            System.out.println("some OpenGL error: " + gl.glGetError());
        }
    }
    private BufferedImage image;
    private double[] viewMatrix = new double[4 * 4];

	@Override
	protected boolean shouldIncreaseResolution() {
		return this.forceQuality || this.lastRenderTime < 350;
	}
	
	@Override
	protected boolean shouldDecreaseResolution() {
		return !this.forceQuality && this.lastRenderTime > 700;
	}
	
	@Override
	public void forceQuality(boolean force) {
		this.forceQuality = force;
	}
	
	@Override
	public void runInBackground(boolean background){
		this.runInBackground = background;
	};
	
	@Override
	public void setInterpolate(boolean interpolate) {
		this.interpolate = interpolate;
	}
	
	public void setRaycastMethod(RaycastMethod method){
		this.method = method;
	}
	
	private class RaycastWorker extends SwingWorker<BufferedImage, Void> {
		@Override
		protected BufferedImage doInBackground() {
			long startTime = System.currentTimeMillis();
			BufferedImage ret;
			switch(method){
				case Slicer:
					ret = slicer(viewMatrix);
					break;
				case MIP:
					ret = mip(viewMatrix, resolution,resolution);
					break;
				case CTF:
					ret = ctf(viewMatrix, resolution);
					break;
                                case First:
					ret = first(viewMatrix, resolution,resolution);
					break;
				default:
					ret = null;
			}
			long endTime = System.currentTimeMillis();
			double runningTime = (endTime - startTime);
			lastRenderTime = runningTime;
			return ret;
		}
		
		@Override
		protected void done(){
			try {
				image = get();
				panel.setSpeedLabel(Double.toString(lastRenderTime));
				increaseResolution();
				decreaseResolution();
			} catch (InterruptedException ex) {}
			catch (ExecutionException ex) {}
		}
	}
}
