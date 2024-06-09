public class IndexedFA extends AbsFileAlloc {

    public IndexedFA () {
    	
        mainDataArray[0] = new IndexedFileTable();
        mainDataArray[1] = new Bitmap();
        for (int i = 2; i < 256; i++) 
            mainDataArray[i] = null;
    }

    public void displayBlock(int blockNumber) { 
    	
        if (mainDataArray[blockNumber] == null)
            System.out.println("NULL");
        else if (mainDataArray[blockNumber] instanceof IndexedBlock)
            ((IndexedBlock)mainDataArray[blockNumber]).displayCI();
        else 
            mainDataArray[blockNumber].displayCI();   
    }

    public void writeToBlock(int blockNum, byte[] array) { 
    	
        if (mainDataArray[blockNum] == null) {
            mainDataArray[blockNum] = new ContiguousIndexedDataBlock();
            ((ContiguousIndexedDataBlock)mainDataArray[blockNum]).writeToBlock(array);
        }
        else
            ((ContiguousIndexedDataBlock)mainDataArray[blockNum]).writeToBlock(array);

        ((Bitmap)mainDataArray[1]).bitmapAllocBlock(blockNum); 
    }

    public void writeToIndexedBlock(int indexedBlockNum, int [] blocksList) { 
    	
        if (mainDataArray[indexedBlockNum] == null) {
            mainDataArray[indexedBlockNum] = new IndexedBlock();
            ((IndexedBlock)mainDataArray[indexedBlockNum]).writeToIndexedBlock(blocksList);
        }
        else 
            ((IndexedBlock)mainDataArray[indexedBlockNum]).writeToIndexedBlock(blocksList);
        
        ((Bitmap)mainDataArray[1]).bitmapAllocBlock(indexedBlockNum);  
    }

}