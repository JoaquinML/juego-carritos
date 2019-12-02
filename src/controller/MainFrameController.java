/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.event.KeyEvent;
import model.Board;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Random;
import view.MainFrame;


/**
 *
 * @author
 */
public class MainFrameController implements KeyListener{
    private MainFrame mf;
    
    //Sobreescribe el método to
    private Board board = new Board();
    
    public MainFrameController(MainFrame mf){
        this.mf = mf;
    }
    
    public void startGame(){
        //Dibuja el primer frame del juego
        drawInitFrame(board);
    }
    
    //Método para mover el carrito del jugador a la derecha
    public void moveToRight(Board board){
        //Obtiene la posición actual del carrito del jugador
        int x = board.getCartIndex();
        
        //Si ya está en el borde del tablero, no hace nada (no se puede mover más)
        if (!(x<2)) return;
        
        //Si no, aumenta en 1 (lo mueve a la derecha del arreglo)
        int newCartIndex = x + 1;
        
        //Vacía la posición actual, y lo dibuja en la nueva
        board.getBoard()[5][x] = " ";
        board.getBoard()[5][newCartIndex] = "|";
        
        //Guarda el nuevo estado de la posición del carrito del jugador
        board.setCartIndex(newCartIndex);
    }
    
    //Método para mover el carrito del jugador a la izquierda
    //Hace lo que el método anterior, pero del otro lado
    public void moveToLeft(Board board){
        int x = board.getCartIndex();
        
        if (!(x>0)) return;
        
        int newCartIndex = x - 1;
        
        board.getBoard()[5][x] = " ";
        board.getBoard()[5][newCartIndex] = "|";
        
        board.setCartIndex(newCartIndex);
    }
    
    //Inicia el tablero
    public void drawInitFrame(Board board){
        //Obtiene la instancia del tablero
        String[][] newBoard = board.getBoard();
        
        //Para cada una de las casillas...
        for (int i = 0; i<6; i++){
            for (int j = 0; j < 3; j++){
                //Si es el lugar del jugador, lo pone en medio
                if (i == 5 && j == 1) {
                    newBoard[i][j] = "|";
                    board.setCartIndex(j);
                    continue;
                }
                
                //Si no, pone una ficha vacía que se va a llenar luego
                newBoard[i][j] = " ";
            }
        }
        //Imprime el tablero en consola
        System.out.println(board);
    }
    
    //Método para ver si termina el juego
    private boolean checkIfCollition(){
        //Si cuando cambia el frame, el carrito de enfrente está en la misma posición
        //que el del jugador, termina el juego (collition = sí hay)
        if (board.getCartIndex() == Arrays.asList(board.getBoard()[4]).indexOf("|")) return true;
        
        //Si no, sigue el juego
        return false;
    }
    
    //Cada frame que pasa, se debe refrescar la vista
    private void reprintBoard(Board board){
        //Por cada una de las filas del juego...
        for (int i = 0; i< board.getBoard().length; i++){
            //Obtiene la posición del carrito enemigo
            int position = Arrays.asList(board.getBoard()[i]).indexOf("|");
            //Pinta las posibles opciones listadas abajo
            paintRowWhenCarInCoord(i, position);
        }
        
        //Imprime el estado del tablero en consola
        System.out.println(board);
    }
    
    //Método para dibujar un nuevo carrito aproximandose, y borrando el que ya pasó
    public void drawNextFrame(){
        //Si hay una colisión antes de cambiar de frame, se termina el juego
        //La excepción se captura en el thread de GameFlow, haciendo que termine
        //la ejecución del programa
        if (checkIfCollition()) throw new RuntimeException("Perdiste!");
        
        //Si no hubo colisión...
        //Crea una fila vacía
        String[] newLine = new String[]{" ", " ", " "};
        //Pone un carrito en una posición aleatoria (del 1 al 3)
        newLine[new Random().nextInt(3)] = "|";
        
        //Linea auxiliar para intercambiar lugares más adelante
        String[] auxLine = null;
        
        //Por cada una de las líneas...
        for (int i = 0; i < 5; i++){
            //Si es la primera línea, agrega la línea que se acaba de crear
            //(El carrito que llega)
            //Adicionalmente, guarda la línea que está sustituyendo (es la línea 
            //que va a avanzar)
            if (i == 0) {
                //Clona las líneas para que no afecte a las originales
                auxLine = board.getBoard()[i].clone();
                board.getBoard()[i] = newLine;
                newLine = auxLine.clone();
                continue;
            }
            
            //Si es la penúltima linea de carritos, avanza la línea al final
            //y termina el ciclo, para no mover la línea del jugador
            if (i == 4){
                auxLine = board.getBoard()[i].clone();
                board.getBoard()[i] = newLine;
                newLine = auxLine.clone();
                break;
            }
            
            //Si es cualquiera de las líneas de enmedio, sólo avanza la línea
            auxLine = board.getBoard()[i].clone();
            board.getBoard()[i] = newLine;
            newLine = auxLine.clone();
            
        }
        
        //Cuando termina de crear un nuevo estado del tablero, lo pinta de nuevo
        reprintBoard(board);
    }

    //Método para escuchar las teclas que presiona el jugador
    @Override
    public void keyPressed(KeyEvent event) {
        //Si la tecla es la flecha a la derecha (su código es 39) ejecuta
        //la acción para moverse a la derecha (se puede cambiar por cualquier tecla, incluso varias)
        if (event.getKeyCode() == 39) moveToRight(board);
        //Lo mismo que el de arriba, pero a la izquierda
        if (event.getKeyCode() == 37) moveToLeft(board);
        
        //Ya que se movió la posición del carrito del jugador, hay que repintar
        //el nuevo estado del tablero
        reprintBoard(board);
    }
    
