package com.ifs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * A classe ServidorSocketUDP é o servidor que recebe pacotes com mensagens do
 * tipo String a partir do protocolo de comunicação UDP. Após receber uma
 * mensagem o servidor envia para os clientes na conexão, que não sejam o mesmo
 * que enviou a mensagem.
 */
public class ServidorSocketUDP {
    public static void main(String[] args) throws SocketException {
        final int portaServidor = 9198;
        final DatagramSocket socketServidor = new DatagramSocket(portaServidor);
        List<ClienteCon> clientesConectados = new ArrayList<ClienteCon>(); // Lista clientes conectados ao servidor
        System.out.println("Servidor em execução...");

        // Thread principal que fica ouvindo os pacotes enviados para o endereço IP
        // 18.228.151.213 na porta 9090
        while (true) {
            byte[] bufMensagemRecebida = new byte[1024];

            DatagramPacket pacoteRecebido = new DatagramPacket(bufMensagemRecebida, bufMensagemRecebida.length);

            try {
                socketServidor.receive(pacoteRecebido); // Método que fica aguardando o recebimento dos pacotes
            } catch (IOException pacoteRecebidoIoException) {
                pacoteRecebidoIoException.printStackTrace();
            }

            System.out.println(
                    "\nMensagem recebida de " + pacoteRecebido.getAddress() + "..." + pacoteRecebido.getPort());

            /**
             * Sempre que um pacote é recebido é criada uma instância de clienteCon
             */
            ClienteCon clienteCon = new ClienteCon();

            boolean estah = false; // Flag

            /**
             * A lista de clientes conectados é percorrida e caso haja um cliente na lista
             * cujo IP seja igual ao do pacote recebido, o qual foi atribuído a uma nova
             * instância de ClienteCon, a flag "estah" é atribuído como true e o clienteCon
             * passa a guardar a referência ao objeto cliente (do tipo ClienteCon) da lista
             * de clientes conectados.
             */
            for (ClienteCon cliente : clientesConectados) {
                if (cliente.getIp().equals(pacoteRecebido.getAddress())) {
                    estah = true;
                    clienteCon = cliente;
                    break;
                }
            }

            String mensagemRecebida = converterByteParaString(pacoteRecebido);

            if (estah) { // O cliente já está na conexão
                if (mensagemRecebida.equalsIgnoreCase("sair")) { // O cliente é removido da lista caso digite "sair"
                    if (clientesConectados.contains(clienteCon)) {
                        clientesConectados.remove(clienteCon);
                        System.out.println("\n" + clienteCon.getIp() + " saiu...");
                    }
                } else {
                    if (clientesConectados.size() == 1) { // Caso só haja um cliente o servidor responde o cliente
                        enviarPacote(socketServidor, clienteCon,
                                "Mensagem recebida de | Servidor: Não há ninguém no chat...");
                    } else { // Caso não é enviada uma mensagem aos demais cliente na conexão
                        enviarParaTodos(socketServidor, clienteCon, mensagemRecebida, clientesConectados);
                    }
                }
            } else { // O cliente não está na lista de clientes conectados
                clienteCon.setIp(pacoteRecebido.getAddress());
                clienteCon.setPorta(pacoteRecebido.getPort());
                clienteCon.setNomeCliente(mensagemRecebida);
                clientesConectados.add(clienteCon);

                /**
                 * São enviados dois pacotes ao cliente 1°: Envia o endereço IP que o cliente
                 * aguarda 2°: Envia uma mensagem informando que a conexão foi bem sucedida
                 */
                enviarPacote(socketServidor, clienteCon, clienteCon.getIp().toString());
                enviarPacote(socketServidor, clienteCon, "Conectado...");
                System.out.println("\n" + clienteCon.getIp() + " entrou no chat...");
            }
        }
    }

    /**
     * 
     * @param pacoteRecebido do cliente
     * @return mensagem contida no pacote já convertida para String
     */
    public static String converterByteParaString(DatagramPacket pacoteRecebido) {
        String mensagem = new String(pacoteRecebido.getData(), pacoteRecebido.getOffset(), pacoteRecebido.getLength());

        return mensagem;
    }

    /**
     * Monta uma mensagem no formato String e aciona o método enviarPacote() para cada cliente na conexão
     * que não seja o que enviou a mensagem.
     * @param socketServidor
     * @param clienteOrigem
     * @param mensagemRecebida
     * @param clientesConectados
     */
    public static void enviarParaTodos(DatagramSocket socketServidor, ClienteCon clienteOrigem, String mensagemRecebida,
            List<ClienteCon> clientesConectados) {

        String mensagemAEnviar = "Mensagem recebida de " + clienteOrigem.getIp() + " | "
                + clienteOrigem.getNomeCliente() + ": " + mensagemRecebida;

        for (ClienteCon cliente : clientesConectados) {
            if (!(cliente.equals(clienteOrigem))) {
                enviarPacote(socketServidor, cliente, mensagemAEnviar);
            }
        }
    }

    /**
     * Método responsável por converter a mensagem formatada pelo servidor para bytes e enviar para um
     * cliente na conexão a partir dos parâmetros recebidos na chamada do método.
     * @param socketServidor
     * @param cliente
     * @param mensagem
     */
    public static void enviarPacote(DatagramSocket socketServidor, ClienteCon cliente, String mensagem) {
        DatagramPacket pacoteParaEnviar = new DatagramPacket(mensagem.getBytes(), mensagem.getBytes().length,
                cliente.getIp(), cliente.getPorta());

        try {
            socketServidor.send(pacoteParaEnviar);
            System.out.println("Mensagem enviada para " + cliente.getIp() + "..." + cliente.getPorta());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
