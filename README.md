# spygame
The app for the Spy Game project, which is a location based elimination game for students at CSUN.

## General Overview

The app is the front end of the project which mostly contains the UI and methods to login, register, and otherwise communicate with [the server](https://github.com/rgilyard/spygame). The app communicates with the server in some manner, either through packets or web requests, and uses the data received from the server to update the UI accordingly.

### Packets

Packets are the established protocol between the app and the server to communicate requests for the player to obtain certain information. A more in depth view explanation of the protocols with an example can be found on the [server's GitHub repository](https://github.com/rgilyard/spygame-server#packets).

#### Logging in

The login process is handled through packets that are unencrypted, as no sensitive data is exposed that can compromise the user's account. The login process results in a shared session encryption key between the player and server (more info [here](https://github.com/rgilyard/spygame-server#data-encryption)) that is used to secure possibly sensitive data exchanged between the server and player in the future.

### Web Requests

Several web requests are made to the server through the app, which include registering an account, checking if a username exists, and requesting a reset of an account's password.

## Incomplete Features

Several features were not completed at the time of last major commits.

Features that were both not tested and integrated into the screens included:
* Creation of game lobbies
* Show game lobby players
* Showing list of all public games
* Joining a lobby with an invite code

Features that were not completed at all included:
* Game record logs
