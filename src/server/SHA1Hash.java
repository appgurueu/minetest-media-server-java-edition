package server;

import java.util.Arrays;

public class SHA1Hash {
    public final byte[] hash;

    public SHA1Hash(byte[] hash) {
        this.hash=hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o.getClass() == SHA1Hash.class) {
            return Arrays.equals(this.hash, ((SHA1Hash)o).hash);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(hash);
    }
}
