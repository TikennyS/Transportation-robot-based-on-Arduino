#include "./IRremote.h"

//红外遥控
int RECV_PIN = 2; // 红外一体化接收头连接到Arduino 11号引脚
IRrecv irrecv(RECV_PIN);
decode_results results; // 用于存储编码结果的对象
unsigned long last = millis();

#define run_car     '1'//按键前
#define back_car    '2'//按键后
#define left_car    '3'//按键左
#define right_car   '4'//按键右
#define stop_car    '0'//按键停
/*小车运行状态枚举*/
enum {
  enSTOP = 0,
  enRUN,
  enBACK,
  enLEFT,
  enRIGHT,
  enTLEFT,
  enTRIGHT
} enCarState;

//==============================
//
//车速控制量 control
int control = 100;          //PWM控制量
#define level1  0x08//速度控制标志位1
#define level2  0x09//速度控制标志位2
#define level3  0x0A//速度控制标志位3
#define level4  0x0B//速度控制标志位4
#define level5  0x0C//速度控制标志位5
#define level6  0x0D//速度控制标志位6
#define level7  0x0E//速度控制标志位7
#define level8  0x0F//速度控制标志位8
//==============================
//==============================
int Left_motor_back = 9;     //左电机后退(IN1)
int Left_motor_go = 5;       //左电机前进(IN2)
int Right_motor_go = 6;      // 右电机前进(IN3)
int Right_motor_back = 10;   // 右电机后退(IN4)
int Right_motor_en = 8;      // 右电机前进(EN2)
int Left_motor_en = 7;       // 右电机后退(EN1)

/*协议用到*/
int incomingByte = 0;       // 接收到的 data byte
String inputString = "";         // 用来储存接收到的内容
boolean newLineReceived = false; // 前一次数据结束标志
boolean startBit  = false;  //协议开始标志
String returntemp = ""; //存储返回值

/*蜂鸣器*/
int BUZZER = 3;              //设置控制蜂鸣器的数字IO脚
/*LED灯表示模式*/
int LED = 13;
/*模式切换按键*/
int KEY = 4;

/*超声波*/
int Echo = A1;  // Echo回声脚(P1.1)
int Trig = A0; //  Trig 触发脚(P1.0)
int Distance = 0;
/*巡线*/
const int SensorRight = A3;   	//右循迹红外传感器(A3 OUT3 P1.3)
const int SensorLeft = A2;     	//左循迹红外传感器(A2 OUT4 P1.2)
int SL;    //左循迹红外传感器状态
int SR;    //右循迹红外传感器状态
/*红外避障*/
const int SensorRight_2 = A4;     //右红外传感器(A4 OUT2 P1.4)
const int SensorLeft_2 = A5;     //左红外传感器(A5 OUT1 P1.5)
int SL_2;    //左红外传感器状态
int SR_2;    //右红外传感器状态

//状态机表示
int g_carstate = enSTOP; //  1前2后3左4右0停止 //车状态指示机
int g_modeSelect = 0;  //0是默认状态;  1:巡线  2: 超声波避障 3: 红外跟踪
int g_modeComunication = 0; //0 是红外 1: 蓝牙
int g_AllState = 0;  // 0: 工作状态; 1:选择模式
int g_IRRealse = 0; //红外松手检测


/*printf格式化字符串初始化*/
int serial_putc( char c, struct __file * )
{
  Serial.write( c );
  return c;
}
void printf_begin(void)
{
  fdevopen( &serial_putc, 0 );
}

