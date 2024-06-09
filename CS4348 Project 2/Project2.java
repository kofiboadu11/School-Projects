import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Project2 {
    public static Semaphore frontDeskSemaphore= new Semaphore(2);
    public static Semaphore bellhopSemaphore= new Semaphore(2);
    public static Semaphore[] guestSemaphore=new Semaphore[25];
    public static Semaphore guestReady=new Semaphore(0);
    public static Semaphore guestNumberLock=new Semaphore(1);
    public static Semaphore roomNumberLock=new Semaphore(1);
    public static Semaphore bellhopLock=new Semaphore(0);
    public static LinkedList<Integer> guestNumberQueue=new LinkedList<>();
    public static LinkedList<Integer> roomQueue=new LinkedList<>();
    public static Semaphore roomQueueLock=new Semaphore(1);
    public static LinkedList<Integer> bellhopQueue=new LinkedList<>();
    public static Semaphore bellhopRequested=new Semaphore(0);
    public static int roomNumber=0;
    public static void main(String[] args) {

        for(int i=0;i<25;i++){
            guestSemaphore[i]= new Semaphore(0);
        }
        System.out.println("Simulation starts");
        for(int i=0;i<2;i++){
            FrontDeskEmployee frontDesk=new FrontDeskEmployee(i);
            Thread front=new Thread(frontDesk);
            front.setDaemon(true);
            front.start();
        }

        for(int i=0;i<2;i++){
            Bellhop bellhops=new Bellhop(i);
            Thread bellhop=new Thread(bellhops);
            bellhop.setDaemon(true);
            bellhop.start();
        }
        Thread[] threadArray=new Thread[25];
        for(int i=0;i<25;i++){
            Guest guests=new Guest(i);
            Thread guest=new Thread(guests);
            guest.setDaemon(true);
            guest.start();
            threadArray[i]=guest;
        }
        for(int i=0;i<25;i++)
        {
            try{
                threadArray[i].join();
            }catch (Exception e){

            }
            
        }
        System.out.println("Simulation End");

      
    }
}

class FrontDeskEmployee implements Runnable {
    private int employeeId;

    public FrontDeskEmployee(int employeeId) {
        this.employeeId = employeeId;
        
        
    }

    @Override
    public void run() {
        System.out.println("Front desk employee " + employeeId + " created");
        while(true){
            try{
                
                Project2.guestReady.acquire();
                Project2.guestNumberLock.acquire();
                int guestNumber= Project2.guestNumberQueue.remove();
                Project2.guestNumberLock.release();
                Project2.roomNumberLock.acquire();
                int roomNumber=Project2.roomNumber;
                Project2.roomNumber+=1;
                Project2.roomNumberLock.release();
                System.out.println("Front Desk Employee "+employeeId+" registers Guest "+ guestNumber+" and assigns room "+roomNumber);
                Project2.roomQueueLock.acquire();
                Project2.roomQueue.add(roomNumber);
                Project2.guestSemaphore[guestNumber].release();
                Project2.roomQueueLock.release();
                Project2.frontDeskSemaphore.release();

            }catch(Exception e){
                e.printStackTrace();
            }
            
        }

        
    }
}

class Bellhop implements Runnable {
    private int bellhopId;
    Semaphore bellhopSemaphore = new Semaphore(1);

    public Bellhop(int bellhopId) {
        this.bellhopId = bellhopId;
        
    }

    @Override
    public void run() {
        try{
            System.out.println("Bellhop " + bellhopId + " created");
           
            
            
            

           
           
               
            
           
        }catch(Exception e){
            e.printStackTrace();
        }
        
        
    }
}

class Guest implements Runnable {
    private int guestId;
    Random random=new Random();
    int bags=random.nextInt(6);
    
    

    public Guest(int guestId) {
        this.guestId = guestId;
       
    }

    @Override
    public void run() {
        try{
                System.out.println("Guest "+guestId+" Created");
                System.out.println("Guest "+guestId+" Enters hotel with "+bags+" bags");
                Project2.frontDeskSemaphore.acquire();
                Project2.guestNumberLock.acquire();
                Project2.guestNumberQueue.add(guestId);
                Project2.guestNumberLock.release();
                Project2.guestReady.release();
                Project2.guestSemaphore[guestId].acquire();
                Project2.roomNumberLock.acquire();
                int roomNumber=Project2.roomQueue.remove();
                Project2.roomNumberLock.release();
                System.out.println("Guest "+guestId+" received keys for room "+ roomNumber);
                
                if(bags>2){
                    
                    
                    System.out.println("Guest "+guestId+" requests help with bags ");
                    Project2.bellhopQueue.add(guestId);
                    Project2.bellhopRequested.release();
                    System.out.println("Guest "+guestId+" enters room "+ roomNumber);
                    System.out.println("Guest"+guestId+"received bags from bellhop and recieved tip");
                    System.out.println("Guest "+guestId+" retires for the night");
                    
                   
                }
                else{
                        System.out.println("Guest "+guestId+" enters room "+ roomNumber);
                        System.out.println("Guest "+guestId+" retires for the night");
                }
                
                
                
                
        }catch (Exception e){
            e.printStackTrace();
        }
        
    }
}