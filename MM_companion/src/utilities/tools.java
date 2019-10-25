/*
 *  tools.java
 * 
 *  Created on 22 juil. 2010, 10:07:55
 * 
 *  Copyright (C) 2010 Fabrice P. Cordelieres
 * 
 *  License:
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.

 * 

 */

package utilities;

import ij.IJ;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author fab
 */
public class tools {
    
    /**
     * Calculates the elapsed time between a start and stop Date
     * @param start start Date
     * @param stop stop Date
     * @return a String containing the elapsed time formatted as HH:mm:ss
     */
    public static String elapsedTime(Date start, Date stop){
        long diff=(stop.getTime()-start.getTime())/1000;
        int hh=(int) (diff/3600);
        int mm=(int) ((diff-hh*3600)/60);
        int ss=(int) (diff-hh*3600-mm*60);

        return new SimpleDateFormat("HH:mm:ss").format(new GregorianCalendar(1, 1, 1, hh, mm, ss).getTime());
    }

    /**
     * Removes the .TIF or .STK image extension from a filename
     * @param filename the input filename
     * @return a String containing the filename without the .TIF or .STK extension
     */
    public static String removeImgExt(String filename){
        String out=filename;
        out=out.replaceAll(".TIF", "");
        out=out.replaceAll(".STK", "");
        out=out.replaceAll(".tif", "");
        out=out.replaceAll(".stk", "");
        return out;
    }
    
    /**
     * Returns the names of the installed LUTs
     * @return an array of String containing the names of the installed LUTs
     */
    public static String[] getLUTsList(){
        String path= IJ.getDirectory("luts");
        if(path==null) return null;
        
        String[] tmpList=new File(path).list();
        int nbLUTs=0;
        for(int i=0; i<tmpList.length; i++) if(tmpList[i].endsWith(".lut") || tmpList[i].endsWith(".LUT")) nbLUTs++;
        String[] out=new String[nbLUTs];
        for(int i=0; i<tmpList.length; i++) if(tmpList[i].endsWith(".lut") || tmpList[i].endsWith(".LUT")) out[i]=tmpList[i].replace(".lut", "");
        
        return out;
    }
    
    /** Converts RGB color coordinates in decimal
     * @param R Red value
     * @param G Green value
     * @param B Blue value
     * @return a color as a decimal
     */
    public static int RGBtoDec(int R, int G, int B){
        return R+G<<8+B<<16; //Same as R+G*256+B*65536;
    }
    
    /** Converts decimal color coordinates into RGB
     * @param decimal the color to decode into R, G and B components
     * @return an integer array containing R, G and B values
     */
    public static int[] DectoRGB(int decimal){
        int R=decimal & 0xFF;   //Keeps only the last 2 bits
        int G=decimal >> 8 & 0xFF; //Shifts bits 8 bits to the right and keeps only the last 2
        int B= decimal >> 16 & 0xFF; //Shifts bits 16 bits to the right and keeps only the last 2 
        
        return new int[]{R, G, B};
    }
    
    /**
     * Converts the input coordinate/dimension into a position for the output image
     * @param dimension input coordinate/dimension
     * @param resolution resolution of the output image (dpi)
     * @return the converted coordinate/dimension
     */
    public static int prepareForImage(double dimension, int resolution){
        return (int) (dimension*constants.MM_TO_INCHES*resolution);
    }
    
    /**
     * Restricts an angle to the [-PI, PI] range
     * @param angle the input angle
     * @return the output angle
     */
    public static double restrictAngle(double angle){
        //Takes benefit of the sin/asin functions to restrict the angle
        double out=(Math.asin(Math.sin(angle*Math.PI/180.0))*180.0/Math.PI);
        return out;
    }
}
