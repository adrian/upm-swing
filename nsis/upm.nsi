/*
 * $Id$
 * 
 * Universal Password Manager
 * Copyright (C) 2005 Adrian Smith
 *
 * This file is part of Universal Password Manager.
 *   
 * Universal Password Manager is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Universal Password Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Universal Password Manager; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

;--------------------------------
;General

  !define UPM_VERSION "1.5b1"

  ;Name and file
  Name "Universal Password Manager"

  ;The file to write
  OutFile "upm-${UPM_VERSION}.exe"

  XPStyle on

  ;Default installation folder
  InstallDir "$PROGRAMFILES\UPM"

  ;Registry key to check for directory (so if you install again, it will 
  ;overwrite the old one automatically)
  InstallDirRegKey HKLM "Software\UPM" "Install_Dir"

  LicenseData "..\dist\build\COPYING.txt"

;--------------------------------
;Installer Pages

  Page license
  Page components
  Page directory
  Page instfiles

  UninstPage uninstConfirm
  UninstPage instfiles

;--------------------------------
;Installer Sections

Section "Universal Password Manager"

  ;Files to install

  SetOutPath "$INSTDIR\server\http"
  File ..\dist\build\server\http\upload.php
  File ..\dist\build\server\http\deletefile.php

  SetOutPath "$INSTDIR"
  File ..\bin\upm.bat
  File ..\images\upm.ico
  File ..\dist\build\upm.jar
  File ..\dist\build\COPYING.txt
  File ..\dist\build\README.txt
  File ..\lib\commons-codec-1.3.jar
  File ..\lib\commons-httpclient-3.0.jar
  File ..\lib\commons-logging-1.1.jar
  File ..\lib\bcprov-jdk14-133.jar

  ;Write the installation path into the registry
  WriteRegStr HKLM SOFTWARE\UPM "Install_Dir" "$INSTDIR"

  ;Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\UPM" "DisplayName" "Universal Password Manager"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\UPM" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\UPM" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\UPM" "NoRepair" 1
  WriteUninstaller "uninstall.exe"
 
SectionEnd

;--------------------------------
;Optional section (can be disabled by the user)
Section "Start Menu Shortcuts"

  CreateDirectory "$SMPROGRAMS\UPM"
  CreateShortCut "$SMPROGRAMS\UPM\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
  CreateShortCut "$SMPROGRAMS\UPM\UPM.lnk" "$INSTDIR\upm.bat" "" "$INSTDIR\upm.ico" 0
  CreateShortCut "$SMPROGRAMS\UPM\README.lnk" "$INSTDIR\README.txt" "" "$INSTDIR\README.txt" 0
  
SectionEnd

;--------------------------------
; Uninstaller
Section "Uninstall"
  
  ;Remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\UPM"
  DeleteRegKey HKLM SOFTWARE\UPM

  ;Remove files
  ;Purposly don't delete the root install folder in case the user placed other files there
  Delete $INSTDIR\upm.bat
  Delete $INSTDIR\upm.ico
  Delete $INSTDIR\upm.jar
  Delete $INSTDIR\COPYING.txt
  Delete $INSTDIR\README.txt
  Delete $INSTDIR\uninstall.exe
  Delete $INSTDIR\commons-codec-1.3.jar
  Delete $INSTDIR\commons-httpclient-3.0.jar
  Delete $INSTDIR\commons-logging-1.1.jar
  Delete $INSTDIR\bcprov-jdk14-133.jar
  Delete $INSTDIR\server\http\upload.php
  Delete $INSTDIR\server\http\deletefile.php
  RMDir /r $INSTDIR\server

  ;Remove shortcuts, if any
  Delete "$SMPROGRAMS\UPM\*.*"
  RMDir "$SMPROGRAMS\UPM"

SectionEnd

