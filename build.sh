#!/usr/bin/env bash

function build {
  mvn clean package -DbuildNumber=$1
  if [ "$?" != "0" ]; then
    echo "Error on maven build"
    echo "No further action can be done; aborting"
    exit 1
  fi
  cp velocity/target/Skins-Velocity-$1.jar Skins-Velocity-$1.jar
  cp bukkit-general/bukkit-bootstrap/target/Skins-Bukkit-$1.jar Skins-Bukkit-$1.jar
  cp bukkit-general/bukkit-menu-communicator/target/Skins-Bukkit-Communicator-$1.jar Skins-Bukkit-Communicator-$1.jar
}

if [ -z $1 ]; then
  # WARNING: DO NOT CHANGE
  # I'm too lazy to make the code recognize other than "unknown" for custom dev build
  # If you're changing this, change it to a number or don't touch anything
  build "unknown"
else
  build $1
fi
