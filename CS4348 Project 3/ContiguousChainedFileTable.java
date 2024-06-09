public class ContiguousChainedFileTable extends FileTable {
	
    private class Entry { 
        String fileName;
        int startBlock;
        int length;

        Entry(String fileName, int startBlock, int length){
            this.fileName = fileName;
            this.startBlock = startBlock;
            this.length = length;
        }
    }

    private Entry[] fileTable;

    ContiguousChainedFileTable() { 
        fileTable = new Entry[12];
    }

    public void displayCI() {  
        if (fileTableEmpty())
            System.out.println("The file table is empty.");
        else 
            for (int i = 0; i < 12; i++) 
                if (fileTable[i] != null)
                    System.out.println(fileTable[i].fileName + "	" + fileTable[i].startBlock + "	" + fileTable[i].length);
    }
    
    public void deleteFileEntry(String fileName) { 
        for (int i = 0; i < 12; i++) 
            if (fileTable[i].fileName.equals(fileName)) {
                fileTable[i] = null;
                break;
            }
    }

    
    private boolean fileTableEmpty() { 
        for (int i = 0; i < 12; i++)
            if (fileTable[i] != null)
                return false; 
        return true;
    }

    public boolean fileTableFull() { 
        for (int i = 0; i < 12; i++)
            if (fileTable[i] == null) 
                return false;
        return true;
    }

    public void addEntryToFileTable(String fName, int firstBlock, int len) { 
        for (int i = 0; i < 12; i++)
            if (fileTable[i] == null) {
                fileTable[i] = new Entry(fName,firstBlock, len);
                break;
            }
    }

    public int getFileFirstBlock(String fileName) { 
        for (int i = 0; i < 12; i++)
            if (fileTable[i] != null && fileTable[i].fileName.equals(fileName))
                return fileTable[i].startBlock;    
        return 0;
    }

    public int getFileLen(String fileName) { 
        for (int i = 0; i < 12; i++)
            if (fileTable[i] != null && fileTable[i].fileName.equals(fileName))
                return fileTable[i].length;
        return 0;
    }
    
    public boolean fileExists(String fileName) { 
        for (int i = 0; i < 12; i++) 
            if (fileTable[i] != null && fileTable[i].fileName.equals(fileName))
                return true;
        return false;
    }

}