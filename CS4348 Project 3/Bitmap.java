import java.util.ArrayList;

public class Bitmap extends AbsBlock {
    public boolean[] bitmap;

    Bitmap() {
        bitmap = new boolean[256];

        
        for (int i = 0; i < 256; i++)
            bitmap[i] = false;

       
        bitmap[0] = bitmap[1] = true;
    }
    
    public void displayCI() {
        for (int i = 1; i <= 256; i++) {
            System.out.print(bitmap[i-1] ? 1: 0);

            if (i % 32 == 0)
                System.out.print("\n");
        }
        System.out.println();
    }
    
    public int [] emptyBlocks(int blockLen) {

        int [] emptyBlocks = new int[blockLen];
        ArrayList<Integer> temp = new ArrayList<>();
        for (int i = 0; i < 256; i++)
            if (!bitmap[i])
                temp.add(i);

        int max = temp.size() + 1;
        for (int i = 0; i < blockLen; i++) {
            int block = (int) (0 + (Math.random() * (max - 0)));
            temp.remove(block);
            emptyBlocks[i] = block;
            max--;
        }
        return emptyBlocks;
    }

    public void bitmapDeleteBlock (int blockNum) {
        bitmap[blockNum] = false;
    }
    
    public void bitmapAllocBlock (int blockNum) {
        bitmap[blockNum] = true;
    }

    public int getFirstBlock(int numberOfBlock) {
        int firstBlock = 2;
        int blockCount = 0;

        for (int i = 2; i < 256; i++) {
            if (!bitmap[i]) 
                blockCount++;
            else 
                firstBlock ++;
            if (blockCount == numberOfBlock)
                break;
        }
        return firstBlock;
    }

}