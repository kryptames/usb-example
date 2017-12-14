#include <avr/io.h>
#include "peri.h"

void init_peripheral()
{
  DDRC |= 0b00001111;
  PORTC &= 0b11111000;
  DDRC &= 0b11100111;
  PORTC |=0b00001000;
  PORTC &= 0b11101111;
}

void set_led(uint8_t pin,uint8_t state)
{
if(state == ON)
{
  if(pin==RED)
   {
     PORTC |= (1<<RED);
   }
  if(pin==YELLOW)
   {
     PORTC |= (1<<YELLOW);
   }
  if(pin==GREEN)
   {
     PORTC |= (1<<GREEN);
   }
 }
if(state==OFF)

{if(pin==RED)
{
     PORTC &= ~(1<<RED);
   }
  if(pin==YELLOW)
   {
     PORTC &= ~(1<<YELLOW);
   }
  if(pin==GREEN)
   {
     PORTC &= ~(1<<GREEN);
}}
  
}

void set_led_value(uint8_t x)
{
  PORTC &= ~(0b00000111);
PORTC |= (x & 0b00000111);
}

uint16_t read_adc(uint8_t channel)
{
ADMUX = (0<<REFS1)|(1<<REFS0) 
| (0<<ADLAR) 
| (channel & 0b1111);
ADCSRA = (1<<ADEN) 
| (1<<ADPS2)|(1<<ADPS1)|(1<<ADPS0) 
| (1<<ADSC); 
while ((ADCSRA & (1<<ADSC))) 
;
return ADCL + ADCH*256; 
}
