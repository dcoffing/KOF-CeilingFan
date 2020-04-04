/**
 Contributions from https://github.com/DavinKD/SmartThings/blob/master/devicetypes/davindameron/tasmota-fan.src/tasmota-fan.groovy
 
 *  
 King Of Fans Zigbee Fan Controller 
 *
 *  To be used with Ceiling Fan Remote Controller Model MR101Z receiver by Chungear Industrial Co. Ltd
 *  at Home Depot Gardinier 52" Ceiling Fan, Universal Ceiling Fan/Light Premier Remote Model #99432
 *
 *  Copyright 2017 Ranga Pedamallu, Stephan Hackett, Dale Coffing
 *
 *  Contributing Authors:
       Ranga Pedamallu; initial release and zigbee parsing mastermind!
       
       Stephan Hackett; new composite (child) device type genius! 
       Dale Coffing; icons, multiAttribute fan, code maintenance flunky 
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
// def version() {"ver 0.2.18"}					//update as needed


//def currVersions(child) {						//Let's user know if running the child versions that corresponds to this parent version
// if(child=="fan")   {return "ver 0.2.18"}	//manually enter the version of the FAN child that matches the parent version above
// if(child=="light") {return "ver 0.2.18a"}	//manually enter the version of the LIGHT child that matches the parent version above
// }

/*

 05/15 added GRN=OK RED=Update to version tile, changed parent tile version to fill empty space, shorten ver to increase font in tile
    a- fixed line 225 -Light
 05/05 modified Refresh text to Delete&Recreate
	b- test new label Speed 1 (LOW) technique
    a- evaluating new Speed 1,2,3,4 for ease of voice and look, it matches the fan speed bar icons instead of Lo, Med, Hi
 05/04 Modified labels lowercase,Comfort Breeze™ , getFanName() to be longer names vs abbr
 05/03 renamed LAMP to LIGHT in all instances to conform to ST standards
 05/01 fixed bug when recreated child names didn't use the new name but the original name; def createFanChild() 
    c- added TurningBreezeOff attributeState to match the Breeze icon 
    b- added CeilingFanParent in version, added new grey OFF icons
    a- move Stephack latest changes;(one step child delete/create, etc) over in a copy/paste; change namespace
 04/30 Moved refresh()Configure() from child creation method to initialize, added individual icons for fan child
 04/29 new icons with fanspeed bar indication
	e- added changes from Stephan to fix createChild error
	d- go back to orginal code on line 182
	c- createFanChild code added line 182 ChildDevice this part is the BUG that wont' create all fanChild devices
	b- details for childVer, added getChildVer() & def getChildVer()
 	a- attribute LchildVer, FchildVer
 04/28 reverted back to 0426 and added new revision labeling to parent
 04/26 label changes to read naturally, CAP light to match child speeds
 04/25 label changes; Breeze color #008B64
 0.2.1b parent on-off states sync with any child state for ActionTiles
 04/19 added version tile to help in troubleshooting with users
*/
metadata {
	definition(name: "King of Fans Z-Wave Fan Controller", namespace: "smartthings", author: "SmartThings", ocfDeviceType: "oic.d.fan", genericHandler: "Zigbee") {
    // definition (cstHandler: true, name: "AKOF Zigbee Fan Controller 1", namespace: "smartthings", author: "Stephan Hackett, Ranga Pedamallu, Dale Coffing, Rafael Borja",
    //ocfDeviceType: "oic.d.fan", genericHandler: "Zigbee") {
    	capability "Switch Level"
		capability "Switch"
		capability "Fan Speed"
		capability "Health Check"
		capability "Actuator"
		capability "Refresh"
		capability "Sensor"

		command "low"
		command "medium"
		command "high"
		command "raiseFanSpeed"
		command "lowerFanSpeed"

		fingerprint profileId: "0104", inClusters: "0000,0003,0004,0005,0006,0008,0003,0019" /*,0202" /, outClusters: "0003,0019" , model: "HDC52EastwindFan" */
     }
     
       tiles(scale: 2) {
		multiAttributeTile(name: "fanSpeed", type: "generic", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.fanSpeed", key: "PRIMARY_CONTROL") {
				attributeState "0", label: "off", action: "switch.on", icon: "st.thermostat.fan-off", backgroundColor: "#ffffff"
				attributeState "1", label: "low", action: "switch.off", icon: "st.thermostat.fan-on", backgroundColor: "#00a0dc"
				attributeState "2", label: "medium", action: "switch.off", icon: "st.thermostat.fan-on", backgroundColor: "#00a0dc"
				attributeState "3", label: "high", action: "switch.off", icon: "st.thermostat.fan-on", backgroundColor: "#00a0dc"
			}
			tileAttribute("device.fanSpeed", key: "VALUE_CONTROL") {
				attributeState "VALUE_UP", action: "raiseFanSpeed"
				attributeState "VALUE_DOWN", action: "lowerFanSpeed"
			}
		}

		standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label: '', action: "refresh.refresh", icon: "st.secondary.refresh"
		}
		main "fanSpeed"
		details(["fanSpeed", "refresh"])
     
   

        
        
        
    /*     runLocally: true, executeCommandsLocally: true,  ocfDeviceType: "oic.d.fan",  vid: "generic-switch" /*, vid: "generic-rgbw-color-bulb", genericHandler: "Zigbee" ) {
    // ocfDeviceType: "oic.d.fan" vid: "generic-rgbw-color-bulb"
    ///  runLocally: true, executeCommandsLocally: true, 
	/*	capability "Actuator"
        capability "Configuration"
        capability "Refresh"
        capability "Switch"       
        capability "Light"
        capability "Sensor" 
        capability "Polling"
        capability "Switch Level"
        //capability "Health Check"
        capability "Button"
        capability "Fan Speed"
        
        capability "Switch Level"
		capability "Switch"
		capability "Fan Speed"
		capability "Health Check"
		capability "Actuator"
		capability "Refresh"
		capability "Sensor"*/
        
        // capability "Switch Level"
		// capability "Switch"
		
		// capability "Health Check"
		// capability "Actuator"
		// capability "Refresh"
		// capability "Sensor"
        // capability "Light"
		//capability "Fan Speed"
        
        /*

		command "low"
		command "medium"
		command "high"
		command "raiseFanSpeed"
		command "lowerFanSpeed"
        
        capability "Stateless Fanspeed Button"
		capability "Stateless Fanspeed Mode Button"
		capability "Stateless Power Button"
   
        command "lightOn"
        command "lightOff"
        command "lightLevel"
        command "setFanSpeed"   
        
        command "low"
		command "medium"
		command "high"
		command "raiseFanSpeed"
		command "lowerFanSpeed"
       
        
        attribute "fanMode", "string" 			//stores fanspeed
        attribute "lightBrightness", "number"	//stores brightness level
        attribute "lastFanMode", "string"		//used to restore previous fanmode
        attribute "LchildVer", "string"			//stores light child version
        attribute "FchildVer", "string"			//stores fan child version
        attribute "LchildCurr", "string"			//stores color of version check
        attribute "FchildCurr", "string"			//stores color of version check
        */
      /*
	fingerprint profileId: "0104", inClusters: "0000,0003,0004,0005,0006,0008,0003,0019" /*,0202" /, outClusters: "0003,0019" , model: "HDC52EastwindFan" */
    // fingerprint profileId: "0104", inClusters: "*", outClusters: "0003,0019", model: "HDC52EastwindFan"
    // }
   

    
    /*
    
    tiles(scale: 2) {
        
		multiAttributeTile(name: "fanSpeed", type: "generic", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.fanSpeed", key: "PRIMARY_CONTROL") {
				attributeState "0", label: "off", action: "switch.on", icon: "st.thermostat.fan-off", backgroundColor: "#ffffff"
				attributeState "1", label: "low", action: "switch.off", icon: "st.thermostat.fan-on", backgroundColor: "#00a0dc"
				attributeState "2", label: "medium", action: "switch.off", icon: "st.thermostat.fan-on", backgroundColor: "#00a0dc"
				attributeState "3", label: "high", action: "switch.off", icon: "st.thermostat.fan-on", backgroundColor: "#00a0dc"
			}
			tileAttribute("device.fanSpeed", key: "VALUE_CONTROL") {
				attributeState "VALUE_UP", action: "raiseFanSpeed"
				attributeState "VALUE_DOWN", action: "lowerFanSpeed"
			}
            
            
		} /*
		
        controlTile("levelSliderControl", "device.level", "slider", height: 1,
             width: 2, inactiveLabel: false, range:"(0..100)") {
    		state "level", action:"switch level.setLevel"
		}
            
		standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label: '', action: "refresh.refresh", icon: "st.secondary.refresh"
		}
		main "fanSpeed"
		details(["fanSpeed", "refresh"]) */
	}
    
    
    
   /* 
    tiles(scale: 2) {    	
	multiAttributeTile(name: "switch", type: "generic", width: 6, height: 4) {        	
		tileAttribute ("fanMode", key: "PRIMARY_CONTROL") {			
			attributeState "04", label:"aaaaHIGH", action:"off", icon:getIcon()+"fan4h.png", backgroundColor:"#79b821", nextState: "turningOff"
			attributeState "03", label:"MED-HI", action:"off", icon:getIcon()+"fan3h.png", backgroundColor:"#79b821", nextState: "turningOff"
			attributeState "02", label:"MED", action:"off", icon:getIcon()+"fan2h.png", backgroundColor:"#79b821", nextState: "turningOff"
			attributeState "01", label:"LOW", action:"off", icon:getIcon()+"fan1h.png", backgroundColor:"#79b821", nextState: "turningOff"
			attributeState "06", label:"BREEZEAAAA", action:"off", icon:getIcon()+"breeze4h_blk.png", backgroundColor:"#008B64", nextState: "turningBreezeOff"
        	attributeState "00", label:"FAN OFF", action:"on", icon:getIcon()+"fan00h_grey.png", backgroundColor:"#ffffff", nextState: "turningOn"
			attributeState "turningOn", action:"on", label:"TURNING ON", icon:getIcon()+"fan0h.png", backgroundColor:"#2179b8", nextState: "turningOn"
			attributeState "turningOff", action:"off", label:"TURNING OFF", icon:getIcon()+"fan0h_grey.png", backgroundColor:"#2179b8", nextState: "turningOff"
            attributeState "turningBreezeOff", action:"off", label:"TURNING OFF", icon:getIcon()+"breeze4h_teal.png", backgroundColor:"#2179b8", nextState: "turningOff"
        }  
        tileAttribute ("lightBrightness", key: "SLIDER_CONTROL") {
			attributeState "lightBrightness", action:"lightLevel"
		}
	}
    
    standardTile("configure", "device.configure", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "configure", label:'', action:"configure", icon:"st.secondary.configure"
	}
        
        
    standardTile("refresh", "refresh", decoration: "flat", width: 2, height: 3) {
		state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
	}  
    valueTile("aaaaversion", "version", width:4, height:1) {
    	state "version", label:"Ceiling Fan Parent\n"+ version()
    }
    valueTile("FchildVer", "FchildVer", width:3, height:1) {
    	state "FchildVer", label: "Fan Child "+'${currentValue}'+"\nGRN=OK RED=Update"
    }
    valueTile("LchildVer", "LchildVer", width:3, height:1) {
    	state "LchildVer", label:"Light Child "+'${currentValue}'+"\nGRN=OK RED=Update"
    }
     valueTile("FchildCurr", "FchildCurr", width:1, height:1) {
    	state "FchildCurr", label: "", backgroundColors:[
            [value: 1, color: "#FF0000"],            
            [value: 2, color: "#3EAE40"]
        ]
    }
    valueTile("LchildCurr", "LchildCurr", width:1, height:1) {
    	state "LchildCurr", label:"", backgroundColors:[
            [value: 1, color: "#FF0000"],            
            [value: 2, color: "#3EAE40"]
        ]
    }
    
    //childDeviceTiles("fanSpeeds", height: 1, width: 6)
    childDeviceTile("fanMode1", "fanMode1", height: 2, width: 2)
    childDeviceTile("fanMode2", "fanMode2", height: 2, width: 2)
    childDeviceTile("fanMode3", "fanMode3", height: 2, width: 2)
    childDeviceTile("fanMode4", "fanMode4", height: 2, width: 2)
    childDeviceTile("fanMode6", "fanMode6", height: 2, width: 2)
    childDeviceTile("fanLight", "fanLight", height: 2, width: 2)
    
	main(["switch", "configure", "fanLight", "fanMode1", "fanMode2", "fanMode6", "fanMode3", "fanMode4"])        
	details(["switch", "configu", "fanLight", "fanMode1", "fanMode2", "fanMode6", "fanMode3", "fanMode4", "refresh", "FchildVer", "FchildCurr", "LchildVer", "LchildCurr", "version"])
	} */
}

