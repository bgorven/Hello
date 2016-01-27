# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure 2 do |config|

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
    config.vm.network "private_network", ip: "10.1.1.10"

    config.vm.provision "shell", inline: <<-SHELL
      sudo add-apt-repository -y ppa:openjdk-r/ppa
      sudo apt-get update
      sudo apt-get install -y openjdk-8-jdk g++-multilib
    SHELL
  end

  config.vm.define "windows" do |config|
    config.vm.box = "modernIE/w10-edge"
  # config.vm.box = "senglin/win-10-enterprise-vs2015community"
    config.vm.network "private_network", ip: "10.1.1.11"
    config.vm.network :forwarded_port, host: 2222, guest: 22

    config.ssh.insert_key = true
    config.ssh.sudo_command = "%c"
    config.ssh.username = "IEUser"
  # config.ssh.password = "Passw0rd!"

    config.vm.provision :shell, path: "provisioning/windows.cmd"
  end

  config.vm.define "freebsd" do |config|
    config.vm.guest = :freebsd
    config.vm.network "private_network", ip: "10.1.1.12"
    config.ssh.shell = 'csh'

    config.vm.synced_folder ".", "/vagrant", id: "vagrant-root", nfs: true

    config.vm.provider :virtualbox do |vb, override|
      override.vm.box_url = "https://wunki.org/files/freebsd-10.2-amd64-wunki.box"
      override.vm.box = "freebsd-10.2-amd64-wunki"

    # vb.customize ["startvm", :id, "--type", "gui"]
      vb.customize ["modifyvm", :id, "--memory", "512"]
      vb.customize ["modifyvm", :id, "--cpus", "2"]
      vb.customize ["modifyvm", :id, "--hwvirtex", "on"]
      vb.customize ["modifyvm", :id, "--audio", "none"]
      vb.customize ["modifyvm", :id, "--nictype1", "virtio"]
      vb.customize ["modifyvm", :id, "--nictype2", "virtio"]
    end

    config.vm.provision "shell", inline: <<-SHELL
      pkg update
      pkg upgrade
      pkg install -y gcc openjdk8 ca_root_nss
      mount -t fdescfs fdesc /dev/fd
      mount -t procfs proc /proc
      echo 'fdesc /dev/fd fdescfs rw 0 0' >> /etc/fstab
      echo 'proc /proc procfs rw 0 0' >> /etc/fstab
    SHELL
  end

end
