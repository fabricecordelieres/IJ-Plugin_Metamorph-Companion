/**
 *
 *  LUT_Chooser v1, 26 mai 2013 
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
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import java.awt.Color;

/**
 * This plugin is aimed at storing the LUTs to be used for instance by the 
 * buildNdStack plugin
 * @author fab
 */
public class LUT_Chooser implements PlugIn{
    /** List of LUTs **/
    public static final String[] LUTs={"Red", "Green", "Blue", "Gray", "Cyan", "Magenta", "Yellow", "Gray"};
    
    /** List of colors **/
    public static final Color[] colors={Color.red, Color.green, Color.blue, Color.gray, Color.cyan, Color.magenta, Color.yellow, Color.gray};
    
    /** Array of the indexes of the default LUTs **/
    public static int[] lutsIndex=new int[LUTs.length];

    /**
     * This method is called when the plugin is loaded.
     * @param arg argument specified for this plugin in IJ_Props.txt (may be blank)
     */
    @Override
    public void run(String arg) {
        GUI(new String[8]);
    }
    
    /**
     * Displays the GUI and stores the preferences
     * @param waveNames an array containing the wave names to be concatenated to the wX indication
     * @return true if the dialog was Oked, false otherwise
     */
    public static boolean GUI(String[] waveNames){
        getLUTsIndexes();
        
        GenericDialog gd=new GenericDialog("LUT Chooser");
        for(int i=0; i<waveNames.length; i++) gd.addChoice("w"+(i+1)+(waveNames[i]==null?"":waveNames[i]), LUTs, LUTs[lutsIndex[i]]);
        gd.showDialog();
        
        if(gd.wasOKed()){
            for(int i=0; i<waveNames.length; i++) Prefs.set("LUT_Chooser_w"+(i+1)+".double", gd.getNextChoiceIndex());
            IJ.showStatus("--- LUTs choice done ---");
            return true;
        }else{
            IJ.showStatus("--- LUTs choice cancelled ---");
            return false;
        }
    }
    
    /**
     * Builds an array of integers containing the indexes of the LUTs to be used
     * @return an array of integers containing the indexes of the LUTs to be used 
     */
    public static int[] getLUTsIndexes(){
        for(int i=0; i<LUTs.length; i++) lutsIndex[i]=(int) Prefs.get("LUT_Chooser_w"+(i+1)+".double", i);
        return lutsIndex;
    }

}
