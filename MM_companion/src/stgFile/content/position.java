/**
 *
 *  position v1, 16 juil. 2012 
    Fabrice P Cordelieres, fabrice.cordelieres at gmail.com
    
    Copyright (C) 2012 Fabrice P. Cordelieres
  
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
package stgFile.content;

/**
 * This class stores positions from a Metamorph MDA stage list
 * @author fab
 */
public class position {
    /** Stage position name **/
    public String stagePositionName="";
    
    /** Stage X coordinate **/
    public float stageXCoordinate=0;
    
    /** Stage Y coordinate **/
    public float stageYCoordinate=0;
    
    /** Stage Z coordinate **/
    public float stageZCoordinate=0;
    
    /** AF Offset **/
    public float AFOffset=0;
    
    /** Stage Z2 coordinate **/
    public float stageZ2Coordinate=0;
    
    /** Use travel offset **/
    public boolean useTravelOffset=false;
    
    /** Sample loader position **/
    public float sampleLoaderPosition=-9999;
    
    /** Stage coordinates are absolute **/
    public boolean stageCoordinatesAreAbsolute=true;
    
    /** Use stage position **/
    public boolean useStagePosition=true;
    
    /** Category index **/
    public int categoryIndex=0;
    
    /** Hardware auto focus memorize position ID **/
    public int hardwareAutofocusMemorizePositionID=-1;
    
    /** Hardware auto focus memorize position data **/
    public String hardwareAutofocusMemorizePositiondata="";
    
    /**
     * Creates a new position object, based on default informations
     */
    public position(){
        
    }
    /**
     * Creates a new position object, based on default informations and the provided
     * X, Y, Z coordinates
     * @param X stage X coordinate 
     * @param Y stage Y coordinate
     * @param Z stage Z coordinate
     * @param AFOffset stage autofocus offset coordinate
     * @param Z2 stage Z2 coordinate
     */
    public position(float X, float Y, float Z, float AFOffset, float Z2){
        stageXCoordinate=X;
        stageYCoordinate=Y;
        stageZCoordinate=Z;
        this.AFOffset=AFOffset;
        stageZ2Coordinate=Z2;
    }
    
    /**
     * Creates a new position object, based on a string containing all informations
     * (to be used when reading the positions from a MDA stage list file)
     * @param line A string representing a position
     */
    public position(String line){
        //Required to handle cases where strings such as position names have a " ," included
        String[] start=line.split("\", ");
        String[] end=start[1].split(", \"");
        String[] middle=end[0].split(", ");
        
        if(start.length!=2 && end.length!=2 && middle.length!=11) throw new IllegalArgumentException("Can't create a new position without the 13 requiered argument");
        
        stagePositionName=start[0].replaceAll("\"", "");
        stageXCoordinate=Float.parseFloat(middle[0]);
        stageYCoordinate=Float.parseFloat(middle[1]);
        stageZCoordinate=Float.parseFloat(middle[2]);
        AFOffset=Float.parseFloat(middle[3]);
        stageZ2Coordinate=Float.parseFloat(middle[4]);
        useTravelOffset=Boolean.parseBoolean(middle[5]);
        sampleLoaderPosition=Float.parseFloat(middle[6]);
        stageCoordinatesAreAbsolute=Boolean.parseBoolean(middle[7]);
        useStagePosition=Boolean.parseBoolean(middle[8]);
        categoryIndex=Integer.parseInt(middle[9]);
        hardwareAutofocusMemorizePositionID=Integer.parseInt(middle[10]);
        hardwareAutofocusMemorizePositiondata=end[1].replaceAll("\"", "");
        
    }
    
    /**
     * Creates a new position object, based on input informations
     * @param stagePositionName Stage position name
     * @param stageXCoordinate Stage X coordinate
     * @param stageYCoordinate Stage Y coordinate
     * @param stageZCoordinate Stage Z coordinate
     * @param AFOffset AF Offset
     * @param stageZ2Coordinate Stage Z2 coordinate
     * @param useTravelOffset Use travel offset
     */
    public position(String stagePositionName, float stageXCoordinate, float stageYCoordinate, float stageZCoordinate, float AFOffset, float stageZ2Coordinate, boolean useTravelOffset){
        this.stagePositionName=stagePositionName.replaceAll("\"", "");
        this.stageXCoordinate=stageXCoordinate;
        this.stageYCoordinate=stageYCoordinate;
        this.stageZCoordinate=stageZCoordinate;
        this.AFOffset=AFOffset;
        this.stageZ2Coordinate=stageZ2Coordinate;
        this.useTravelOffset=useTravelOffset;
    }
    