def parse(String description) {
	log.info "Parse description $description"
    def event = zigbee.getEvent(description)
    if (event) {
    	"Sample 0104 0006 01 01 0000 00 D42D 00 00 0000 01 01 010086"
        
        
        log.info "Parse description ${description}"
    	log.info "Light event detected on controller (event): ${event}"
        
        
    	def childDevice = getChildDevices()?.find {		//find light child device
        	it.device.deviceNetworkId == "${device.deviceNetworkId}-Light" 
        }                
        childDevice.sendEvent(event)	//send light events to light child device and update lightBrightness attribute
        if(event.value != "on" && event.value != "off") {
        	log.debug "sendEvent lightBrightness"
        	sendEvent(name: "lightBrightness", value: event.value) 
            sendEvent(name: "levelSliderControl", value: event.value) 
            sendEvent(name: "level", value: event.value) 
            sendEvent(name: "switch level", value: event.value) 
        } else {
        	log.debug "not sending lightBrightness"
        }
    }
	else {
     	"Sample: 0104 0006 01 01 0000 00 D42D 00 00 0000 07 01 86000100"
        "Sample: 0104 0006 01 01 0000 00 D42D 00 00 0000 07 01 00"
        "Sample: D42D0102020800003000, dni: D42D, endpoint: 01, cluster: 0202, size: 8, attrId: 0000, result: success, encoding: 30, value: 00"
       	log.info "Fan event detected on controller"
		def map = [:]
		if (description?.startsWith("read attr -")) {
			def descMap = zigbee.parseDescriptionAsMap(description)
            log.debug "descMap in parse $descMap"
			if (descMap.cluster == "0202" && descMap.attrId == "0000") {     // Fan Control Cluster Attribute Read Response            	                  
				map.name = "fanMode"
				map.value = descMap.value
                fanSync(descMap.value)
			} 
		}	// End of Read Attribute Response
		def result = null            
        if (map) {            
			result = createEvent(map)                
		} else {
        	log.debug("parse: event map is null")
        }
		log.debug "Parse returned $map"            
		
        return result 
   	}                
}

