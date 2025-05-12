package utilities;

/**
*
*  parseMetadata v1, 2 nov. 2020 
   Fabrice P Cordelieres, fabrice.cordelieres at gmail.com
   
   Copyright (C) 2020 Fabrice P. Cordelieres
 
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * XML MetaData example:

<MetaData>
	<Description>Nerve stack acquired with an automated microscope.</Description>
	<MetaDataVersion Value=”1.0”/>
	<ApplicationName=”MetaMorph” />
	<ApplicationVersion=”7.5” />

	<PlaneInfo>
		<prop id=”plane-type” value=”plane”/>	// or “overlay” or “thumbnail”
		<prop id=”pixel-size-x” type=”int” value=”512”/>
		<prop id=”pixel-size-y” type=”int” value=”512”/>
		<prop id=”bits-per-pixel” type=”int” value=”16”/>
		<prop id=”overlay-guid” type=”guid” value=”{3984-028402-840-24}”/>		// optional
		<prop id=”overlay-catalog-id” type=”int” value=”0”/> 			// optional
		<prop id=”special-overlay-guid” type=”guid” value=”{3984-028402-840-24}”/>	// optional
		<prop id=”special-overlay-catalog-id” type=”int” value=”1”/>			// optional
		<prop id=”autoscale-state” type=”bool” value=”on”/>	// or “off”
		<prop id=”autoscale-min-percent” type=”float” value=”1.0”/>	//
		<prop id=”autoscale-max-percent” type=”float” value=”99.0”/>
		<prop id=”autoscale-red -state” type=”bool” value=”on”/>	// or “off”		// optional
		<prop id=”autoscale-green-state” type=”bool” value=”on”/>	// or “off”		// optional
		<prop id=”autoscale-blue-state” type=”bool” value=”on”/>	// or “off”		// optional
		<prop id=”autoscale-red-min-percent” type=”float” value=”1.0”/>		// optional
		<prop id=”autoscale-green-min-percent” type=”float” value=”1.0”/>		// optional
		<prop id=”autoscale-blue-min-percent” type=”float” value=”1.0”/>		// optional
		<prop id=”autoscale-red-max-percent” type=”float” value=”99.0”/>		// optional
		<prop id=”autoscale-green-max-percent” type=”float” value=”99.0”/>		// optional
		<prop id=”autoscale-blue-max-percent” type=”float” value=”99.0”/>		// optional
		<prop id=”scale-min” type=”int” value=”123”/>
		<prop id=”scale-max” type=”int” value=”23242”/>
		<prop id=”scale-red-min” type=”int” value=”123”/>				// optional
		<prop id=”scale-green-min” type=”int” value=”123”/>			// optional
		<prop id=”scale-blue-min” type=”int” value=”123”/>			// optional
		<prop id=”scale-red-max” type=”int” value=”23242”/>			// optional
		<prop id=”scale-green-max” type=”int” value=”23242”/>			// optional
		<prop id=”scale-blue-max” type=”int” value=”23242”/>			// optional
		<prop id=”spatial-calibration-state” type=”bool” value=”on”/>   // or “off”
		<prop id=”spatial-calibration-x” type=”float” value=”1.4”/>
		<prop id=”spatial-calibration-y” type=”float value=”2.3”/>
		<prop id=”spatial-calibration-units” value=”microns”/>
		<prop id=”image-name” value=”Nerve”/>
		<prop id=”threshold-state” value=”ThresholdInside”/>	// or “ThresholdOutside” or “ThresholdOff”
		<prop id=”threshold-low” type=”int”  value=”12”/>
		<prop id=”threshold-high” type=”int” value=”234”/>
		<prop id=”threshold-color” type=”colorref” value=”ff008f”/>
		<prop id=”zoom-percent” type=”int” value=”100”/>
		<prop id=”gamma” type=”float” value=”1.3”/>				// optional
		<prop id=”gamma-red” type=”float” value=”1.3”/>				// optional
		<prop id=”gamma-green” type=”float” value=”1.3”/>			// optional
		<prop id=”gamma-blue” type=”float” value=”1.3”/>				// optional
		<prop id=”look-up-table-type” value=”by-wavelength”/>	// or “standard” or “custom”
		<prop id=”look-up-table-name” value=”mono”/>
		<prop id=”look-up-table-custom-values” type=”int-array” value=”0,1,2,3,4,5,6,…,254,255”/>  // optional
		<prop id=”photonegative-mode” type=”bool” value=”on”/>   // or “off”
		<prop id=”gray-calibration-curve-fit-algorithm” type=”int” value=”1”/>
		<prop id=”gray-calibration-values” type=”float-array” value=”1,2,3,4,5,6,…”/>
		<prop id=”gray-calibration-min” type=”float” value=”3.4”/>
		<prop id=”gray-calibration-max” type=”float” value=”433.2”/>
		<prop id=” gray-calibration-units” value=”dunno”/>
		<prop id=”plane-guid” type=”guid” value=”{3984-028402-840-24}”/>
		<prop id=”acquisition-time-local” type=”time” value=”06212007 3:32:45.123”/>
		<prop id=” modification -time-local” type=”time” value=”06212007 3:32:45.123”/>
		<prop id=”stage-position-x” type=”float” value=”2.323232”/>
		<prop id=”stage-position-y” type=” float” value=”6.342323”/>
		<prop id=”stage-label” value=”Stage Position 1”/>					// optional
		<prop id=”z-position” type=”float” value=”4.53434”/>
		<prop id=”wavelength” type=”float” value=”488”/>
		<prop id=”camera-binning-x” type=”int” value=”2”/>
		<prop id=”camera-binning-y” type=”int” value=”2”/>
		<prop id=”camera-chip-offset-x” type=” float” value=”100”/>
		<prop id=”camera-chip-offset-y” type=” float” value=”100”/>
		<custom-prop id=”temperature” type=”float” value=”98.6”/>			// there can be any number
		<custom-prop id=”custom property 1” value=”my custom property”/>		// of custom plane properties
	</PlaneInfo>
	<SetInfo>
		<prop id=”number-of-planes” type=”int” value=”25”/>
		<prop id=”number-of-stage-positions” type=”int” value=”8”/>
		<custom-prop id=”some-set-prop” value=”set property 1”/>			// there can be any number
		<custom-prop id=”some-other-prop” value=”set property 2”/>		// of custom set properties
		<custom-prop id=”yet-another-prop” type=”float” value=”1.1”/>
	</SetInfo>
</MetaData>
 */

