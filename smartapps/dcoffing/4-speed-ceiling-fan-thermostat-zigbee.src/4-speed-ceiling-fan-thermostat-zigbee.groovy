
//   ZigBee 4 Speed Ceiling Fan Thermostat Control
   
  def version() {return "v0.1b.20170609" }    
/*  Change Log
 2017-06-09 removed the delay line for LOW speed start until ST platform issues are resolved
 2017-06-01 removed singleInstance since we don't use a Service Manager, move Version Info, User Guide to Parent screen
 2017-05-04 fixed user manual title to 4Speed, icons moved to KOF repo, user manual content revised
  bugfix, even though published and I load this from MyApps it still is using the older version code from zwave parent/child
 2017-04-27  starting modifications for zigbee
 2017-04-11 Added 10.0 selection for Fan Differential Temp to mimic single speed control
 2016-10-19 Ver2 Parent / Child app to allow for multiple use cases with a single install - @ericvitale
  
*/
definition(
    name: "4 Speed Ceiling Fan Thermostat - ZigBee",
    namespace: "dcoffing",
    author: "Dale Coffing",
    description: "Thermostat control for ZigBee 4 Speed Ceiling Fan device (Home Decorators Ceiling Fan/Light Controller MR101Z) staging each speeds with any temperature sensor.",
    category: "My Apps",
//    singleInstance: true,
	iconUrl: "https://cdn.rawgit.com/dcoffing/KOF-CeilingFan/master/resources/images/3scft125x125.png", 
   	iconX2Url: "https://cdn.rawgit.com/dcoffing/KOF-CeilingFan/master/resources/images/3scft250x250.png",
	iconX3Url: "https://cdn.rawgit.com/dcoffing/KOF-CeilingFan/master/resources/images/3scft250x250.png",
)

preferences {
        page(name: "startPage")
        page(name: "parentPage")
        page(name: "childStartPage")
        page(name: "optionsPage")
        page(name: "aboutPage")
}

def startPage() {
    if (parent) {
        childStartPage()
    } else {
        parentPage()
    }
}

def parentPage() {
	return dynamicPage(name: "parentPage", title: "", nextPage: "", install: false, uninstall: true) {
        section("Create a new fan automation.") {
            app(name: "childApps", appName: appName(), namespace: "dcoffing", title: "New ZigBee Ceiling Fan Automation", multiple: true)
        }
        section("Version Info, User's Guide") {
			href (name: "aboutPage", 
			title: "4 Speed Ceiling Fan Thermostat \n"+ version() +" \n"+"Copyright © 2017 Dale Coffing", 
			description: "Tap for SmartApp info & User's Guide",
			image: "https://cdn.rawgit.com/dcoffing/KOF-CeilingFan/master/resources/images/3scft125x125.png",
			required: false,
			page: "aboutPage"
			)
		}
    }
}

def childStartPage() {
	dynamicPage(name: "childStartPage", title: "Select your devices and settings", install: true, uninstall: true) {
    
        section("Select a room temperature sensor to control the fan..."){
			input "tempSensor", "capability.temperatureMeasurement", multiple:false, title: "Temperature Sensor", required: true, submitOnChange: true  
		}
        if (tempSensor) {  //protects from a null error
    		section("Enter the desired room temperature setpoint...\n" + "NOTE: ${tempSensor.displayName} room temp is ${tempSensor.currentTemperature}° currently"){
        		input "setpoint", "decimal", title: "Room Setpoint Temp", defaultValue: tempSensor.currentTemperature, required: true
    		}
        }
        else 
        	section("Enter the desired room temperature setpoint..."){
        		input "setpoint", "decimal", title: "Room Setpoint Temp", required: true
    		}       
        section("Select the Parent ceiling fan/light control hardware... (NOT the Light or Fan Speed Child )"){
        // fanDimmer
			input "fanSwitch", "capability.switch", multiple:false, title: "ZigBee Fan Control device", required: true
		}
        section("Optional Settings (Diff Temp, Timers, Motion, etc)") {
			href (name: "optionsPage", 
        	title: "Configure Optional settings", 
        	description: none,
        	image: "https://cdn.rawgit.com/dcoffing/KOF-CeilingFan/master/resources/images/settings250x250.png",
        	required: false,
        	page: "optionsPage"
        	)
        }
        
        section("Name") {
        	label(title: "Assign a name", required: false)
        }
        
/*        section("Version Info, User's Guide") {
// VERSION
			href (name: "aboutPage", 
			title: "4 Speed Ceiling Fan Thermostat \n"+ version() +" \n"+"Copyright © 2017 Dale Coffing", 
			description: "Tap to get user's guide.",
			image: "https://cdn.rawgit.com/dcoffing/KOF-CeilingFan/master/resources/images/3scft125x125.png",
			required: false,
			page: "aboutPage"
			)
		}
*/	}
}      

