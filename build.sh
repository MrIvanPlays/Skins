#!/usr/bin/env bash

function build {
  mvn clean package -DbuildNumber=$1
  cp velocity/target/Skins-Velocity-$1.jar Skins-Velocity-$1.jar
  cp bukkit-general/bukkit-bootstrap/target/Skins-Bukkit-$1.jar Skins-Bukkit-$1.jar
}

if [ -z $1 ]; then
  # WARNING: DO NOT CHANGE
  # I'm too lazy to make the code recognize other than "unknown" for custom dev build
  # If you're changing this, change it to a number or don't touch anything
    build "unknown"
else
    build $1
fi