import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class NodeSerializer {

    public static byte[] toBytes(BTreeNode n) {
        ByteBuffer buf = ByteBuffer.allocate(BTreeNode.BLOCK_SIZE);
        buf.order(ByteOrder.BIG_ENDIAN);

        buf.putLong(n.blockId);
        buf.putLong(n.parentBlock);
        // store numKeys as a long to match older on-disk format compatibility
        buf.putLong((long) n.numKeys);

        for (int i = 0; i < BTreeNode.MAX_KEYS; i++) buf.putLong(n.keys[i]);
        for (int i = 0; i < BTreeNode.MAX_KEYS; i++) buf.putLong(n.values[i]);
        for (int i = 0; i < BTreeNode.MAX_CHILDREN; i++) buf.putLong(n.children[i]);

        return buf.array();
    }

    public static BTreeNode fromBytes(byte[] block) {
        ByteBuffer buf = ByteBuffer.wrap(block);
        buf.order(ByteOrder.BIG_ENDIAN);

        long blockId = buf.getLong();
        long parent = buf.getLong();
        long numKeysLong = buf.getLong();

        BTreeNode n = new BTreeNode(blockId);
        n.parentBlock = parent;
        n.numKeys = (int) numKeysLong;

        for (int i = 0; i < BTreeNode.MAX_KEYS; i++) n.keys[i] = buf.getLong();
        for (int i = 0; i < BTreeNode.MAX_KEYS; i++) n.values[i] = buf.getLong();
        for (int i = 0; i < BTreeNode.MAX_CHILDREN; i++) n.children[i] = buf.getLong();

        return n;
    }
}
