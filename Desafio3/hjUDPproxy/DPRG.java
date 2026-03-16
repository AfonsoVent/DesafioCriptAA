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

    // gen flux pseudorandom tam = length
    public byte[] generate(int length) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // While out reques < length them we want
        while(out.size() < length) {
            md.update(key);
            // Convert (long -> byte[])
            md.update(ByteBuffer.allocate(8).putLong(counter).array());

            byte[] block = md.digest();
            out.write(block);
            counter++;
        }

        // Convert to Array byte and send just length requested
        return Arrays.copyOf(out.toByteArray(), length);
    }
}