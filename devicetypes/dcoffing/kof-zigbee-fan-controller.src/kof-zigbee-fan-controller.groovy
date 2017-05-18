/**
 *  King Of Fans Zigbee Fan Controller
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
def version() {"ver 0.2.170517"}					//update as needed


def currVersions(child) {							//Let's user know if running the child versions that corresponds to this parent version
if(child=="fan") {return "ver 0.2.170517"}		//manually enter the version of the FAN child that matches the parent version above
if(child=="light") {return "ver 0.2.170517"}	//manually enter the version of the LIGHT child that matches the parent version above
}

/*  
 05/17 updated to ST standards and removed unused code
 05/16  05/16 removed dash in label: "${device.displayName} Light"
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
	definition (name: "KOF Zigbee Fan Controller", namespace: "dcoffing", author: "Stephan Hackett, Ranga Pedamallu, Dale Coffing") {
		capability "Actuator"
        capability "Configuration"
        capability "Refresh"
        capability "Switch"
        capability "Light"
        capability "Sensor"
        capability "Polling"
        capability "Health Check"

        command "lightOn"
        command "lightOff"
        command "lightLevel"
        command "setFanSpeed"

        attribute "fanMode", "string" 			//stores fanspeed
        attribute "lightBrightness", "number"	//stores brightness level
        attribute "lastFanMode", "string"		//used to restore previous fanmode
        attribute "LchildVer", "string"			//stores light child version
        attribute "FchildVer", "string"			//stores fan child version
        attribute "LchildCurr", "string"			//stores color of version check
        attribute "FchildCurr", "string"			//stores color of version check

	fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0202", outClusters: "0003, 0019", model: "HDC52EastwindFan"
    }

    preferences {
    	page(name: "childToRebuild", title: "This does not display on DTH preference page")
            section("section") {
            	input(name: "refreshChildren", type: "bool", title: "Delete & Recreate all child devices?\n\nTypically used after modifying the parent device name " +
                "above to give all child devices the new name.\n\nPLEASE NOTE: Child Devices must be removed from any smartApps BEFORE attempting this " +
                "process or 'An unexpected error' occurs attempting to delete the child's.")
       }
    }

    tiles(scale: 2) {
	multiAttributeTile(name: "switch", type: "lighting", width: 6, height: 4) {
		tileAttribute ("fanMode", key: "PRIMARY_CONTROL") {
			attributeState "04", label:"HIGH", action:"off", icon:getIcon()+"fan4h.png", backgroundColor:"#79b821", nextState: "turningOff"
			attributeState "03", label:"MED-HI", action:"off", icon:getIcon()+"fan3h.png", backgroundColor:"#79b821", nextState: "turningOff"
			attributeState "02", label:"MED", action:"off", icon:getIcon()+"fan2h.png", backgroundColor:"#79b821", nextState: "turningOff"
			attributeState "01", label:"LOW", action:"off", icon:getIcon()+"fan1h.png", backgroundColor:"#79b821", nextState: "turningOff"
			attributeState "06", label:"BREEZE", action:"off", icon:getIcon()+"breeze4h_blk.png", backgroundColor:"#008B64", nextState: "turningBreezeOff"
        	attributeState "00", label:"FAN OFF", action:"on", icon:getIcon()+"fan00h_grey.png", backgroundColor:"#ffffff", nextState: "turningOn"
			attributeState "turningOn", action:"on", label:"TURNING ON", icon:getIcon()+"fan0h.png", backgroundColor:"#2179b8", nextState: "turningOn"
			attributeState "turningOff", action:"off", label:"TURNING OFF", icon:getIcon()+"fan0h_grey.png", backgroundColor:"#2179b8", nextState: "turningOff"
            attributeState "turningBreezeOff", action:"off", label:"TURNING OFF", icon:getIcon()+"breeze4h_teal.png", backgroundColor:"#2179b8", nextState: "turningOff"
        }
        tileAttribute ("lightBrightness", key: "SLIDER_CONTROL") {
			attributeState "lightBrightness", action:"lightLevel"
		}
	}
    standardTile("refresh", "refresh", decoration: "flat", width: 2, height: 3) {
		state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
	}
    valueTile("version", "version", width:4, height:1) {
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

	main(["switch"])
	details(["switch", "fanLight", "fanMode1", "fanMode2", "fanMode6", "fanMode3", "fanMode4", "refresh", "FchildVer", "FchildCurr", "LchildVer", "LchildCurr", "version"])
	}
}

def parse(String description) {
	//log.debug "Parse description $description"
    def event = zigbee.getEvent(description)
    if (event) {
    	log.info "Light event (or poll report) detected: ${event}"
    	def childDevice = getChildDevices()?.find {		//find light child device
        	it.device.deviceNetworkId == "${device.deviceNetworkId}-Light"
        }
        childDevice.sendEvent(event)	//send light events to light child device and update lightBrightness attribute
        if(event.value != "on" && event.value != "off") sendEvent(name: "lightBrightness", value: event.value)
    }
	else {
		def map = [:]
		if (description?.startsWith("read attr -")) {
			def descMap = zigbee.parseDescriptionAsMap(description)
			if (descMap.cluster == "0202" && descMap.attrId == "0000") {     // Fan Control Cluster Attribute Read Response
				map.name = "fanMode"
				map.value = descMap.value
                fanSync(descMap.value)
                log.info "Fan event (or poll report) detected: FanMode(${descMap.value})"
			}
		}	// End of Read Attribute Response
		def result = null
        if (map) {
			result = createEvent(map)
		}
		//log.debug "Parse returned $map"
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

def installed() {
	log.info "Installing"
	initialize()
    refresh()
    log.info "Exiting Install"
}

def updated() {
	log.info "Updating"
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
    	}
}

def updateChildLabel() {
	log.info "Updating Device Labels"
	for(i in 1..6) {
    	def childDevice = getChildDevices()?.find {
        	it.device.deviceNetworkId == "${device.deviceNetworkId}-0${i}"
    	}
        if (childDevice && i != 5) {childDevice.label = "${device.displayName} ${getFanName()["0${i}"]}"} // rename with new label
    }
    def childDeviceL = getChildDevices()?.find {
        	it.device.deviceNetworkId == "${device.deviceNetworkId}-Light"
    }
    if (childDeviceL) {childDeviceL.label = "${device.displayName} Light"}    // rename with new label
}

def createFanChild() {
	state.oldLabel = device.label    //save the label for reference if it ever changes
	for(i in 1..6) {
    	def childDevice = getChildDevices()?.find {
        	it.device.deviceNetworkId == "${device.deviceNetworkId}-0${i}"
    	}
        if (!childDevice && i != 5) {
           	log.info "Creating Fan Child ${childDevice}"
        	childDevice = addChildDevice("KOF Zigbee Fan Controller - Fan Speed Child Device", "${device.deviceNetworkId}-0${i}", null,[completedSetup: true,
            label: "${device.displayName} ${getFanName()["0${i}"]}", isComponent: true, componentName: "fanMode${i}",
            componentLabel: "${getFanName()["0${i}"]}", "data":["speedVal":"0${i}","parent version":version()]])
		}
       	else {
        	log.info "Fan Child ${i} already exists"
		}
	}
}

def createLightChild() {
    def childDevice = getChildDevices()?.find {
        	it.device.deviceNetworkId == "${device.deviceNetworkId}-Light"
    }
    if (!childDevice) {
        log.info "Creating Light Child ${childDevice}"
		childDevice = addChildDevice("KOF Zigbee Fan Controller - Light Child Device", "${device.deviceNetworkId}-Light", null,[completedSetup: true,
        label: "${device.displayName} Light", isComponent: false, componentName: "fanLight",
        componentLabel: "Light", "data":["parent version":version()]])
    }
	else {
        log.info "Light Child already exists"
	}
}

def deleteChildren() {
	log.info "Deleting children"
	def children = getChildDevices()
    children.each {child->
  		deleteChildDevice(child.deviceNetworkId)
    }
}

def configure() {
	log.info "Configuring Reporting and Bindings."
	zigbee.configureReporting(0x0006, 0x0000, 0x10, 0, 600, null) +	//light on/off state - report min 0 max 600secs(5mins)
	zigbee.configureReporting(0x0008, 0x0000, 0x20, 1, 600, 0x01) +	//light level state - report min 1 max 600secs(5mins)
	zigbee.configureReporting(0x0202, 0x0000, 0x30, 0, 600, null)	//fan mode state - report min 0 max 600secs(5mins)
}

def on() {
	log.info "Resuming Previous Fan Speed"
	def lastFan =  device.currentValue("lastFanMode")	 //resumes previous fanspeed
    return setFanSpeed(lastFan)
}

def off() {
	log.info "Turning fan Off"
    def fanNow = device.currentValue("fanMode")    //save fanspeed before turning off so it can be resumed when turned back on
    if(fanNow != "00") sendEvent("name":"lastFanMode", "value":fanNow)  //do not save lastfanmode if fan is already off
    zigbee.writeAttribute(0x0202, 0x0000, 0x30, 00)
}

def lightOn()  {
	log.info "Turning Light On"
	zigbee.on()
}

def lightOff() {
	log.info "Turning Light Off"
	zigbee.off()
}

def lightLevel(val) {
	log.info "Adjusting Light Brightnes Level"
    zigbee.setLevel(val) + (val?.toInteger() > 0 ? zigbee.on() : [])
}

def setFanSpeed(speed) {
	log.info "Adjusting Fan Speed to "+ getFanName()[speed]
    zigbee.writeAttribute(0x0202, 0x0000, 0x30, speed)
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
           		child.sendEvent(name:"switch",value:"off")	//send OFF event to all other child fans
                child.sendEvent(name:"fanSpeed", value:"off${childSpeedVal}")	//custom icon code
           	}
        }
   	}
    if(whichFan == "00") sendEvent(name:"switch",value:"off") //send OFF event to Fan Parent

}

def ping() {
    return zigbee.onOffRefresh()
}

def refresh() {
	getChildVer()
	zigbee.onOffRefresh() + zigbee.levelRefresh() + zigbee.readAttribute(0x0202, 0x0000)
}

def getChildVer() {
	log.info "Updating Child Versioning"
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
