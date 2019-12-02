/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juegocarritos;

import controller.MainFrameController;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.StopWatch;
import view.GameOverFrame;
import view.MainFrame;
/**
 *
 * @author 
 */
public class JuegoCarritos {
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Crea una instancia del frame principal del juego y su controlador de acciones
        MainFrame mf = new MainFrame();
        MainFrameController mfc = new MainFrameController(mf);
        
        //Liga el controlador al frame principal
        mf.addKeyListener(mfc);
        //Muestra el frame principal
        mf.setVisible(true);
        
        //Declara una instancia del Thread que controla el flujo del juego
        GameFlow gf = new GameFlow(mf, mfc);
        Thread t1 = new Thread(gf, "T1");
        
        //Empieza el juego
        mfc.startGame();
        t1.start();
        
    }
}

//Clase interna para llevar un control del score
class Timer implements Runnable{
    private volatile boolean exit = false;
    //Instancia de la clase StopWatch de la biblioteca apache commons
    private StopWatch sw = new StopWatch();
    private MainFrame mf;
    
    {
        //Inicia el contador
        sw.start();
    }
    
    public Timer(MainFrame mf){
        this.mf = mf;

    }
    
    @Override
    public void run() {
        while(!exit){
            //Actualiza el score cada segundo que pasa
            mf.getScoreLabel().setText("Score: " + sw.getTime(TimeUnit.SECONDS));
        }
    }

    //Detiene el contador, y regresa la cantidad total de tiempo que sobrevivió (score final)
    public long stop(){
        exit = true;
        sw.stop();
        return sw.getTime(TimeUnit.SECONDS);
    }
}

//Clase interna que controla el flujo del juego
class GameFlow implements Runnable{
        private volatile boolean exit = false;
        private MainFrame mf;
        private MainFrameController mfc;
        private Timer timer;
        private Thread t2;
        
        //Constructor para ligarlo al frame principal y al controlador de dicho frame
        //Adicionalmente inicia el thread para llevar el score
        public GameFlow(MainFrame mf, MainFrameController mfc){
            this.mf = mf;
            this.mfc = mfc;
            timer = new Timer(mf);
            t2 = new Thread(timer, "T2");
            t2.start();
            
        }

        @Override
        public void run() {
            while(!exit){
                try{
                    //Dibuja un nuevo frame
                    mfc.drawNextFrame();
                    //Espera 1 segundo para dibujar el siguiente
                    Thread.sleep(1000);
                }catch(RuntimeException | InterruptedException e ){
                    //Si hay una colisión, se detiene la ejecución del juego y
                    //se destruye la ventana principal y se crea la pantalla del final del juego
                    mf.dispose();
                    GameOverFrame go = new GameOverFrame();
                    go.getScoreLabel().setText("Your final score is: " + timer.stop());
                    go.setVisible(true);
                    this.stop();
                }
            }

            System.out.println("Game over....");
        }

        //Detiene el thread del juego
        public void stop(){
            exit = true;
        }
    }