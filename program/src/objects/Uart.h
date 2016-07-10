/*
 * uart.h
 *
 *  Created on: Jun 7, 2015
 *      Author: gijs
 */

#ifndef UART_H_
#define UART_H_

#include <stdio.h>
#include <termios.h>
#include <iostream>

typedef enum {
	uart0 = 0, uart1 = 1, uart2 = 2, uart3 = 3, uart4 = 4, uart5 = 5
} uart_number;


typedef struct {
	int fd;
	uart_number uart_id;
	int baudrate;
} uart_properties;

#define SLOTS "/sys/devices/bone_capemgr.9/slots/"
class UART {
public:
	UART(uart_number number, int baudrate);
	~UART();
	int sendData(std::string msg);
	std::string readData();
private:
	uart_properties *properties;
};

#endif /* UART_H_ */
