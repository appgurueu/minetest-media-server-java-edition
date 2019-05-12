package server;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class MinetestRequestHandler implements Runnable {
    public static int MAX_HEADER_SIZE = 2048;
    public static byte[] METHOD_POST = stringToArray("POST");
    public static byte[] METHOD_GET = stringToArray("GET ");
    public static byte[] REQUIRED_PROTOCOL = stringToArray("MTHS");
    public static byte[] CONTENT_LENGTH = stringToArray("Content-Length"); // Content Length Info

    public Socket socket;
    public InputStream request;
    public OutputStream response;
    public MinetestFileServer server;

    public MinetestRequestHandler(Socket socket, MinetestFileServer server) throws IOException {
        InputStream request=socket.getInputStream();
        OutputStream response=socket.getOutputStream();
        this.socket=socket;
        this.request = request;
        this.response = response;
        this.server = server;
    }

    public static byte charToNumber(char c) {
        if (c >= 'a') {
            return (byte)(10 + c - 'a');
        }
        return (byte)(c - '0');
    }

    public synchronized void run() {
        try {
            byte[] used_method=new byte[4];
            this.request.read(used_method);
            this.request.skip(1);
            if (startsWith(used_method, METHOD_POST, 0)) {
                int len=getContentLength(this.request);
                skipToBody(this.request);
                byte[] header=new byte[6];
                this.request.read(header);
                if (!startsWith(header, REQUIRED_PROTOCOL, 0)) {
                    throw new InvalidHeaderException("Invalid REQUIRED_PROTOCOL : not MTHS");
                }
                if (header[4] != 0 || header[5] != 1) {
                    throw new InvalidHeaderException("Invalid version : not 00 01");
                }
                len-=6;
                Set<SHA1Hash> hashes_found=new HashSet();
                for (int i=0; i < len/20; i++) {
                    byte[] hash_array=new byte[20];
                    this.request.read(hash_array);
                    SHA1Hash hash=new SHA1Hash(hash_array);
                    if (server.file_lookup.containsKey(hash)) {
                        hashes_found.add(hash);
                    }
                }
                String http_header="HTTP/1.1 200 OK\r\n" +
                        "Access-Control-Allow-Origin: *\r\n"+
                        "Server: MTMediaServerJE\r\n" +
                        "Content-Type: octet/stream"+"\r\n"+
                        "Content-Length: "+(hashes_found.size()*20+6) +
                        "\r\n\r\n";
                for (int i=0; i < http_header.length(); i++) {
                    this.response.write((byte)http_header.charAt(i));
                }
                this.response.write(header);
                for (SHA1Hash hash:hashes_found) {
                    for (byte b=0; b < 20; b++) {
                        this.response.write(hash.hash[b]);
                    }
                }
            } else if (startsWith(used_method, METHOD_GET, 0)) {
                byte[] hash=new byte[20];
                for (byte i=0; i < 20; i++) {
                    hash[i]=(byte)(charToNumber((char)this.request.read())*16+charToNumber((char)this.request.read()));
                }
                server.writeResponse(new SHA1Hash(hash), response);
            }
            this.request.close();
            this.response.flush();
            this.response.close();
            this.socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] stringToArray(String s) {
        byte[] res=new byte[s.length()];
        for (int i=0; i < res.length; i++) {
            res[i]=(byte)s.charAt(i);
        }
        return res;
    }

    public static int getContentLength(InputStream input) throws IOException {
        int length=0;
        int i=input.read();
        int j=-1;
        while (i >= 0 && length < MAX_HEADER_SIZE) {
            if ((char)i == '\n') {
                j=0;
            } else if (j >= 0) {
                if (CONTENT_LENGTH[j] == i) {
                    j++;
                    if (j == CONTENT_LENGTH.length) {
                        // Read until next CRLF
                        input.skip(2);
                        String l="";
                        int len=input.read();
                        while (len >= '0' && (char)len <= '9') {
                            l+=(char)len;
                            len=input.read();
                        }
                        return Integer.parseInt(l);
                    }
                } else {
                    j=-1;
                }
            }
            i=input.read();
            length++;
        }
        throw new IOException("No valid Content-Length specified");
    }

    public static void skipToBody(InputStream input) throws IOException {
        boolean newline=true;
        int length=0;
        int i=input.read();
        while (i >= 0 && length < MAX_HEADER_SIZE) {
            if (i == '\n') {
                newline=true;
            } else if (i == '\r' && newline) {
                input.skip(1);
                return;
            } else {
                newline=false;
            }
            i=input.read();
            length++;
        }
        throw new IOException("Header too long");
    }


    public static boolean startsWith(byte[] array, byte[] pattern, int offset) {
        for (int i=offset; i < Math.min(array.length, offset+pattern.length); i++) {
            if (array[i] != pattern[i-offset]) {
                return false;
            }
        }
        return true;
    }
}
