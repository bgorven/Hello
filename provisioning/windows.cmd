# https://gist.github.com/gregsochanik/7582661
@powershell -NoProfile -ExecutionPolicy unrestricted -Command "iex ((new-object net.webclient).DownloadString('https://chocolatey.org/install.ps1'))"
set PATH=%PATH%;%systemdrive%\chocolatey\bin
choco install jdk8,visualstudiocommunity2013 -y
if not exist "C:\Program Files\OpenSSH\vagrant" mklink /D "C:\Program Files\OpenSSH\vagrant" "C:\vagrant"
