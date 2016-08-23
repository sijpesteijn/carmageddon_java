/*
 * HttpServer.cpp
 *
 *  Created on: Aug 21, 2016
 *      Author: carmageddon
 */


#include "HttpHandler.h"

#include "Base64.h"
#include "../Utils.h"
#include <fstream>


HttpHandler* HttpHandler::getInstance(string html_root) {
	static HttpHandler obj(html_root);
	return &obj;
}

HttpHandler::HttpHandler(string html_root) {
	this->html_root = html_root;
}

Resource HttpHandler::createResource(string resourcePath) {
	Resource resource;
	if (resourcePath.compare("/") == 0) {
		resource.filename = html_root + "index.html";
		resource.type = "text/html";
	}
	if (has_suffix(resourcePath, ".js")) {
		resource.filename = html_root + resourcePath;
		resource.type = "application/javascript";
	}
	return resource;
}

string encode(string line) {
	return base64_encode(reinterpret_cast<const unsigned char*>(line.c_str()), line.length());
}

Response HttpHandler::resolveRequest(Request *request) {
	string responseCall;
	Response response;
	if (request->method.compare("GET") == 0) {
		Resource resource = createResource(request->path);
		ifstream infile(resource.filename.c_str());
		if (infile.good()) {
			string line;
			while (std::getline(infile, line))
			{
				response.body.push_back(encode(line));
			}
			response.header.push_back(encode("HTTP/1.1 200\r\n"));
			response.header.push_back(encode("Content-Type: " + resource.type + "\r\n"));
			response.header.push_back(encode("\r\n\r\n"));
		} else {
			response.header.push_back(encode("HTTP/1.1 404\r\n"));
			response.header.push_back(encode("Content-Type: text/html\r\n\r\n"));
			response.body.push_back(encode("File not found.\r\n"));
		}
	} else {
		response.header.push_back(encode("HTTP/1.1 501\r\n"));
		response.header.push_back(encode("Content-Type: text/html\r\n\r\n"));
		response.body.push_back(encode("Not implemented.\r\n"));
	}
	return response;
}

