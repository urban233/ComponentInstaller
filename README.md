# Component Installer for deploying PySSA
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)
[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-blue.svg)](https://GitHub.com/urban233/ComponentInstaller/graphs/commit-activity)
[![GitHub issues](https://img.shields.io/github/issues/zielesny/PySSA)](https://GitHub.com/urban233/ComponentInstaller/issues/)
[![GitHub contributors](https://img.shields.io/github/contributors/urban233/ComponentInstaller.svg)](https://GitHub.com/zielesny/PySSA/graphs/contributors/)
[![GitHub release](https://img.shields.io/github/release/urban233/PySSA.svg)](https://github.com/urban233/PySSA/releases/)

## Contents of this document
* [Description](#Description)
* [Contents of this repository](#Contents-of-this-repository)
    * [Sources](#Sources)
    * [Tutorial](#Tutorial)
    * [Images](#Images)
* [Installation](#Installation)
    * [Windows](#Windows)
    * [Source code](#Source-code)
* [Dependencies](#Dependencies)
* [Citation](#Citation)
* [References and useful links](#References-and-useful-links)
* [Acknowledgements](#Acknowledgements)

## Description
The Component Installer is a software tool designed for use as a component-based installer. 
It is capable of addressing a range of deployment contexts that typically necessitate more 
intricate configuration than is typically required in other scenarios. 
The PySSA Installer serves as a case study demonstrating 
the potential utility of the component-based installation methodology.


## Contents of this repository
### Sources
The repository comprises four distinct source code units.

- _src/main/java_
    - The package contains the communicator class, which is utilized for communication with WindowsTasks.exe.
- _src/main/kotlin_
    - The package contains the main source code, including gui and model.
- _WindowsWrapper/WindowsCmdElevator_
    - The package contains the class to elevate a normal cmd shell with admin privileges.
- _WindowsWrapper/WindowsTasks_
    - The package contains the communicator, which facilitates communication with the Kotlin application, as well as a number of useful classes for the management of the MS Windows operating system.

## Build
The Component-Installer can be used on Windows 10 and 11.

### Source code
To build ComponentInstaller, gradle tasks can be used. There are different 
tasks for building the application, building the C# WindowsWrapper projects
and create deployable inno setups.

## Dependencies

**The ComponentInstaller project team would like to thank
the communities behind the open software libraries for their amazing work.**

<!--
**ComponentInstaller was developed at:**
<br>
<br>Zielesny Research Group
<br>Westphalian University of Applied Sciences
<br>August-Schmidt-Ring 10
<br>D-45665 Recklinghausen Germany
--!>
