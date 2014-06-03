;This file will be executed next to the application bundle image
;I.e. current directory will contain folder MetaView with application files
[Setup]
AppId={{app}}
AppName=MetaView
AppVersion=1.0.0
AppVerName=MetaView 1.0.0
AppPublisher=tjumyk
AppComments=MetaView
AppCopyright=© tjumyk. All rights reserved.
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
DefaultDirName=D:\MetaView
DisableStartupPrompt=Yes
;DisableDirPage=Yes
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
;DisableFinishedPage=Yes
;DisableWelcomePage=Yes
DefaultGroupName=tjumyk
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,5.1 
OutputBaseFilename=MetaView-1.0.0
Compression=lzma
SolidCompression=yes
PrivilegesRequired=admin
SetupIconFile=MetaView\MetaView.ico
UninstallDisplayIcon={app}\MetaView.ico
UninstallDisplayName=MetaView
WizardImageStretch=no
WizardSmallImageFile=MetaView-setup-icon.bmp
ChangesAssociations=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "chinesesimplified"; MessagesFile: "compiler:Languages\ChineseSimplified.isl"

[Files]
Source: "MetaView\MetaView.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "MetaView\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\MetaView"; Filename: "{app}\MetaView.exe"; IconFilename: "{app}\MetaView.ico"; Check: returnTrue()
Name: "{commondesktop}\MetaView"; Filename: "{app}\MetaView.exe";  IconFilename: "{app}\MetaView.ico"; Check: returnTrue()

[Registry]
Root: HKCR; Subkey: ".mvd"; ValueType: string; ValueName: ""; ValueData: "MetaVideoDescriptionFile"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "MetaVideoDescriptionFile"; ValueType: string; ValueName: ""; ValueData: "MetaVideo Description File"; Flags: uninsdeletekey
Root: HKCR; Subkey: "MetaVideoDescriptionFile\DefaultIcon"; ValueType: string; ValueName: ""; ValueData: "{app}\MetaView.exe,0"
Root: HKCR; Subkey: "MetaVideoDescriptionFile\shell\open\command"; ValueType: string; ValueName: ""; ValueData: """{app}\MetaView.exe"" ""%1"""

[Run]
Filename: "{app}\MetaView.exe"; Description: "{cm:LaunchProgram,MetaView}"; Flags: nowait postinstall skipifsilent

[Code]
function returnTrue(): Boolean;
begin
  Result := True;
end;

function returnFalse(): Boolean;
begin
  Result := False;
end;

function InitializeSetup(): Boolean;
begin
// Possible future improvements:
//   if version less or same => just launch app
//   if upgrade => check if same app is running and wait for it to exit
//   Add pack200/unpack200 support? 
  Result := True;
end;  
