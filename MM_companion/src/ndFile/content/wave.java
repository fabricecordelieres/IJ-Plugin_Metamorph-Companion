package ndFile.content;

import java.util.ArrayList;

/*
 *  wave.java
 * 
 *  Created on 19 juil. 2010, 11:05:19
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

/**
 * This class stores wavelength related informations, extracted from/to be written to
 * an nd file
 * @author fab
 */
public class wave {
    /** Names for wavelentghs **/
    public String WaveName="";

    /** Wavelength acquiered as Z serie ? true/false **/
    public boolean WaveDoZ=false;

    /** WavePointsCollected **/
    public ArrayList<Integer> WavePointsCollected=null;
    
    /** Special charaters that should be replaced by a dash to form the wave name 
     *  to be used in the image's filename **/
    public static final String[] SPECIAL_CHARACTERS={"_", "/", "$", "%", "#", "!", "(", ")", "&"};
    
    
    /**
     * Replaces all the pecial characters by a dash to form the wave name to be 
     * used in the image's filename
     * @return a String containing the formatted wave name
     */
    public String getFileName(){
        String out=WaveName;
        for(int i=0; i<SPECIAL_CHARACTERS.length; i++) out=out.replace(SPECIAL_CHARACTERS[i], "-");
        return out;
    }
}
