/**
 *
 *  ndStackBuilder v1, 11 mai 2013 
    Fabrice P Cordelieres, fabrice.cordelieres at gmail.com
    
    Copyright (C) 2013 Fabrice P. Cordelieres
  
    License:
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package ndFile;

import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.NewImage;
import ij.io.FileSaver;
import ij.plugin.Concatenator;
import ij.plugin.Duplicator;
import ij.plugin.HyperStackConverter;
import ij.plugin.ZProjector;
import ij.process.ImageProcessor;
import ij.process.LUT;
import java.io.File;
import java.util.ArrayList;
import plugins.ndFile.LUT_Chooser;

/**
 * This plugin aims at building (hyper)stack(s) from informations contained in the
 * Metmaorph nd file
 * @author fab
 */
public class ndStackBuilder {
    /** nd file to process **/
    public ndFile nd=null;
    
    /** Output ImagePlus **/
    public ImagePlus out=null;
    
    /** Output image width **/
    int width=0;
    
    /** Output image height **/
    int height=0;
    
    /** Output image nSlices **/
    int nSlices=0;
    
    /** Output image bit depth **/
    int bitDepth=0;
    
    /** Start position **/
    public int startPosition=0;
    
    /** Stop position **/
    public int stopPosition=0;
    
    /** Start wave **/
    public int startWave=0;
    
    /** Stop wave **/
    public int stopWave=0;
    
    /** Start timepoint **/
    public int startTime=1;
    
    /** Stop timepoint **/
    public int stopTime=1;
    
    /** Start slice **/
    public int startSlice=1;
    
    /** Stop slice **/
    public int stopSlice=1;
    
    /** No projection **/
    public static final int NO_PROJECTION=-1;
    
    /** Z projection method index **/
    public int projMethod=NO_PROJECTION;
    
    /** True if images are to be saved **/
    public boolean saveImages=true;
    
    /** True if images are to be saved as hyperstacks**/
    public boolean hyperStack=true;
    
    /** True if images should be displayed **/
    public boolean showImages=false;
    
    /** Array of original images to open **/
    ArrayList<String> fileNames=null;
    
    /**
     * Create a new ndStackBuilder instance, based on the input Metamorph nd file
     * @param nd the input Metamorph nd file to process
     */
    public ndStackBuilder(ndFile nd){
        this.nd=nd;
        resetParameters();
    }
    
    /**
     * Create a new ndStackBuilder instance, based on a Metamorph nd file located 
     * in the input directory and filenName
     * @param directory  directory where the input Metamorph nd file to process is located
     * @param fileName name of the input Metamorph nd file to process
     */
    public ndStackBuilder(String directory, String fileName){
        this(new ndFile(directory, fileName));
    }
    
    /**
     * Sets the positions parameters
     * @param start start position [0, NStagePositions-1]
     * @param stop stop position [0, NStagePositions-1]
     */
    public void setPositionsParameters(int start, int stop){
        startPosition=start;
        stopPosition=stop;
    }
    
    /**
     * Sets the channels parameters
     * @param start start channel [0, NWavelengths-1]
     * @param stop stop channel [0, NWavelengths-1]
     */
    public void setWavesParameters(int start, int stop){
        startWave=start;
        stopWave=stop;
    }
    
    /**
     * Sets the timepoints parameters
     * @param start start timepoint [1, NTimePoints]
     * @param stop stop timepoint [1, NTimePoints]
     */
    public void setTimepointsParameters(int start, int stop){
        startTime=start;
        stopTime=stop;
    }
    
    /**
     * Sets the z stack parameters
     * @param start start position [1, NZSteps]
     * @param stop stop position [1, NZSteps]
     * @param projType projection type (from the ImageJ ZProjector class or NO_PROJECTION)
     */
    public void setZParameters(int start, int stop, int projType){
        startSlice=start;
        stopSlice=stop;
        projMethod=projType;
    }
    
    /**
     * Sets the output parameters
     * @param save true to save the (hyper)stack(s)
     * @param saveAsHyperstack true to save images as hyperstack(s)
     * @param show true to display the (hyper)stack(s)
     */
    public void setOutputParameters(boolean save, boolean saveAsHyperstack, boolean show){
        saveImages=save;
        hyperStack=saveAsHyperstack;
        showImages=show;
    }
    
    /**
     * Resets all parameters to their default values, based on the original's Metamorph 
     * nd file content
     */
    public void resetParameters(){
        width=0;
        height=0;
        nSlices=0;
        bitDepth=0;
        startPosition=0;
        stopPosition=nd.NStagePositions-1;
        startWave=0;
        stopWave=nd.NWavelengths==0?0:nd.NWavelengths-1;
        startTime=1;
        stopTime=nd.NTimePoints;
        startSlice=1;
        stopSlice=nd.NZSteps;
        projMethod=NO_PROJECTION;
        fileNames=null;
    }
    
    /**
     * Builds the stacks, based on the pre-defined parameters
     * @param outputDirectory folder where the (hyper)stack(s) has to be saved
     */
    public void buildStacks(String outputDirectory){
        width=0;
        height=0;
        nSlices=nd.NZSteps;
        bitDepth=0;
        
        for(int pos=startPosition; pos<=stopPosition; pos++){
            if(hyperStack && startWave==0 && stopWave==nd.NWavelengths-1){
                //Full hyperstack
                fileNames=nd.getImageList(pos, ndFile.ALL_WAVES, startTime, stopTime, true);
                buildAndSaveHyperStack(outputDirectory, pos, ndFile.ALL_WAVES);
            }else{
                //Wave 
                for(int wave=startWave; wave<=stopWave; wave++){
                    fileNames=nd.getImageList(pos, wave, startTime, stopTime, false);
                    buildAndSaveHyperStack(outputDirectory, pos, wave);
                }
            }
        }
    }
    
