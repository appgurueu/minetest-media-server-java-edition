/*
Minetest Media Server Java Edition
Java Implementation of the Minetest Media Server Specification.
Licensed under AGPLv3. For more details visit Readme.md.
Copyright © Lars Müller alias LMD, appguru, appgurueu or appgurulars (github.com/appgurueu)
 */

import server.MinetestFileServer;
import server.RequestAwaiter;

import java.io.File;

public class Main {

    // args : {ip, filepath, other_parts, all concatenated}
    public static void main(String... args) {
        if (args.length < 2) {
            System.err.println("Provide at least two arguments : port and multiple paths to media");
            System.exit(1);
        }
        int port=0;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.err.println("Provide a valid integer port number");
            System.exit(1);
        }
        MinetestFileServer srv=new MinetestFileServer();
        for (int i=1; i < args.length; i++) {
            File collect_in=new File(args[i]);
            if (!collect_in.exists() || collect_in.isFile()) {
                System.err.println("Argument #"+(i+1)+" : '"+args[i]+"' does not exist or is no directory path");
                System.exit(1);
            }
            System.out.println("Retrieving media from "+args[i]);
            srv.retrieveMedia(collect_in);
        }
        System.out.println("Finished retrieving media - "+srv.file_lookup.size()+" media files found");
        RequestAwaiter r=new RequestAwaiter(port, srv);
        System.out.println("Finished starting server - now running on port "+port);
        r.run();
    }
}

