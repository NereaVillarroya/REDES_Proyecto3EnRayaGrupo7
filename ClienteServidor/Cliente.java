import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        try {
            // Establecer conexión con el servidor
            Socket socket = new Socket("localhost", 5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner sc = new Scanner(System.in);

            // Leer mensajes del servidor
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
                if (message.contains("Es tu turno")) {
                    System.out.print("Introduce fila y columna: ");
                    String input = sc.nextLine();
                    out.println(input); // Enviar jugada al servidor
                }
            }

            // Cerrar conexión
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}