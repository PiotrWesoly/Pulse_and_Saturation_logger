# Pulse_and_Saturation_logger

Application is used to visualize the data obtained from the pulse and saturation logger device that I have created using pulse and saturation sensor along with ESP32 microcontroller. The application connects to the microcontroller via Bluetooth. The microcontroller then sends recorded history of readings to the phone, where it gets stored in the buffer. After all of the history has been received, the microcontroller will keep sending current readings. 

Gathered data can be seen in a history activity, where user can filter out any day and time they want. There is also a graph activity, which shows the change in heart rate and blood saturation over time. User can also see the current values on the main screen. Additionally, when the heart rate is over threshold values, the user gets informed about this via notification.


ScreenShots from the application

![alt text](https://github.com/PiotrWesoly/Pulse_and_Saturation_logger/blob/master/Design.png?raw=true=200x100)