def getIcon() {
	return "https://cdn.rawgit.com/dcoffing/KOF-CeilingFan/master/resources/images/"
}

def getFanName() { 
	[  
    "00":"Off",
    "01":"Low",
    "02":"Med",
    "03":"Med-Hi",
	"04":"High",
    "05":"Off",
    "06":"Comfort Breeze™",
    "07":"Light"
	]
}

def getFanNameAbbr() { 
	[  
    "00":"Off",
    "01":"Low",
    "02":"Med",
    "03":"Med-Hi",
	"04":"High",
    "05":"Off",
    "06":"Breeze™",
    "07":"Light"
	]
}

/*
def installed() {
	
	initialize()
}
*/

def updated() {
	if(state.oldLabel != device.label) {updateChildLabel()}
		initialize()    
}

def initialize() {	
	log.info "Initializing"     
       	if(refreshChildren) {        	
            deleteChildren()            
    		device.updateSetting("refreshChildren", false)            
    	}
    	else {
			createFanChild()
    		createLightChild()
            response(refresh() + configure())
    	}    	
}

def updateChildLabel() {
	log.info "UPDATE LABEL"
	for(i in 1..6) {   		
    	def childDevice = getChildDevices()?.find {
        	it.device.deviceNetworkId == "${device.deviceNetworkId}-0${i}"
    	}                 
        if (childDevice && i != 5) {childDevice.label = "${device.displayName} ${getFanName()["0${i}"]}"} // rename with new label
    }
    
    def childDeviceL = getChildDevices()?.find {
        	it.device.deviceNetworkId == "${device.deviceNetworkId}-Light"
    }
    if (childDeviceL) {childDeviceL.label = "${device.displayName}-Light"}    // rename with new label
}
def createFanChild() {
	state.oldLabel = device.label    //save the label for reference if it ever changes
	for(i in 1..6) {   		
    	def childDevice = getChildDevices()?.find {
        	it.device.deviceNetworkId == "${device.deviceNetworkId}-0${i}"
    	}                 
        if (!childDevice && i != 5) {        
        	childDevice = addChildDevice("KOF Zigbee Fan Controller - Fan Speed Child Device", "${device.deviceNetworkId}-0${i}", null,[completedSetup: true,
            label: "${device.displayName} ${getFanName()["0${i}"]}", isComponent: false, componentName: "fanMode${i}",
            componentLabel: "${getFanName()["0${i}"]}", "data":["speedVal":"0${i}","parent version":version()]])        	
           	log.info "Creating child fan mode ${childDevice}"  
		}
       	else {
        	log.info "Child already exists"          
		}
	}
}




