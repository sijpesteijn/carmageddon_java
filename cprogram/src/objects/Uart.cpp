/*
 * uart.c
 *
 *  Created on: Jun 7, 2015
 *      Author: gijs
 */

#include "Uart.h"
#include <syslog.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <fcntl.h>
#include <iostream>
#include <vector>
#include <string>
#include <sstream>
#include <iostream>
#include <string>
#include <sstream>
#include <algorithm>
#include <iterator>
#include <algorithm>
#include <iterator>

UART::UART(uart_number number, int baudrate) {
	properties = (uart_properties *) malloc(sizeof(uart_properties));
	properties->baudrate = baudrate;
	properties->uart_id = number;
	char buf[30] = "/dev/ttyO";
	char port_nr[2];
	sprintf(port_nr, "%d", properties->uart_id);
	strcat(buf,port_nr);
	struct termios uart_port;

//	FILE *slots;
//	slots = fopen(SLOTS, "w");
//	if(slots == NULL) printf("slots didn't open\n");
//	fseek(slots,0,SEEK_SET);
//
//	fprintf(slots, "BB-UART%i", properties->uart_id+1);
//	fflush(slots);
//	fclose(slots);

	properties->fd = open(buf, O_RDWR | O_NOCTTY);
	if(properties->fd < 0) printf("port failed to open\n");

	bzero(&uart_port,sizeof(uart_port));

	uart_port.c_cflag = properties->baudrate | CS8 | CLOCAL | CREAD;
	uart_port.c_iflag = IGNPAR | ICRNL;
	uart_port.c_oflag = 0;
	uart_port.c_lflag = 0;

	uart_port.c_cc[VTIME] = 0;
	uart_port.c_cc[VMIN]  = 1;

	//clean the line and set the attributes
	tcflush(properties->fd,TCIFLUSH);
	tcsetattr(properties->fd,TCSANOW,&uart_port);
	syslog(LOG_INFO, "%s", "UART initialized.");
}

int UART::sendData(std::string msg) {
	if (write(properties->fd, msg.c_str(), msg.length()) == -1) {
		syslog(LOG_ERR, "Could not write %s to uart %i", msg.c_str(), properties->uart_id);
		return -1;
	}
	printf("%s\n", msg.c_str());
	return 0;
}

std::string UART::readData() {
	char str[4096];
	read(this->properties->fd,str,4096);
	std::string answer(str);
	syslog(LOG_INFO, "m:%i %s.\n", answer.length(), answer.c_str());
	return answer;
}

UART::~UART() {
	close(properties->fd);
}
