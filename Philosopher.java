import java.util.concurrent.Semaphore;
import java.util.Random;

public class Philosopher extends Thread{
//	the number of Philosopher
	final static int N = 5;
//	the number of food for each Philosopher
    private int FoodAmount = 3;
    
    private int remainedFork = N;
    private static final Random rand = new Random();    
    public static Semaphore[] fork = new Semaphore[N];
    private int id;                
    private Semaphore FirstFork; 
    private Semaphore SecondFork;
    final int foods=FoodAmount;
 
    private void waiting()
    {
        try
        {
            sleep(rand.nextInt(3000));
        } 
        catch (InterruptedException e){}
    }
    
    public Philosopher(int i, Semaphore f1, Semaphore f2)
    {
        FirstFork = f1;
        SecondFork = f2;
        id = i;
    }

    public void run()
    {
        while (FoodAmount > 0)
        {
        	System.out.printf("#%d Forks left; Philosopher %d: thinking \n", remainedFork, id);
        	waiting();
        	
        	if (remainedFork < 2){
            	System.out.printf("#%d Forks left; Philosopher %d: waiting\n", remainedFork, id);

            } else {
            	System.out.printf("#%d Forks left; Philosopher %d:trying for second fork\n", remainedFork, id);

            }
            waiting();
            try {
            	remainedFork--;
            	FirstFork = fork[remainedFork - 1];
            	FirstFork.acquire();
                remainedFork--;
                SecondFork = fork[remainedFork - 1];
                if (!SecondFork.tryAcquire()) {
                	System.out.printf("#%d Forks left; Philosopher %dfailed to peack second fork \n", remainedFork, id);
                    return;
                };
            	System.out.printf("#%d Forks left; Philosopher %d: eating food #%d \n", remainedFork, id, foods- --FoodAmount);
            	waiting();
            	System.out.printf("#%d Forks left; Philosopher %d: returns Forks \n", remainedFork, id);
                remainedFork++;
                SecondFork.release();
            } catch (InterruptedException e) {
            	System.out.printf("#%d Forks left; Philosopher %d is waiting for the forks \n", remainedFork, id);
            }
            finally {
            	remainedFork++;
            	FirstFork.release();
            }
        }
    }
    
    public static void main(String[] args)
    {
        for (int i = 0; i < N; i++) {
            fork[i] = new Semaphore(1, true);
        }
        Philosopher[] philosopher = new Philosopher[N];
        for (int m = 0; m < N; m++) {
            int mn = m + 1;
            if (mn == N) mn = 0;
            philosopher[m] = new Philosopher(m, fork[m], fork[mn]); 
        }
        for (int i = 0; i < N; i++) {
              philosopher[i].start();
        }
        for (int i = 0; i < N; i++) {
          try {
            philosopher[i].join();
          } 
          catch(InterruptedException ex) { }
        }
        System.out.println("Finish");
    }
}
