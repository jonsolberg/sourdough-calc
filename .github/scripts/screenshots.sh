#!/usr/bin/env bash
# Installs the debug APK on the emulator and captures screenshots of the calculator.
set -x
mkdir -p shots
PKG=se.jonsolberg.sourdough
adb shell settings put global hide_error_dialogs 1
sleep 20
adb shell am broadcast -a android.intent.action.CLOSE_SYSTEM_DIALOGS
adb install -r app/build/outputs/apk/debug/app-debug.apk
shot() { adb exec-out screencap -p > "shots/$1.png"; }
launch() { adb shell monkey -p $PKG -c android.intent.category.LAUNCHER 1 >/dev/null; sleep 6; adb shell am broadcast -a android.intent.action.CLOSE_SYSTEM_DIALOGS; sleep 1; }
launch
shot 01-top
adb shell input swipe 540 1800 540 700 600; sleep 1.5
shot 02-middle
adb shell input swipe 540 1800 540 700 600; sleep 1.5
shot 03-lower
adb shell input swipe 540 1800 540 700 600; sleep 1.5
shot 04-bottom
adb shell cmd uimode night yes; sleep 2
adb shell am force-stop $PKG
launch
shot 05-top-dark
adb shell input swipe 540 1800 540 700 600; sleep 1.5
shot 06-middle-dark
adb shell cmd uimode night no
adb logcat -d | grep -E "AndroidRuntime|FATAL" > shots/crashes.txt || true
exit 0
