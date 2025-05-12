/**
 *
 *  transformStgPos v1, 4 may 2013 
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
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.io.OpenDialog;
import ij.io.SaveDialog;
import ij.plugin.PlugIn;
import java.awt.AWTEvent;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.TextField;
import java.util.Vector;
import stgFile.content.position;
import stgFile.stgFile;
import stgFile.stgFileTransformer;
/**
 * This plugin returns a transformed version of a stage positions file
 * @author fab
 */
public class transformStgPos implements PlugIn, DialogListener{
    boolean doFlip=Prefs.get("transformStgPos_doFlip.boolean", true);
    int flipType=(int) Prefs.get("transformStgPos_flipType.double", 0);
    boolean doScale=Prefs.get("transformStgPos_doScale.double", true);
    double scaleFactor=Prefs.get("transformStgPos_scaleFactor.double", 1);
    boolean doRotate=Prefs.get("transformStgPos_doRotate.double", true);
    double angle=Prefs.get("transformStgPos_angle.double", 1);
    boolean doTranslate=Prefs.get("transformStgPos_doTranslate.boolean", true);
    float X=(float) Prefs.get("transformStgPos_xTranslate.double", 0);
    float Y=(float) Prefs.get("transformStgPos_yTranslate.double", 0);
    float Z=(float) Prefs.get("transformStgPos_zTranslate.double", 0);
    float AFOffset=(float) Prefs.get("transformStgPos_AFOffsetTranslate.double", 0);
    float Z2=(float) Prefs.get("transformStgPos_z2Translate.double", 0);
    boolean doFixZ=Prefs.get("transformStgPos_doFixZ.boolean", true);
    float zfix=(float) Prefs.get("transformStgPos_zfix.double", 0);
    boolean doFixAFOffset=Prefs.get("transformStgPos_doFixAFOffset.boolean", true);
    float AFOffsetFix=(float) Prefs.get("transformStgPos_AFOffsetfix.double", 0);
    boolean doFixZ2=Prefs.get("transformStgPos_doFixZ2.boolean", true);
    float z2fix=(float) Prefs.get("transformStgPos_z2fix.double", 0);
    
    
    Vector<Checkbox> cb=null;
    Vector<Choice> c=null;
    Vector<TextField> tf=null;
    
    /**
     * This method is called when the plugin is loaded.
     * @param arg argument specified for this plugin in IJ_Props.txt (may be blank)
     */
    @SuppressWarnings("unchecked")
	@Override
    public void run(String arg) {
        OpenDialog od=new OpenDialog("Where is the stage positions file ?", null);
        
        GenericDialog gd=new GenericDialog("Transform Stage Position File");
        gd.addCheckbox("Flip", doFlip);
        gd.addChoice("Flip_type", stgFileTransformer.FLIP_NAMES, stgFileTransformer.FLIP_NAMES[flipType]);
        gd.addCheckbox("Scale", doScale);
        gd.addNumericField("Scale_factor", scaleFactor, 2);
        gd.addCheckbox("Rotate", doRotate);
        gd.addNumericField("Angle_(degrees)", angle, 2);
        gd.addCheckbox("Translate", doTranslate);
        gd.addNumericField("X_translation", X, 2);
        gd.addNumericField("Y_translation", Y, 2);
        gd.addNumericField("Z_translation", Z, 2);
        gd.addNumericField("AFOffset_translation", AFOffset, 2);
        gd.addNumericField("Z2_translation", Z2, 2);
        gd.addCheckbox("Fix_Z_position", doFixZ);
        gd.addNumericField("Z_position_value", zfix, 2);
        gd.addCheckbox("Fix_AFOffset_position", doFixAFOffset);
        gd.addNumericField("AFOffset_position_value", AFOffsetFix, 2);
        gd.addCheckbox("Fix Z2 position", doFixZ2);
        gd.addNumericField("Z2_position_value", z2fix, 2);
        
        
        
        
        
        cb=gd.getCheckboxes();
        c=gd.getChoices();
        tf=gd.getNumericFields();
        gd.addDialogListener(this);
        dialogItemChanged(gd, null);
        gd.showDialog();
        
        if(gd.wasOKed()){
            stgFile stg=new stgFile(od.getDirectory(), od.getFileName());
            stgFileTransformer trans=new stgFileTransformer(stg);
            
            trans.doFlip=doFlip;
            trans.flipType=flipType;
            trans.doScale=doScale;
            trans.scaleFactor=scaleFactor;
            trans.doRotate=doRotate;
            trans.angle=angle;
            trans.doTranslate=doTranslate;
            trans.translationVector=new position(X, Y, Z, AFOffset, Z2);
            trans.transform();
            
            if(doFixZ) trans.fixZ(zfix);
            if(doFixAFOffset) trans.fixAFOffset(AFOffsetFix);
            if(doFixZ2) trans.fixZ2(z2fix);
            
            SaveDialog sd=new SaveDialog("Where to save the stage positions file ?", "Transformed_"+od.getFileName() ,null);
            stg.write(sd.getDirectory()+sd.getFileName());
            IJ.showStatus("--- Transformed stage positions file saved ---");
        }else{
            IJ.showStatus("--- Stage positions file transformation cancelled ---");
        }
    }

