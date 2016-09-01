/*
 * HttpServer.h
 *
 *  Created on: Aug 21, 2016
 *      Author: carmageddon
 */

#ifndef OBJECTS_HTTPHANDLER_H_
#define OBJECTS_HTTPHANDLER_H_

#include <string>
#include <vector>
#include "EventHandler.h"

using namespace std;

class Resource {
public:
	string filename;
	string type;
};

class Request {
public:
	string method;
	string path;
	string version;
};

class Response {
public:
	vector<string> header;
	vector<string> body;
};

class HttpHandler {
public:
	static HttpHandler* getInstance(string html_root);
	HttpHandler(string html_root);
	Response resolveRequest(Request *request);
private:
	string html_root;
	Resource createResource(string resourcePath);
};


#endif /* OBJECTS_HTTPHANDLER_H_ */
