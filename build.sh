#!/bin/bash
# Script de build y despliegue — Biblioteca Digital UNTEC
# Uso: ./build.sh [clean|package|deploy <TOMCAT_HOME>]

set -e

echo "==================================================="
echo "  Biblioteca Digital UNTEC — Build Script"
echo "==================================================="

ACTION=${1:-package}
TOMCAT_HOME=${2:-""}

case "$ACTION" in
  clean)
    echo "[1/1] Limpiando proyecto..."
    mvn clean
    echo "Limpieza completada."
    ;;

  package)
    echo "[1/2] Compilando y empaquetando..."
    mvn clean package -q
    echo ""
    echo "WAR generado: target/biblioteca-digital.war"
    echo ""
    echo "Para desplegar en Tomcat:"
    echo "  ./build.sh deploy /ruta/a/tomcat"
    ;;

  deploy)
    if [ -z "$TOMCAT_HOME" ]; then
      echo "ERROR: Especificá la ruta a Tomcat."
      echo "  Uso: ./build.sh deploy /opt/tomcat"
      exit 1
    fi
    echo "[1/3] Compilando..."
    mvn clean package -q
    echo "[2/3] Copiando WAR a $TOMCAT_HOME/webapps/ ..."
    cp target/biblioteca-digital.war "$TOMCAT_HOME/webapps/"
    echo "[3/3] Iniciando Tomcat..."
    "$TOMCAT_HOME/bin/startup.sh"
    echo ""
    echo "Aplicación disponible en:"
    echo "  http://localhost:8080/biblioteca-digital/"
    ;;

  *)
    echo "Uso: ./build.sh [clean|package|deploy <TOMCAT_HOME>]"
    exit 1
    ;;
esac
