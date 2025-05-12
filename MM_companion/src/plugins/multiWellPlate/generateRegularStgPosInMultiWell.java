/**
 *
 *  generateRegularStgPosInMultiWell v1, 8 mai 2013 
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

package plugins.multiWellPlate;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.io.FileSaver;
import ij.io.SaveDialog;
import ij.plugin.PlugIn;
import java.awt.AWTEvent;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.TextField;
import java.util.Vector;
import multiWellPlate.multiWellPlate;
import plugins.stgPos.adaptStgPos;
import stgFile.stgFile;

/**
 * This plugin allows creating a new STG stage positions file from a multi-well plate matrix
 * @author fab
 */
public class generateRegularStgPosInMultiWell implements PlugIn, DialogListener{
    /** The matrix type **/
    int matrixType=(int) Prefs.get("matrixStgPos_matrixType.double", 0);
    /** Enabled/Disabled array **/
    static final String[] ENABLED_DISABLED=new String[]{"Enabled", "Disabled"};
    /** All wells enabled ? **/
    boolean enableAllWells=Prefs.get("matrixStgPos_enableAllWells.boolean", true);
    /** Generate regularly spaced stage positions **/
    boolean generateRegularPositions=Prefs.get("matrixStgPos_generateRegularPositions.boolean", true);
    /** Number of columns of spots per well to generate **/
    int nColumns=(int) Prefs.get("matrixStgPos_nColumns.double", 2);
    /** Number of rows of spots per well to generate **/
    int nRows=(int) Prefs.get("matrixStgPos_nRows.double", 2);
    /** Inter-spot distance in mm **/
    double interSpotDistance=Prefs.get("matrixStgPos_interSpotDistance.double", 1.2);
    
    /** Include references in the STG file **/
    boolean includeReferences=Prefs.get("matrixStgPos_includeReferences.boolean", true);
    /** Type of reference points to add **/
    int referencesOption=(int) Prefs.get("matrixStgPos_referencesOption.double", 0);
    /** Include centres in the STG file **/
    boolean includeCentres=Prefs.get("matrixStgPos_includeCentres.boolean", true);
    /** Show map that is generated **/
    boolean showMap=Prefs.get("matrixStgPos_showMap.boolean", true);
    /** True if the STG file should be adapted to a reference STG file **/
    boolean adaptFile=Prefs.get("matrixStgPos_adaptFile.boolean", true);
    
    /** Vector of the checkboxes from the GenericDialog **/
    Vector<Checkbox> cb=null;
    /** Vector of the choices from the GenericDialog **/
    Vector<Choice> c=null;
    /** Vector of the numeric fields from the GenericDialog **/
    Vector<TextField> nf=null;
    
    /** Multi-well plate matrix **/
    multiWellPlate mwp=null;
    /** Reference to the adaptStgPos process **/
    adaptStgPos asp;
    
    /**
     * This method is called when the plugin is loaded.
     * @param arg argument specified for this plugin in IJ_Props.txt (may be blank)
     */
    @Override
    public void run(String arg) {
        if(GUI()) if(GUIbis()) saveFiles();
        savePrefs();       
    }
    
