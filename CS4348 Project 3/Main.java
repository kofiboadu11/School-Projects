import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
	
	public static AbsFileAlloc fs;

    public static void main (String [] args) throws Exception {
    	
    	int option = 0;
        String fsMethod = args[0];
        //checking argument and create new object
        if (fsMethod.equals("contiguous"))
            fs = new ContiguousFA();
        else if (fsMethod.equals("chained"))
            fs = new ChainedFA();
        else
            fs = new IndexedFA();
        //menu
        do {
            System.out.println("1) Display a file.");
            System.out.println("2) Display the file table.");
            System.out.println("3) Display the free space bitmap.");
            System.out.println("4) Display a disk block.");
            System.out.println("5) Copy a file from the simulation to a file on the real system.");
            System.out.println("6) Copy a file from the real system to a file in the simulation.");
            System.out.println("7) Delete a file.");
            System.out.println("8) Exit.");
            
            
    		Scanner sc = new Scanner(System.in);
            String arg2 = sc.next();
            option = Integer.parseInt(arg2);

            switch (option) {
            
                case 1: //Displaying File
                	Scanner sc1 = new Scanner(System.in);
                    String diskFile2 = sc1.nextLine();
                    displayFile(diskFile2);
                    break;
                    
                case 2: //Displaying FileTable
                	fs.displayBlock(0);
                    break;
                    
                case 3: //Displaying Bitmap
                	fs.displayBlock(1);
                    break;
                    
                case 4: //Display Disk block
                    String arg3 = sc.next();
                    int blockNumber = Integer.parseInt(arg3);
                    fs.displayBlock(blockNumber);
                    break;
                    
                case 5: //copy from simulation to system
                	System.out.print("Copy from: ");
                	Scanner sc2 = new Scanner(System.in);
                    diskFile2= sc2.nextLine();
                    System.out.print("Copy to: ");
                    String realSysFile2 = sc2.nextLine();
                    copyToRealSystem(diskFile2, realSysFile2);
                    System.out.println(diskFile2 + " copied.");
                    break;
                    
                case 6: //copy from system tp simulation
                    String realSysFile = "";
                    String simFile = "";
                    File file = null;
                    Scanner s3 = new Scanner(System.in);
                    do {
                        System.out.print("Copy From: ");
                        realSysFile = s3.nextLine().trim();
                        file = new File(realSysFile);
                        System.out.println(file.getName());
                        
                        System.out.print("Copy to: ");
                        simFile = s3.nextLine().trim();
                    }
                    while (file.getName().length() > 8 || simFile.length() > 8);

                    if (fs.isFull())
                    	System.out.print("File table is full.");
                    else if (doesFileExist(realSysFile) || doesFileExist(simFile))
                    	System.out.print("File already exists.");
                    else {
                    	copyFileToDisk(realSysFile, simFile);
                    	System.out.println(realSysFile + " copied.");
                    }
                    break;
                    
                case 7: //deleting file
                    System.out.print("File name: ");
                    Scanner sc4 = new Scanner(System.in);
                    diskFile2 = sc4.nextLine();
                    deleteFile(diskFile2);
                    break;
                }
        }
        while (option != 8);//Exit
    }
    
    public static boolean doesFileExist(String fileName) { //Checking if file exists
    	
        if (fs instanceof ContiguousFA || fs instanceof ChainedFA)
            return ((ContiguousChainedFileTable)fs.getBlock(0)).fileExists(fileName);
        
        else if (fs instanceof IndexedFA) {
            IndexedFileTable indexedFileTable = (IndexedFileTable)fs.getBlock(0);
            return indexedFileTable.fileExists(fileName);
        }
        else
            System.out.println();
        
        return true;
    }
    
    
    public static void copyFileToDisk(String realSysFile, String diskFile) throws Exception { //Function to copy file to disk
        
    	int firstBlock = 0;
    	byte[] fileData = Files.readAllBytes(new File(realSysFile).toPath()); 
        int blockLen = 0;
        byte[] arr = new byte[508];
        int c = 0;

        if (fs instanceof ContiguousFA) {//if contiguous allocation
        	if (fileData.length > 5120)
                throw new Exception("The File Is Too Big For Memory.");
            
            else {
                
                blockLen = (int) Math.ceil((double)fileData.length / 512); 

                firstBlock = ((Bitmap)(fs.getBlock(1))).getFirstBlock(blockLen); 
                System.out.println("The File Is Written From Block " + firstBlock);

                for (int i = 1; i <= fileData.length; i++) { 
                    int j = i % 512;

                    if (j % 512 == 0 || i == (fileData.length - 1)){
                        arr = Arrays.copyOfRange(fileData, 0, 513);

                        if (c <= blockLen) {
                            fs.writeToBlock(firstBlock + c, arr);
                            c++;
                        }
                    }
                }

                ((ContiguousChainedFileTable)fs.getBlock(0)).addEntryToFileTable(realSysFile, firstBlock, blockLen); 
            }
        }
        
        else if (fs instanceof ChainedFA) { //if chained allocation
            
            ChainedDataBlock chainedBlock;
            int [] emptyBlocks;
            
            int blockNum = 0;
            if (fileData.length > 5080)
                throw new Exception("The File Is Too Big For Memory.");
            
            else {
            	blockLen = (int) Math.ceil((double)fileData.length / 508); 

                emptyBlocks = ((Bitmap)fs.getBlock(1)).emptyBlocks(blockLen); 
                firstBlock = emptyBlocks[0];

                for (int i = 1; i <= fileData.length; i++) { 
                    
                	int j = i % 508;

                    if (j % 508 == 0 || i == (fileData.length - 1)) {
                        
                    	arr = Arrays.copyOfRange(fileData, 0, 509);
                        if (c < blockLen) {
                            
                            if (c != (blockLen - 1)) {
                                blockNum = emptyBlocks[c];
                                chainedBlock = ((ChainedDataBlock)fs.getBlock(blockNum));
                                chainedBlock.writeToBlock(arr, emptyBlocks[c + 1]);
                                ((Bitmap)(fs.getBlock(1))).bitmapAllocBlock(blockNum);
                            }
                            else if (c == blockLen - 1){
                                blockNum = emptyBlocks[c];
                                ((ChainedDataBlock)fs.getBlock(blockNum)).writeToBlock(arr, -1);
                                ((Bitmap)(fs.getBlock(1))).bitmapAllocBlock(blockNum);
                            }
                            c++;
                        }
                    }
                }
                
                ((ContiguousChainedFileTable)fs.getBlock(0)).addEntryToFileTable(realSysFile, firstBlock, blockLen); 
            }

        }
        
        else { 
        	int [] emptyBlocks;
        	int indexedBlock = 0;
            int blockNum = 0;

            if (fileData.length > 5120)
                throw new Exception("The File Is Too Big For Memory.");
            
            else {
                blockLen = (int) Math.ceil((double)fileData.length / 508); 
                emptyBlocks = ((Bitmap)fs.getBlock(1)).emptyBlocks(blockLen + 1); 

                indexedBlock = emptyBlocks[0]; 
                ((IndexedFA) fs).writeToIndexedBlock(indexedBlock, emptyBlocks);

                for (int i = 1; i <= fileData.length; i++) { 
                    
                	int j = i % 512;
                    if (j % 512 == 0 || i == (fileData.length - 1)){
                        arr = Arrays.copyOfRange(fileData, 0, 513);

                        if (c <= blockLen) {
                            if (c != blockLen) {
                                blockNum = emptyBlocks[c + 1];
                                (fs).writeToBlock(blockNum, arr);
                            }
                            else
                                break;
                            
                            c++;
                        }
                    }
                }

                ((IndexedFileTable)fs.getBlock(0)).addEntryToFileTable(realSysFile, indexedBlock); 
            }

        }
    }

    public static void displayFile(String fileName) {//function for displaying file
        if (fs instanceof ContiguousFA) { //if contiguous allocation
        	
            int firstBlock = 0; 
            int fileLen = 0; 

            firstBlock = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileFirstBlock(fileName);
            fileLen = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileLen(fileName);

            for (int i = firstBlock; i < (firstBlock + fileLen); i++) { 
                fs.displayBlock(i);
                System.out.println();
            }
        }
        
        else if (fs instanceof ChainedFA) { // if chained allocation
            int firstBlock = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileFirstBlock(fileName); 
            int fileLen = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileLen(fileName); 
            
            int nextBlock = firstBlock;

            for (int i = firstBlock; i < (firstBlock + fileLen); i++) {
                if (nextBlock != -1) {
                    ((ChainedDataBlock)fs.getBlock(nextBlock)).displayCI();
                    nextBlock = ((ChainedDataBlock)fs.getBlock(nextBlock)).nextBlock;
                }
                else 
                    break;
            }
        }
        
        else { // indexed allocation
            IndexedFileTable indexedBlockFileTable = ((IndexedFileTable)fs.getBlock(0));

            int indexedBlockNum = indexedBlockFileTable.getIndexedFileBlock(fileName);

            IndexedBlock indexedBlock = ((IndexedBlock)fs.getBlock(indexedBlockNum)); 
            int [] blockList = indexedBlock.getIndexBlocks(); 

            ContiguousIndexedDataBlock contiguousIndexedDataBlock;
            for (int i = 0; i < blockList.length; i++) { 
                int blockNum = blockList[i];
                contiguousIndexedDataBlock = ((ContiguousIndexedDataBlock)fs.getBlock(blockNum));

                contiguousIndexedDataBlock.displayCI();

                System.out.println();
            }
        }
    }
    
    public static void copyToRealSystem(String diskFile, String realSysFile) throws Exception, IOException { //function to copy to real system
    	
    	byte [] fileData = null;
        
    	if (fs instanceof ContiguousFA) { 
            
    		int fileLen = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileLen(diskFile);
            int firstBlock = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileFirstBlock(diskFile);

            fileData = new byte[(512 * fileLen)];

            int c = 0;
            for (int i = firstBlock; i < (firstBlock + fileLen); i++) { 
                
            	if (c == fileLen)
                    break;

                byte [] arr = ((ContiguousIndexedDataBlock)fs.getBlock(i)).data;
                System.arraycopy(arr, 0, fileData, c * 512, 512);
                c++;
            }

    	}
    	
        else if (fs instanceof ChainedFA) {//chained allocation  
        	
        	int fileLen = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileLen(diskFile); 
            int firstBlock = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileFirstBlock(diskFile); 
            int nextBlock = firstBlock;

            fileData = new byte[(508 * fileLen)];

            int c = 0;
            for (int i = firstBlock; i < (firstBlock + fileLen); i++) {  
            	
                if (nextBlock != -1) {
                    byte [] array = ((ChainedDataBlock)fs.getBlock(nextBlock)).data;
                    System.arraycopy(array, 0, fileData, c * 508, 508);
                    nextBlock = ((ChainedDataBlock)fs.getBlock(nextBlock)).nextBlock;
                    c++;
                }
                else 
                    break;   
            }
        }
    	
        else { //indexed allocation
        	
        	IndexedFileTable indexedFileTable = ((IndexedFileTable)fs.getBlock(0));
            int indexedBlockNumber = indexedFileTable.getIndexedFileBlock(diskFile);

            IndexedBlock indexedBlock = ((IndexedBlock)fs.getBlock(indexedBlockNumber));
            int [] blockList = indexedBlock.getIndexBlocks();
            int length = blockList.length;

            fileData = new byte[(512 * length)];

            int c = 0;
            for (int i = 0; i < length; i++) { 
                int blockNum = blockList[i];
                if (c == length)
                    break;
                byte [] array = ((ContiguousIndexedDataBlock)fs.getBlock(blockNum)).data;
                System.arraycopy(array, 0, fileData, c * 512, 512);
                c++;
            }

        }
    	
        File file = new File(realSysFile);
        FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
        try {
            fos.write(fileData);
        } 
        finally {
            fos.close();
        }
    	       
    }

    static void deleteFile(String fileName) {//function for delete file
        if (fs instanceof ContiguousFA) { 
        	
        	int fileLen = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileLen(fileName); 
            int firstBlock = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileFirstBlock(fileName); 

            for (int i = firstBlock; i < (firstBlock + fileLen); i++) { 
                ((ContiguousIndexedDataBlock)fs.getBlock(i)).deleteCIBlock();
                ((Bitmap)fs.getBlock(1)).bitmapDeleteBlock(i); 
            }

            ((ContiguousChainedFileTable)fs.getBlock(0)).deleteFileEntry(fileName); 
        }
        else if (fs instanceof ChainedFA) { 
            int fileLen = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileLen(fileName); 
            int firstBlock = ((ContiguousChainedFileTable)fs.getBlock(0)).getFileFirstBlock(fileName); 
            int nextBlock = firstBlock;
            
            int tempBlock;
            for (int i = firstBlock; i < (firstBlock + fileLen); i++) { 
                if (nextBlock != -1) {
                    tempBlock = ((ChainedDataBlock)fs.getBlock(nextBlock)).nextBlock;
                    ((ChainedDataBlock)fs.getBlock(nextBlock)).deleteBlock();
                    ((Bitmap)fs.getBlock(1)).bitmapDeleteBlock(nextBlock);
                    nextBlock = tempBlock;
                }
                else 
                    break;  
            }

            ((ContiguousChainedFileTable)fs.getBlock(0)).deleteFileEntry(fileName); 

        }
        
        else { 
            
            IndexedFileTable indexedFileTable = ((IndexedFileTable)fs.getBlock(0));
            int indexedBlockNum = indexedFileTable.getIndexedFileBlock(fileName);

            
            IndexedBlock indexedBlock = ((IndexedBlock)fs.getBlock(indexedBlockNum));
            int [] blockList = indexedBlock.getIndexBlocks();

            indexedBlock.deleteIndexedBlock(); 
            ((Bitmap)fs.getBlock(1)).bitmapDeleteBlock(indexedBlockNum); 

            ContiguousIndexedDataBlock contiguousIndexedDataBlock;
            for (int i = 0; i < blockList.length; i++) { 
                int blockNum = blockList[i];
                contiguousIndexedDataBlock = ((ContiguousIndexedDataBlock) fs.getBlock(blockNum));
                contiguousIndexedDataBlock.deleteCIBlock();
                ((Bitmap)fs.getBlock(1)).bitmapDeleteBlock(blockNum);
            }

            ((IndexedFileTable)fs.getBlock(0)).deleteFileTableEntry(fileName); 

        }
    }

}