    /**
     * Calculates the euclidian XY distance between the current position and the 
     * one entered as argument
     * @param pos the position to which the XY distance should be calculated
     * @return the euclidian XY distance between the current position and the 
     * one entered as argument
     */
    public double distanceXY(position pos){
        return Math.sqrt((pos.stageXCoordinate-stageXCoordinate)*(pos.stageXCoordinate-stageXCoordinate)+(pos.stageYCoordinate-stageYCoordinate)*(pos.stageYCoordinate-stageYCoordinate));
    }
    
    /**
     * Calculates the angle formed between the current position, an input position 
     * and the horizontal, taking the current position as tip of the angle
     * @param pos the position with which the angle to the horizontal should be calculated
     * @return the angle, in degrees
     */
    public double angleXY(position pos){
        return Math.atan2(pos.stageYCoordinate-stageYCoordinate, pos.stageXCoordinate-stageXCoordinate)*180/Math.PI;
    }
    
    /**
     * Calculates the angle formed between the current position, an two input positions 
     * BÂC (i.e. taking the current position as tip of the angle)
     * @param posB the first position
     * @param posC the second position
     * @return the angle, in degrees
     */
    public double angleXY(position posB, position posC){
        return angleXY(posC)-angleXY(posB);
    }
    
    /**
     * Computes a xy rotation around the origin, of the input angle for the current position
     * @param origin a position representing the origin of the referential
     * @param angle angle of rotation in degrees
     */
    public void xyRotate(position origin, double angle){
        origin.invert();
        translate(origin);
//        double distOrigin=distanceXY(new position());
//        double initAngle=Math.atan2(stageYCoordinate, stageXCoordinate);
//        stageXCoordinate=(float) (distOrigin*Math.cos(initAngle+angle*Math.PI/180));
//        stageYCoordinate=(float) (distOrigin*Math.sin(initAngle+angle*Math.PI/180));
        float x=stageXCoordinate;
        float y=stageYCoordinate;
        stageXCoordinate=(float) (x*Math.cos(angle*Math.PI/180)-y*Math.sin(angle*Math.PI/180));
        stageYCoordinate=(float) (x*Math.sin(angle*Math.PI/180)+y*Math.cos(angle*Math.PI/180));
        origin.invert();
        translate(origin);
    }
    
    /**
     * Computes a xy rotation of the input angle for the current position, the origin 
     * of referential being taken as (0, 0)
     * @param angle angle of rotation in degrees
     */
    public void xyRotate(double angle){
        xyRotate(new position(), angle);
    }
    
    /**
     * Computes a xy scaled version of the position, using the input origin, and the input scaling factor
     * @param origin a position representing the origin of the referential
     * @param scaleFactor scaling factor
     */
    public void xyScale(position origin, double scaleFactor){
        origin.invert();
        translate(origin);
        stageXCoordinate*=scaleFactor;
        stageYCoordinate*=scaleFactor;
        origin.invert();
        translate(origin);
    }
    
    /**
     * Computes a xy scaled version of the position, using the input scaling factor, 
     * the origin of referential being taken as (0, 0)
     * @param scaleFactor scaling factor
     */
    public void xyScale(double scaleFactor){
        xyScale(new position(), scaleFactor);
    }
    
    /**
     * Computes the extrapolated version of the Z coordinate(s), based on 3 reference positions
     * @param ref1 reference position 1
     * @param ref2 reference position 2
     * @param ref3 reference position 3
     */
    public void zExtrapolate(position ref1, position ref2, position ref3){
        double distToRef1=1/distanceXY(ref1);
        double distToRef2=1/distanceXY(ref2);
        double distToRef3=1/distanceXY(ref3);
        
        if(Double.isInfinite(distToRef1)){
            distToRef1=1;
            distToRef2=0;
            distToRef3=0;
        }
        
        if(Double.isInfinite(distToRef2)){
            distToRef1=0;
            distToRef2=1;
            distToRef3=0;
        }
        
        if(Double.isInfinite(distToRef3)){
            distToRef1=0;
            distToRef2=0;
            distToRef3=1;
        }
        
        double sum=distToRef1+distToRef2+distToRef3;
        stageZCoordinate=(float) ((distToRef1*ref1.stageZCoordinate+distToRef2*ref2.stageZCoordinate+distToRef3*ref3.stageZCoordinate)/sum);
        stageZ2Coordinate=(float) ((distToRef1*ref1.stageZ2Coordinate+distToRef2*ref2.stageZ2Coordinate+distToRef3*ref3.stageZ2Coordinate)/sum);
    }
    
    
    /**
     * Computes a translated version of the position, using the input position as 
     * a translation vector
     * @param translationVector the amplitude of translation, as a position
     */
    public void translate(position translationVector){
        stageXCoordinate+=translationVector.stageXCoordinate;
        stageYCoordinate+=translationVector.stageYCoordinate;
        stageZCoordinate+=translationVector.stageZCoordinate;
        AFOffset+=translationVector.AFOffset;
        stageZ2Coordinate+=translationVector.stageZ2Coordinate;
    }
    
