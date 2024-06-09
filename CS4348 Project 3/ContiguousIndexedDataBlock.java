import java.util.Arrays;

public class ContiguousIndexedDataBlock extends DataBlock {
    public byte[] data;

    ContiguousIndexedDataBlock() { 
        data = new byte[512];
    }
    
    public void deleteCIBlock() { 
        for (int i = 0; i < 256; i++)
            data[i] = 0;
    }

    public void displayCI() { 
        for (int i = 1; i <= 512; i++){
            System.out.print(data[i-1]);
            if (i % 32 == 0)
                System.out.print("\n");
        }
        System.out.println();
    }

    public void writeToBlock(byte[] dataArray) { 
        data = Arrays.copyOf(dataArray, dataArray.length);
    }

}