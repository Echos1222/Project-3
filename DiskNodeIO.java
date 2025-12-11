import java.io.RandomAccessFile;

public class DiskNodeIO implements NodeIO {
    private final RandomAccessFile raf;
    private final Header header;

    public DiskNodeIO(RandomAccessFile raf, Header header) {
        this.raf = raf;
        this.header = header;
    }

    @Override
    public BTreeNode readNode(long blockId) throws Exception {
        // validate blockId: block 0 is header, valid nodes start at 1 and must be < nextBlock
        if (blockId <= 0 || blockId >= header.nextBlock) {
            throw new Exception("Invalid block id to read: " + blockId);
        }

        byte[] block = new byte[BTreeNode.BLOCK_SIZE];
        raf.seek(blockId * BTreeNode.BLOCK_SIZE);
        raf.readFully(block);

        BTreeNode n = NodeSerializer.fromBytes(block);
        // ensure blockId matches the requested id
        n.blockId = blockId;
        return n;
    }

    @Override
    public void writeNode(BTreeNode n) throws Exception {
        byte[] data = NodeSerializer.toBytes(n);
        if (data.length != BTreeNode.BLOCK_SIZE) {
            throw new Exception("Node serialization produced incorrect block size");
        }
        raf.seek(n.blockId * BTreeNode.BLOCK_SIZE);
        raf.write(data);
    }

    @Override
    public synchronized BTreeNode allocateNode() throws Exception {
        // allocate new block id and persist the change to the header immediately
        long id = header.nextBlock++;
        BTreeNode n = new BTreeNode(id);

        // write initialized node to disk so the block exists
        writeNode(n);

        // persist updated header.nextBlock to disk immediately so file state stays consistent
        header.writeHeader(raf);

        return n;
    }

    
    public long getNextBlock() {
        return header.nextBlock;
    }
}
