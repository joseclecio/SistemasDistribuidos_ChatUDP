package com.ifs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

public class ClienteSocketUDP {
    public static void main(String[] args) throws SocketException, UnknownHostException {

        //utilização do DatagramSocket para enviar e receber pacotes
        final DatagramSocket socketCliente = new DatagramSocket();

        // Obtem o endereco do ip do servidor, associando ao
        // objeto "ipServidor". Observe que esse objeto nao é
        // criado por um construtor, mas sim retornado por
        // um metodo estatico da classe.
        final InetAddress ipServidor = InetAddress.getByName("18.228.151.213");

        //aqui srá a porta na qual o servidor está conectado
        final int portaServidor = 9198;

        //declaração das strings do nome do cliete e o ip do cliente que no caso sou eu
        final String nomeCliente;
        final String ipCliente;

        //aqui tem uma janela para digitação do meu nome de usuário
        nomeCliente = JOptionPane.showInputDialog("Insira o nome de usuário:");

        //tratamento caso na janela o usuario clicar em 'cancelar' retornará um null, com a menssagem 'saiu' e logo
        //a janela será fechada
        if (nomeCliente == null){

            System.out.println("saiu");
            System.exit(0);

        }

        //aqui será invocado o método enviarMensagem capturando o socketCliente onde será o envio e recebimento de pacotes,
        //o ipServidor que pega o ip do servidor, a portaServidor onde está alocada a porta do servidor e o nomeCliente que
        //será o nome digitado na janela JOptionPane. Logo em seguida envia a mensagem para oa servidor e depois para os outros
        //clientes que estejam conectados
        enviarMensagem(socketCliente, ipServidor, portaServidor, nomeCliente);

        //o ipCliente irá receber a mensagem enviada pelo servidor
        ipCliente = receberMensagem(socketCliente);


        //Thread criada com um loop, onde será aberta outra janela JOptionPane para a escrita de mensagem para os outros clientes conectados,
        //contendo o ipCliente que será o ip do cliente e o nomeCliente que será o nome do cliente.
        //logo abaixo será invocado o método enviarMensagem capturando o socketCliente onde será o envio e recebimento de pacotes,
        //o ipServidor que pega o ip do servidor, a portaServidor onde está alocada a porta do servidor e a mensagemParaServidor, que será o envio
        //da mensagem digitada na janela JOptionPane para oa servidor
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    String mensagemParaServidor = JOptionPane.showInputDialog(ipCliente + " | " + nomeCliente + ": ");

                    enviarMensagem(socketCliente, ipServidor, portaServidor, mensagemParaServidor);

                }
            }
        }.start();

        //aqui terá um loop para receber todas as mensagens enviadas do outro cliente
        while (true) {
            System.out.println(receberMensagem(socketCliente));
        }
    }

    //método booleano enviarMensagem, onde tem a caracteristica de enviar todas as mensagens para o servidor
    public static boolean enviarMensagem(DatagramSocket socketCliente, InetAddress ipServidor, int portaServidor,
            String mensagemParaServidor) {

        //no if abaixo caso a mensagem para o servidor for null (quando clicar em 'cancelar' na janela JOptionPane), o cliente sairá do servidor
        if (mensagemParaServidor == null) {
            mensagemParaServidor = "sair";
        }

        //envio de mensagens para o servidor através do DatagramPacket, contendo a menságem, o tamanho dela, o ip do servidor e a porta do servidor
        DatagramPacket pacoteParaServidor = new DatagramPacket(mensagemParaServidor.getBytes(),
                mensagemParaServidor.length(), ipServidor, portaServidor);

        //tratamento de exceção caso ocorrer falha durante o envio do pacote para o servidor
        try {
            socketCliente.send(pacoteParaServidor);
        } catch (IOException pacoteParaServidorIoException) {
            System.out.println("Falha ao enviar pacote...");
            pacoteParaServidorIoException.printStackTrace();
        }

        //caso a mensagem para o servidor for 'sair' obtida como null pela janela JOptionPane, o cliente sairá do servidor
        if (mensagemParaServidor.equalsIgnoreCase("sair")){
            System.exit(0);
        }

        return true;
    }

    //método para o recebimento de mensagem do servidor
    public static String receberMensagem(DatagramSocket socketCliente) {

        //tamanho dos caracteres de mensagem
        byte[] bufMensagemParaReceber = new byte[1024];
        DatagramPacket pacoteParaReceber;
        String mensagemParaReceber;

        //pacoteParaReceber irá receber mensagem do servidor com seu respectivo tamanho
        pacoteParaReceber = new DatagramPacket(bufMensagemParaReceber, bufMensagemParaReceber.length);

        //tratamento de exceção caso ocorrer falha durante o recebimento do pacote para o servidor
        try {
            socketCliente.receive(pacoteParaReceber);
        } catch (IOException pacoteRecebiIoException) {
            System.out.println("Falha no recebimento do pacote...");
            pacoteRecebiIoException.printStackTrace();
        }

        //aqui irá receber uma instancia contendo todas as especificações do pacoteParaReceber com os dados da mensagem, o horario que foi enviado para
        //o servidor e o tamanho da mesma.
        mensagemParaReceber = new String(pacoteParaReceber.getData(), pacoteParaReceber.getOffset(),
                pacoteParaReceber.getLength());

        //retorna a mensagem recebida pelo servidor
        return mensagemParaReceber;
    }
}
