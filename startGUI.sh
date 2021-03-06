#!/bin/bash

# Usage: ./startGUI.sh nrOfPlayers debug

trap 'killall' INT

killall() {
    trap '' INT TERM     # ignore INT and TERM while shutting down
    echo "**** Shutting down... ****"     # added double quotes
    kill -TERM 0         # fixed order, send TERM not INT
    wait
    echo DONE
}

ant build && cd bin

if [ -z ${1+x} ]; then nr=0; else nr=$[$1 - 1]; fi
for i in `seq 0 $nr`;
do 
cmd="java -Djava.rmi.server.disableHttp=true -Dsun.rmi.transport.proxy.connectTimeout=1000 -Dsun.rmi.transport.tcp.handshakeTimeout=1000 -Dsun.rmi.transport.tcp.responseTimeout=1000 -Djava.library.path=../lib/native:../lib/native -Dfile.encoding=UTF-8 -classpath .:../lib/jinput.jar:../lib/lwjgl_util.jar:../lib/lwjgl.jar:../lib/slick.jar org.unoxuno.game.MainClass $i $2 &"
eval xterm -hold -T $i -e $cmd;
echo Launched $i
sleep 2;
done

echo Waiting for Ctrl+c for killing all processes...
cat # wait forever
