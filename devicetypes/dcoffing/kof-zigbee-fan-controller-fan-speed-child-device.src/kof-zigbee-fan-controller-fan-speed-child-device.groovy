/**
 *  King Of Fans Zigbee Fan Controller - Fan Speed Child Device
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
KNOWN ISSUES
 - Windows Phone app is messed up on all views
 - fan and light child device views are only available in iOS mobile app
 - Fan child device view can't change name when using gear icon like you can in Light child device
 */ 
 def version() {return "ver 0.2.1.20170502"}
 /*
 05/02 added fanXX_off2 icons to show larger contrast between LED's 
    a- changed fan OFF icon to be fanXXh_grey.png with green LED
    x- was for testing to verify version uploads
 05/01 fixed flat bug by removing space?
    c- iOS fan child device view modifications= added decoration:"flat", added version tile
	** STbug? can't change name using gear in fan child like you can in light child, fan child device view not accessible in android app
    b- added new state for TurningBreezeOff with new icon to match 
    a- move Stephack latest changes over in a copy/paste; change namespace  
 04/30 custom icons for each fan child speed
 04/29 used new icon URL shortcut https://cdn.rawgit.com/ and located to /resources/images/
 04/26 moved icons to KOF repo and renamed for final release
 04/20 modified version tile 
 04/19 added version tile to help in troubleshooting with users
 Year2017
*/

metadata {
	definition (name: "KOF Zigbee Fan Controller - Fan Speed Child Device", namespace: "dcoffing", author: "Stephan Hackett") {
		capability "Actuator"
        capability "Switch"
        capability "Light"
        capability "Sensor"
        
        attribute "fanSpeed", "string"
        
   }
   
   tiles(scale: 2) {
		//standardTile("switch", "switch", decoration: "flat", width: 2, height: 2) {
     		//state "off", label:"off", action: "on", icon: getIcon(), backgroundColor: "#ffffff", nextState: "turningOn"
			//state "on", label: "on", action: "off", icon: getIcon(), backgroundColor: "#79b821", nextState: "turningOff"
        	//state "turningOn", label:"ADJUSTING", action: "on", icon: getIcon(), backgroundColor: "#2179b8", nextState: "turningOn"
           // state "turningOff", label:"TURNING OFF", action:"off", icon: getIcon(), backgroundColor:"#2179b8", nextState: "turningOff"
		//}
        standardTile("fanSpeed", "fanSpeed", decoration: "flat", width: 2, height: 2) {  
     		state "off", label:"off", action: "on", icon: getIcon()+"fan00h_grey.png", backgroundColor: "#ffffff", nextState: "turningOn"
			//state "default", label: "ADJUSTING", action: "on", icon: "https://cdn.rawgit.com/stephack/KOF-Fan/master/resources/images/fanspeed04.png", backgroundColor: "#2179b8"
            state "on01", label: "LOW", action: "off", icon: getIcon()+"fan1h.png", backgroundColor: "#79b821", nextState: "turningOff"
           	state "on02", label: "MED", action: "off", icon: getIcon()+"fan2h.png", backgroundColor: "#79b821", nextState: "turningOff"
			state "on03", label: "MED-HI", action: "off", icon: getIcon()+"fan3h.png", backgroundColor: "#79b821", nextState: "turningOff"
			state "on04", label: "HIGH", action: "off", icon: getIcon()+"fan4h.png", backgroundColor: "#79b821", nextState: "turningOff"
			state "on06", label: "BREEZE", action: "off", icon: getIcon()+"breeze4h_teal.png", backgroundColor: "#79b821", nextState: "turningBreezeOff"
			state "off01", label: "PUSH", action: "on", icon: getIcon()+"fan1h_off2.png", backgroundColor: "#ffffff", nextState: "turningOn"
           	state "off02", label: "PUSH", action: "on", icon: getIcon()+"fan2h_off2.png", backgroundColor: "#ffffff", nextState: "turningOn"
			state "off03", label: "PUSH", action: "on", icon: getIcon()+"fan3h_off2.png", backgroundColor: "#ffffff", nextState: "turningOn"
			state "off04", label: "PUSH", action: "on", icon: getIcon()+"fan4h_off.png", backgroundColor: "#ffffff", nextState: "turningOn"
			state "off06", label: "PUSH", action: "on", icon: getIcon()+"breeze4h_off.png", backgroundColor: "#ffffff", nextState: "turningBreezeOn"
        	state "turningOn", label:"ADJUSTING", action: "on", icon: getIcon()+"fan0h_grey.png", backgroundColor: "#2179b8", nextState: "turningOn"
            state "turningOff", label:"TURNING OFF", action:"off", icon: getIcon()+"fan0h.png", backgroundColor:"#2179b8", nextState: "turningOff"
            state "turningBreezeOn", label:"ADJUSTING", action: "on", icon: getIcon()+"breeze4h_blk.png", backgroundColor: "#2179b8", nextState: "turningOn"
            state "turningBreezeOff", label:"TURNING OFF", action:"off", icon: getIcon()+"breeze4h_blk.png", backgroundColor:"#2179b8", nextState: "turningOff"
		}
 		valueTile("version", "version", width: 4, height: 2) {
			state "version", label:"Fan Speed Child\n" + version()
		}   
    	main(["fanSpeed"])        
		details(["fanSpeed", "version"])    
    
	}
}

def getIcon() {
	return "https://cdn.rawgit.com/dcoffing/KOF-CeilingFan/master/resources/images/"
}

def off() {
	parent.off()  
}

def on() {
	log.info "CHILD ${getDataValue('speedVal')} TURNED ON"    
    parent.setFanSpeed(getDataValue("speedVal"))
}