def createLightChild() {
	def childDevice = getChildDevices()?.find {
        	it.device.deviceNetworkId == "${device.deviceNetworkId}-Light"
    }
    if (!childDevice) {  
		childDevice = addChildDevice("KOF Zigbee Fan Controller - Light Child Device", "${device.deviceNetworkId}-Light", null,[completedSetup: true,
        label: "${device.displayName} Light", isComponent: false, componentName: "fanLight",
        componentLabel: "Light", "data":["parent version":version()]])       
        log.info "Creating child light ${childDevice}" 
    }
	else {
        log.info "Child already exists"          
	}	
}

def deleteChildren() {	
	def children = getChildDevices()        	
    children.each {child->
  		deleteChildDevice(child.deviceNetworkId)
    }	
    log.info "Deleting children"                  
}


// Filename: printAllMethodsExample.groovy
void printAllMethods( obj ){
    if( !obj ){
		println( "Object is null\r\n" );
		return;
    }
	if( !obj.metaClass && obj.getClass() ){
        printAllMethods( obj.getClass() );
		return;
    }
	def str = "class ${obj.getClass().name} functions:\r\n";
	obj.metaClass.methods.name.unique().each{ 
		str += it+"(); "; 
	}
	log.debug "${str}\r\n";
}


def configure() {
	log.info "Configuring Reporting and Bindings."
    log.info zigbee.configureReporting(0x0006, 0x0000, 0x10, 0, 600, null)
    log.info zigbee.configureReporting(0x0006, 0x00011, 0x10, 0, 600, null)
    
    log.debug device.dump()
    log.debug  this.dump()
    
    "[zdo bind 0xD42D 0x01 0x01 0x0006 {0022A3000016B5F4} {}, delay 2000, st cr 0xD42D 0x01 0x0006 0x0000 0x10 0x0000 0x0258 {}, delay 2000]"
    /*def configure() {
    configureReporting(0x0006, 0x0000, 0x10, 0, 600, null)
}
	sendEvent(name: "checkInterval", value: 2 * 10 * 60 + 1 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
*/
	

	def cmd = 
    [
	  //Set long poll interval
	  "raw 0x0020 {11 00 02 02 00 00 00}", "delay 100",
	  "send 0x${device.deviceNetworkId} 1 1", "delay 100",
	  //Bindings for Fan Control
      // "zdo bind 0x${device.deviceNetworkId} 1 0 0x006 {${device.zigbeeId}} {}", "delay 100",
      
      "zdo bind 0x${device.deviceNetworkId} 1 1 0x006 {${device.zigbeeId}} {}", "delay 100",
      "zdo bind 0x${device.deviceNetworkId} 1 1 0x008 {${device.zigbeeId}} {}", "delay 100",
	  "zdo bind 0x${device.deviceNetworkId} 1 1 0x202 {${device.zigbeeId}} {}", "delay 100",
	  //Fan Control - Configure Report
      "zcl global send-me-a-report 0x006 0 0x10 1 300 {}", "delay 100",
       "send 0x${device.deviceNetworkId} 0 1", "delay 100",
       // Light?
      "zcl global send-me-a-report 0x006 1 0x10 1 300 {}", "delay 100",
       "send 0x${device.deviceNetworkId} 1 1", "delay 100",
       
      "zcl global send-me-a-report 0x008 0 0x20 1 300 {}", "delay 100",
       "send 0x${device.deviceNetworkId} 1 1", "delay 100",
	  "zcl global send-me-a-report 0x202 0 0x30 1 300 {}", "delay 100",
	  "send 0x${device.deviceNetworkId} 1 1", "delay 100",
      //Light Control - Configure Report
	  //Update values
      "st rattr 0x${device.deviceNetworkId} 1 0x006 0", "delay 100",
      "st rattr 0x${device.deviceNetworkId} 1 0x006 1", "delay 100", // Light?
      "st rattr 0x${device.deviceNetworkId} 1 0x008 0", "delay 100",
	  "st rattr 0x${device.deviceNetworkId} 1 0x202 0", "delay 100",
      
	 //Set long poll interval
	  "raw 0x0020 {11 00 02 1C 00 00 00}", "delay 100",
	  "send 0x${device.deviceNetworkId} 1 1", "delay 100",
      zigbee.configureReporting(0x0006, 0x00011, 0x10, 0, 600, null),
	]
    return cmd + refresh()
}


