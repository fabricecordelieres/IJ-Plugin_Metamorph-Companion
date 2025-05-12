/**
 *
 *  buildNdStack v1, 12 mai 2013 
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

package plugins.ndFile;

import ij.IJ;
import ij.Prefs;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.io.DirectoryChooser;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;
import ij.plugin.ZProjector;
import java.awt.AWTEvent;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.TextField;
import java.util.Vector;
import ndFile.ndFile;
import ndFile.ndStackBuilder;

/**
 * This plugin allows building (hyper)stack(s), based on the informations retrieved 
 * from a Metamorph nd file
 * @author fab
 */
public class buildNdStack implements PlugIn, DialogListener{
    /** Input directory **/
    public String inDirectory=null;
    
    /** nd filename **/
    public String fileName=null;
    
    /** nd file **/
    public ndFile nd=null;
    
    /** Z mode **/
    String[] zMode={"All slices", "Keep selected slices", "Z project"};
    
    /** Save mode **/
    String[] saveMode={"All channels together", "Each channel separately"};
    
    /** GenericDialog **/
    GenericDialog gd=null;
    
    /** Vector of checkboxes from the GenericDialog **/
    Vector<Checkbox> chbox=null;
    
    /** Vector of choices from the GenericDialog **/
    Vector<Choice> choices=null;
    
    /** Vector of numeric fields from the GenericDialog **/
    Vector<TextField> numFields=null;
    
    /** Output directory **/
    public String outDirectory=null;
    
    /** True if all positions are to be built **/
    public boolean buildAllPosition=true;
    
    /** Positions names **/
    String[] posNames=null;
    
    /** Wave names **/
    String[] waveNames=null;
    
    /** Position to build **/
    public int positionToBuild=0;
    
    /** True if all channels are to be built **/
    public boolean buildAllChannels=true;
    
    /**Channel to build **/
    public int channelToBuild=0;
    
    /** True if all timepoints are to be built **/
    public boolean buildAllTimepoints=true;
    
    /** Start timepoint **/
    public int startTimepoint=1;
    
    /** Stop timepoint **/
    public int stopTimepoint=1;
    
    /** Index of the chosen z mode to use **/
    public int zModeIndex=0;
    
    /** Index of the chosen projection mode to use **/
    public int projModeIndex=0;
    
    /** Start slice **/
    public int startSlice=1;
    
    /** Stop slice **/
    public int stopSlice=1;
    
    /** True if stacks should be displayed **/
    public boolean showStack=Prefs.get("buildNdStack_showStack.boolean", false);
    
    /** True if stacks should be saved **/
    public boolean saveStack=Prefs.get("buildNdStack_saveStack.boolean", true);
    
    /** True if LUT chooser should be displayed **/
    public boolean showLUTChooser=Prefs.get("buildNdStack_showLUTChooser.boolean", false);
    
    /** Index of the chosen save/show mode to use **/
    public int saveShowModeIndex=0;

    /**
     * This method is called when the plugin is loaded.
     * @param arg argument specified for this plugin in IJ_Props.txt (may be blank)
     */
    @Override
    public void run(String arg) {
        if(openNdFile()){
            nd=new ndFile(inDirectory, fileName);
            
            //Handles cases where a single position is recorded
            posNames=nd.getPosNames();
            if(posNames.length==0){
                posNames=new String[1];
                posNames[0]="Single position";
            }
            
            //Handles cases where a single position is recorded
            waveNames=nd.getWaveNames();
            if(waveNames.length==0){
                waveNames=new String[1];
                waveNames[0]="Single channel";
            }else{
            	for(int i=0; i<waveNames.length; i++) waveNames[i]="w"+(i+1)+waveNames[i];
            }
            
            if(GUI()){
                IJ.showStatus("--- nd stack building done ---");
            }else{
                IJ.showStatus("--- nd stack building cancelled ---");
            }
        }
    }
    