void setup()
{
  //初始化电机驱动IO为输出方式
  pinMode(Left_motor_go, OUTPUT);    // PIN 5 (PWM)
  pinMode(Left_motor_back, OUTPUT);  // PIN 9 (PWM)
  pinMode(Right_motor_go, OUTPUT);   // PIN 6 (PWM)
  pinMode(Right_motor_back, OUTPUT); // PIN 10 (PWM)
  pinMode(Right_motor_en, OUTPUT);   // PIN 8
  pinMode(Left_motor_en, OUTPUT);    // PIN 7

  pinMode(BUZZER, OUTPUT);//设置数字IO脚模式，OUTPUT为输出
  pinMode(LED, OUTPUT);//设置数字IO脚模式，OUTPUT为输出
  pinMode(KEY, INPUT_PULLUP);     //将2号管脚设置为输入并且内部上拉模式

  pinMode(Echo, INPUT);    // 定义超声波输入脚
  pinMode(Trig, OUTPUT);   // 定义超声波输出脚

  pinMode(SensorRight, INPUT); //定义右循迹红外传感器为输入
  pinMode(SensorLeft, INPUT); //定义左循迹红外传感器为输入

  Serial.begin(9600);	//波特率9600 （蓝牙通讯设定波特率）

  digitalWrite(BUZZER, HIGH);   //不发声
  digitalWrite(Left_motor_en, HIGH); // 右电机前进
  digitalWrite(Right_motor_en, HIGH); // 右电机前进

  g_carstate = enSTOP; //  1前2后3左4右0停止 //车状态指示机
  g_modeComunication = 0; //0 是红外 1: 蓝牙
  g_modeSelect = 0;    //0是默认状态;  1:巡线  2: 超声波避障 3: 红外跟踪
  irrecv.enableIRIn(); // 初始化红外解码
  pinMode(RECV_PIN, INPUT_PULLUP);     //将2号管脚设置为输入并且内部上拉模式

  printf_begin();
}

void Distance_test()   // 量出前方距离
{
  digitalWrite(Trig, LOW);   // 给触发脚低电平2μs
  delayMicroseconds(2);
  digitalWrite(Trig, HIGH);  // 给触发脚高电平10μs，这里至少是10μs
  delayMicroseconds(10);
  digitalWrite(Trig, LOW);    // 持续给触发脚低电
  float Fdistance = pulseIn(Echo, HIGH);  // 读取高电平时间(单位：微秒)
  Fdistance = Fdistance / 58;    //为什么除以58等于厘米，  Y米=（X秒*344）/2
  Distance = Fdistance;
}

void run()     // 前进
{
  digitalWrite(Right_motor_go, HIGH); // 右电机前进
  digitalWrite(Right_motor_back, LOW);
  analogWrite(Right_motor_go, control); //PWM比例0~255调速，左右轮差异略增减

  digitalWrite(Left_motor_go, HIGH); // 左电机前进
  digitalWrite(Left_motor_back, LOW);
  analogWrite(Left_motor_go, control); //PWM比例0~255调速，左右轮差异略增减

}

void brake()         //刹车，停车
{
  digitalWrite(Left_motor_back, LOW);
  digitalWrite(Left_motor_go, LOW);
  digitalWrite(Right_motor_go, LOW);
  digitalWrite(Right_motor_back, LOW);
}

void left()         //左转(左轮不动，右轮前进)
{
  digitalWrite(Right_motor_go, HIGH);	// 右电机前进
  digitalWrite(Right_motor_back, LOW);
  analogWrite(Right_motor_go, control);//control);

  digitalWrite(Left_motor_go, LOW);  //左轮不动
  digitalWrite(Left_motor_back, LOW);
}

void spin_left()         //左转(左轮后退，右轮前进)
{
  digitalWrite(Right_motor_go, HIGH);	// 右电机前进
  digitalWrite(Right_motor_back, LOW);
  analogWrite(Right_motor_go, control);

  digitalWrite(Left_motor_go, LOW);  //左轮后退
  digitalWrite(Left_motor_back, HIGH);
  analogWrite(Left_motor_back, control); //PWM比例0~255调速
  delay(1000);

}

void right()        //右转(右轮不动，左轮前进)
{
  digitalWrite(Right_motor_go, LOW);  //右电机不动
  digitalWrite(Right_motor_back, LOW);

  digitalWrite(Left_motor_go, HIGH); //左电机前进
  digitalWrite(Left_motor_back, LOW);
  analogWrite(Left_motor_go, control); //control);
}

void spin_right()        //右转(右轮后退，左轮前进)
{
  digitalWrite(Right_motor_go, LOW);  //右电机后退
  digitalWrite(Right_motor_back, HIGH);
  analogWrite(Right_motor_back, control); //PWM比例0~255调速

  digitalWrite(Left_motor_go, HIGH); //左电机前进
  digitalWrite(Left_motor_back, LOW);
  analogWrite(Left_motor_go, control); //PWM比例0~255调速

}

