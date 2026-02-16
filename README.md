# Secu-Script-JE
Version 1.1.5-JE

# Purpose of Secu-Script
Many advanced and novice users alike download scripts off of the internet to use on linux; the issue with this is that not many people look over the script before using it.
That is what Secu-Script is for.
    
# Why JE?
Secuscript JE is the next step in the evolution of secuscript. It's built with Java 25, allowing for much faster file scan time on larger files.

# Prerequisites
In order to use this program you must have a java runtime environment installed. This can be any version 21 and onwards (openjdk 25 recommended)

For Ubuntu based distributions you can use:

	sudo apt install openjdk-25-jre

	
For rpm (Fedora) based distributions you can use:

	sudo dnf install java-25-openjdk

	
And for Arch based distributions you can use:

	sudo pacman -S jre-openjdk

# Installation (WARNING: THIS REQUIRES SUDO ACCESS!)

Extract the zip file you downloaded (hopefully from github).

Change directory into the "SecuScriptJE1.1.5" folder (as of Java Edition Version 1.1.5)

	cd /path/to/SecuScriptJE1.1.5
	
Execute installer.sh

	chmod +x installer.sh
	
	./installer.sh
	
Done!

# Usage

After installing Secu-Script you can run it from any directory by using the command 'secuscript'

Here is the most basic use of Secu-Script

    secuscript /path/to/file
	
As of 1.1.5 you can also use:

	ssc /path/to/file

replace the path with the RELATIVE path to the script you want to run.

There are currently 3 flags you can use as arguments

-U: Unsafe mode. Allows you to run scripts that contain destructive commands or malicious URLS it is NOT RECOMMENDED to use this mode FOR ANY REASON WHATSOEVER

-O: Offline mode. Disables URL checking. It is NOT RECOMMENDED to use this option unless for some reason you cannot connect to the internet.

-Dr: Dont Run mode. Doesn't prompt you to run the script after scanning, reccomended for riskier scripts.

-h: shows you the help screen.

There are currently 2 compatible filetypes you can use with this program.

.sh Shell Scripts

.py Python Files (see limitations)



# Changelog (1.1.5-JE compared to 1.1-JE)

- Added the -Dr flag.
- Added the ssc command (shortened version of the secuscript command)

# Limitations (as of verison 1.1.5-JE)

- This program can only read scripts encoded in utf-8.
- Python script scanning is more of an afterthought than a feature, more work must be done. (15% progress on this)
    
    