    /**
     * First graphical user interface, asking the type of multi-well plate to generate
     * @return true if the dialog was OKed, false otherwise
     */
    public boolean GUI(){
        GenericDialog gd=new GenericDialog("Muti-well plate to STG file");
        gd.addChoice("Multi-well_plate_type", multiWellPlate.FORMAT, multiWellPlate.FORMAT[matrixType]);
        gd.addChoice("Set_all_wells_as", ENABLED_DISABLED, ENABLED_DISABLED[enableAllWells?0:1]);
        gd.showDialog();

        if(gd.wasOKed()){
            matrixType=gd.getNextChoiceIndex();
            mwp=new multiWellPlate(matrixType);
            enableAllWells=gd.getNextChoiceIndex()==0;
            mwp.changeAllWellsStatus(enableAllWells);
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * Second graphical user interface, asking the options to be used for the multi-well plate to generate
     * @return true if the dialog was OKed, false otherwise
     */
    @SuppressWarnings("unchecked")
	public boolean GUIbis(){
        GenericDialog gd=new GenericDialog("Muti-well plate to STG file: Options");
        gd.addMessage("Enable/Disable wells:");
        gd.addMessage("");
        gd.addCheckboxGroup(mwp.nRows, mwp.nColumns, mwp.getWellNames(), mwp.getWellStatus());
        gd.addMessage("");
        gd.addMessage("");
        gd.addCheckbox("Generate_regularly_spaced_positions", generateRegularPositions);
        gd.addNumericField("Nb_of_columns_per_well", nColumns, 0);
        gd.addNumericField("Nb_of_rows_per_well", nRows, 0);
        gd.addNumericField("Inter_columns/rows_distance", interSpotDistance, 2);
        gd.addMessage("Additional options:");
        gd.addCheckbox("Include_references", includeReferences);
        gd.addChoice("References_option", multiWellPlate.REFERENCE_NAME, multiWellPlate.REFERENCE_NAME[referencesOption]);
        gd.addCheckbox("Include_well_centres", includeCentres);
        gd.addCheckbox("Show_map", showMap);
        gd.addCheckbox("Adapt_file", adaptFile);

        cb=gd.getCheckboxes();
        c=gd.getChoices();
        nf=gd.getNumericFields();

        gd.addDialogListener(this);
        dialogItemChanged(gd, null);

        gd.showDialog();

        if(gd.wasOKed()){
            boolean[] status=new boolean[mwp.nRows*mwp.nColumns];
            for(int i=0; i<status.length; i++) status[i]=gd.getNextBoolean();
            mwp.setWellStatus(status);
            generateRegularPositions=gd.getNextBoolean();
            nColumns=(int) gd.getNextNumber();
            nRows=(int) gd.getNextNumber();
            interSpotDistance=gd.getNextNumber();
            includeReferences=gd.getNextBoolean();
            referencesOption=gd.getNextChoiceIndex();
            includeCentres=gd.getNextBoolean();
            showMap=gd.getNextBoolean();
            adaptFile=gd.getNextBoolean();

            if(includeCentres) mwp.addCentres();
            if(includeReferences) mwp.addReferences(referencesOption==1);
            
            if(generateRegularPositions) mwp.addRegularPois(nColumns, nRows, interSpotDistance);
            
            if(adaptFile){
                return adaptFile();
            }else{
                return true;
            }
        }else{
            return false;
        }
        
    }
    
    /**
     * Method to be called in case the STG file has to be generated
     * @return true if everything went fine, false otherwise
     */
    public boolean adaptFile(){
        asp=new adaptStgPos();
        asp.stg2Adapt=mwp.getStageFile();
        return asp.openRef() && asp.GUI();
    }
    
    /**
     * Save all files (original STG file, adapted STG file and image as jpg)
     * @return true if the dialog was OKed, false otherwise
     */
    public boolean saveFiles(){
        SaveDialog sd=new SaveDialog("Where_to_save_the_stage_positions_file(s)_?", multiWellPlate.FORMAT[matrixType], ".STG");
        if(sd.getFileName()!=null){
            stgFile sf=mwp.getStageFile();
            sf.write(sd.getDirectory()+sd.getFileName());
            
            if(adaptFile) asp.stg2Adapt.write(sd.getDirectory()+"Adapted_"+sd.getFileName());
            
            ImagePlus ip=mwp.getImage(600);
            new FileSaver(ip).saveAsJpeg(sd.getDirectory()+sd.getFileName().replace(".STG", ".jpg"));
            if(showMap) ip.show();
            IJ.showStatus("--- Stage positions file(s) saved ---");
            return true;
        }else{
            IJ.showStatus("--- Process cancelled ---");
            return false;
        }
    }
    
    /**
     * Saves all preferences
     */
    public void savePrefs(){
        Prefs.set("matrixStgPos_matrixType.double", matrixType);
        Prefs.set("matrixStgPos_enableAllWells.boolean", enableAllWells);
        Prefs.set("matrixStgPos_generateRegularPositions.boolean", generateRegularPositions);
        Prefs.set("matrixStgPos_referencesOption.double", referencesOption);
        Prefs.set("matrixStgPos_nColumns.double", nColumns);
        Prefs.set("matrixStgPos_nRows.double", nRows);
        Prefs.set("matrixStgPos_interSpotDistance.double", interSpotDistance);
        Prefs.set("matrixStgPos_includeReferences.boolean", includeReferences);
        Prefs.set("matrixStgPos_includeCentres.boolean", includeCentres);
        Prefs.set("matrixStgPos_showMap.boolean", showMap);
        Prefs.set("matrixStgPos_adaptFile.boolean", adaptFile);
        
    }

    @Override
    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        c.elementAt(0).setEnabled(cb.get(cb.size()-4).getState());
        nf.elementAt(0).setEnabled(cb.get(cb.size()-5).getState());
        nf.elementAt(1).setEnabled(cb.get(cb.size()-5).getState());
        return true;
    }
}
