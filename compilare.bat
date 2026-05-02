@echo off
title Compilare Fortata
mkdir bin
echo Se compileaza...
javac -d bin -cp "lib/opencv-460.jar;src" src/core/Main.java src/ui/FaceApp.java src/ml/*.java src/util/*.java
if %errorlevel% equ 0 (
    echo [SUCCES] Fisierele au fost create in bin!
) else (
    echo [EROARE] Ceva nu a mers la compilare. Verifica mesajele de mai sus.
)
pause