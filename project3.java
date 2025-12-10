import java.io.File;

public class project3 {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: project3 <command> <arguments>");
            System.exit(1);
        }

        String cmd = args[0].toLowerCase();
        try {
            switch (cmd) {
                case "create":
                    // project3 create test.idx
                    if (args.length != 2) {
                        System.err.println("Usage: project3 create <index-file>");
                        System.exit(1);
                    }
                    IndexFile.create(args[1]);
                    break;

                case "insert":
                    // project3 insert test.idx 15 100
                    if (args.length != 4) {
                        System.err.println("Usage: project3 insert <index-file> <key> <value>");
                        System.exit(1);
                    }
                    IndexFile idxIns = new IndexFile(args[1]);
                    long ikey = Long.parseUnsignedLong(args[2]);
                    long ival = Long.parseUnsignedLong(args[3]);
                    idxIns.insert(ikey, ival);
                    idxIns.close();
                    break;

                case "search":
                    // project3 search test.idx 15
                    if (args.length != 3) {
                        System.err.println("Usage: project3 search <index-file> <key>");
                        System.exit(1);
                    }
                    IndexFile idxS = new IndexFile(args[1]);
                    long skey = Long.parseUnsignedLong(args[2]);
                    Long sres = idxS.search(skey);
                    if (sres == null) {
                        System.err.println("key not found");
                        idxS.close();
                        System.exit(1);
                    } else {
                        // print unsigned form
                        System.out.println(Long.toUnsignedString(skey) + "," + Long.toUnsignedString(sres));
                    }
                    idxS.close();
                    break;

                case "load":
                    // project3 load test.idx input.csv
                    if (args.length != 3) {
                        System.err.println("Usage: project3 load <index-file> <input.csv>");
                        System.exit(1);
                    }
                    IndexFile idxL = new IndexFile(args[1]);
                    idxL.loadCSV(args[2]); // will error and exit on CSV missing
                    idxL.close();
                    break;

                case "print":
                    // project3 print test.idx
                    if (args.length != 2) {
                        System.err.println("Usage: project3 print <index-file>");
                        System.exit(1);
                    }
                    IndexFile idxP = new IndexFile(args[1]);
                    idxP.printAll();
                    idxP.close();
                    break;

                case "extract":
                    // project3 extract test.idx output.csv
                    if (args.length != 3) {
                        System.err.println("Usage: project3 extract <index-file> <output.csv>");
                        System.exit(1);
                    }
                    File out = new File(args[2]);
                    if (out.exists()) {
                        System.err.println("Error: output file already exists and will not be modified.");
                        System.exit(1);
                    }
                    IndexFile idxE = new IndexFile(args[1]);
                    idxE.extract(args[2]);
                    idxE.close();
                    break;

                default:
                    System.err.println("Unknown command: " + cmd);
                    System.exit(1);
            }
        } catch (NumberFormatException nfe) {
            System.err.println("Error: invalid numeric argument - " + nfe.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