def optionsPage() {
	dynamicPage(name: "optionsPage", title: "Configure Optional Settings", install: false, uninstall: false) {
       	section("Enter the desired differential temp between fan speeds (default=1.0)..."){
			input "fanDiffTempString", "enum", title: "Fan Differential Temp", options: ["0.5","1.0","1.5","2.0","10.0"], required: false
		}
		section("Enable ceiling fan thermostat only if motion is detected at (optional, leave blank to not require motion)..."){
			input "motionSensor", "capability.motionSensor", title: "Select Motion device", required: false, submitOnChange: true
		}
        if (motionSensor) {
			section("Turn off ceiling fan thermostat when there's been no motion detected for..."){
				input "minutesNoMotion", "number", title: "Minutes?", required: true
			}
		}
        section("Select ceiling fan operating mode desired (default to 'YES-Auto'..."){
			input "autoMode", "enum", title: "Enable Ceiling Fan Thermostat?", options: ["NO-Manual","YES-Auto"], required: false
		}
    	section ("Change SmartApp name, Mode selector") {
		mode title: "Set for specific mode(s)", required: false
		}
    }
}

def aboutPage() {
	dynamicPage(name: "aboutPage", title: none, install: true, uninstall: true) {
     	section("User's Guide; 4 Speed Ceiling Fan Thermostat - ZigBee") {
        	paragraph textHelp()
 		}
	}
}

/* I might be able to take advantage of this next line of code to selectively open the zwave OR the zigbee parent based on hardware input selected?
private def appName() { return "${parent ? "3 Speed Fan Automation" : "3 Speed Ceiling Fan Thermostat"}" }
*/
private def appName() { return "${parent ? "4 Speed Fan Automation" : "4 Speed Ceiling Fan Thermostat - ZigBee"}" }

def installed() {
	log.debug "def INSTALLED with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "def UPDATED with settings: ${settings}"
	unsubscribe()
	initialize()
    handleTemperature(tempSensor.currentTemperature) //call handleTemperature to bypass temperatureHandler method 
}

def initialize() {

    if(parent) { 
    	initChild() 
    } else {
    	initParent() 
    }  
}

def initChild() {
	log.debug "def INITIALIZE with settings: ${settings}"
	subscribe(tempSensor, "temperature", temperatureHandler) //call temperatureHandler method when any reported change to "temperature" attribute
	if (motionSensor) {
		subscribe(motionSensor, "motion", motionHandler) //call the motionHandler method when there is any reported change to the "motion" attribute
	}   
}

def initParent() {
	log.debug "Parent Initialized"
}
                                   //Event Handler Methods                     
def temperatureHandler(evt) {
	log.debug "temperatureHandler called: $evt"	
    handleTemperature(evt.doubleValue)
	log.debug "temperatureHandler evt.doubleValue : $evt"
}

def handleTemperature(temp) {		//
	log.debug "handleTemperature called: $evt"	
	def isActive = hasBeenRecentMotion()
	if (isActive) {
		//motion detected recently
		tempCheck(temp, setpoint)
		log.debug "handleTemperature ISACTIVE($isActive)"
	}
	else {
     	fanSwitch.off()
 	}
}

def motionHandler(evt) {
	if (evt.value == "active") {
		//motion detected
		def lastTemp = tempSensor.currentTemperature
		log.debug "motionHandler ACTIVE($isActive)"
		if (lastTemp != null) {
			tempCheck(lastTemp, setpoint)
		}
	} else if (evt.value == "inactive") {		//testing to see if evt.value is indeed equal to "inactive" (vs evt.value to "active")
		//motion stopped
		def isActive = hasBeenRecentMotion()	//define isActive local variable to returned true or false
		log.debug "motionHandler INACTIVE($isActive)"
		if (isActive) {
			def lastTemp = tempSensor.currentTemperature
			if (lastTemp != null) {				//lastTemp not equal to null (value never been set) 
				tempCheck(lastTemp, setpoint)
			}
		}
		else {
     	    fanSwitch.off()
		}
	}
}

