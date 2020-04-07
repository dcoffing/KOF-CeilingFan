/**
 *  King Of Fans Zigbee Fan Controller - Light Child Device
 *
 *  Copyright 2017 Stephan Hackett
 *  in collaboration with Ranga Pedamallu, Dale Coffing
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */
 def version() {return "ver 0.2.18a"}
/*
    a- added valueTile for rangeValue; forced to use 2x2 or bug in device handler makes font unreadably small
       so modified controlTile 4x2 to match up rangeValue tile size, shorten ver to increase font in tile  
 05/15 pull request merge with Stephan and Ranga changes, to allow flat look in parent, changed from multiAttribute to Standard/Control Tile functionality
 05/10 decoration: "flat" added to tile for consistancy across all childs
 05/05 edit Zigbee to proper ZigBee trademark
 05/04 clean up display in iOS child version, smaller text and center version
 05/03 tweak to icons for ON to match the lighter grey LED look
 05/02 replaced blue rays on icon to be grey when TurningOnOff
    b- changed light TurningON/OFF icon to light_blue 
    a- changed light ON icon from light_blue to lightH for Stephan  
 05/01 added version tile for iOS child device view, light icon ON with blue rays
 04/30a move Stephack latest changes over in a copy/paste; change namespace
 04/29 larger matching icon; used URL shortcut https://cdn.rawgit.com/ and located to /resources/images/
 04/26 moved icons to KOF repo and renamed for final release
 04/20 modified version tile 
 04/19 added version tile to help in troubleshooting with users
 2017 Year
*/
metadata {
	definition (name: "KOF Zigbee Fan Controller - Light Child Device", namespace: "dcoffing", author: "Stephan Hackett", mnmn: "SmartThings", 
    	ocfDeviceType: "oic.d.light", vid: "generic-rgbw-color-bulb", minHubCoreVersion: '000.021.00001',runLocally: true, executeCommandsLocally: true) {
        capability "Actuator"
        capability "Configuration"
        capability "Refresh"
        capability "Sensor"
        capability "Switch"
        capability "Switch Level"
        capability "Polling"
        capability "Light"
        
        command "on"
        command "off"
        command "setLevel"
   }

	tiles(scale: 2) { 		
        //multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true) {
    	//	tileAttribute ("switch", key: "PRIMARY_CONTROL") {
        //		attributeState "off", label:"off", action: "on", icon: getIcon()+"light_grey.png", backgroundColor: "#ffffff", nextState: "turningOn"
		//		attributeState "on", label: "on", action: "off", icon: getIcon()+"lightH.png", backgroundColor: "#00A0DC", nextState: "turningOff"
        //      attributeState "turningOn", label:"TURNING ON", action: "on", icon: getIcon()+"lightI.png", backgroundColor: "#2179b8", nextState: "turningOn"
        //	attributeState "turningOff", label:"TURNING OFF", action:"off", icon: getIcon()+"lightI.png", backgroundColor:"#2179b8", nextState: "turningOff"
        //	}    	
    	//	tileAttribute ("device.level", key: "SLIDER_CONTROL") {
        //		attributeState "level", action: "setLevel"
    	//	}  
		//}
        multiAttributeTile(name: "switch", type: "lighting", width: 6, height: 4, canChangeIcon: true) {   
        	tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
            	attributeState "on", label:'${name}', action:"off", icon:"st.switches.light.on", backgroundColor:"#00A0DC", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "turningOn", label:'${name}', action:"off", icon:"st.switches.light.on", backgroundColor:"#00A0DC", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
            }
            
            tileAttribute ("device.level", key: "SLIDER_CONTROL", width: 4, height: 2) {
				attributeState "level", action:"setLevel"
			}
        }    	
        
        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label: "", action: "refresh.refresh", icon: "st.secondary.refresh"
		}
            
    	// controlTile ("level", "level", "slider", width: 4, height: 2) {
        // 	state "level", action: "setLevel"
    	// } 
		controlTile("levelSliderControl", "device.level", "slider", width: 6, height: 1) {
	      	state "level", action:"switch level.setLevel"
      	}
 		
    	main(["switch"])        
		details(["switch", "refresh"])    
    }	
}

def on() {
	parent.lightOn()
    log.debug("Fan light turned on")
    parent.childOn(device.deviceNetworkId)
    sendEvent(name: "switch", value: "on")
}

def off() {
	parent.lightOff()
    log.debug("Fan light turned off")
    sendEvent(name: "switch", value: "off")
    parent.childOff(device.deviceNetworkId)
}

def setLevel(val) {
	log.debug("Set level called")
	parent.lightLevel(val)
    sendEvent(name: "level", value: val)
}


/**
 * PING is used by Device-Watch in attempt to reach the Device
 * */
def ping() {
	log.debug("Ping called")
    log.debug(parent.refresh())
	return 
}


def refresh() {
	log.debug("Refresh called")
    log.debug(parent.getLevel())
	return parent.getLevel()
}