/*
 * Utils.cpp
 *
 *  Created on: Jun 9, 2016
 *      Author: gijs
 */

#include <syslog.h>
#include <stdio.h>
#include <stdlib.h>
#include "Utils.h"

char *substring(char *string, int position, int length) {
	char *pointer;
	int c;

	pointer = (char*) malloc(length + 1);

	if (pointer == NULL) {
		printf("Unable to allocate memory.\n");
		return NULL;
	}

	for (c = 0; c < length; c++) {
		*(pointer + c) = *(string + position - 1);
		string++;
	}

	*(pointer + c) = '\0';
	return pointer;
}

int copyFile(char *srcFile, char *destinationFile) {
	char ch;
	FILE *source, *target;

	source = fopen(srcFile, "r");

	if (source == NULL) {
		syslog(LOG_INFO, "could not open source file: %s", srcFile);
		return 1;
	}

	target = fopen(destinationFile, "w");

	if (target == NULL) {
		fclose(source);
		syslog(LOG_INFO, "could not create destination file: %s", destinationFile);
		return 1;
	}

	while ((ch = fgetc(source)) != EOF)
		fputc(ch, target);

	syslog(LOG_INFO, "File copied from %s to %s.", srcFile, destinationFile);

	fclose(source);
	fclose(target);

	return 0;

}
