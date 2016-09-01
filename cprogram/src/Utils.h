/*
 * Utils.h
 *
 *  Created on: Jun 9, 2016
 *      Author: gijs
 */

#ifndef UTILS_H_
#define UTILS_H_

#include "jansson/jansson.h"
#include <string>


char *substring(char *string, int position, int length);
int copyFile(char *srcFile, char *destinationFile);
json_t *parseRoot(const char *buffer, size_t buflen);
std::string getJsonString(json_t *value);
bool has_suffix(const std::string &str, const std::string &suffix);

#endif /* UTILS_H_ */
