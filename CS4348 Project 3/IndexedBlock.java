public class IndexedBlock extends AbsBlock {
	
    public int [] indexBlocks = new int[128];

    public void displayCI() { 
        for (int i = 0; i < 128; i++)
            if (indexBlocks[i] != 0) 
                System.out.print(indexBlocks[i] + "	");
        System.out.println();
    }

    public void writeToIndexedBlock(int [] arrays) { 
        for (int i = 1; i < arrays.length; i++)
            indexBlocks[i - 1] = arrays[i];
    }

    private int indexedBlockLen() { 
        int len = 0;
        for (int i = 0; i < 128; i++)
            if (indexBlocks[i] != 0)
                len++;
        return len;
    }


    public int [] getIndexBlocks() { 
        int length = indexedBlockLen();
        int [] idxblocks = new int[length];
        for (int i = 0; i < 128; i++)
            if (indexBlocks[i] != 0)
                idxblocks[i] = indexBlocks[i];
        return idxblocks;
    }

    public void deleteIndexedBlock () { 
        for (int i = 0; i < 128; i++) 
            indexBlocks[i] = 0;
    }
}