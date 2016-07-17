# Carmageddon

# Koop een beaglebone black
- https://tweakers.net/pricewatch/466806/beaglebone-black-(4gb-emmc).html

# Installatie van Debian 7.9 op de beaglebone.
- https://debian.beagleboard.org/images/bone-debian-7.9-lxde-4gb-armhf-2015-11-12-4gb.img.xz
- uitpakken en op sdcard branden dmv: sudo dd bs=1m if=bone-debian-7.9-lxde-4gb-armhf-2015-11-12-4gb.img of=/dev/diskX (waar x jouw sd kaart is)
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

# Carmageddon startup script
Bij booten moeten we een aantal dingen klaar zetten. Oa. device tree overlays (die zeggen hoe io poorten geconfigureerd worden) en de sd kaart.
- Copieer carmageddon.sh file naar /usr/bin/
- Copieer carmageddon.service file naar /lib/systemd/
- Maak een symbolic link aan in: /etc/systemd/system/
ln /lib/systemd/carmageddon.service carmageddon.service
- systemctl daemon-reload
- systemctl start carmageddon.service
- systemctl enable carmageddon.service

# Root gebruiker configureren
We willen vanuit het programma gewoon over root rechten beschikken. Lekker makkelijk.
- sudo su
- Open /etc/ssh/sshd_config en wijzig 'PermitRootLogin yes'
- Geef root het wachtwoord: root
- Nu kan je met root/root inloggen. (Zo meteen ook vanuit eclipse)

# Installatie van DeviceTree Overlay op de beaglebone. (Nodig om io poorten te configureren)
- git clone https://github.com/beagleboard/bb.org-overlays
- cd bb.org-overlays/
- ./dtc-overlay.sh
- dtc --version (geeft zoiets als: Version: DTC 1.4.1-ge733c7b8)
- sudo ./install.sh
- reboot de beaglebone
Nu heb je een bonecape manager in /sys/devices/platform/bone_capemgr/slots

# Geef root een wachtwoord en sta ssh toegang toe:
- login op beaglebone (default is user: ubuntu pwd: temppwd)
- geef root een wachtwoord: passwd root (paswoord: root)
- sudo vi /etc/ssh/sshd_config en wijzig: PermitRootLogin yes
- restart ssh (sudo service ssh reload)



# Webcam timeout
lsmod
rmmod uvcvideo
modprobe uvcvideo nodrop=1 timeout=5000

# sites 
http://docs.opencv.org/2.4/doc/tutorials/introduction/crosscompilation/arm_crosscompile_with_cmake.html?highlight=arm
https://github.com/DmitrySandalov/Install-OpenCV
http://docs.opencv.org/2.4/doc/tutorials/introduction/linux_install/linux_install.html