private tempCheck(currentTemp, desiredTemp)
{
	log.debug "TEMPCHECK#1(CT=$currentTemp,SP=$desiredTemp,FS=$fanSwitch.currentSwitch,automode=$autoMode,FDTstring=$fanDiffTempString, FDTvalue=$fanDiffTempValue)"
    
    //convert Fan Diff Temp input enum string to number value and if user doesn't select a Fan Diff Temp default to 1.0 
    def fanDiffTempValue = (settings.fanDiffTempString != null && settings.fanDiffTempString != "") ? Double.parseDouble(settings.fanDiffTempString): 1.0
	
    //if user doesn't select autoMode then default to "YES-Auto"
    def autoModeValue = (settings.autoMode != null && settings.autoMode != "") ? settings.autoMode : "YES-Auto"	
    
    def LowDiff = fanDiffTempValue*1 
    def MedDiff = fanDiffTempValue*2
    def MedHighDiff = fanDiffTempValue*3
    def HighDiff = fanDiffTempValue*4
	
	log.debug "TEMPCHECK#2(CT=$currentTemp,SP=$desiredTemp,FS=$fanSwitch.currentSwitch, automode=$autoMode,FDTstring=$fanDiffTempString, FDTvalue=$fanDiffTempValue)"
	if (autoModeValue == "YES-Auto") {
    	switch (currentTemp - desiredTemp) {
        case { it  >= HighDiff }:
        		// turn on fan HIGH speed4
       			fanSwitch.setFanSpeed(4) 
            	log.debug "HI speed(CT=$currentTemp, SP=$desiredTemp, FS=$fanSwitch.currentSwitch, Speed=$currentsetFanSpeed, HighDiff=$HighDiff)"
	        break 
        case { it  >= MedHighDiff }:
        		// turn on fan MED-HIGH speed3
       			fanSwitch.setFanSpeed(3) 
            	log.debug "HI speed(CT=$currentTemp, SP=$desiredTemp, FS=$fanSwitch.currentSwitch, Speed=$currentsetFanSpeed, HighDiff=$HighDiff)"
	        break 
		case { it >= MedDiff }:
            	// turn on fan MEDIUM speed2
            	fanSwitch.setFanSpeed(2) 
            	log.debug "MED speed(CT=$currentTemp, SP=$desiredTemp, FS=$fanSwitch.currentSwitch, MedDiff=$MedDiff)"
                break
       		case { it >= LowDiff }:
            	// turn on fan LOW speed1
            	if (fanSwitch.currentSwitch == "off") {		// if fan is OFF to make it easier on motor by   
//            		fanSwitch.setFanSpeed(4)					// starting fan in High speed temporarily then 
//                	fanSwitch.setFanSpeed(1, [delay: 500])	// change to Low speed after 1/2 second
                    fanSwitch.setFanSpeed(1)	            // took out Hi speed start delay until ST platform issues are resolved causing HI speed stuck on
                	log.debug "LO speed after HI 3secs(CT=$currentTemp, SP=$desiredTemp, FS=$fanSwitch.currentSwitch, LowDiff=$LowDiff)"
          		} else {
                	fanSwitch.setFanSpeed(1)	//fan is already running, not necessary to protect motor
            	}							//set Low speed immediately
            	log.debug "LO speed immediately(CT=$currentTemp, SP=$desiredTemp, FS=$fanSwitch.currentSwitch, LowDiff=$LowDiff)"
                break
		default:
            	// check to see if fan should be turned off
            	if (desiredTemp - currentTemp >= 0 ) {	//below or equal to setpoint, turn off fan, zero level
            		fanSwitch.off()
            		log.debug "below SP+Diff=fan OFF (CT=$currentTemp, SP=$desiredTemp, FS=$fanSwitch.currentSwitch, autoMode=$autoMode,)"
				} 
                log.debug "autoMode YES-MANUAL? else OFF(CT=$currentTemp, SP=$desiredTemp, FS=$fanSwitch.currentSwitch, autoMode=$autoMode,)"
        }	
	}	
}

private hasBeenRecentMotion()
{
	def isActive = false
	if (motionSensor && minutes) {
		def deltaMinutes = minutes as Long
		if (deltaMinutes) {
			def motionEvents = motionSensor.eventsSince(new Date(now() - (60000 * deltaMinutes)))
			log.trace "Found ${motionEvents?.size() ?: 0} events in the last $deltaMinutes minutes"
			if (motionEvents.find { it.value == "active" }) {
				isActive = true
			}
		}
	}
	else {
		isActive = true
	}
	isActive
}

private def textHelp() {
	def text =
	
    	"This smartapp provides automatic control of 4 speeds on a"+
		" ZigBee ceiling fan using any temperature sensor based on its' temperature setpoint"+
        " turning on each speed automatically in 1 degree differential increments."+
        " For example, if the desired room temperature setpoint is 72, speed 1 (low)"+
        " turns on at 73, then speed 2 (medium) at 74, then speed 3 (med-high) at 75, then"+
        " speed 4 (high) at 76. And vice versa on decreasing temperature until at 72 the ceiling"+
        " fan turns off. The differential is adjustable from 0.5 to 2.0 in half degree increments. \n\n" +
        "A notable feature is when low speed is initially requested from"+
        " the off condition, high speed is turned on briefly to overcome the startup load"+
        " then low speed is engaged. This mimics the pull chain switches that most"+
        " manufacturers use by always starting in high speed. \n\n"+
      	"A motion option turns off automatic mode when no motion is detected. A thermostat"+
        " mode option will disable the smartapp and pass control to manual control.\n\n"+
        "This app written specifically for the 'KOF ZigBee Fan Controller Custom Device Handler' used"+
        " in the Hampton Bay Wink Ceiling Fan MR101Z receiver in the Gardinier 52' Ceiling Fan or"+
        " Universal Ceiling Fan Premier Remote from Home Depot."
    
    }