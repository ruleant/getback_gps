# -*- mode: ruby -*-
# vi: set ft=ruby :

# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  # All Vagrant configuration is done here. The most common configuration
  # options are documented and commented below. For a complete reference,
  # please see the online documentation at vagrantup.com.

  # Every Vagrant virtual environment requires a box to build off of.
  config.vm.box = "ubuntu/xenial64"

  # increase memory of virtualbox
  config.vm.provider "virtualbox" do |v|
    v.memory = 2048
    v.customize ["modifyvm", :id, "--usb", "on"]
    v.customize ["modifyvm", :id, "--usbxhci", "on"]
    v.customize ['usbfilter', 'add', '0', '--target', :id, '--name', 'Google Inc. Nexus 4 (debug)', '--vendorid', '0x18d1', '--productid', '0x4ee1']
    v.customize ['usbfilter', 'add', '0', '--target', :id, '--name', 'Google Inc. Nexus 4 (debug)', '--vendorid', '0x18d1', '--productid', '0x4ee2']
    v.customize ['usbfilter', 'add', '0', '--target', :id, '--name', 'Google Inc. Nexus 4 (debug)', '--vendorid', '0x18d1', '--productid', '0x4ee6']
    v.customize ['usbfilter', 'add', '0', '--target', :id, '--name', 'Motorola Moto X4', '--vendorid', '0x22b8', '--productid', '0x2e76']
    v.customize ['usbfilter', 'add', '0', '--target', :id, '--name', 'Motorola PCS', '--vendorid', '0x22b8', '--productid', '0x2e81']
    v.customize ['usbfilter', 'add', '0', '--target', :id, '--name', 'Motorola PCS', '--vendorid', '0x22b8', '--productid', '0x2e84']
    v.customize ['usbfilter', 'add', '0', '--target', :id, '--name', 'Huawei Technologies Co., Ltd.', '--vendorid', '0x12d1', '--productid', '0x107e']
  end

  # Disable automatic box update checking. If you disable this, then
  # boxes will only be checked for updates when the user runs
  # `vagrant box outdated`. This is not recommended.
  # config.vm.box_check_update = false

  # Create a forwarded port mapping which allows access to a specific port
  # within the machine from a port on the host machine. In the example below,
  # accessing "localhost:8080" will access port 80 on the guest machine.
  # config.vm.network "forwarded_port", guest: 80, host: 8080

  # Create a private network, which allows host-only access to the machine
  # using a specific IP.
  # config.vm.network "private_network", ip: "192.168.33.10"

  # Create a public network, which generally matched to bridged network.
  # Bridged networks make the machine appear as another physical device on
  # your network.
  # config.vm.network "public_network"

  # If true, then any SSH connections made will enable agent forwarding.
  # Default value: false
  # config.ssh.forward_agent = true

  # Share an additional folder to the guest VM. The first argument is
  # the path on the host to the actual folder. The second argument is
  # the path on the guest to mount the folder. And the optional third
  # argument is a set of non-required options.
  # config.vm.synced_folder "../data", "/vagrant_data"

  # bootstrap VM with python2.7 before provisioning with ansible.
  config.vm.provision "shell", inline: "command -v python2.7 || (apt-get update && apt-get -y install python2.7)"

  # start Ansible provisioning
  config.vm.provision "ansible" do |ansible|
    ansible.verbose = "v"
    ansible.become = true
    ansible.playbook = "provisioning/playbook.yml"
    ansible.extra_vars = { ansible_python_interpreter:"/usr/bin/python2.7" }
    ansible.compatibility_mode = "1.8"
  end
end
