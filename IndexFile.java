import java.io.*;

public class IndexFile {
    private RandomAccessFile raf;
    private Header header;
    private DiskNodeIO nodeIO;
    private BTree btree;

    // create command
    public static void create(String fileName) throws Exception {
        File f = new File(fileName);
        if (f.exists()) {
            System.err.println("Error: File already exists and will not be modified.");
            System.exit(1);
        }

        RandomAccessFile raf = new RandomAccessFile(f, "rw");

        // write header block (magic + root + nextBlock)
        Header h = new Header();
        h.magic = Header.MAGIC;
        h.rootBlock = 0;      // empty tree
        h.nextBlock = 1;      // next available block (block 0 is header)
        h.writeHeader(raf);

        raf.close();
        System.out.println("Created index file: " + fileName);
    }

    // open existing index file - validates header magic
    public IndexFile(String fileName) throws Exception {
        File f = new File(fileName);
        if (!f.exists()) throw new Exception("Index file does not exist.");

        raf = new RandomAccessFile(f, "rw");
        header = Header.readHeader(raf); // validates magic or throws
        nodeIO = new DiskNodeIO(raf, header);
        btree = new BTree(nodeIO, header.rootBlock);
    }

    public void close() throws Exception {
        // persist header (rootBlock and nextBlock)
        header.rootBlock = btree.getRootBlock();
        header.writeHeader(raf);
        raf.close();
    }

    public void insert(long key, long value) throws Exception {
        btree.insert(key, value);
        // update header in memory; write on close
        header.rootBlock = btree.getRootBlock();
    }

    public Long search(long key) throws Exception {
        return btree.search(key);
    }

    public void loadCSV(String csvFile) throws Exception {
        File f = new File(csvFile);
        if (!f.exists()) {
            throw new Exception("CSV file does not exist.");
        }

        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split(",");
            if (parts.length < 2) continue;
            long key = Long.parseUnsignedLong(parts[0].trim());
            long val = Long.parseUnsignedLong(parts[1].trim());
            insert(key, val);
        }
        br.close();
    }

    public void printAll() throws Exception {
        btree.traversePrintCSV();
    }

    public void extract(String outputCSV) throws Exception {
        // ensure not exist (checked by caller), create and write
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputCSV));
        btree.traverseWriteCSV(bw);
        bw.close();
    }
}
