# Minetest Media Server Java Edition
Also called MT Media Server JE in short.
An implementation of a Minetest Media Server written in Java.
Serves both index.mth and the actual media files.

## Requirements
Requires Java `1.8` or higher. Probably also works with `1.6`.
Tested using Ubuntu 16.04 with Minetest `5.0.1`, and Java `11` on IntelliJ `2019.2`.
Below instructions are all for Linux/Ubuntu.

## Building
Run `cd src` and `javac Main.java`. I however recommend using IntelliJ for building. Builds are already available in `out`. Directory is IntelliJ project.

## Running
Run `cd out/production` and `java Main <port> <path_to_media>` or `java -jar MinetestMediaServerJavaEdition.jar`

Arguments : 
* `<port>` - Integer port number, for example `8000`, note that you will need root permission for port numbers below `1024`
* `<path_to_media>` - Path to retrieve media from, for example `/home/user/.minetest/games/mineclone2`, can be given multiple times to retrieve media from multiple directories

Example use :
0. Change directory to the one you extracted it to, for example `cd /home/user/Downloads/MinetestMediaServerJavaEdition` 
1. `cd out/artifacts/MinetestMediaServerJavaEdition_jar`
2. `java -jar MinetestMediaServerJavaEdition.jar 8000 /home/user/.minetest/games/mineclone2 /home/user/.minetest/games/nodecore`

Will retrieve media from MineClone2 and NodeCore. Server will run on port 8000.
Accordingly you would have to set your `remote_media` setting in `minetest.conf` to `http://<your_ip_or_url>:<port>/`.
Using this example, if you have an url `mt.media.server`, it would look like this : `http://mt.media.server:8000/`

Output (for this example) if successfull (note that the number of media files can of cours be different) : 

    Retrieving media from /home/lars/.minetest/games/mineclone2
    Retrieving media from /home/lars/.minetest/games/nodecore
    Finished retrieving media - 1840 media files found
    Finished starting server - now running on port 8000
    
Example configuration is already default run configuration if you open this in IntelliJ.

## Licensing
Licensed under AGPLv3. Roughly summarized (not legally binding) : This software comes without any warranty.
If you modify it and redistribute or run it (on your server), you have to keep the license.

Copyright © Lars Müller alias LMD, appguru, appgurueu or appgurulars (https://github.com/appgurueu)