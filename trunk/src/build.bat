@echo off
rm -rf ../bin/pt*
cp pt* ../bin
rm -rf ../bin/build.bat
dos2unix ../bin/pt*
chmod +x ../bin/pt*
echo Done building.