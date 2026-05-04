import java.io.*;
import java.net.*;

public class Servidor {
    public static void main(String[] args) {
        try {
            // Crear servidor en el puerto 5000
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Servidor iniciado, esperando conexiones...");

            // Esperar las conexiones de los dos clientes
            Socket client1 = serverSocket.accept();
            System.out.println("Cliente 1 conectado");
            Socket client2 = serverSocket.accept();
            System.out.println("Cliente 2 conectado");

            // Crear los streams para enviar/recibir datos
            BufferedReader inClient1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
            PrintWriter outClient1 = new PrintWriter(client1.getOutputStream(), true);
            BufferedReader inClient2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
            PrintWriter outClient2 = new PrintWriter(client2.getOutputStream(), true);

            // Inicializar el tablero
            char[][] board = {
                {'*', '*', '*'},
                {'*', '*', '*'},
                {'*', '*', '*'}
            };

            // Jugar alternadamente
            int turn = 1; // 1 para el primer jugador, 2 para el segundo
            while (true) {
                // Mostrar tablero a ambos jugadores
                outClient1.println("Tablero:\n" + printBoard(board));
                outClient2.println("Tablero:\n" + printBoard(board));

                // Solicitar jugada al jugador correspondiente
                if (turn == 1) {
                    outClient1.println("Es tu turno. Introduce fila (0-2) y columna (0-2) separados por espacio.");
                    String input = inClient1.readLine();
                    String[] coordinates = input.split(" ");
                    int row = Integer.parseInt(coordinates[0]);
                    int col = Integer.parseInt(coordinates[1]);
                    board[row][col] = '1'; // Jugada del jugador 1
                } else {
                    outClient2.println("Es tu turno. Introduce fila (0-2) y columna (0-2) separados por espacio.");
                    String input = inClient2.readLine();
                    String[] coordinates = input.split(" ");
                    int row = Integer.parseInt(coordinates[0]);
                    int col = Integer.parseInt(coordinates[1]);
                    board[row][col] = '2'; // Jugada del jugador 2
                }

                // Verificar si alguien ha ganado
                if (checkWinner(board, '1')) {
                    outClient1.println("¡Ganaste!");
                    outClient2.println("Perdiste.");
                    break;
                } else if (checkWinner(board, '2')) {
                    outClient1.println("Perdiste.");
                    outClient2.println("¡Ganaste!");
                    break;
                }

                // Verificar empate
                if (isBoardFull(board)) {
                    outClient1.println("Empate.");
                    outClient2.println("Empate.");
                    break;
                }

                // Cambiar turno
                turn = (turn == 1) ? 2 : 1;
            }

            // Cerrar conexiones
            client1.close();
            client2.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Función para imprimir el tablero
    private static String printBoard(char[][] board) {
        StringBuilder sb = new StringBuilder();
        for (char[] row : board) {
            for (char cell : row) {
                sb.append(cell).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // Función para verificar si hay un ganador
    private static boolean checkWinner(char[][] board, char player) {
        // Revisar filas y columnas
        for (int i = 0; i < 3; i++) {
            if ((board[i][0] == player && board[i][1] == player && board[i][2] == player) || 
                (board[0][i] == player && board[1][i] == player && board[2][i] == player)) {
                return true;
            }
        }
        // Revisar diagonales
        if ((board[0][0] == player && board[1][1] == player && board[2][2] == player) || 
            (board[0][2] == player && board[1][1] == player && board[2][0] == player)) {
            return true;
        }
        return false;
    }

    // Función para verificar si el tablero está lleno (empate)
    private static boolean isBoardFull(char[][] board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '*') {
                    return false;
                }
            }
        }
        return true;
    }
}