void back()          //后退
{
  digitalWrite(Right_motor_go, LOW); //右轮后退
  digitalWrite(Right_motor_back, HIGH);
  analogWrite(Right_motor_back, control); //PWM比例0~255调速

  digitalWrite(Left_motor_go, LOW); //左轮后退
  digitalWrite(Left_motor_back, HIGH);
  analogWrite(Left_motor_back, control); //PWM比例0~255调速

}
void whistle()   //鸣笛
{
  int i;
  for (i = 0; i < 80; i++) //输出一个频率的声音
  {
    digitalWrite(BUZZER, LOW); //发声音
    delay(10);//延时1ms
    digitalWrite(BUZZER, HIGH); //不发声音
    delay(1);//延时ms
  }
  for (i = 0; i < 100; i++) //输出另一个频率的声音
  {
    digitalWrite(BUZZER, LOW); //发声音
    delay(20);//延时2ms
    digitalWrite(BUZZER, HIGH); //不发声音
    delay(2);//延时2ms
  }
}

/*蓝牙接收处理*/
void Bluetooth(void)
{
  if (newLineReceived)
  {
    //先判断是否是模式选择
    if (inputString[1] == 'M' && inputString[2] == 'O' && inputString[3] == 'D' && inputString[4] == 'E')
    {
      if (inputString[6] == '0') //停止模式
      {
        g_carstate = enSTOP;
        g_modeSelect = 0;
        g_AllState = 0;
        BeepOnOffMode();
      }
      else
      {
        switch (inputString[5])
        {
          case '0': g_modeSelect = 0; break;
          case '1': g_modeSelect = 1; break;
          case '2': g_modeSelect = 2; break;
          case '3': g_modeSelect = 3; break;
          default: g_modeSelect = 0; break;
        }
        g_AllState = 0;
         BeepOnOffMode();
      }
    }
    else if (g_modeSelect == 0 && g_AllState == 0) //默认遥控模式
    {
      switch (inputString[1])
      {
        case run_car:   g_carstate = enRUN;  break;
        case back_car:  g_carstate = enBACK;  break;
        case left_car:  g_carstate = enLEFT;  break;
        case right_car: g_carstate = enRIGHT; break;
        case stop_car:  g_carstate = enSTOP;  break;
        default: g_carstate = enSTOP; break;
      }
      if (inputString[3] == '1') //旋转
      {
        spin_left();
        //Serial.print("revolve\r\n");
        //delay(2000);//延时
        brake();
      }
      else if (inputString[3] == '2') //旋转
      {
        spin_right();
        //Serial.print("revolve\r\n");
        //delay(2000);//延时2ms
        brake();
      }
      if (inputString[5] == '1') //鸣笛
      {
        whistle();
       // Serial.print("whistle\r\n");
      }
      if (inputString[7] == '1') //加速
      {
        control += 50;
        if (control > 255)
        {
          control = 255;
        }
        //Serial.print("expedite\r\n");
      }
      if (inputString[9] == '1') //减速
      {
        control -= 50;
        if (control < 50)
        {
          control = 100;
        }
        //Serial.print("reduce\r\n");
      }

      //返回状态
      Distance_test();
      returntemp = "$0,0,0,0,0,0,0,0,0,0,0,";
      returntemp.concat(Distance);
      returntemp += "cm,8.2V#";
      Serial.print(returntemp); //返回协议数据包

    }
    inputString = "";   // clear the string
    newLineReceived = false;

  }
}

