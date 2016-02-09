#!/bin/sh

pkg update
pkg upgrade
pkg install -y gcc openjdk8 ca_root_nss
mount -t fdescfs fdesc /dev/fd
mount -t procfs proc /proc
echo 'fdesc /dev/fd fdescfs rw 0 0' >> /etc/fstab
echo 'proc /proc procfs rw 0 0' >> /etc/fstab
