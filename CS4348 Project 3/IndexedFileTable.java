public class IndexedFileTable extends FileTable {
    
	private class Entry { 
        String fileName;
        int indexedBlock;
        Entry(String fileName, int indexedBlock){
            this.indexedBlock = indexedBlock;
            this.fileName = fileName;
        }
    }

    private Entry[] fileTable;

    IndexedFileTable() { 
        fileTable = new Entry[14];
    }

    private boolean fileTableEmpty() { 
        for (int i = 0; i < 12; i++)
            if (fileTable[i] != null)
                return false;
        
        return true;
    }
    
    public void displayCI() { 
        if (fileTableEmpty())
            System.out.println("The File Table Is Empty.");
        
        else 
            for (int i = 0; i < 14; i++)
                if (fileTable[i] != null)
                    System.out.println(fileTable[i].fileName + "	" + fileTable[i].indexedBlock);
                
    }

    public boolean fileExists(String fileName) { 
        for (int i = 0; i < 12; i++)
            if (fileTable[i] != null && fileTable[i].fileName.equals(fileName))
                return true;
        return false;
    }

    public boolean fileTableFull() { 
        for (int i = 0; i < 12; i++)
            if (fileTable[i] == null)
                return false;
        
        return true;
    }

    public void addEntryToFileTable(String fileName, int indexedBlock) {
        for (int i = 0; i < 12; i++)
            if (fileTable[i] == null) {
                fileTable[i] = new Entry(fileName, indexedBlock);
                break;
            }
    }

    public int getIndexedFileBlock(String fileName) { 
        for (int i = 0; i < 12; i++) 
            if (fileTable[i] != null && fileTable[i].fileName.equals(fileName))
                return fileTable[i].indexedBlock;
        
        return 0;
    }

    public void deleteFileTableEntry(String fileName) { 
        for (int i = 0; i < 12; i++)
            if (fileTable[i].fileName.equals(fileName)) {
                fileTable[i] = null;
                break;
            }
    }
}