    //No hacen nada pero los pide la interfaz
    @Override
    public void keyReleased(KeyEvent event) {

    }

    @Override
    public void keyTyped(KeyEvent event) {

    }
    
    //Método que guarda los posibles estados del tablero (i=fila, j=posición del tablero)
    //Recibe la fila que entra, y la posición del carrito (amigo o enemigo)
    //Para pintar las filas, se hace 1 por 1
    //Los carritos existen en todo momento, simplemente se hacen visibles o no.
    private void paintRowWhenCarInCoord(int i, int j){
        if (i == 0) {
            //Si el carrito está en la primera posición, oculta los otros dos carritos de la fila
            //Lo mismo ocurre con los métodos siguientes
            //El que tiene -1, si no hay carrito en ninguna posición, oculta todos
            if(j == 0){
                mf.getCar_0_0().setVisible(true);
                mf.getCar_0_1().setVisible(false);
                mf.getCar_0_2().setVisible(false);
            }
            
            if(j == 1){
                mf.getCar_0_0().setVisible(false);
                mf.getCar_0_1().setVisible(true);
                mf.getCar_0_2().setVisible(false);
            }
            
            if(j == 2){
                mf.getCar_0_0().setVisible(false);
                mf.getCar_0_1().setVisible(false);
                mf.getCar_0_2().setVisible(true);
            }
            
            if(j == -1){
                mf.getCar_0_0().setVisible(false);
                mf.getCar_0_1().setVisible(false);
                mf.getCar_0_2().setVisible(false);
            }
           
        }
        
        if (i == 1) {
            if(j == 0){
                mf.getCar_1_0().setVisible(true);
                mf.getCar_1_1().setVisible(false);
                mf.getCar_1_2().setVisible(false);
            }
            
            if(j == 1){
                mf.getCar_1_0().setVisible(false);
                mf.getCar_1_1().setVisible(true);
                mf.getCar_1_2().setVisible(false);
            }
            
            if(j == 2){
                mf.getCar_1_0().setVisible(false);
                mf.getCar_1_1().setVisible(false);
                mf.getCar_1_2().setVisible(true);
            }
            
            if(j == -1){
                mf.getCar_1_0().setVisible(false);
                mf.getCar_1_1().setVisible(false);
                mf.getCar_1_2().setVisible(false);
            }
           
        }
        
        if (i == 2) {
            if(j == 0){
                mf.getCar_2_0().setVisible(true);
                mf.getCar_2_1().setVisible(false);
                mf.getCar_2_2().setVisible(false);
            }
            
            if(j == 1){
                mf.getCar_2_0().setVisible(false);
                mf.getCar_2_1().setVisible(true);
                mf.getCar_2_2().setVisible(false);
            }
            
            if(j == 2){
                mf.getCar_2_0().setVisible(false);
                mf.getCar_2_1().setVisible(false);
                mf.getCar_2_2().setVisible(true);
            }
            
            if(j == -1){
                mf.getCar_2_0().setVisible(false);
                mf.getCar_2_1().setVisible(false);
                mf.getCar_2_2().setVisible(false);
            }
           
        }
        
        if (i == 3) {
            if(j == 0){
                mf.getCar_3_0().setVisible(true);
                mf.getCar_3_1().setVisible(false);
                mf.getCar_3_2().setVisible(false);
            }
            
            if(j == 1){
                mf.getCar_3_0().setVisible(false);
                mf.getCar_3_1().setVisible(true);
                mf.getCar_3_2().setVisible(false);
            }
            
            if(j == 2){
                mf.getCar_3_0().setVisible(false);
                mf.getCar_3_1().setVisible(false);
                mf.getCar_3_2().setVisible(true);
            }
            
            if(j == -1){
                mf.getCar_3_0().setVisible(false);
                mf.getCar_3_1().setVisible(false);
                mf.getCar_3_2().setVisible(false);
            }
           
        }
        
        if (i == 4) {
            if(j == 0){
                mf.getCar_4_0().setVisible(true);
                mf.getCar_4_1().setVisible(false);
                mf.getCar_4_2().setVisible(false);
            }
            
            if(j == 1){
                mf.getCar_4_0().setVisible(false);
                mf.getCar_4_1().setVisible(true);
                mf.getCar_4_2().setVisible(false);
            }
            
            if(j == 2){
                mf.getCar_4_0().setVisible(false);
                mf.getCar_4_1().setVisible(false);
                mf.getCar_4_2().setVisible(true);
            }
            
            if(j == -1){
                mf.getCar_4_0().setVisible(false);
                mf.getCar_4_1().setVisible(false);
                mf.getCar_4_2().setVisible(false);
            }
           
        }
        
        if (i == 5) {
            if(j == 0){
                mf.getCar_5_0().setVisible(true);
                mf.getCar_5_1().setVisible(false);
                mf.getCar_5_2().setVisible(false);
            }
            
            if(j == 1){
                mf.getCar_5_0().setVisible(false);
                mf.getCar_5_1().setVisible(true);
                mf.getCar_5_2().setVisible(false);
            }
            
            if(j == 2){
                mf.getCar_5_0().setVisible(false);
                mf.getCar_5_1().setVisible(false);
                mf.getCar_5_2().setVisible(true);
            }
           
        }
        
        
        
    }
}
