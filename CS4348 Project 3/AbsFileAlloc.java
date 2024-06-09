abstract public class AbsFileAlloc { 
    AbsBlock[] mainDataArray=new AbsBlock[256];
    abstract public void displayBlock(int blockNumber); 
    
    public AbsBlock getBlock(int blockNum) { 
        return mainDataArray[blockNum];
    }

    public boolean isFull() {
        return ((FileTable)mainDataArray[0]).fileTableFull();
    }
    
    abstract public void writeToBlock (int blockNum, byte[] array); 

    
}
