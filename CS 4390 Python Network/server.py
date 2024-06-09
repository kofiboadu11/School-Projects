from socket import *

def test():
    
    serverPort=80
    serverSocket=socket(AF_INET,SOCK_STREAM)
    serverSocket.bind(('',serverPort))
    serverSocket.listen(1)
    print("web server on port",serverPort)
    while True:
        print("ready")
        
        connectionSocket,addr=serverSocket.accept()
        try:
            message=connectionSocket.recv(1024)
            print(message)
            filename=message.split()[1]
            print(filename[1])
            print(filename,'||',filename[1])
            f=open(filename[1:])
            outputdata=f.read()
            print(outputdata)
            connectionSocket.send("""HTTP/1.0 200 OK
                        Content-Type: text/html

                    <html>
                    <head>
                    <title>Success</title>
                    </head>
                    <body>
                        Your file Exist!
                    </body>
                    </html>
                    """.encode());
        except IOError:
            print("404 NOT FOUND")
            connectionSocket.send("""HTTP/1.0 404 NOT FOUND\r\n""".encode());
            pass
        break
    pass
if __name__ =="__main__":
    test()

                                  
   
       


    
    