#include <SoftwareSerial.h>

#define rxPin 2
#define txPin 3
#define ledPin 13
#define buttonPin 4
#define reedSwitch 5
#define fsrPin 0

SoftwareSerial sim800L(rxPin,txPin); 

unsigned long previousMillis = 0;
long interval = 60000;
int buttonState = 0;
int buttonSwitch = 0;
int reedSwitchState = 0;
int fsrReading;

void setup()
{
  //Begin serial communication with Arduino and Arduino IDE (Serial Monitor)
  Serial.begin(9600);
  
  //Begin serial communication with Arduino and SIM800L
  sim800L.begin(9600);

  Serial.println("Initializing...");
  //delay(10000);

  //Once the handshake test is successful, it will back to OK
  sendATcommand("AT", "OK", 2000);
  sendATcommand("AT+CMGF=1", "OK", 2000);
  //sim800L.print("AT+CMGR=40\r");

  pinMode(ledPin,OUTPUT);
  pinMode(buttonPin,INPUT);
  pinMode(reedSwitch,INPUT);
  
}

void loop()
{
  while(sim800L.available()){
    Serial.println(sim800L.readString());
  }
  while(Serial.available())  {
    sim800L.println(Serial.readString());
  }

    unsigned long currentMillis = millis();
    if(currentMillis - previousMillis > interval) {
       previousMillis = currentMillis;
       sendDataToServer();
    }
}

int sendDataToServer()
{
    String reg_no = "KA04AB1234";
    int sb_st = getSensorData();

    String url, temp;
    //url = "http://e-waka.000webhostapp.com/gpsdata.php?lat="; //https://databases-auth.000webhost.com/db_structure.php?server=1&db=id19697922_ewakatracker
    url = "http://seatbelt-data.000webhostapp.com/sbdata.php?reg_no=";
    url += reg_no;
    url += "&sb_st=";
    url += sb_st;

    //url = "http://ahmadssd.000webhostapp.com/gpsdata.php?lat=222&lng=222";

    Serial.println(url); 
    Serial.println("");   
    delay(300);
          
    sendATcommand("AT+CFUN=1", "OK", 2000);
    Serial.println("AT+CFUN=1");
    //AT+CGATT = 1 Connect modem is attached to GPRS to a network. AT+CGATT = 0, modem is not attached to GPRS to a network
    sendATcommand("AT+CGATT=1", "OK", 2000);
    Serial.println("AT+CGATT=1");
    //Connection type: GPRS - bearer profile 1
    sendATcommand("AT+SAPBR=3,1,\"Contype\",\"GPRS\"", "OK", 2000);
    Serial.println("72 AT+SAPBR");
    //sets the APN settings for your network provider.
    sendATcommand("AT+SAPBR=3,1,\"APN\",\"airtelgprs.com\"", "OK", 2000);
    Serial.println("75 AT+SAPBR");
    //enable the GPRS - enable bearer 1
    sendATcommand("AT+SAPBR=1,1", "OK", 2000);
    Serial.println("77 AT+SAPBR");
    //Init HTTP service
    sendATcommand("AT+HTTPINIT", "OK", 2000);
    Serial.println("AT+HTTPINIT");
    sendATcommand("AT+HTTPPARA=\"CID\",1", "OK", 1000);
    Serial.println("82 AT+HTTPPARA");
    //Set the HTTP URL sim800.print("AT+HTTPPARA="URL","http://ahmadssd.000webhostapp.com/gpsdata.php?lat=222&lng=222"\r");
    sim800L.print("AT+HTTPPARA=\"URL\",\"");
    Serial.println("85 AT+HTTPINIT");
    sim800L.print(url);
    sendATcommand("\"", "OK", 1000);
    //Set up the HTTP action
    sendATcommand("AT+HTTPACTION=0", "0,200", 1000);
    Serial.println("AT+HTTPACTION=0");
    delay(2000);
    //Terminate the HTTP service
    sendATcommand("AT+HTTPTERM", "OK", 1000);
    Serial.println("AT+HTTPTERM");
    //shuts down the GPRS connection. This returns "SHUT OK".
    sendATcommand("AT+CIPSHUT", "SHUT OK", 1000);
    Serial.println("AT+CIPSHUT");
  return 1;    
}

int8_t sendATcommand(char* ATcommand, char* expected_answer, unsigned int timeout){

    uint8_t x=0,  answer=0;
    char response[100];
    unsigned long previous;

    //Initialice the string
    memset(response, '\0', 100);
    delay(100);
    
    //Clean the input buffer
    while( sim800L.available() > 0) sim800L.read();
    
    if (ATcommand[0] != '\0'){
      //Send the AT command 
      sim800L.println(ATcommand);
    }

    x = 0;
    previous = millis();

    //this loop waits for the answer with time out
    do{
        //if there are data in the UART input buffer, reads it and checks for the asnwer
        if(sim800L.available() != 0){
            response[x] = sim800L.read();
            //Serial.print(response[x]);
            x++;
            // check if the desired answer (OK) is in the response of the module
            if(strstr(response, expected_answer) != NULL){
                answer = 1;
            }
        }
    }while((answer == 0) && ((millis() - previous) < timeout));

  Serial.println(response);
  return answer;
}

int getSensorData(){
  reedSwitchState = digitalRead(reedSwitch);
  fsrReading = analogRead(fsrPin);

  if(fsrReading > 500){
    if(reedSwitchState == LOW){
      digitalWrite(ledPin,LOW);
      delay(1000);
      digitalWrite(ledPin,HIGH);
      return 1;
    }
    else{
      digitalWrite(ledPin,LOW);
      delay(500);
      digitalWrite(ledPin,HIGH); 
      delay(500);
      digitalWrite(ledPin,LOW);
      delay(500);
      digitalWrite(ledPin,HIGH);
      return 0;
    }
  }

  else{
    return 2;
  }
}