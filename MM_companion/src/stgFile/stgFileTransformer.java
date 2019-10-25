/**
 *
 *  stgFileTransformer v1, 4 mai 2013 
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

package stgFile;

import ij.IJ;
import stgFile.content.position;
import utilities.tools;

/**
 * This class allows transforming a stage positions file (flip, scale, rotate, translate)
 * @author fab
 */
public class stgFileTransformer {
    /** Flip X **/
    public static final int FLIP_X=0;
    
    /** Flip Y **/
    public static final int FLIP_Y=1;
    
    /** Flip types names**/
    public static final String[] FLIP_NAMES={"Flip_X", "Flip_Y"};
    
    /** Stage positions file to transform **/
    public stgFile stg=null;
    
    /** Tag to activate to do a stg file flip **/
    public boolean doFlip=false;
    
    /** Tag to activate to do a X stg file flip **/
    public boolean doFlipX=false;
    
    /** Tag to activate to do a Y stg file flip **/
    public boolean doFlipY=false;
    
    /** Flip type **/
    public int flipType=-1;
    
    /** Tag to activate to do a stg file scaling **/
    public boolean doScale=false;
    
    /** Scaling factor **/
    public double scaleFactor=1;
    
    /** Tag to activate to do a stg file rotation **/
    public boolean doRotate=false;
    
    /** Angle of rotation **/
    public double angle=0;
    
    /** Tag to activate to do a stg file translation **/
    public boolean doTranslate=false;
    
    /** Translation translationVector **/
    public position translationVector=null;
    
    
    /**
     * Creates a new instance of stgFileTransformer
     * @param stg the stage positions file to transform
     */
    public stgFileTransformer(stgFile stg){
        this.stg=stg;
    }
    
    /**
     * Computes a xy rotation around the origin, of the input angle for the stage positions file
     * @param origin a position representing the origin of the referential
     * @param angle angle of rotation in degrees
     */
    public void rotate(position origin, double angle){
        for(int i=0; i<stg.nStagePositions; i++) stg.stagePositionsList.get(i).xyRotate(origin, angle);
    }
    
    /**
     * Computes a xy rotation of the input angle for the current stage positions list, the origin 
     * of referential being taken as (0, 0)
     * @param angle angle of rotation in degrees
     */
    public void rotate(double angle){
        rotate(new position(), angle);
    }
    
    
    /**
     * Computes a xy scaled version of the current stage positions list, using the input origin, and the input scaling factor
     * @param origin a position representing the origin of the referential
     * @param scaleFactor scaling factor
     */
    public void scale(position origin, double scaleFactor){
        for(int i=0; i<stg.nStagePositions; i++) stg.stagePositionsList.get(i).xyScale(origin, scaleFactor);
    }
    
    /**
     * Computes a xy scaled version of the current stage positions list, using the input scaling factor, 
     * the origin of referential being taken as (0, 0)
     * @param scaleFactor scaling factor
     */
    public void scale(double scaleFactor){
        scale(new position(), scaleFactor);
    }
    
    /**
     * Computes the extrapolated version of the Z coordinate(s)of the current stage 
     * positions list, based on 3 reference positions
     * @param ref1 reference position 1
     * @param ref2 reference position 2
     * @param ref3 reference position 3
     */
    public void zExtrapolate(position ref1, position ref2, position ref3){
        for(int i=0; i<stg.nStagePositions; i++) stg.stagePositionsList.get(i).zExtrapolate(ref1, ref2, ref3);
    }
    
    /**
     * Computes a translated version of the current stage positions list, using the input position as 
     * a translation translationVector
     * @param translationVector the amplitude of translation, as a position
     */
    public void translate(position translationVector){
        for(int i=0; i<stg.nStagePositions; i++) stg.stagePositionsList.get(i).translate(translationVector);
    }
    
    /**
     * Computes the symetrical version of the current stage positions list, relative to the input origin position
     * @param origin the origin of the referential
     */
    public void symetry(position origin){
        for(int i=0; i<stg.nStagePositions; i++) stg.stagePositionsList.get(i).symetry(origin);
    }
    
    /**
     * Computes the symetrical version of the current stage positions list, relative to the input origin X position
     * @param origin the origin of the referential
     */
    public void xSymetry(position origin){
        for(int i=0; i<stg.nStagePositions; i++) stg.stagePositionsList.get(i).xSymetry(origin);
    }
    
    /**
     * Computes the symetrical version of the current stage positions list, relative to the input origin Y position
     * @param origin the origin of the referential
     */
    public void ySymetry(position origin){
        for(int i=0; i<stg.nStagePositions; i++) stg.stagePositionsList.get(i).ySymetry(origin);
    }
    
    /**
     * Computes the symetrical version of the current stage positions list, relative to the (0,0) position
     */
    public void symetry(){
        symetry(new position());
    }
    
    /**
     * Computes the symetrical version of the current stage positions list, relative to the (0,0) position
     */
    public void xSymetry(){
        xSymetry(new position());
    }
    
    /**
     * Computes the symetrical version of the current stage positions list, relative to the (0,0) position
     */
    public void ySymetry(){
        ySymetry(new position());
    }
    
    
    /**
     * Performs a stg file transformation, according to tags set
     */
    public void transform(){
        if(doFlip){
            switch(flipType){
                case FLIP_X: xSymetry();break;
                case FLIP_Y: ySymetry();break;
            }
        }

        if(doScale) scale(scaleFactor);
        if(doRotate) rotate(angle);
        if(doTranslate && translationVector!=null) translate(translationVector);
    }
    
