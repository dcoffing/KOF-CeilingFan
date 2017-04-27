
//   Zigbee 4 Speed Ceiling Fan Thermostat Control
   
  def version() {return "v2.1b.20170427b" }    
/*  Change Log
      b - fixed parent name when creating new automation, modified description, user manual    
  04-27  starting modifications for zigbee
  2017-04-11 Added 10.0 selection for Fan Differential Temp to mimic single speed control
  2016-10-19 Ver2 Parent / Child app to allow for multiple use cases with a single install - @ericvitale
  
*/
definition(
    name: "4 Speed Ceiling Fan Thermostat - Zigbee",
    namespace: "dcoffing",
    author: "Dale Coffing",
    description: "Thermostat control for Zigbee 4 Speed Ceiling Fan device MR101Z staging Low, Medium, Medium-High, High speeds with any temperature sensor.",
    category: "My Apps",
    singleInstance: true,
	iconUrl: "https://raw.githubusercontent.com/dcoffing/SmartThingsPublic/master/smartapps/dcoffing/3-speed-ceiling-fan-thermostat.src/3scft125x125.png", 
   	iconX2Url: "https://raw.githubusercontent.com/dcoffing/SmartThingsPublic/master/smartapps/dcoffing/3-speed-ceiling-fan-thermostat.src/3scft250x250.png",
	iconX3Url: "https://raw.githubusercontent.com/dcoffing/SmartThingsPublic/master/smartapps/dcoffing/3-speed-ceiling-fan-thermostat.src/3scft250x250.png",
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
            app(name: "childApps", appName: appName(), namespace: "dcoffing", title: "New Zigbee Ceiling Fan Automation", multiple: true)
        }
    }
}

def childStartPage() {
	dynamicPage(name: "childStartPage", title: "Select your devices and settings", install: true, uninstall: true) {
    
        section("Select a room temperature sensor to control the fan..."){
			input "tempSensor", "capability.temperatureMeasurement", multiple:false, title: "Temperature Sensor", required: true, submitOnChange: true  
		}
        if (tempSensor) {  //protects from a null error
    		section("Enter the desired room temperature setpoint...\n" + "NOTE: ${tempSensor.displayName} room temp is currently ${tempSensor.currentTemperature}°"){
        		input "setpoint", "decimal", title: "Room Setpoint Temp", defaultValue: tempSensor.currentTemperature, required: true
    		}
        }
        else 
        	section("Enter the desired room temperature setpoint..."){
        		input "setpoint", "decimal", title: "Room Setpoint Temp", required: true
    		}       
        section("Select the Zigbee ceiling fan device (NOT Light or Speeds)..."){
        // fanDimmer
			input "fanSwitch", "capability.switch", multiple:false, title: "Zigbee Fan Control device", required: true
		}
        section("Optional Settings (Diff Temp, Timers, Motion, etc)") {
			href (name: "optionsPage", 
        	title: "Configure Optional settings", 
        	description: none,
        	image: "https://raw.githubusercontent.com/dcoffing/SmartThingsPublic/master/smartapps/dcoffing/evap-cooler-thermostat.src/settings250x250.png",
        	required: false,
        	page: "optionsPage"
        	)
        }
        
        section("Name") {
        	label(title: "Assign a name", required: false)
        }
        
        section("Version Info, User's Guide") {
// VERSION
			href (name: "aboutPage", 
			title: "4 Speed Ceiling Fan Thermostat \n"+ version() +" \n"+"Copyright © 2017 Dale Coffing", 
			description: "Tap to get user's guide.",
			image: "https://raw.githubusercontent.com/dcoffing/SmartThingsPublic/master/smartapps/dcoffing/3-speed-ceiling-fan-thermostat.src/3scft125x125.png",
			required: false,
			page: "aboutPage"
			)
		}
	}
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
     	section("User's Guide; 3 Speed Ceiling Fan Thermostat") {
        	paragraph textHelp()
 		}
	}
}

/* I might be able to take advantage of this next line of code to selectively open the zwave OR the zigbee parent based on hardware input selected?
private def appName() { return "${parent ? "3 Speed Fan Automation" : "3 Speed Ceiling Fan Thermostat"}" }
*/
private def appName() { return "${parent ? "3 Speed Fan Automation" : "4 Speed Ceiling Fan Thermostat - Zigbee"}" }

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
            		fanSwitch.setFanSpeed(4)					// starting fan in High speed temporarily then 
                	fanSwitch.setFanSpeed(1, [delay: 500])	// change to Low speed after 1/2 second
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
		"This smartapp provides automatic control of Low, Med, Med-Hi, High speeds of a"+
		" zigbee ceiling fan using any temperature sensor based on its' temperature setpoint"+
        " turning on each speed automatically in 1 degree differential increments."+
        " For example, if the desired room temperature setpoint is 72, the low speed"+
        " turns on first at 73, the medium speed at 74, the med-hi speed at 75, the high"+
        " speed at 76. And vice versa on decreasing temperature until at 72 the ceiling"+
        " fan turns off. The differential is adjustable from 0.5 to 2.0 in half degree increments. \n\n" +
        "A notable feature is when low speed is initially requested from"+
        " the off condition, high speed is turned on briefly to overcome the startup load"+
        " then low speed is engaged. This mimics the pull chain switches that most"+
        " manufacturers use by always starting in high speed. \n\n"+
      	"A motion option turns off automatic mode when no motion is detected. A thermostat"+
        " mode option will disable the smartapp and pass control to manual control.\n\n"+
        "@ChadCK's 'Z-Wave Smart Fan Control Custom Device Handler' along with hardware"+
        " designed specifically for motor control such as the GE 12730 Z-Wave Smart Fan Control or"+
        " Leviton VRF01-1LX works well together with this smartapp."
	}
