# -*- mode: ruby -*-
# vi: set ft=ruby :

# All Vagrant configuration is done below. The "2" in Vagrant.configure
# configures the configuration version (we support older styles for
# backwards compatibility). Please don't change it unless you know what
# you're doing.
Vagrant.configure(2) do |config|

  config.vm.provider "virtualbox" do |vb|
    vb.customize ["modifyvm", :id, "--memory", "1024"]
    vb.customize ["modifyvm", :id, "--vram", "128"]
    vb.customize ["modifyvm", :id,  "--cpus", "2"]
    vb.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
    vb.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
    vb.customize ["guestproperty", "set", :id, "/VirtualBox/GuestAdd/VBoxService/--timesync-set-threshold", 10000]
  end

  config.vm.define "linux" do |config|
    config.vm.box = "ubuntu/trusty64"
    config.vm.network "private_network", type: "dhcp"

    config.vm.provision "shell", inline: <<-SHELL
      sudo add-apt-repository -y ppa:openjdk-r/ppa
      sudo apt-get update
      sudo apt-get install -y openjdk-8-jdk g++-multilib
    SHELL
  end

  config.vm.define "windows" do |config|
    config.vm.box = "modernIE/w10-edge"
    config.vm.network "private_network", type: "dhcp"
  end

  config.vm.define "freebsd" do |config|
    config.vm.box = "freebsd/FreeBSD-10.2-STABLE"
  # config.vm.network "private_network", type: "dhcp"
    config.ssh.shell = 'csh'

  # config.vm.synced_folder ".", "/vagrant", :nfs => true, id: "vagrant-root"

    config.vm.provision "shell", inline: <<-SHELL
      fetch -arRo /tmp/ https://download.freebsd.org/ftp/releases/amd64/10.2-RELEASE/lib32.txz
      tar -xpf /tmp/lib32.txz -C /
      pkg install -y openjdk8
      sudo mount -t fdescfs fdesc /dev/fd
      sudo mount -t procfs proc /proc
      rehash
    SHELL
  end

end
