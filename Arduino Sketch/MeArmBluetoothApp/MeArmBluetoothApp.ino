#include <SoftwareSerial.h>
#include <Servo.h>

#define RxD 6
#define TxD 7

int pos = 0, rot = 0, grab = 0;
Servo extend, reach, rotate, grabber;
SoftwareSerial bluetoothSerial(RxD, TxD);

void setup() {
  Serial.begin(9600);
  
  extend.attach(10);
  reach.attach(11);
  rotate.attach(12);
  grabber.attach(13);
  
  pinMode(RxD, INPUT);
  pinMode(TxD, OUTPUT);
  setupBluetooth();
}

void moveArm(char direction, int amount)
{
  switch(direction)
  {
    case 'f':
    pos += amount;
    break;
    case 'b':
    pos -= amount;
    break;
  }
  pos = max(0, pos);
  pos = min(180, pos);
  extend.write(pos);
}
void rotateArm(char direction, int amount)
{
  switch(direction)
  {
    case 'l':
    rot += amount;
    break;
    case 'r':
    rot -= amount;
    break;
  }
  rot = max(0, rot);
  rot = min(180, rot);
  rotate.write(rot);
}
void clawArm(char direction, int amount)
{
  switch(direction)
  {
    case 'g':
    grab += amount;
    break;
    case 'r':
    grab -= amount;
    break;
  }
  grab = max(0, grab);
  grab = min(180, grab);
  grabber.write(grab);
}

void doMove(char move, char direction, int amount)
{
  switch(move)
  {
    case 'm':
    moveArm(direction, amount);
    break;
    case 'r':
    rotateArm(direction, amount);
    break;
    case 'c':
    clawArm(direction, amount);
    break;
  }
}

void loop() {
  while(1)
  {
    if(bluetoothSerial.available())
    {
      int count = 0;
      String input;
      while(bluetoothSerial.available())
      {
        char inputChar = bluetoothSerial.read();
        if(inputChar == '\n')
        {
          break;
        }
        count += 1;
        input += inputChar;
      }
      
      if(count == 3)
      {
        char move = input[0];
        char direction = input[1];
        int amount = input[2] - '0';
        doMove(move, direction, amount);
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


