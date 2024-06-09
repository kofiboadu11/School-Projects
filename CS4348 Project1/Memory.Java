import java.io.File;
import java.io.FileNotFoundExecption;
import java.util.Scanner;
public class Memory{
    final static int memory=new int[2000];
    public static void main(String args[]){
        try{
            Scanner CPUread=new Scanner(System.in);
            File file=null;
            if(CPUread.hasNextLine()){
                file=new File(CPUread.nextLine());
                if(!file.exists())
                {
                    System.out.println("File not Found")
                    System.exit(0);
                }
            }
            readFile(file);
            
        }
    }
}