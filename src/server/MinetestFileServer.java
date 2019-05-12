package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class MinetestFileServer {
    public static String[] image_endings=new String[] {"png","jpg","jpeg"};
    public static String[] sound_endings=new String[] {"ogg"};
    public static String[] model_endings=new String[] {"b3d", "obj", "x"};
    public HashMap<SHA1Hash, byte[]> file_lookup; // Lookup by SHA1 Hash : Hash -> Filename
    public static MessageDigest mDigest;
    static {
        try {
            mDigest=MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.err.println("SHA1 hash algorithm not available");
            System.exit(1);
        }
    }

    public MinetestFileServer() {
        file_lookup=new HashMap();
    }

    public static byte[] getHashBytes(byte[] b) {
        return mDigest.digest(b);
    }

    public void collectMedia(File folder, String[] allowed_endings) throws IOException {
        for (File f:folder.listFiles()) {
            if (f.isFile()) {
                String filename=f.getName();
                int i=filename.lastIndexOf('.');
                if (i > 0) {
                    String extension=filename.substring(i+1);
                    for (int j=0; j < allowed_endings.length; j++) {
                        if (allowed_endings[j].equals(extension)) {
                            addFile(f);
                            break;
                        }
                    }
                }
            }
        }
    }
    public void retrieveMedia(File folder) {
        for (File f:folder.listFiles()) {
            if (f.isDirectory()) {
                try {
                    if (f.getName().equals("textures")) {
                        collectMedia(f, image_endings);
                    } else if (f.getName().equals("sounds")) {
                        collectMedia(f, sound_endings);
                    } else if (f.getName().equals("models")) {
                        collectMedia(f, model_endings);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Couldn't collect media from "+f.getAbsolutePath());
                }
                retrieveMedia(f);
            }
        }
    }

    // Serves a file
    public boolean writeResponse(SHA1Hash filehash, OutputStream s) throws IOException {
        byte[] media=file_lookup.get(filehash);
        if (media == null) {
            return false;
        }
        String http_header="HTTP/1.1 200 OK\r\n" +
                "Access-Control-Allow-Origin: *\r\n"+
                "Server: MTMediaServerJE\r\n" +
                "Content-Type: octet/stream\r\n" +
                "Content-Length: "+media.length+
                "\r\n\r\n";
        for (int i=0; i < http_header.length(); i++) {
            s.write((byte)http_header.charAt(i));
        }
        s.write(media);
        return true;
    }

    public void addFile(File file) throws IOException {
        FileInputStream input=new FileInputStream(file);
        byte[] bytes=input.readAllBytes();
        byte[] hash=getHashBytes(bytes);
        file_lookup.put(new SHA1Hash(hash), bytes);
    }
}
