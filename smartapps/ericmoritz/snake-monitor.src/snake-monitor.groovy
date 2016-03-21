/**
 *  Snake Monitor
 *
 *  Copyright 2016 Eric Moritz
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
definition(
    name: "Snake Monitor",
    namespace: "ericmoritz",
    author: "Eric Moritz",
    description: "Manages a snake temperature and night off",
    category: "Pets",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Devices") {
    	input "tempSensor", "capability.temperatureMeasurement", title: "Thermometer?"
        input "heatLamp", "capability.switchLevel", title: "Heat Lamp?"
    }
    section("Settings") {
        input "minTemp", "number", title: "Min Temp", defaultValue: 75
        input "maxTemp", "number", title: "Max Temp", defaultValue: 80
        input "timeout", "number", title: "Check Every x Seconds", defaultValue: 60
        input "adjustBy", "number", title: "Adjust Lamp By x Percent", defaultValue: 1
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	runIn(settings.timeout, checkTemp)
}

def checkTemp() {
	def currentTemp = tempSensor.currentValue("temperature")
    def currentPercent = heatLamp.currentValue("level")
    if(settings.minTemp > currentTemp) {
    	def newLevel = currentPercent + settings.adjustBy
        log.debug("Current Temp ($currentTemp) is less than $settings.minTemp, raising to $newLevel")
        heatLamp.setLevel(newLevel)
    } else if(currentTemp > settings.maxTemp) {    	
    	def newLevel = currentPercent - settings.adjustBy
        log.debug("Current Temp ($currentTemp) is more than $settings.maxTemp, lowering to $newLevel")
        heatLamp.setLevel(newLevel)
    } else {
	    log.debug("Current Temp ($currentTemp) is fine")
    }
}