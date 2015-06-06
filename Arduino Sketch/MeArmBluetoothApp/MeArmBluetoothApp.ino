#include <SoftwareSerial.h>

#define RxD 6
#define TxD 7

SoftwareSerial bluetoothSerial(RxD, TxD);

void setup() {
  Serial.begin(9600);
  pinMode(RxD, INPUT);
  pinMode(TxD, OUTPUT);
  setupBluetooth();
}

void loop() {
  while(1)
  {
    if(bluetoothSerial.available())
    {
      String input = "";
      while(bluetoothSerial.available())
      {
        char inputChar = bluetoothSerial.read();
        if(inputChar == '\n')
        {
          break;
        }
        input += inputChar;
      }
      
      Serial.println(input);
    }
  }
}

void setupBluetooth()
{
  bluetoothSerial.begin(38400);
  bluetoothSerial.print("\r\n+STDWMOD=0\r\n");
  bluetoothSerial.print("\r\n+STNA=ME_ARM\r\n");
  bluetoothSerial.print("\r\n+STOAUT=1\r\n");
  bluetoothSerial.print("\r\n+STAUTO=0\r\n");
  delay(2000);
  bluetoothSerial.print("\r\n+INQ=1\r\n");
  Serial.println("Master is ready!");
  delay(2000);
  bluetoothSerial.flush();
}


