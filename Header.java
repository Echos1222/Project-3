import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Header {
    public static final int BLOCK_SIZE = 512;
    public static final String MAGIC = "4348PRJ3"; // 8 bytes

    public String magic = MAGIC;
    public long rootBlock;
    public long nextBlock;

    public static Header readHeader(RandomAccessFile raf) throws Exception {
        raf.seek(0);
        byte[] block = new byte[BLOCK_SIZE];
        raf.readFully(block);

        ByteBuffer buf = ByteBuffer.wrap(block);
        buf.order(ByteOrder.BIG_ENDIAN);

        byte[] magicBytes = new byte[8];
        buf.get(magicBytes);
        String m = new String(magicBytes, "UTF-8");

        if (!m.equals(MAGIC)) {
            throw new Exception("Invalid index file (bad magic)");
        }

        Header h = new Header();
        h.magic = m;
        h.rootBlock = buf.getLong();
        h.nextBlock = buf.getLong();
        return h;
    }

    public void writeHeader(RandomAccessFile raf) throws Exception {
        ByteBuffer buf = ByteBuffer.allocate(BLOCK_SIZE);
        buf.order(ByteOrder.BIG_ENDIAN);

        byte[] mb = new byte[8];
        byte[] actual = magic.getBytes("UTF-8");
        System.arraycopy(actual, 0, mb, 0, Math.min(actual.length, mb.length));
        buf.put(mb);

        buf.putLong(rootBlock);
        buf.putLong(nextBlock);

        raf.seek(0);
        raf.write(buf.array());
    }
}
