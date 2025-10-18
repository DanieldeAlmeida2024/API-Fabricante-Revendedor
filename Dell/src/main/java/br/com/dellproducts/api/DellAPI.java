package br.com.dellproducts.api;


public class DellAPI {
    private String host;
    private int porta;
    private int id;
    private final String contextPath = "/ProdutosAPI/";

    public DellAPI(){
        this.host = "http://localhost:";
        this.porta = 8081;
    }

    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContextPath() {
        return contextPath;
    }
}
