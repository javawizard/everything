@echo off
rm -rf ../bin/*
cp * ../bin
rm -rf ../bin/build.bat
dos2unix ../bin/*
chmod +x ../bin/*
echo Done building.