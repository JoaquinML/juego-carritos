/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author
 */
public class Board {
    //Guarda el estado gblobal del tablero
    private String[][] board;
    
    //Guarda la posición actual del carro verde (jugador)
    private int cartIndex;
    
    public Board(){
        //Declara un arreglo del tamaño del tablero que se quiere usar
        this.board = new String[6][3];
        this.cartIndex = 1;
    }

    public String[][] getBoard() {
        return board;
    }

    public void setBoard(String[][] board) {
        this.board = board;
    }

    public int getCartIndex() {
        return cartIndex;
    }

    public void setCartIndex(int cartIndex) {
        this.cartIndex = cartIndex;
    }
    
    @Override
        public String toString(){
            String[][] aux = this.getBoard();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i<6; i++){
                for (int j = 0; j < 3; j++){
                    sb.append(aux[i][j]).append(" ");
                }
                sb.append("\n");
                
            }
            sb.append("------------------------");
            sb.append("\n");
            
            return sb.toString();
        }
}
