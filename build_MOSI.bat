@echo off
echo --------------------------- Building MOSI ------------------------------
set /p Input=Enter Enter Version Number:
cd ..
echo Backing up src
XCOPY forge\mcp\src forge\mcp\src-bak /E /I /Q /y
echo.
echo Copying source 
XCOPY "MOSI\src" "forge\mcp\src\minecraft" /E /Q /y
echo.
echo Recompile
pushd forge\mcp
echo | call recompile.bat
echo Done.
echo.
echo Reobfuscate
echo | call reobfuscate_srg.bat
echo Done.
popd
echo.

echo Moving Art Assets to Setup Folder
XCOPY "MOSI\buffbarresources" forge\mcp\reobf\minecraft\SETUP\BuffBarMod\buffbarresources /E /I /Q /y
XCOPY "MOSI\armorbarresources" forge\mcp\reobf\minecraft\SETUP\ArmorBarMod\armorbarresources /E /I /Q /y

echo Copy Buff Bar into Buff Bar Module in Setup 
XCOPY forge\mcp\reobf\minecraft\buffbarmod forge\mcp\reobf\minecraft\SETUP\BuffBarMod\buffbarmod /E /I /Q /y
echo Copy Armor Bar into Armor Bar Module in Setup
XCOPY forge\mcp\reobf\minecraft\armorbarmod forge\mcp\reobf\minecraft\SETUP\ArmorBarMod\armorbarmod /E /I /Q /y

echo Move Active into Setup
pushd forge\mcp\reobf\minecraft\SETUP
echo Using 7Zip to Zip Buff Bar Mod
"C:\Program Files\7-zip\7z.exe" a BuffBar%Input%.zip .\BuffBarMod\* -r | findstr /b /r /c:"\<Everything is Ok" /c:"\<Scanning" /c:"\<Creating archive"
echo Using 7Zip to Zip Armor Bar Mod
"C:\Program Files\7-zip\7z.exe" a ArmorBar%Input%.zip .\ArmorBarMod\* -r | findstr /b /r /c:"\<Everything is Ok" /c:"\<Scanning" /c:"\<Creating archive"
popd

echo Restoring src-bak
RMDIR /S /Q forge\mcp\src
REN forge\mcp\src-bak src
PAUSE