    @Override
    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        doFlip=gd.getNextBoolean();
        flipType=gd.getNextChoiceIndex();
        doScale=gd.getNextBoolean();
        scaleFactor=gd.getNextNumber();
        doRotate=gd.getNextBoolean();
        angle=gd.getNextNumber();
        doTranslate=gd.getNextBoolean();
        X=(float) gd.getNextNumber();
        Y=(float) gd.getNextNumber();
        Z=(float) gd.getNextNumber();
        AFOffset=(float) gd.getNextNumber();
        Z2=(float) gd.getNextNumber();
        doFixZ=gd.getNextBoolean();
        zfix=(float) gd.getNextNumber();
        doFixAFOffset=gd.getNextBoolean();
        AFOffsetFix=(float) gd.getNextNumber();
        doFixZ2=gd.getNextBoolean();
        z2fix=(float) gd.getNextNumber();
        
        //doFlip
        c.elementAt(0).setEnabled(doFlip);

        //doScale
        tf.elementAt(0).setEnabled(doScale);

        //doRotate
        tf.elementAt(1).setEnabled(doRotate);

        //doTranslate
        tf.elementAt(2).setEnabled(doTranslate);
        tf.elementAt(3).setEnabled(doTranslate);
        tf.elementAt(4).setEnabled(doTranslate);
        tf.elementAt(5).setEnabled(doTranslate);
        tf.elementAt(6).setEnabled(doTranslate);
        
        //doFix
        tf.elementAt(7).setEnabled(doFixZ);
        tf.elementAt(8).setEnabled(doFixAFOffset);
        tf.elementAt(9).setEnabled(doFixZ2);
        
        //Ok button
        if(gd.isVisible()) ((Button) gd.getButtons()[0]).setVisible(doFlip || doScale || doRotate || doTranslate || doFixZ || doFixAFOffset || doFixZ2);
        
        Prefs.set("transformStgPos_doFlip.boolean", doFlip);
        Prefs.set("transformStgPos_flipType.double", flipType);
        Prefs.set("transformStgPos_doScale.double", doScale);
        Prefs.set("transformStgPos_scaleFactor.double", scaleFactor);
        Prefs.set("transformStgPos_doRotate.double", doRotate);
        Prefs.set("transformStgPos_angle.double", angle);
        Prefs.set("transformStgPos_doTranslate.boolean", doTranslate);
        Prefs.set("transformStgPos_xTranslate.double", X);
        Prefs.set("transformStgPos_yTranslate.double", Y);
        Prefs.set("transformStgPos_zTranslate.double", Z);
        Prefs.set("transformStgPos_AFOffsetTranslate.double", AFOffset);
        Prefs.set("transformStgPos_z2Translate.double", Z2);
        Prefs.set("transformStgPos_doFixZ.boolean", doFixZ);
        Prefs.set("transformStgPos_zfix.double", zfix);
        Prefs.set("transformStgPos_doFixAFOffset.boolean", doFixAFOffset);
        Prefs.set("transformStgPos_AFOffsetfix.double", AFOffsetFix);
        Prefs.set("transformStgPos_doFixZ2.boolean", doFixZ2);
        Prefs.set("transformStgPos_z2fix.double", z2fix);
        
        return true;
    }
}
