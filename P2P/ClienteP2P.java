import java.io.*;
import java.net.*;
import java.util.*;

public class ClienteP2P {
    static boolean connected1 = false;
    static boolean connected2 = false;
    static boolean myTurn = false;

    static char[][] board = {
        {'*', '*', '*'},
        {'*', '*', '*'},
        {'*', '*', '*'}
    };

    static int myPort;
    static int otherPort;
    static String otherIp;

    public static void main(String[] args) {
        System.out.println("Cliente P2P iniciado...");
        System.out.println("------------------------------------------------");

        // --- Parseo de Argumentos ---
        if (args.length == 0) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Introduce la IP del otro cliente: ");
            otherIp = sc.nextLine();
            System.out.print("Introduce tu puerto: ");
            myPort = Integer.parseInt(sc.nextLine());
            System.out.print("Introduce el puerto del otro cliente: ");
            otherPort = Integer.parseInt(sc.nextLine());
        } else if (args.length != 3) {
            System.out.println("Parametros incorrectos.");
            return;
        } else {
            otherIp = args[0];
            myPort = Integer.parseInt(args[1]);
            otherPort = Integer.parseInt(args[2]);
        }

        System.out.println("------------ CONECTANDO ------------");

        // Intento de conexión P2P
        try {
            // --- 1. Iniciar ServerSocket en el Cliente 2 (para escuchar) ---
            ServerSocket serverSocket = new ServerSocket(myPort);
            System.out.println("Cliente 2 esperando conexión en el puerto " + myPort + "...");
            Socket socketFromOther = serverSocket.accept(); // Espera la conexión

            connected2 = true;
            System.out.println("Conexión recibida de " + socketFromOther.getRemoteSocketAddress());

            // --- 2. Intento de Conexión Saliente desde Cliente 1 ---
            Socket socketToOther = new Socket(otherIp, otherPort);
            connected1 = true;
            System.out.println("Conexión establecida con " + socketToOther.getRemoteSocketAddress());

            // --- 3. Configuración de los flujos de entrada y salida para ambos clientes ---
            BufferedReader inFromOther = new BufferedReader(new InputStreamReader(socketFromOther.getInputStream()));
            PrintWriter outToOther = new PrintWriter(socketToOther.getOutputStream(), true);

            // --- 4. Lógica para iniciar el juego y alternar turnos ---
            if (myTurn) {
                playGame(true, inFromOther, outToOther); // Empieza el cliente que se conecta primero
            } else {
                playGame(false, inFromOther, outToOther); // El otro cliente empieza
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- Función para jugar la partida ---
    private static void playGame(boolean isMyTurn, BufferedReader inFromOther, PrintWriter outToOther) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            if (isMyTurn) {
                System.out.println("Es tu turno, introduce una fila y columna (0-2): ");
                String guess = sc.nextLine();
                outToOther.println("GUESS:" + guess); // Enviar mi jugada
            }

            // Recibir respuesta del oponente
            try {
                String response = inFromOther.readLine();
                if (response == null) break;
                System.out.println("Respuesta del oponente: " + response);

                // Procesar respuesta
                if (response.startsWith("RESULT:WIN_FOR_ME")) {
                    System.out.println("¡Has ganado!");
                    break;
                } else if (response.startsWith("RESULT:HIT")) {
                    System.out.println("Acierto");
                } else if (response.startsWith("RESULT:MISS")) {
                    System.out.println("Fallaste");
                    isMyTurn = !isMyTurn; // Cambiar turno
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}