    /**
     * Opens the Metamorph nd file to process
     * @return true if the dialog was OKed, false otherwise
     */
    public boolean openNdFile(){
        OpenDialog od=new OpenDialog("Input nd file selection", null);
        inDirectory=od.getDirectory();
        fileName=od.getFileName();
        
        return fileName!=null;
    }
    
    /**
     * Graphical user interface, asking for the parameters to use
     * @return true if the dialog was OKed, false otherwise
     */
    @SuppressWarnings("unchecked")
	public boolean GUI(){
        gd=new GenericDialog("nd stack builder");
        gd.addCheckbox("Build_all_positions", buildAllPosition);
        gd.addChoice("Position_to_build", posNames, posNames[0]);
        gd.addCheckbox("Build_all_channels", buildAllChannels);
        gd.addChoice("Channels_to_build", waveNames, waveNames[0]);
        gd.addCheckbox("Build_all_timepoints", buildAllTimepoints);
        gd.addNumericField("Start_timepoint", 1, 0);
        gd.addNumericField("Stop_timepoint", nd.NTimePoints, 0);
        gd.addChoice("Z_mode", zMode, zMode[0]);
        gd.addChoice("Projection_mode", ZProjector.METHODS, ZProjector.METHODS[0]);
        gd.addNumericField("Start_slice", 1, 0);
        gd.addNumericField("Stop_slice", nd.NZSteps, 0);
        gd.addCheckbox("Save_built_stack(s)", saveStack);
        gd.addCheckbox("Show_built_stack(s)", showStack);
        gd.addCheckbox("Show_LUT_chooser", showLUTChooser);
        gd.addChoice("Save/show_mode", saveMode, saveMode[0]);
        
        chbox=gd.getCheckboxes();
        choices=gd.getChoices();
        numFields=gd.getNumericFields();
        
        gd.addDialogListener(this);
        dialogItemChanged(gd, null);
        
        gd.showDialog();
        
        if(gd.wasOKed()){
            boolean goOn=true;
            getParameters();
            
            Prefs.set("buildNdStack_showStack.boolean", showStack);
            Prefs.set("buildNdStack_saveStack.boolean", saveStack);
            Prefs.set("buildNdStack_showLUTChooser.boolean", showLUTChooser);
            
            if(buildAllChannels && showLUTChooser) goOn=LUT_Chooser.GUI(nd.getWaveNames());
            
            if(goOn){
                if(saveStack) goOn=saveDialog();
                if(goOn){
                    ndStackBuilder nsb=new ndStackBuilder(nd);
                    if(!buildAllPosition) nsb.setPositionsParameters(positionToBuild, positionToBuild);
                    if(!buildAllChannels) nsb.setWavesParameters(channelToBuild, channelToBuild);
                    if(!buildAllTimepoints) nsb.setTimepointsParameters(startTimepoint, stopTimepoint);
                    if(zModeIndex!=0) nsb.setZParameters(startSlice, stopSlice, zModeIndex==2?projModeIndex:ndStackBuilder.NO_PROJECTION);
                    nsb.setOutputParameters(saveStack, saveShowModeIndex==0, (showStack && chbox.elementAt(4).isEnabled()));
                    nsb.buildStacks(outDirectory);
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    
    /**
     * Select the folder where the (hyper)stack(s) will be saved
     * @return true if the dialog was OKed, false otherwise
     */
    public boolean saveDialog(){
        DirectoryChooser dc=new DirectoryChooser("Output folder selection");
        outDirectory=dc.getDirectory();
        return outDirectory!=null;
    }

    @Override
    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        getParameters();
        
        //Handles cases where a single position is recorded
        if(posNames.length==1){buildAllPosition=true; ((Checkbox) gd.getCheckboxes().elementAt(0)).setEnabled(false);}
        //Handles cases where a single channel is recorded
        if(waveNames.length==1){buildAllChannels=true; ((Checkbox) gd.getCheckboxes().elementAt(1)).setEnabled(false);}
        //Handles cases where a single timepoint is recorded
        if(nd.NTimePoints==1){buildAllTimepoints=true; ((Checkbox) gd.getCheckboxes().elementAt(2)).setEnabled(false);}
        
        choices.elementAt(0).setEnabled(!buildAllPosition);                         //Position to build
        choices.elementAt(1).setEnabled(!buildAllChannels);                         //Channel to build
        numFields.elementAt(0).setEnabled(!buildAllTimepoints);                     //Start timepoint
        numFields.elementAt(1).setEnabled(!buildAllTimepoints);                     //Stop timepoint
        choices.elementAt(2).setEnabled(nd.NZSteps!=1);                             //Z mode
        choices.elementAt(3).setEnabled(zModeIndex==2);                             //Projection mode
        numFields.elementAt(2).setEnabled(zModeIndex==1 || zModeIndex==2);          //Start slice
        numFields.elementAt(3).setEnabled(zModeIndex==1 || zModeIndex==2);          //Stop slice
        choices.elementAt(4).setEnabled(buildAllChannels && nd.NWavelengths>1);     //Save mode
        if(!buildAllChannels || nd.NWavelengths<2) choices.elementAt(4).select(1);  //Save mode
        
        if(buildAllPosition && nd.NStagePositions!=1){
            chbox.elementAt(3).setState(true);  //Save
            chbox.elementAt(4).setState(false); //Show
        }
        
        chbox.elementAt(3).setEnabled(!buildAllPosition || nd.NStagePositions==1);  //Save enable/disable                         //Save enable/disable
        chbox.elementAt(4).setEnabled(!buildAllPosition || nd.NStagePositions==1);  //Show enable/disable
        
        chbox.elementAt(5).setEnabled(buildAllChannels);
        
        if(gd.isVisible()) ((Button) gd.getButtons()[0]).setVisible(saveStack || showStack); //Ok button
        
        return true;
    }
    
    /**
     * Retrieves all parameters as set in the GUI
     */
    public void getParameters(){
        buildAllPosition=gd.getNextBoolean();
        positionToBuild=gd.getNextChoiceIndex();
        buildAllChannels=gd.getNextBoolean();
        channelToBuild=gd.getNextChoiceIndex();
        buildAllTimepoints=gd.getNextBoolean();
        startTimepoint=(int) gd.getNextNumber();
        stopTimepoint=(int) gd.getNextNumber();
        zModeIndex=gd.getNextChoiceIndex();
        projModeIndex=gd.getNextChoiceIndex();
        startSlice=(int) gd.getNextNumber();
        stopSlice=(int) gd.getNextNumber();
        saveStack=gd.getNextBoolean();
        showStack=gd.getNextBoolean();
        showLUTChooser=gd.getNextBoolean();
        saveShowModeIndex=gd.getNextChoiceIndex();
    }
    
    /**
     * Logs the current parameters
     */
    public void logParameters(){
        String out="buildAllPosition: "+buildAllPosition+"\n";
        out+="positionToBuild: "+positionToBuild+"\n";
        out+="buildAllChannels: "+buildAllChannels+"\n";
        out+="channelToBuild: "+channelToBuild+"\n";
        out+="buildAllTimepoints: "+buildAllTimepoints+"\n";
        out+="startTimepoint: "+startTimepoint+"\n";
        out+="stopTimepoint: "+stopTimepoint+"\n";
        out+="zModeIndex: "+zModeIndex+"\n";
        out+="projModeIndex: "+projModeIndex+"\n";
        out+="startSlice: "+startSlice+"\n";
        out+="stopSlice: "+stopSlice+"\n";
        out+="saveStack: "+saveStack+"\n";
        out+="showStack: "+showStack+"\n";
        out+="showLUTChooser: "+showLUTChooser+"\n";
        out+="saveShowModeIndex: "+saveShowModeIndex;
        System.out.println(out);
    }
}
