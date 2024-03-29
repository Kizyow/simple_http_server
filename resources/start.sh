#!/bin/bash
cd /usr/local/bin/myweb
echo "e" | sudo -S java HttpServer /etc/myweb/myweb.conf
echo OK





[Unit]
Description=Serveur Web
[Service]
User=kizyow
# The configuration file application.properties should be here:

#change this to your workspace
WorkingDirectory=/usr/local/bin/myweb

#path to executable.
#executable is a bash script which calls jar file
ExecStart=/usr/local/bin/myweb-script

SuccessExitStatus=143
TimeoutStopSec=10
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target









#!/bin/sh -e
#
### BEGIN INIT INFO
# Provides:          myweb
# Required-Start:    $all
# Required-Stop:
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: serveur web
### END INIT INFO

DAEMON="/usr/local/bin/myweb-script"
DAEMONUSER="kizyow"
daemon_NAME="myweb-script"

PATH="/sbin:/bin:/usr/sbin:/usr/bin" #Ne pas toucher

test -x $DAEMON || exit 0

. /lib/lsb/init-functions

d_start () {
    log_daemon_msg "Starting system $daemon_NAME Daemon"
    start-stop-daemon --background --name $daemon_NAME --start --quiet --chuid $DAEMONUSER --exec $DAEMON -- $daemon_OPT
  log_end_msg $?
}

d_stop () {
    log_daemon_msg "Stopping system $daemon_NAME Daemon"
    start-stop-daemon --name $daemon_NAME --stop --retry 5 --quiet --name $daemon_NAME
  log_end_msg $?
}

    case "$1" in

            start|stop)
                    d_${1}
                    ;;

            restart|reload|force-reload)
                            d_stop
                            d_start
                    ;;

            force-stop)
                   d_stop
                    killall -q $daemon_NAME || true
                    sleep 2
                    killall -q -9 $daemon_NAME || true
                    ;;

            status)
                    status_of_proc "$daemon_NAME" "$DAEMON" "system-wide $daemon_NAME" && exit 0 || exit $?
                    ;;
            *)
                    echo "Usage: /etc/init.d/$daemon_NAME {start|stop|force-stop|restart|reload|force-reload|status}"
                    exit 1
                    ;;
    esac

exit 0