def off (physicalgraph.device.cache.DeviceDTO child) { lightOn(child) }

def on (physicalgraph.device.cache.DeviceDTO child) { lightOff(String id) }


def on() {
	log.info "Resuming Previous Fan Speed"   
	def lastFan =  device.currentValue("lastFanMode")	 //resumes previous fanspeed
	return setFanSpeed("$lastFan")
    
}

def off() {	
    def fanNow = device.currentValue("fanMode")    //save fanspeed before turning off so it can be resumed when turned back on
    if(fanNow != "00") sendEvent("name":"lastFanMode", "value":fanNow)  //do not save lastfanmode if fan is already off    
	def cmds=[
	"st wattr 0x${device.deviceNetworkId} 1 0x202 0 0x30 {00}"
    ]
    log.info "Turning fan Off"    
    return cmds
}

def lightOn(String dni)  {
	log.info "Turning Light On"
	zigbee.on()
    
    log.debug "Loading childlights"
    def childDevice = getChildDevices()?.find {
        	it.device.deviceNetworkId == "${device.deviceNetworkId}-Light"
    }
    if (childDevice) {
    	log.debug "Sending event to child"
        log.debug childDevice
    	childDevice.sendEvent(name: "device.switch", value: "on")
        childDevice.sendEvent(name: "switch", value: "on")
        childDevice.createEvent(childDevice.createAndSendEvent(name: "switch", value: "on"))
    }
}