    /**
     * Adapts a stage file positions, using 3 reference positions
     * @param ref a stage file used as reference
     * @param refPos the indexes of the 3 reference positions within the reference stage positions file
     * @param initPos the indexes of the 3 reference positions within the stage positions file to transform
     */
    public void adapt(stgFile ref, int[] refPos, int[] initPos){
        position refPos1=ref.getPosition(refPos[0]);
        position refPos2=ref.getPosition(refPos[1]);
        position refPos3=ref.getPosition(refPos[2]);
        
        position initPos1=stg.getPosition(initPos[0]);
        position initPos2=stg.getPosition(initPos[1]);
        position initPos3=stg.getPosition(initPos[2]);
        
        //Checks if the positions to adapt are flipped relative to the reference and applies it
        //doFlip=tools.restrictAngle(refPos1.angleXY(refPos2, refPos3))*tools.restrictAngle(initPos1.angleXY(initPos2, initPos3))<0;
        //if(doFlip) symetry(refPos1);
        doFlipX=(refPos2.stageXCoordinate-refPos1.stageXCoordinate)*(initPos2.stageXCoordinate-initPos1.stageXCoordinate)<0;
        if(doFlipX) xSymetry(refPos1);
        
        doFlipY=(refPos3.stageYCoordinate-refPos1.stageYCoordinate)*(initPos3.stageYCoordinate-initPos1.stageYCoordinate)<0;
        if(doFlipY) ySymetry(refPos1);
        
        //Calculates and applies the scaling factor, based on the mean distances 
        //between the 3 reference points
        scaleFactor=refPos1.distanceXY(refPos2)/initPos1.distanceXY(initPos2);
        scaleFactor+=refPos2.distanceXY(refPos3)/initPos2.distanceXY(initPos3);
        scaleFactor+=refPos3.distanceXY(refPos1)/initPos3.distanceXY(initPos1);
        scaleFactor/=3;
        doScale=scaleFactor!=1;
        if(doScale) scale(scaleFactor);
        
        //Moves initPos1 to refPos1 and applies the same translation to all other points
        initPos1=stg.getPosition(initPos[0]);
        
        translationVector=new position(refPos1.stageXCoordinate-initPos1.stageXCoordinate,
                                       refPos1.stageYCoordinate-initPos1.stageYCoordinate,
                                       refPos1.stageZCoordinate-initPos1.stageZCoordinate,
                                       refPos1.AFOffset-initPos1.AFOffset+refPos2.AFOffset,
                                       refPos1.stageZ2Coordinate-initPos1.stageZ2Coordinate);
        
        doTranslate=   translationVector.stageXCoordinate!=0
                    || translationVector.stageYCoordinate!=0
                    || translationVector.stageZCoordinate!=0
                    || translationVector.AFOffset!=0
                    || translationVector.stageZ2Coordinate!=0;
        if(doTranslate) translate(translationVector);
        
        //Calculates the angle and applies the rotation, based on the angles 
        //between the 3 reference points
        initPos2=stg.getPosition(initPos[1]);
        initPos3=stg.getPosition(initPos[2]);
        
        double A1=tools.restrictAngle(refPos1.angleXY(initPos2, refPos2));
        double A2=tools.restrictAngle(refPos1.angleXY(initPos3, refPos3));
        
        angle=(A1+A2)/2;
        
        
        //System.out.println(A1+"/"+A2+"==>"+angle);
        
        doRotate=angle!=0;
        if(doRotate) rotate(refPos1, angle);
        
        //Extrapolates the z position relative to the 3 reference positions
        zExtrapolate(refPos1, refPos2, refPos3);
        
        IJ.log(toString());
    }
    
    /**
     * Resets all transformation parameters to their original values
     */
    public void resetTransformationParameters(){
        doFlip=false;
        flipType=-1;
        doScale=false;
        scaleFactor=1;
        doRotate=false;
        angle=0;
        doTranslate=false;
        translationVector=null;
    }
    
    /**
     * Replaces all positions' Z coordinate by the input value
     * @param Z the new Z coordinate to apply to all positions
     */
    public void fixZ(float Z){
        for(int i=0; i<stg.nStagePositions; i++) stg.stagePositionsList.get(i).stageZCoordinate=Z;
    }
    
    /**
     * Replaces all positions' Z2 coordinate by the input value
     * @param Z2 the new Z2 coordinate to apply to all positions
     */
    public void fixZ2(float Z2){
        for(int i=0; i<stg.nStagePositions; i++) stg.stagePositionsList.get(i).stageZ2Coordinate=Z2;
    }
    
    /**
     * Replaces all positions' AFOffset coordinate by the input value
     * @param AFOffset the new AFOffset coordinate to apply to all positions
     */
    public void fixAFOffset(float AFOffset){
        for(int i=0; i<stg.nStagePositions; i++) stg.stagePositionsList.get(i).AFOffset=AFOffset;
    }
    
    @Override
    public String toString(){
        String out="-------------------\n";
        out+="stgFileTransformer:\n";
        out+="STG file name: "+stg.getFileName()+"\n";
        //out+="doFlip: "+doFlip+"\n";
        out+="doFlipX: "+doFlipX+"\n";
        out+="doFlipY: "+doFlipY+"\n";
        out+="doScale: "+doScale+"\n";
        out+="scaleFactor: "+scaleFactor+"\n";
        out+="doRotate: "+doRotate+"\n";
        out+="angle (°): "+angle+"\n";
        out+="doTranslate: "+doTranslate+"\n";
        out+="translationVector: "+"\n";
        out+="\tX: "+translationVector.stageXCoordinate+"\n";
        out+="\tY: "+translationVector.stageYCoordinate+"\n";
        out+="\tZ: "+translationVector.stageZCoordinate+"\n";
        out+="\tAFOffset: "+translationVector.AFOffset+"\n";
        out+="\tZ2: "+translationVector.stageZ2Coordinate+"\n";
        out+="-------------------";
    
        return out;
    }
    
    

}
