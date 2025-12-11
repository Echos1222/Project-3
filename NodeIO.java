public interface NodeIO {
    BTreeNode readNode(long blockId) throws Exception;
    void writeNode(BTreeNode node) throws Exception;
    BTreeNode allocateNode() throws Exception;
}
