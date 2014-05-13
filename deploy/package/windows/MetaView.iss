;This file will be executed next to the application bundle image
;I.e. current directory will contain folder MetaView with application files
[Setup]
AppId={{app}}
AppName=MetaView
AppVersion=1.0
AppVerName=MetaView 1.0
AppPublisher=tjumyk
AppComments=MetaView
AppCopyright=
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
DefaultDirName={localappdata}\MetaView
DisableStartupPrompt=Yes
DisableDirPage=Yes
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
DisableFinishedPage=Yes
DisableWelcomePage=Yes
DefaultGroupName=tjumyk
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,5.1 
OutputBaseFilename=MetaView-1.0
Compression=lzma
SolidCompression=yes
PrivilegesRequired=lowest
SetupIconFile=MetaView\MetaView.ico
UninstallDisplayIcon={app}\MetaView.ico
UninstallDisplayName=MetaView
WizardImageStretch=No
WizardSmallImageFile=MetaView-setup-icon.bmp   

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "MetaView\MetaView.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "MetaView\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\MetaView"; Filename: "{app}\MetaView.exe"; IconFilename: "{app}\MetaView.ico"; Check: returnTrue()
Name: "{commondesktop}\MetaView"; Filename: "{app}\MetaView.exe";  IconFilename: "{app}\MetaView.ico"; Check: returnFalse()

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