void Key_Scan(void)
{
  int val;
  while (!digitalRead(KEY)) //当按键被按下时
  {
    delay(10);	//延时10ms
    val = digitalRead(KEY);//读取数字4 口电平值赋给val 0
    if (val == LOW) //第二次判断按键是否被按下
    {
      if (g_modeComunication == 0)
      {
        g_modeComunication = 1; //蓝牙模式
        digitalWrite(LED, HIGH); //点亮LED
      }
      else
      {
        g_modeComunication = 0; //红外模式
        digitalWrite(LED, LOW); //熄灭LED
      }
      digitalWrite(BUZZER, LOW);		//蜂鸣器响
      delay(100);//100ms
      digitalWrite(BUZZER, HIGH);		//蜂鸣器不响
      while (!digitalRead(KEY));	//判断按键是否被松开
    }
    else
      digitalWrite(BUZZER, HIGH); //蜂鸣器停止
  }
}

//遥控控制小车
void CarControl()
{
  if (g_modeSelect != 2 )
  {
    switch (g_carstate)
    {
      case enSTOP: brake(); break;
      case enRUN: run(); break;
      case enLEFT: left(); break;
      case enRIGHT: right(); break;
      case enBACK: back(); break;
      case enTLEFT: spin_left(); break;
      case enTRIGHT: spin_right(); break;
      default: brake(); break;
    }
   
  }
}

void IR_Deal()
{
  if (irrecv.decode(&results)) 
  {
    //Serial.println(results.value, HEX);

    //if (((results.value >> 16) & 0x0000ffff) == 0x00ff)
    //{
    //printf("$AR,HSX,%08lX#\n", results.value);
    //根据不同值来执行不同操作
    //  00FFA25D  开关
    //  00FF02FD   +
    //  00FF9867   -
    //  00FFA857   启动
    //  00FFE01F   上一个
    //  00FF906F   下一个
    //  00FF6897   0  beep
    //  00FF18E7   2  前进
    //  00FF10EF   4  左转
    //  00FF38C7   5  停止
    //  00FF5AA5   6  右转
    //  00FF4AB5   8  后退
    //  00FF42BD   7  左旋
    //  00FF52AD   9  右旋

    //  00FFE21D   MENU 遥控模式  1
    //  00FFC23D   Back 巡线模式  2
    //  00FF906F   NEXT 避障模式  3
    //  00FFB04F   C    跟随模式  4
    switch (results.value)
    {
      case 0x00FFA25D: g_carstate = enSTOP; g_AllState = 0; g_modeSelect = 0; BeepOnOffMode() ;break;
      case 0x00FFA857: g_carstate = enSTOP; g_AllState = 0; BeepOnOffMode() ; break; // PLAY
      //case 0x00FFE01F: g_AllState = 1; g_modeSelect--; if (g_modeSelect == -1) g_modeSelect = 3; break;
      //case 0x00FF906F: g_AllState = 1; g_modeSelect++; if (g_modeSelect == 4) g_modeSelect = 0;  break;
      case 0x00FFE21D: g_AllState = 1; g_modeSelect = 0;  ModeBEEP(g_modeSelect); break; // MENU 遥控模式  1
      case 0x00FFC23D: g_AllState = 1; g_modeSelect = 2;  ModeBEEP(g_modeSelect); break; // NEXT 避障模式  2
      case 0x00FF906F: g_AllState = 1; g_modeSelect = 1;  ModeBEEP(g_modeSelect); break; // Back 巡线模式  3
      case 0x00FFB04F: g_AllState = 1; g_modeSelect = 3;  ModeBEEP(g_modeSelect); break; // C    跟随模式  4

      default: break;
    }
    if (g_modeSelect == 0 && g_AllState == 0)
    {
      switch (results.value)
      {
        
        case 0x00FF02FD: control += 50; if (control > 255) control = 255; break;
        case 0x00FF9867: control -= 50; if (control < 50) control = 100; break;

        case 0x00FF6897: whistle(); break;
        case 0x00FF18E7:  g_carstate = enRUN; break;
        case 0x00FF10EF:  g_carstate = enLEFT; break;
        case 0x00FF38C7:  g_carstate = enSTOP; break;
        case 0x00FF5AA5:  g_carstate = enRIGHT; break;
        case 0x00FF4AB5:  g_carstate = enBACK; break;
        case 0x00FF42BD:  g_carstate = enTLEFT; break;
        case 0x00FF52AD:  g_carstate = enTRIGHT; break;
        default: break; //保持原来状态

      }
     
    }

    //}
    last = millis();
    irrecv.resume(); // 接收下一个编码
  }
  else if (millis() - last > 120)
  {
    g_carstate = enSTOP;
    last = millis();
  }

}
//模式显示函数
void ModeBEEP(int mode)
{
  for (int i = 0; i < mode + 1; i++)
  {
    digitalWrite(BUZZER, LOW); //鸣
    delay(100);
    digitalWrite(BUZZER, HIGH); //不鸣
    delay(100);
  }
  delay(100);
  digitalWrite(BUZZER, HIGH); //不鸣
}
//开关模式长鸣1秒
void BeepOnOffMode()
{
  digitalWrite(BUZZER, LOW); //鸣
  delay(1000);
  digitalWrite(BUZZER, HIGH); //不鸣
}