def lightOff(String id) {
	log.info "Turning Light Off"
	zigbee.off()
}

/*
void childOn(String dni) {
        onOffCmd(0xFF, channelNumber(dni))
}
void childOff(String dni) {
        onOffCmd(0, channelNumber(dni))
}*/

def lightLevel(val) {
	log.info "Adjusting Light Brightness - called lightLevel on parent"    
    zigbee.setLevel(val) + (val?.toInteger() > 1 ? zigbee.on() : []) 
    sendEvent(name:"level",value: val)
    
    log.debug "Loading childlights"
    def childDevice = getChildDevices()?.find {
        	it.device.deviceNetworkId == "${device.deviceNetworkId}-Light"
    }
    if (childDevice) {
    	log.debug "Sending event to child"
        log.debug childDevice
    	//childDevice.sendEvent(name: "device.value", value: val)
        // childDevice.sendEvent(name: "device.switch", value: "on", isStatusChange: true)
        childDevice.sendEvent(name: "switch", value: isDeviceOn? "on": "off", isStatusChange: true)
        childDevice.sendEvent(name: "value", value: val, isStatusChange: true)
        childDevice.createEvent(childDevice.createAndSendEvent(name: "level", value: value))
        
    }
}

/**
 * Called from APP when sliding light dimmer
 */