    /**
     * Computes the symetrical version of the position, relative to the input origin position
     * @param origin the origin of the referential
     */
    public void symetry(position origin){
        origin.invert();
        translate(origin);
        invert();
        origin.invert();
        translate(origin);
    }
    
    /**
     * Computes the symetrical version of the position, relative to the input origin X position
     * @param origin the origin of the referential
     */
    public void xSymetry(position origin){
        position xSym=duplicate();
        xSym.symetry(origin);
        stageXCoordinate=xSym.stageXCoordinate;
    }
    
    /**
     * Computes the symetrical version of the position, relative to the input origin Y position
     * @param origin the origin of the referential
     */
    public void ySymetry(position origin){
        position ySym=duplicate();
        ySym.symetry(origin);
        stageYCoordinate=ySym.stageYCoordinate;
    }
    
    /**
     * Computes the symetrical version of the position, relative to the input origin XY position
     * @param origin the origin of the referential
     */
    public void xySymetry(position origin){
        position sym=duplicate();
        sym.symetry(origin);
        stageXCoordinate=sym.stageXCoordinate;
        stageYCoordinate=sym.stageYCoordinate;
    }
    
    /**
     * Inverts all the position's coordinates
     */
    public void invert(){
        stageXCoordinate=-stageXCoordinate;
        stageYCoordinate=-stageYCoordinate;
        stageZCoordinate=-stageZCoordinate;
        AFOffset=-AFOffset;
        stageZ2Coordinate=-stageZ2Coordinate;
        sampleLoaderPosition=-sampleLoaderPosition;
    }
    
    /**
     * Inverts the position's XY coordinates
     */
    public void xyInvert(){
        stageXCoordinate=-stageXCoordinate;
        stageYCoordinate=-stageYCoordinate;
    }
    
    
    //TODO extrapolated the Zsss coordinates from 3 references
    
    /**
     * Returns a copy of the current position
     * @return a copy of the current position
     */
    public position duplicate(){
        position out=new position();
        
        out.stagePositionName=stagePositionName;
        out.stageXCoordinate=stageXCoordinate;
        out.stageYCoordinate=stageYCoordinate;
        out.stageZCoordinate=stageZCoordinate;
        out.AFOffset=AFOffset;
        out.stageZ2Coordinate=stageZ2Coordinate;
        out.useTravelOffset=useStagePosition;
        out.sampleLoaderPosition=sampleLoaderPosition;
        out.stageCoordinatesAreAbsolute=stageCoordinatesAreAbsolute;
        out.useStagePosition=useStagePosition;
        out.categoryIndex=categoryIndex;
        out.hardwareAutofocusMemorizePositionID=hardwareAutofocusMemorizePositionID;
        out.hardwareAutofocusMemorizePositiondata=hardwareAutofocusMemorizePositiondata;
        
        return out;
    }
    
    /**
     * Return a string representation of the position
     * @return a string formated as in the MDA stage list file
     */
    @Override
    public String toString(){
        String out="\""+stagePositionName+"\", ";
        out+=stageXCoordinate+", ";
        out+=stageYCoordinate+", ";
        out+=stageZCoordinate+", ";
        out+=AFOffset+", ";
        out+=stageZ2Coordinate+", ";
        out+=(""+useTravelOffset).toUpperCase()+", ";
        out+=sampleLoaderPosition+", ";
        out+=(""+stageCoordinatesAreAbsolute).toUpperCase()+", ";
        out+=(""+useStagePosition).toUpperCase()+", ";
        out+=categoryIndex+", ";
        out+=hardwareAutofocusMemorizePositionID+", ";
        out+="\""+hardwareAutofocusMemorizePositiondata+"\"";
   
        return out;
    }
    
}
