#include <SPI.h>      //include the SPI library
#include <MFRC522.h>  //include the MFRC522 RFID reader library
#define RST_PIN 9  //reset pin, which can be changed to another digital pin if needed.
#define SS_PIN 10  //SS or the slave select pin, which can be changed to another digital pin if needed.
MFRC522 mfrc522(SS_PIN, RST_PIN);  // create a MFRC522 instant.
MFRC522::MIFARE_Key key;          //create a MIFARE_Key struct named 'key' to hold the card information
byte readbackblock[10];  //Array for reading out a block.
const int ledPin = 13;
String msg;

void setup()
{
  Serial.begin(9600);        // Initialize serial communications with the PC
  SPI.begin();               // Init SPI bus
  mfrc522.PCD_Init();        // Init MFRC522 card (in case you wonder what PCD means: proximity coupling device)
  for (byte i = 0; i < 6; i++)
  {
    key.keyByte[i] = 0xFF;  // Prepare the security key for the read and write operations.
  }
}

void loop()
{

  if (Serial.available() > 0){ // Check if there is data coming
    msg = Serial.readString(); // Read the message as String
  }

  // Look for new cards if not found rerun the loop function
  if ( ! mfrc522.PICC_IsNewCardPresent()) {
    return;
  }
  // read from the card if not found rerun the loop function
  if ( ! mfrc522.PICC_ReadCardSerial())
  {
    return;
  }

  if(msg == "1"){
    digitalWrite(ledPin, HIGH);
    readBlock(1, readbackblock);   //read block 1
    //print data
    Serial.print("<");
    for (int j = 0 ; j < 10 ; j++)
    {
      Serial.write (readbackblock[j]);
    }
    Serial.println(">");
    digitalWrite(ledPin, LOW);
    msg = "0";
  }
}

int readBlock(int blockNumber, byte arrayAddress[])
{
  int largestModulo4Number = blockNumber / 4 * 4;
  int trailerBlock = largestModulo4Number + 3; //determine trailer block for the sector
  //authentication of the desired block for access
  byte status = mfrc522.PCD_Authenticate(MFRC522::PICC_CMD_MF_AUTH_KEY_A, trailerBlock, &key, &(mfrc522.uid));
  if (status != MFRC522::STATUS_OK) {
    // Serial.print("Authentication failed : ");
    // Serial.println(mfrc522.GetStatusCodeName(status));
    return 3;//return "3" as error message
  }
  //reading data from the block
  byte buffersize = 18;
  status = mfrc522.MIFARE_Read(blockNumber, arrayAddress, &buffersize);//&buffersize is a pointer to the buffersize variable; MIFARE_Read requires a pointer instead of just a number
  if (status != MFRC522::STATUS_OK) {
    // Serial.print("Data read failed: ");
    // Serial.println(mfrc522.GetStatusCodeName(status));
    return 4;//return "4" as error message
  }
  // Serial.println("Data read successfully");
}