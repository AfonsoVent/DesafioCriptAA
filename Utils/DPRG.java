package Utils;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Arrays;

public class DPRG{
    private byte[] key;
    private long counter; // framer counter

    // Constructor
    public DPRG(byte[] key) {
        this.key = key;
        this.counter = 0;
    }

    public byte[] generate(int length) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        while(out.size() < length) {
            md.update(key);
            md.update(ByteBuffer.allocate(8).putLong(counter).array());

            byte[] block = md.digest();
            out.write(block);
            counter++;
        }

        // Convert to Array byte
        byte[] result = out.toByteArray();

        return Arrays.copyOf(result, length);
    }
}