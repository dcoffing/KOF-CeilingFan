/**
 *  King Of Fans Zigbee Fan Controller
 ***
 *  To be used with Zigbee receiver from Home Decorators Collection, Hampton Bay 
 *
 *  Copyright 2017 Ranga Pedamallu, Stephan Hackett, Dale Coffing
 *
 *  Contributing Authors:
       Ranga Pedamallu; initial release and zigbee parsing mastermind!
       Stephan Hackett; new composite (child) device type genius! 
       Dale Coffing; icons, code maintenance flunky 
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
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
        
        attribute "fanMode", "string"
        attribute "lightBrightness", "number"    
        attribute "lastFanMode", "string"        
      
	fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0202", outClusters: "0003, 0019", model: "HDC52EastwindFan"
    }
    
    preferences {
    	page(name: "childToRebuild", title: "This does not display on DTH preference page")
            section("section") {            	
            	input(name: "clearChildren", type: "bool", title: "Delete all child devices?\n\nPlease note: Devices must be removed from any smartApps BEFORE attempting to delete.")                      
       }
    }
    
    tiles(scale: 2) {    	
	multiAttributeTile(name: "switch", type: "lighting", width: 6, height: 4) {        	
		tileAttribute ("fanMode", key: "PRIMARY_CONTROL") {			
			attributeState "04", label:"HIGH", action:"off", icon:"st.Lighting.light24", backgroundColor:"#558216", nextState: "turningOff"
			attributeState "03", label:"MED-HI", action:"off", icon:"st.Lighting.light24", backgroundColor:"#669c1c", nextState: "turningOff"
			attributeState "02", label:"MED", action:"off", icon:"st.Lighting.light24", backgroundColor:"#79b821", nextState: "turningOff"
			attributeState "01", label:"LOW", action:"off", icon:"st.Lighting.light24", backgroundColor:"#8ad424", nextState: "turningOff"
			attributeState "06", label:"BREEZE", action:"off", icon:"st.Lighting.light24", backgroundColor:"#00A0DC", nextState: "turningOff"
        	attributeState "00", label:"FAN OFF", action:"on", icon:"st.Lighting.light24", backgroundColor:"#ffffff", nextState: "turningOn"
			attributeState "turningOn", action:"on", label:"TURNING ON", icon:"st.Lighting.light24", backgroundColor:"#2179b8", nextState: "turningOn"
			attributeState "turningOff", action:"off", label:"TURNING OFF", icon:"st.Lighting.light24", backgroundColor:"#2179b8", nextState: "turningOff"
        }  
        tileAttribute ("lightBrightness", key: "SLIDER_CONTROL") {
			attributeState "lightBrightness", action:"lightLevel"
		}         
	}  	  
   	standardTile("refresh", "refresh", decoration: "flat", width: 2, height: 2) {
		state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
	}
    valueTile("version", "version", width: 2, height: 2) {
    	state "version", label:"KOF Ceiling Fan"+"\r\n"+"Device Handler"+"\r\r\n"+" Beta Version"+"\r\n"+"v0.2.1.20170418"+"\r\r\n"
		}
    childDeviceTiles("fanSpeeds")
	main(["switch"])        
	details(["switch", "fanSpeeds", "refresh", "version"])
	}
}

def parse(String description) {
		//log.debug "Parse description $description"           
        def event = zigbee.getEvent(description)
    	if (event) {
        	//log.info "ENTER LIGHT"
            //Don't know what this part of the parse is for
        	if (event.name == "power") {            	
                event.value = (event.value as Integer) / 10                
                sendEvent(event)
        	}
        	else {
            	log.info "Light event detected on controller: ${event}"
            	def childDevice = getChildDevices()?.find {		//find light child device
        				it.device.deviceNetworkId == "${device.deviceNetworkId}-Lamp" 
                }                
                childDevice.sendEvent(event)	//send light events to light child device and update lightBrightness attribute
                if(event.value != "on" && event.value != "off") sendEvent(name: "lightBrightness", value: event.value)
        	}        
    	}
		else {
        	log.info "Fan event detected on controller"
			def map = [:]
			if (description?.startsWith("read attr -")) {
            	//log.info "FAN - READ"
				def descMap = zigbee.parseDescriptionAsMap(description)
				// Fan Control Cluster Attribute Read Response               
                //log.info descMap
				if (descMap.cluster == "0202" && descMap.attrId == "0000") {                	                  
					map.name = "fanMode"
					map.value = descMap.value
                    fanSync(descMap.value)
				} 
			}	// End of Read Attribute Response
			def result = null            
            if (map) {            
				result = createEvent(map)                
			}
			log.debug "Parse returned $map"            
			return result 
    	}                
}

def getFanName() { 
	[  
    "00":"OFF",
    "01":"LOW",
    "02":"MEDIUM",
    "03":"MEDIUM HIGH",
	"04":"HIGH",
    "05":"OFF",
    "06":"BREEZE",
    "07":"LAMP"
	]
}

def getFanNameAbbr() { 
	[  
    "00":"OFF",
    "01":"LOW",
    "02":"MED",
    "03":"MED-HI",
	"04":"HI",
    "05":"OFF",
    "06":"BREEZE",
    "07":"LAMP"
	]
}

def installed() {	
	initialize()	
}

def updated() {	
	initialize()
}

def initialize() {
	log.info "Initializing"
    if(clearChildren) {
    	deleteChildren()        
    }
    else {
		createFanChild()
    	createLightChild()
    }    
}

def createFanChild() {
	for(i in 1..6) {   		
    	def childDevice = getChildDevices()?.find {
        	it.device.deviceNetworkId == "${device.deviceNetworkId}-0${i}"
    	}                 
        if (!childDevice && i != 5) {        
        	childDevice = addChildDevice("KOF Zigbee Fan Controller - Fan Speed Child Device", "${device.deviceNetworkId}-0${i}", null,[completedSetup: true, label: "${device.displayName} ${getFanName()["0${i}"]}", isComponent: true, componentName: "fanMode${i}", componentLabel: "Fan Speed ${getFanNameAbbr()["0${i}"]}", "data":["speedVal":"0${i}"]])
        	response(refresh() + configure())
           	log.info "Creating child fan mode ${childDevice}"  
		}
       	else {
        	log.info "Child already exists"          
		}
	}
}

def createLightChild() {	
	def childDevice = getChildDevices()?.find {
        	it.device.deviceNetworkId == "${device.deviceNetworkId}-Lamp"
    }
    if (!childDevice) {  
		childDevice = addChildDevice("KOF Zigbee Fan Controller - Light Child Device", "${device.deviceNetworkId}-Lamp", null,[completedSetup: true, label: "${device.displayName} LAMP", isComponent: false, componentName: "fanLight", componentLabel: "Fan LAMP"])
        response(refresh() + configure())
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
		   	response(refresh() + configure())
        	log.info "Deleting children"
              
}

def configure() {
	log.info "Configuring Reporting and Bindings."
	def cmd = 
    [
	  //Set long poll interval
	  "raw 0x0020 {11 00 02 02 00 00 00}", "delay 100",
	  "send 0x${device.deviceNetworkId} 1 1", "delay 100",
	  //Bindings for Fan Control
      "zdo bind 0x${device.deviceNetworkId} 1 1 0x006 {${device.zigbeeId}} {}", "delay 100",
      "zdo bind 0x${device.deviceNetworkId} 1 1 0x008 {${device.zigbeeId}} {}", "delay 100",
	  "zdo bind 0x${device.deviceNetworkId} 1 1 0x202 {${device.zigbeeId}} {}", "delay 100",
	  //Fan Control - Configure Report
      "zcl global send-me-a-report 0x006 0 0x10 1 300 {}", "delay 100",
       "send 0x${device.deviceNetworkId} 1 1", "delay 100",
      "zcl global send-me-a-report 0x008 0 0x20 1 300 {}", "delay 100",
       "send 0x${device.deviceNetworkId} 1 1", "delay 100",
	  "zcl global send-me-a-report 0x202 0 0x30 1 300 {}", "delay 100",
	  "send 0x${device.deviceNetworkId} 1 1", "delay 100",
	  //Update values
      "st rattr 0x${device.deviceNetworkId} 1 0x006 0", "delay 100",
      "st rattr 0x${device.deviceNetworkId} 1 0x008 0", "delay 100",
	  "st rattr 0x${device.deviceNetworkId} 1 0x202 0", "delay 100",
	 //Set long poll interval
	  "raw 0x0020 {11 00 02 1C 00 00 00}", "delay 100",
	  "send 0x${device.deviceNetworkId} 1 1", "delay 100"
	]
    return cmd + refresh()
}

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

def lightOn()  {
	log.info "Turning Lamp On"
	zigbee.on()
}

def lightOff() {
	log.info "Turning Lamp Off"
	zigbee.off()
}

def lightLevel(val) {
	log.info "Adjusting Lamp Brightness"    
    zigbee.setLevel(val) + (val?.toInteger() > 0 ? zigbee.on() : []) 
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
        if(childSpeedVal == whichFan) {
           	child.sendEvent(name:"switch",value:"on")
        }
        else {            	
           	if(childSpeedVal!=null){ 
           		//log.info childSpeedVal
           		child.sendEvent(name:"switch",value:"off")
           	}
        }
   	}    	
    
}

def ping() {	
    return zigbee.onOffRefresh()
}

def refresh() {	
    zigbee.onOffRefresh() + zigbee.levelRefresh() + zigbee.readAttribute(0x0202, 0x0000)
}
