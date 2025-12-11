import java.io.BufferedWriter;

public class BTree {
    private final NodeIO io;
    private long rootBlock; // 0 means empty tree

    public BTree(NodeIO io, long rootBlock) {
        this.io = io;
        this.rootBlock = rootBlock;
    }

    public long getRootBlock() { return rootBlock; }

    // ---------------- SEARCH ----------------
    public Long search(long key) throws Exception {
        if (rootBlock == 0) return null;
        return searchNode(rootBlock, key);
    }

    private Long searchNode(long blockId, long key) throws Exception {
        BTreeNode n = io.readNode(blockId);
        int i = 0;
        while (i < n.numKeys && key > n.keys[i]) i++;
        if (i < n.numKeys && key == n.keys[i]) return n.values[i];
        if (n.isLeaf()) return null;
        long child = n.children[i];
        if (child == 0) return null;
        return searchNode(child, key);
    }

    // ---------------- INSERT ----------------
    public void insert(long key, long value) throws Exception {
        if (rootBlock == 0) {
            BTreeNode root = io.allocateNode();
            root.keys[0] = key;
            root.values[0] = value;
            root.numKeys = 1;
            io.writeNode(root);
            rootBlock = root.blockId;
            return;
        }

        BTreeNode r = io.readNode(rootBlock);
        if (r.numKeys == BTreeNode.MAX_KEYS) {
            BTreeNode s = io.allocateNode();
            s.children[0] = r.blockId;
            r.parentBlock = s.blockId;
            io.writeNode(r);

            splitChild(s, 0, r);

            int i = 0;
            if (s.numKeys > 0 && key > s.keys[0]) i = 1;
            BTreeNode child = io.readNode(s.children[i]);
            insertNonFull(child, key, value);

            io.writeNode(s);
            rootBlock = s.blockId;
        } else {
            insertNonFull(r, key, value);
        }
    }

    private void insertNonFull(BTreeNode x, long key, long value) throws Exception {
        if (x.isLeaf()) {
            int i = x.numKeys - 1;
            while (i >= 0 && key < x.keys[i]) {
                x.keys[i+1] = x.keys[i];
                x.values[i+1] = x.values[i];
                i--;
            }
            x.keys[i+1] = key;
            x.values[i+1] = value;
            x.numKeys++;
            io.writeNode(x);
        } else {
            int i = x.numKeys - 1;
            while (i >= 0 && key < x.keys[i]) i--;
            i++;
            BTreeNode child = io.readNode(x.children[i]);
            if (child.numKeys == BTreeNode.MAX_KEYS) {
                splitChild(x, i, child);
                if (key > x.keys[i]) i++;
            }
            BTreeNode next = io.readNode(x.children[i]);
            insertNonFull(next, key, value);
        }
    }

    private void splitChild(BTreeNode parent, int index, BTreeNode y) throws Exception {
        BTreeNode z = io.allocateNode();
        z.parentBlock = parent.blockId;
        z.numKeys = BTreeNode.T - 1;

        for (int j = 0; j < BTreeNode.T - 1; j++) {
            z.keys[j] = y.keys[j + BTreeNode.T];
            z.values[j] = y.values[j + BTreeNode.T];
        }

        if (!y.isLeaf()) {
            for (int j = 0; j < BTreeNode.T; j++) {
                z.children[j] = y.children[j + BTreeNode.T];
                if (z.children[j] != 0) {
                    BTreeNode child = io.readNode(z.children[j]);
                    child.parentBlock = z.blockId;
                    io.writeNode(child);
                }
            }
        }

        y.numKeys = BTreeNode.T - 1;

        for (int j = parent.numKeys; j >= index + 1; j--) {
            parent.children[j + 1] = parent.children[j];
        }
        parent.children[index + 1] = z.blockId;

        for (int j = parent.numKeys - 1; j >= index; j--) {
            parent.keys[j + 1] = parent.keys[j];
            parent.values[j + 1] = parent.values[j];
        }

        parent.keys[index] = y.keys[BTreeNode.T - 1];
        parent.values[index] = y.values[BTreeNode.T - 1];
        parent.numKeys++;

        io.writeNode(y);
        io.writeNode(z);
        io.writeNode(parent);
    }

    // ---------------- TRAVERSAL ----------------
    // Print as CSV: key,value per line
    public void traversePrintCSV() throws Exception {
        if (rootBlock == 0) {
            // nothing to print
            return;
        }
        traversePrintCSV(rootBlock);
    }

    private void traversePrintCSV(long blockId) throws Exception {
        BTreeNode n = io.readNode(blockId);
        boolean leaf = n.isLeaf();
        for (int i = 0; i < n.numKeys; i++) {
            if (!leaf && n.children[i] != 0) traversePrintCSV(n.children[i]);
            System.out.println(n.keys[i] + "," + n.values[i]);
        }
        if (!leaf && n.children[n.numKeys] != 0) traversePrintCSV(n.children[n.numKeys]);
    }

    // Write CSV to BufferedWriter (for extract)
    public void traverseWriteCSV(BufferedWriter bw) throws Exception {
        if (rootBlock == 0) return;
        traverseWriteCSV(rootBlock, bw);
    }

    private void traverseWriteCSV(long blockId, BufferedWriter bw) throws Exception {
        BTreeNode n = io.readNode(blockId);
        boolean leaf = n.isLeaf();
        for (int i = 0; i < n.numKeys; i++) {
            if (!leaf && n.children[i] != 0) traverseWriteCSV(n.children[i], bw);
            bw.write(Long.toString(n.keys[i]) + "," + Long.toString(n.values[i]));
            bw.newLine();
        }
        if (!leaf && n.children[n.numKeys] != 0) traverseWriteCSV(n.children[n.numKeys], bw);
    }
}