def setLevel(val, rate = null) {
	log.info "Adjusting Light Brightness via setlevel on parent: {$val}" 
    
    // sendEvent(name:"level",value: val)
   
	def isDeviceOn = val?.toInteger() > 1
    def cmds = zigbee.setLevel(val.toInteger(), 1) + refresh() // + refresh() (isDeviceOn ? zigbee.on() : []) 
    
    log.debug "cmds {$cmds}"
    
    return cmds
    
    /*
    log.debug "Loading childlights"
    def childDevice = getChildDevices()?.find {
        	it.device.deviceNetworkId == "${device.deviceNetworkId}-Light"
    }
    if (childDevice) {
    	log.debug "Sending event to child"
        log.debug childDevice
    	//childDevice.sendEvent(name: "device.value", value: val)
        // childDevice.sendEvent(name: "device.switch", value: "on", isStatusChange: true)
        childDevice.sendEvent(name: "switch", value: isDeviceOn? "on": "off", isStatusChange: true)
        childDevice.sendEvent(name: "value", value: val, isStatusChange: true)
        
    }
    
    
    return zigbee.command(0x0006, 0x01) */
}

def poll() {
	log.debug("####POLL HAS BEEN CALLED")
}

def setFanSpeed(speed) {	  
    def cmds=[
	"st wattr 0x${device.deviceNetworkId} 1 0x202 0 0x30 {${speed}}"
    ]
    log.info "Adjusting Fan Speed to "+ getFanName()[speed]    
    return cmds
}

def fanSync(whichFan) {	
	def children = getChildDevices()
   	children.each {child->
       	def childSpeedVal = child.getDataValue('speedVal')
        if(childSpeedVal == whichFan) {	//send ON event to corresponding child fan
           	child.sendEvent(name:"switch",value:"on")
            child.sendEvent(name:"fanSpeed", value:"on${childSpeedVal}")	//custom icon code
            sendEvent(name:"switch",value:"on") //send ON event to Fan Parent
        }
        else {            	
           	if(childSpeedVal!=null){ 
           		//log.info childSpeedVal
           		child.sendEvent(name:"switch",value:"off")	//send OFF event to all other child fans
                child.sendEvent(name:"fanSpeed", value:"off${childSpeedVal}")	//custom icon code
           	}
        }
   	}
    if(whichFan == "00") sendEvent(name:"switch",value:"off") //send OFF event to Fan Parent
    
}

