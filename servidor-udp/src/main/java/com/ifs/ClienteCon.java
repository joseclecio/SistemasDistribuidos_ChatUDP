package com.ifs;

import java.net.InetAddress;

/**
 * Classe que define quais os atributos e métodos dos clientes conectados.
 */
public class ClienteCon {

    private String nomeCliente;

    private InetAddress ip;

    private int porta;

    public ClienteCon() {

    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }

    
    
}