    /**
     * Concatenates the current image (opened, based on the input name and the Metamorph 
     * nd file's folder) to the stack being built. If no stack is present, the current 
     * image becomes the start of a new stack
     * @param name name of the image to be opened and concatenated to the current stack
     * @param pad3D true if empty slices should be repeated as many time as required to fill the stack (ex: when 2 channels were acquired, one in 3D, one in 2D)
     */
    private void concatenate(String name, boolean pad3D){
        String path=nd.Directory+name;
        ImagePlus currImg=null;

        if(!new File(path).exists()){
            if(width!=0 && height!=0 && nSlices!=0 && bitDepth!=0){
                currImg=NewImage.createImage(name, width, height, nSlices, bitDepth, NewImage.FILL_BLACK);
            }else{
                IJ.log("File "+name+" not found, image skipped");
            }
        }else{
            currImg=new ImagePlus(path);
            IJ.showStatus("Processing images "+name);
            if(width==0 || height==0 || nSlices==0 || bitDepth==0){
                width=currImg.getWidth();
                height=currImg.getHeight();
                nSlices=currImg.getNSlices();
                bitDepth=currImg.getBitDepth();
            }
        }
        
        if(currImg!=null){
            //Do the projection
            if(projMethod!=-1 && nSlices>1){
                ZProjector zp=new ZProjector(currImg);
                zp.setStartSlice(startSlice);
                zp.setStopSlice(stopSlice);
                zp.setMethod(projMethod);
                zp.doProjection();
                currImg=zp.getProjection();
            }
            
            /**In case no projection should be done, but start/stop slice are different
             * from extreme values, duplicates the relevant portion of the stack
             */
            if(projMethod==-1 && nSlices>1 && (startSlice!=1 || stopSlice!=nSlices)) currImg=new Duplicator().run(currImg, startSlice, stopSlice);
            
            /**
             * In case no projection should be done, but the image is a single slice while some acquisitions
             * have been done in 3D, pads the Z with empty slices
             */
            if(projMethod==-1 && currImg.getNSlices()!=nd.NZSteps && pad3D){
            	ImageProcessor iproc=currImg.getProcessor().createProcessor(width, height);
            	ImageStack currStack=currImg.getStack();
            	for(int i=1; i<nd.NZSteps; i++) currStack.addSlice(iproc);
            	currImg.setStack(currImg.getTitle(), currStack);
            }
            
            
            
            if(out==null){
                out=currImg;
            }else{
                out=new Concatenator().concatenate(out, currImg, false);
                currImg.flush();
            }
        }
    }
    
    /**
     * Builds and saves the (hyper)stack(s) for a single positions and either all 
     * or a single wavelength
     * @param outputDirectory folder where the (hyper)stack(s) has to be saved
     * @param pos position to build
     * @param wave single wave to build or nd.ALL_WAVES for all waves
     */
    private void buildAndSaveHyperStack(String outputDirectory, int pos, int wave){
        out=null;
        for(int file=0; file<fileNames.size(); file++) concatenate(fileNames.get(file), true);
        
        String name=(projMethod!=NO_PROJECTION?ZProjector.METHODS[projMethod]+"_":"")+(nd.BaseName)+(nd.DoStage?"_"+nd.Stage.get(pos):"")+((nd.DoWave && (wave!=-1 && nd.NWavelengths!=0))?"_w"+(wave+1)+nd.Waves.get(wave).WaveName:"");
        if(out!=null){
            out.setTitle(name);

            if(projMethod!=NO_PROJECTION) for(int i=0; i<fileNames.size(); i++) out.getStack().setSliceLabel(fileNames.get(i), i+1);

            int nC=wave!=nd.ALL_WAVES?1:nd.NWavelengths;
            int nZ=projMethod==NO_PROJECTION?nd.NZSteps:1;
            int nT=out.getImageStackSize()/nC/nZ;
            
            out.setOpenAsHyperStack(true);
            out.setDimensions(nC, nZ, nT);

            CompositeImage ci=null;;
            if(wave==nd.ALL_WAVES && nd.NWavelengths!=1){
                new HyperStackConverter().shuffle(out, HyperStackConverter.ZTC);

                ci=new CompositeImage(out, CompositeImage.COMPOSITE);
                ci.setSlice(nSlices/2);
                
                int[] luts=LUT_Chooser.getLUTsIndexes();
                for(int i=1; i<=nC; i++) ci.setChannelLut(LUT.createLutFromColor(LUT_Chooser.colors[luts[(i-1)%luts.length]]), i);
                
                ci.reset();
                ci.resetDisplayRanges();

                out=ci;
            }

            if(saveImages) new FileSaver(out).saveAsZip(outputDirectory+name);

            if(showImages){
                out.show();
            }else{
                out.flush();
                if(ci!=null) ci.flush();
            }
        }else{
            IJ.log(name+": no images found to build stack");
        }
    }
}