def ping() {	
	log.debug("#####PING HAS BEN CALLED!!!!!")
    return zigbee.onOffRefresh()
}

def refresh() {	
	log.info "Refresh called" 
    
	getChildVer()
    
	return zigbee.onOffRefresh() + zigbee.levelRefresh() + zigbee.readAttribute(0x0202, 0x0000) + zigbee.readAttribute(0x0006, 0x0000) +
    zigbee.readAttribute(0x0202, 0x0000) + zigbee.readAttribute(0x0202, 0x0001) + zigbee.readAttribute(0x0006, 0x0001) + zigbee.readAttribute(0x0006, 0x0000) + zigbee.readAttribute(0x0008, 0x0004)
}


def getChildVer() {
	def FchildDevice = getChildDevices()?.find {
        	it.device.deviceNetworkId == "${device.deviceNetworkId}-01"
    	}                 
	if(FchildDevice){	//find a fan device, 1. get version info and store in FchildVer, 2. check child version is current and set color accordingly
    	sendEvent(name:"FchildVer", value: FchildDevice.version())	
    	FchildDevice.version() != currVersions("fan")?sendEvent(name:"FchildCurr", value: 1):sendEvent(name:"FchildCurr", value: 2)
    }
    
    def LchildDevice = getChildDevices()?.find {
        	it.device.deviceNetworkId == "${device.deviceNetworkId}-Light"
    	}                 
	if(LchildDevice) {	    //find the light device, get version info and store in LchildVer    
    	sendEvent(name:"LchildVer", value: LchildDevice.version())
    	LchildDevice.version() != currVersions("light")?sendEvent(name:"LchildCurr", value: 1):sendEvent(name:"LchildCurr", value: 2)
	}
}



def installed() {
	sendEvent(name: "checkInterval", value: 2 * 15 * 60 + 2 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID, offlinePingable: "1"])
	response(refresh())
}



def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
	fanEvents(cmd)
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
	fanEvents(cmd)
}

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv1.SwitchMultilevelReport cmd) {
	fanEvents(cmd)
}

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv1.SwitchMultilevelSet cmd) {
	fanEvents(cmd)
}

def zwaveEvent(physicalgraph.zwave.commands.hailv1.Hail cmd) {
	log.debug "received hail from device"
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	// Handles all Z-Wave commands we aren't interested in
	log.debug "Unhandled: ${cmd.toString()}"
	[:]
}

def fanEvents(physicalgraph.zwave.Command cmd) {
	def rawLevel = cmd.value as int
	def result = []

	if (0 <= rawLevel && rawLevel <= 100) {
		def value = (rawLevel ? "on" : "off")
		result << createEvent(name: "switch", value: value, isStateChange: true)
		result << createEvent(name: "level", value: rawLevel == 99 ? 100 : rawLevel, isStateChange: true)

		def fanLevel = 0

		// The GE, Honeywell, and Leviton treat 33 as medium, so account for that
		if (1 <= rawLevel && rawLevel <= 32) {
			fanLevel = 1
		} else if (33 <= rawLevel && rawLevel <= 66) {
			fanLevel = 2
		} else if (67 <= rawLevel && rawLevel <= 100) {
			fanLevel = 3
		}
		result << createEvent(name: "fanSpeed", value: fanLevel, isStateChange: true)
	}

	return result
}

def getDelay() {
	// the leviton is comparatively well-behaved, but the GE and Honeywell devices are not
	zwaveInfo.mfr == "001D" ? 2000 : 5000
}


def raiseFanSpeed() {
	setFanSpeed(Math.min((device.currentValue("fanSpeed") as Integer) + 1, 3))
}

def lowerFanSpeed() {
	setFanSpeed(Math.max((device.currentValue("fanSpeed") as Integer) - 1, 0))
}

def low() {
	setLevel(32)
}

def medium() {
	setLevel(66)
}

def high() {
	setLevel(99)
}

