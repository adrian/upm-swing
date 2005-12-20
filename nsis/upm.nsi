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

  !define UPM_VERSION "1.0"

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

  LicenseData "..\dist\jar\COPYING.txt"

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

  ;Set output path to the installation directory.
  SetOutPath "$INSTDIR"
  
  ;Files to install
  File ..\bin\upm.bat
  File ..\images\upm.ico
  File ..\dist\jar\upm.jar
  File ..\dist\jar\COPYING.txt
  File ..\dist\jar\README.txt

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

  ;Remove files and uninstaller
  Delete $INSTDIR\*.*

  ;Remove shortcuts, if any
  Delete "$SMPROGRAMS\UPM\*.*"

  ;Remove directories used
  RMDir "$SMPROGRAMS\UPM"
  RMDir "$INSTDIR"

SectionEnd