//巡线模式
void track()
{ 
  
  //有信号为LOW  没有信号为HIGH
  SR = digitalRead(SensorRight);//有信号表明在白色区域，车子底板上L3亮；没信号表明压在黑线上，车子底板上L3灭
  SL = digitalRead(SensorLeft);//有信号表明在白色区域，车子底板上L2亮；没信号表明压在黑线上，车子底板上L2灭
  if (SL == LOW && SR == LOW) //当两边RPR220同时检测白色
  g_carstate = enSTOP;   //调用停止函数
  else if (SL == LOW & SR == HIGH)// 左循迹红外传感器,检测到信号，车子向右偏离轨道，向左转
  g_carstate = enRIGHT;
  else if (SR == LOW & SL ==  HIGH) // 右循迹红外传感器,检测到信号，车子向左偏离轨道，向右转
  g_carstate = enLEFT;
  else // 双探头都是检测到了黑线的情况下, 前进
  g_carstate = enRUN;
    
}
//超声波避障
void ultrason_obstacle_avoiding()
{
  Distance_test();//测量前方距离

  if (Distance < 25) //数值为碰到障碍物的距离，可以按实际情况设置
  {
      g_carstate = enSTOP;
      whistle();
  }
}
//跟随模式
void Infrared_follow()
{
  //有信号为LOW  没有信号为HIGH
  SR_2 = digitalRead(SensorRight_2);
  SL_2 = digitalRead(SensorLeft_2);
  if (SL_2 == LOW && SR_2 == LOW)
    g_carstate = enRUN;   //调用前进函数
  else if (SL_2 == HIGH & SR_2 == LOW)// 右边探测到有障碍物，有信号返回，向右转
    g_carstate = enRIGHT;
  else if (SR_2 == HIGH & SL_2 == LOW) //左边探测到有障碍物，有信号返回，向左转
    g_carstate = enLEFT;
  else // 没有障碍物，停
    g_carstate = enSTOP;
}

/*程序主循环入口*/
void loop()
{
  /*通信模式扫描*/
  Key_Scan();

  if (g_modeComunication == 0) //红外模式
  {
    IR_Deal();
  }
  else//蓝牙模式
  {
    Bluetooth();
  }

  // 切换不同功能模式, 功能模式显示
  if (g_AllState == 0) // 切换不同功能模式
  {
    switch (g_modeSelect)
    {
      case 1: track(); break; //巡线模式
      case 2: ultrason_obstacle_avoiding(); break; //避障
      case 3: Infrared_follow(); break; //跟随
    }
  }
  Distance_test();
  if(Distance<20&&g_carstate!=enBACK&&g_carstate!=enTLEFT)
  g_carstate=enSTOP;
  CarControl();


}

//serialEvent()是IDE1.0及以后版本新增的功能，不清楚为什么大部份人不愿意用，这个可是相当于中断功能一样的啊!
void serialEvent()
{
  while (Serial.available())
  {
    incomingByte = Serial.read();              //一个字节一个字节地读，下一句是读到的放入字符串数组中组成一个完成的数据包
    if (incomingByte == '$')
    {
      startBit = true;
    }
    if (startBit == true)
    {
      inputString += (char) incomingByte;     // 全双工串口可以不用在下面加延时，半双工则要加的//
    }
    if (incomingByte == '#')
    {
      newLineReceived = true;
      startBit = false;
    }
  }
}


