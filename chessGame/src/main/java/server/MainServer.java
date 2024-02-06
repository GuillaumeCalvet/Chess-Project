package server;

import java.io.IOException;


/* 
COMPILE + LAUNCH
>> cd C:\Users\raphg\eclipse-workspace\chessGame\src\main\java
>> $env:PATH_TO_FX = "C:\Program Files\javafx-sdk-21.0.2\lib"
>> javac --module-path $env:PATH_TO_FX --add-modules javafx.controls,javafx.graphics,javafx.base $(Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName })
>> java server/MainServer 2024
*/

// Start a server. Reads the server's port from the command line argument
public class MainServer {

	public static void main(String[] args) {
		
		try {
			if(args.length != 1) {
				printUsage();
			} else {
				int port = Integer.parseInt(args[0]);
				Server server = new Server(port);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void printUsage() {
		System.out.println("java server.Server <port>");
		System.out.println("\t<port>: Server's port");
	}

}
