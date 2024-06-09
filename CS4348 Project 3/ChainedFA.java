public class ChainedFA extends AbsFileAlloc {
    public ChainedFA(){
        mainDataArray[0]= new ContiguousChainedFileTable();
        mainDataArray[1]=new Bitmap();
        for(int i=2;i<256;i++){
            mainDataArray[i]=new ChainedDataBlock();
        }
    
    }
    public void displayBlock(int blockNumber){
        mainDataArray[blockNumber].displayCI();
    }
    public void writeToBlock(int blockNum, byte[] array) { 
    	((ChainedDataBlock)mainDataArray[blockNum]).writeToBlock(array);
    }
    
}
