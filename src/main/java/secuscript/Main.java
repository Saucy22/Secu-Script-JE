package secuscript;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.python.util.PythonInterpreter;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		
		//Boolean Declarations
		boolean debugMode = true;
		boolean offlineMode = false;
		boolean unsafeMode = false;

		//Integer Declarations
		int dDetections = 0;
		int sDetections = 0;
		int urlDetections = 0;
		int sudo = 0;
		int packageChanges = 0;
		
		//List Declarations
		List<String> commandsDetected = new ArrayList<>(List.of());;
		List<String> urlsDetected = new ArrayList<>(List.of());;
		
		//Array Declarations
		final String[] destructiveCommands = {"rm -rf", ":(){ :|:& };:","sudo chmod -R 777 /", "sudo rm -rf"};
		final String[] suspiciousCommands = {"|sh", "|bash", "curl", "wget", "os.system(","sudo", 
				"doas", "py_compile","browser_history","browser_cookie3","requests.put(", "pacman -", "apt", "dnf",
				"yay -", "snap", "flatpak"};
		
		//String Declarations
		String fileName;
		String version = "1.1-JE";
		
		//Debug Mode Handler
		if (debugMode == false) {
			System.out.println("DEBUG MODE ACTIVE, USER INPUT IGNORED.");
			unsafeMode = false;
			offlineMode = true;
			fileName = "/home/caleb/Downloads/shell.sh";
		//Normal Mode.
		} else {
			for (int i = 0; i < args.length; i++) {
				switch (args[i]) {
					case "-U":
						if (! (i <= 0)) {
							unsafeMode = true;
						}
					case "-O":
						if (! (i <= 0)) {
							offlineMode = true;
						}
						
				}
			}
			try {
				fileName = args[0];
			}
			catch (Exception ArrayIndexOutOfBoundsException) {
				fileName = "nothing";
				System.out.println("No filename specified, run 'secuscript -h' for help using this program.");
				System.exit(0);
			}
			if (fileName.contains(".sh") || fileName.contains(".py")) {
				
			} else if (fileName.equals("-h")) {
				System.out.println("secuscript version " + version);
				System.out.println("usage: secuscript [FILENAME] [-U (optional)] [-O (optional)]");
				System.out.println("FILENAME: relative or absolute path of the script you want to scan, must be a .sh (shell) script or a .py (python) file.");
				System.out.println("-U: unsafe mode, allows you to execute destructive scripts, NOT RECCOMENDED");
				System.out.println("-O: offline mode, scans the script without checking for malicious URLs, NOT RECCOMENDED");
				System.exit(0);
			} else if ( (! fileName.contains(".sh") && ! fileName.contains(".py")) ) {
				System.out.println("File is not compatible with SecuScript, run 'secuscript -h' for more information.");
				System.exit(0);
			}

		}
		Path filePath = Path.of(fileName);
		try {
			String[] fileLines = Files.readString(filePath).split("\n");
			String[] badURLS = getURLDefinitions(offlineMode);
			
			//Scan loop start.
			for (int i = 0; i < fileLines.length; i++) {
				for (int x = 0; x < destructiveCommands.length; x++) {
					if (fileLines[i].contains(destructiveCommands[x])) {
						commandsDetected.add(destructiveCommands[x]);
						dDetections++;
					}
				}
				for (int x = 0; x < suspiciousCommands.length; x++) {
					if (fileLines[i].contains(suspiciousCommands[x])) {
						sDetections++;
						if (suspiciousCommands[x].equals("sudo") || suspiciousCommands[x].equals("doas")) {
							sDetections--;
							sudo++;
						} else if (suspiciousCommands[x].equals("apt") || suspiciousCommands[x].equals("dnf") || suspiciousCommands[x].equals("pacman -") 
								|| suspiciousCommands[x].equals("yay -") || suspiciousCommands[x].equals("snap") || suspiciousCommands[x].equals("flatpak") ) {
							sDetections--;
							packageChanges++;
						} else {
							commandsDetected.add(suspiciousCommands[x]);
						}
					}
				}
				if (badURLS == null) {
					
				} else {
					for (int x = 0; x < badURLS.length; x++) {
						if (fileLines[i].contains(badURLS[x])) {
							urlDetections++;
							if (badURLS[x].charAt(0) == '#') {
								urlDetections--;
							} else {
								urlsDetected.add(badURLS[x]);
							}
						}
					}
					
				}

			}
			//Scan loop ends, judgment begins.
			System.out.println(dDetections + " Destructive commands found.");
			System.out.println(sDetections + " Suspicious commands found.");
			System.out.println(packageChanges + " Potential package manager calls found.");
			if (offlineMode) {
				System.out.println("Url checking disabled");
			} else {
				System.out.println(urlDetections + " Malicious URLS found.");
			}
			System.out.println("And " + sudo + " calls to Sudo/Doas.");
			if (sDetections > 0 || dDetections > 0) {
				System.out.println("Commands flagged: " + commandsDetected.toString());
			}
			if (urlsDetected.size() != 0) {
				System.out.println("URLS Flagged: " + urlsDetected.toString());
			}
			
			//Execution time
			
			Scanner userInput = new Scanner(System.in);
			
			if (dDetections > 0 || urlDetections > 0) {
				System.out.println("For your safety do not run this script.");
				if (unsafeMode) {
					System.out.println("UNSAFE MODE IS ENABLED, WOULD YOU LIKE TO (probably) DESTROY YOUR SYSTEM? [N/y]");
					String answer = userInput.nextLine().toLowerCase();
					if (answer.equals("y")) {
						if (fileName.contains(".sh")) {
							try (PythonInterpreter py = new PythonInterpreter()) {
								py.set("FileName", fileName);
								py.exec("import os");
								py.exec("os.system('cat ' + FileName + '|sh')");
							}
							System.exit(0);
						} else if (fileName.contains(".py")) {
							try (PythonInterpreter py = new PythonInterpreter()) {
								py.set("FileName", fileName);
								py.exec("import os");
								py.exec("os.system('python3 ' + FileName)");
							}
							System.exit(0);
						}
					} else {
						System.out.println("Script not ran.");
						System.exit(0);
					}
				}
				System.exit(0);
			}
			if (sDetections > 0 || sudo > 0) {
				if (sudo > 0) {
					System.out.println("THIS SCRIPT REQUIRES ROOT ACCESS.");
				}
				if (packageChanges > 0) {
					System.out.println("THIS SCRIPT MAY INSTALL AND/OR REMOVE PACKAGES.");
				}
				System.out.println("This script may make (un)wanted changes to your system. Would you like to proceed? [N/y]");
				String answer = userInput.nextLine().toLowerCase();
				if (answer.equals("y")) {
					if (fileName.contains(".sh")) {
						try (PythonInterpreter py = new PythonInterpreter()) {
							py.set("FileName", fileName);
							py.exec("import os");
							py.exec("os.system('cat ' + FileName + '|sh')");
						}
						System.exit(0);
					} else if (fileName.contains(".py")) {
						try (PythonInterpreter py = new PythonInterpreter()) {
							py.set("FileName", fileName);
							py.exec("import os");
							py.exec("os.system('python3 ' + FileName)");
						}
						System.exit(0);
					}
				} else {
					System.out.println("Script not ran.");
					System.exit(0);
				}
			} else {
				System.out.println("Would you like to run this script now? [N/y]");
				String answer = userInput.nextLine().toLowerCase();
				if (answer.equals("y")) {
					if (fileName.contains(".sh")) {
						try (PythonInterpreter py = new PythonInterpreter()) {
							py.set("FileName", fileName);
							py.exec("import os");
							py.exec("os.system('cat ' + FileName + '|sh')");
						}
						System.exit(0);
					} else if (fileName.contains(".py")) {
						try (PythonInterpreter py = new PythonInterpreter()) {
							py.set("FileName", fileName);
							py.exec("import os");
							py.exec("os.system('python3 ' + FileName)");
						}
						System.exit(0);
					}
				} else {
					System.out.println("Script not ran.");
					System.exit(0);
				}
				
			}
		} catch(FileNotFoundException e) {
			System.out.println("The filepath you entered was not valid (FileNotFoundException)");
			System.exit(0);
		} catch(IOException e) {
			System.out.println("Generic I/O Exception (IOException)");
			System.exit(0);
		}
		
	}
	
	public static String[] getURLDefinitions(boolean offline) throws IOException, InterruptedException {
		if (offline == true) {
			return null;
		} else {
	        HttpClient client = HttpClient.newHttpClient();
	 
	        HttpRequest request = HttpRequest.newBuilder()
	               .uri(URI.create("https://cdn.jsdelivr.net/gh/hagezi/dns-blocklists@latest/domains/tif.txt"))
	               .GET()
	               .build();

	        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
	 
	        String[] urlDef = response.body().split("\n");
	        return urlDef;
		}

	}

}