/*!
\file    serialib.h
\brief   Serial library to communicate throught serial port, or any device emulating a serial port.
\author  Philippe Lucidarme (University of Angers) <serialib@googlegroups.com>
\version 1.2
\date    28 avril 2011
This Serial library is used to communicate through serial port.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE X CONSORTIUM BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

This is a licence-free software, it can be used by anyone who try to build a better world.
*/


#ifndef SERIALIB_H
#define SERIALIB_H

#include <sys/time.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/shm.h>
#include <termios.h>
#include <string.h>
#include <iostream>
#include <fcntl.h>
#include <unistd.h>
#include <sys/ioctl.h>

class Serialib
{
public:
    Serialib();
    ~Serialib();
    char Open(const char *Device,const unsigned int Bauds);
    void Close();
    char WriteChar(char);
    char ReadChar(char *pByte,const unsigned int TimeOut_ms=NULL);
    char WriteString(const char *String);
    int ReadString(char *String, char FinalChar, unsigned int MaxNbBytes, const unsigned int TimeOut_ms=NULL);
    char Write(const void *Buffer, const unsigned int NbBytes);
    int Read(void *Buffer,unsigned int MaxNbBytes,const unsigned int TimeOut_ms=NULL);
    void    FlushReceiver();
    int     Peek();

private:
    int ReadStringNoTimeOut(char *String,char FinalChar,unsigned int MaxNbBytes);
    int             fd;
};

class TimeOut
{
public:
    TimeOut();
    void InitTimer();
    unsigned long int ElapsedTime_ms();

private:    
    struct timeval PreviousTime;
};

#endif // SERIALIB_H

