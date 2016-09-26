# Carmageddon

# Software
- mvn sass:watch 
- mvn jetty:run
- http://localhost:8082/carmageddon


# Koop een beaglebone black
- https://tweakers.net/pricewatch/466806/beaglebone-black-(4gb-emmc).html

# Hardware
- Beaglebone black: http://beagleboard.org
- Servo: http://www.servodatabase.com/servo/modelcraft/4519
- Regelaar: https://www.conrad.nl/nl/modelcraft-carbon-series-snelheidsregelaar-belastbaarheid-60-a-50-a-35-a-motorlimiet-20-turns-207368.html
- Wifi: http://nl.tp-link.com/products/details/cat-11_TL-WN823N.html
- Webcam: http://www.logitech.com/nl-nl/product/hd-webcam-c310?crid=34

#Software
- Frontend met angular 1
- Backend jetty server met guice&jersey applicatie.

# Installatie van Debian 7.9 op de beaglebone.
- https://debian.beagleboard.org/images/bone-debian-7.9-lxde-4gb-armhf-2015-11-12-4gb.img.xz
- uitpakken en op sdcard branden dmv: sudo dd bs=1m if=bone-debian-7.9-lxde-4gb-armhf-2015-11-12-4gb.img of=/dev/diskX (waar x jouw sd kaart is)
- Nadat dit is gelukt plaats je de sdcard in de beaglebone. Hou het bootknopje ingedrukt terwijl je de spanning weer aansluit. Eerst zullen alle leds oplichten en daarna beetje random knipperen. Vervolgens zal je een looplichtje gaan zien. Wanneer alle leds branden (of alles is uit, dan is hij al even klaar) dan is het image op de eMMC geflashed.
- <A> Nadat dit is gelukt plaats je de sdcard in de beaglebone. Hou het bootknopje ingedrukt terwijl je de spanning weer aansluit. Eerst zullen alle leds oplichten en daarna beetje random knipperen. Vervolgens zal je een looplichtje gaan zien. Wanneer alle leds branden (of alles is uit, dan is hij al even klaar) dan is het image op de eMMC geflashed.
- Op de bb open file /boot/uEnv.txt haal '#' weg voor regel:
  ##enable BBB: eMMC Flasher:
  cmdline=init=/opt/scripts/tools/eMMC/init-eMMC-flasher-v3.sh

  Kijk anders hier: http://elinux.org/Beagleboard:BeagleBoneBlack_Debian#Flashing_eMMC
- Herhaal stap A


# Device tree overlay update
    $ sudo apt-get install git
    $ git clone https://github.com/beagleboard/bb.org-overlays
    $ cd bb.org-overlays/
    $ ./dtc-overlay.sh
    $ ./install.sh

# SD card als extra storage.
De beaglebone black heeft maar 4 Gb schijfruimte. Om de sd card als extra storage te gebruiken ga dan als volgt te werk:
- boot de bb
- plaats geforamateerde sdcard
- mount de sd card
- maak een uEnv.txt bestand aan in de root dir.
- vult uEnv.txt met:
mmcdev=1
bootpart=1:2
mmcroot=/dev/mmcblk1p1 ro
optargs=quiet
Dit zorgt ervoor dat je de sd card kan laten zitten bij booten en geboot wordt vanaf interne schijf.

# Root gebruiker configureren
We willen vanuit het programma gewoon over root rechten beschikken. Lekker makkelijk.
- sudo su
- Open /etc/ssh/sshd_config en wijzig 'PermitRootLogin yes'
- Geef root het wachtwoord: root
- Nu kan je met root/root inloggen. (Zo meteen ook vanuit eclipse)

# Wifi dongle configureren
Beetje vergeten, maar deze sites hebben mij geholpen:
- https://sites.google.com/site/easylinuxtipsproject/reserve-7#TOC-Realtek-RTL8188CUS-and-RTL8192CU-chipsets-0bda:8176-and-0bda:8178-
- http://brilliantlyeasy.com/ubuntu-linux-tl-wn725n-tp-link-version-2-wifi-driver-install/
- en de TL-WN823N.tar.gz driver in beaglebone folder

# Java
- apt-get install oracle-java8-installer

# Carmageddon startup script
Bij booten moeten we een aantal dingen klaar zetten. Oa. device tree overlays (die zeggen hoe io poorten geconfigureerd worden) en de sd kaart.
- Copieer carmageddon.sh file naar /usr/bin/
- Maak het script executable: chmod +x /usr/bin/carmageddon.sh
- open: crontabe -e
- voeg toe: @reboot /usr/bin/carmageddon.sh
