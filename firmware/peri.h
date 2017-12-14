#define IS_SWITCH_PRESSED() ((PINC&(1<<PC3))==0)
#define RED    PC0
#define YELLOW PC1
#define GREEN  PC2
#define ON     1
#define OFF    0



void init_peripheral();
void set_led(uint8_t,uint8_t);
void set_led_value(uint8_t);
uint16_t read_adc(uint8_t);
