/*
 * Base64.h
 *
 *  Created on: Aug 19, 2016
 *      Author: carmageddon
 */

#ifndef OBJECTS_BASE64_H_
#define OBJECTS_BASE64_H_

#include <string>

using namespace std;

string base64_encode(unsigned char const* , unsigned int len);
string base64_decode(string const& s);

#endif /* OBJECTS_BASE64_H_ */
