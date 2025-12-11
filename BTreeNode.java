public class BTreeNode {
	public static final int T = 10;
	public static final int MAX_KEYS = 2 * T - 1; // 19
	public static final int MAX_CHILDREN = 2 * T; // 20
	public static final int BLOCK_SIZE = 512;

	public long blockId;
	public long parentBlock;
	public int numKeys;

	public long[] keys = new long[MAX_KEYS];
	public long[] values = new long[MAX_KEYS];
	public long[] children = new long[MAX_CHILDREN];

	public BTreeNode() {
		this.blockId = 0;
		this.parentBlock = 0;
		this.numKeys = 0;
		for (int i = 0; i < MAX_KEYS; i++) {
			keys[i] = 0;
			values[i] = 0;
		}
		for (int i = 0; i < MAX_CHILDREN; i++) children[i] = 0;
	}

	public BTreeNode(long blockId) {
		this();
		this.blockId = blockId;
	}

	public boolean isLeaf() {
		return children[0] == 0;
	}
}
