/**
 *
 *  drawStgPos v1, 14 avr. 2013 
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

package plugins.stgPos;

import ij.IJ;
import ij.Prefs;
import ij.gui.GenericDialog;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;
import stgFile.stgFile;

/**
 * This plugin returns a graphical representation of a stage positions file
 * @author fab
 */
public class drawStgPos  implements PlugIn{
    
    /**
     * This method is called when the plugin is loaded.
     * @param arg argument specified for this plugin in IJ_Props.txt (may be blank)
     */
    @Override
    public void run(String arg) {
        int targetWidth=(int) Prefs.get("drawStgPos_targetWidth.double", 1024);
        double bordersWidth=Prefs.get("drawStgPos_bordersWidth.double", 50);
        boolean drawPath=Prefs.get("drawStgPos_drawPath.boolean", false);
        boolean drawNames=Prefs.get("drawStgPos_drawNames.boolean", true);
        boolean drawBlack=Prefs.get("drawStgPos_drawBlack.boolean", true);
        
        
        OpenDialog od=new OpenDialog("Where is the stage positions file ?", null);
        
        if(od.getFileName()!=null){
            stgFile stg=new stgFile(od.getDirectory(), od.getFileName());

            GenericDialog gd=new GenericDialog("Draw Stage Position File");
            gd.addNumericField("Image_target_width", targetWidth, 0);
            gd.addNumericField("Borders_width_(%)", bordersWidth, 0);
            gd.addCheckbox("Draw_path", drawPath);
            gd.addCheckbox("Draw_positions_names", drawNames);
            gd.addCheckbox("Draw_in_black", drawBlack);
            gd.showDialog();

            if(gd.wasOKed()){
                targetWidth=(int) gd.getNextNumber();
                targetWidth=(targetWidth<=0)? 1024:targetWidth;
                bordersWidth=gd.getNextNumber();
                bordersWidth=(bordersWidth>100 || bordersWidth <0)? 50:bordersWidth;
                drawPath=gd.getNextBoolean();
                drawNames=gd.getNextBoolean();
                drawBlack=gd.getNextBoolean();

                Prefs.set("drawStgPos_targetWidth.double", targetWidth);
                Prefs.set("drawStgPos_bordersWidth.double", bordersWidth);
                Prefs.set("drawStgPos_drawPath.boolean", drawPath);
                Prefs.set("drawStgPos_drawNames.boolean", drawNames);
                Prefs.set("drawStgPos_drawBlack.boolean", drawBlack);

                stg.getImage(targetWidth, bordersWidth, drawPath, drawNames, drawBlack).show();
                IJ.showStatus("--- Draw stage positions file done ---");
            }
        }else{
            IJ.showStatus("--- Draw stage positions file cancelled ---");
        }
        
    }

}
