import java.util.Arrays;

public class ChainedDataBlock extends DataBlock {
    public int nextBlock;
    public byte[] data;

    ChainedDataBlock() { 
        this.nextBlock = -1;
        data = new byte[508];
    }

    public void displayCI() {
        for (int i = 1; i <= 508; i++){
            System.out.print(data[i - 1]);
            if (i % 32 == 0)
                System.out.print("\n");
        }
        System.out.println();
    }

    public void writeToBlock(byte[] dataArray) {
    	data = Arrays.copyOf(dataArray, dataArray.length);
    }

    public void writeToBlock(byte[] dataArray, int nextBlock) { 
        data = Arrays.copyOf(dataArray, dataArray.length);
        this.nextBlock = nextBlock;
    }

    public void deleteBlock() { 
        for (int i = 0; i < 508; i++)
            data[i] = 0;
        nextBlock = -1;
    }
}