/**
 * This class will monitor a folder and plot informations from the data it will find there, separating in datasets depending on the time/position/channel
 * @author fab
 *
 */

public class metadataParser {
	/** The structure of the metadata according to Metamorph **/
	public Pattern p=Pattern.compile("<prop id=\"(?<parameterName>.*?)\"( type=\"(?<parameterType>.*?)\")* value=\"(?<parameterValue>.*?)\"\\/>");
	
	/** HashMap containing the parameters/values as key/value pairs **/
	HashMap<String, String> metadata=new HashMap<String, String>();
	
	
	/**
	 * Creates a new metadataParser object, based on the input info field from a Metamorph file
	 * @param infoField the info field from a Metamorph file
	 */
	public metadataParser(String infoField) {
		Matcher m=p.matcher(infoField);
		
		while(m.find()) {
			metadata.put(m.group("parameterName"), m.group("parameterValue"));
		}
	}
	
	
	/**
	 * Returns the acquisition time as a Date object or null if not available
	 * @return the acquisition time as a Date object or null if not available
	 */
	public Date getAcquisitionTime(){
		Date out=null;
		
		try {
			out=new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS").parse(metadata.get("acquisition-time-local"));
		} catch (ParseException e) {
		}
		
		return out;
	}
	
	/**
	 * Returns the modification time as a Date object or null if not available
	 * @return the modification time as a Date object or null if not available
	 */
	public Date getModificationTime(){
		Date out=null;
		
		try {
			out=new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS").parse(metadata.get("modification-time-local"));
		} catch (ParseException e) {
		}
		
		return out;
	}
	
	/**
	 * Returns the value associated to the input parameter or null if not found
	 * @param parameter the parameter to retrieve
	 * @return the value associated to the input parameter or null if not found
	 */
	public String getValue(String parameter) {
		return metadata.get(parameter);
	}
	
	@Override
	public String toString() {
		String out="";
		Set<Entry<String, String>> entrySet = metadata.entrySet();
		Iterator<Entry<String, String>> it=entrySet.iterator();
		
		while(it.hasNext()) {
			Entry<String, String> me = it.next();
			out+=me.getKey()+": "+me.getValue()+"\n";
		}
		
		return out;
	}

}
