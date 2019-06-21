package bo.com.skysoft.core.server;

import bo.com.skysoft.core.server.http.RootHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.concurrent.Executors;

/**
 * Created by Ricardo Laredo on 19-04-18.
 */
@Slf4j
public class Server {
    private HttpServer server = null;
    private boolean isServerDone = false;

    public Server(){
    }
    public boolean start() {
        try {
            this.server = HttpServer.create(new InetSocketAddress(8082), 0);
            //this.server.setHttpsConfigurator(new HttpsConfigurator(createSSLContext()));
            this.server.createContext("/", new RootHandler());
            log.info("Iniciando la creacion de contextos");
            this.server.setExecutor(Executors.newFixedThreadPool(2));
            this.server.start();
            log.info("Servicio iniciado ...");
        return true;
        } catch (IOException e) {
            log.error("No se logro iniciar el servicio",e);
            this.server = null;
        }
        return false;
    }
    
    public void stop(){
        this.server.stop(0);
        this.server = null;
        log.info("Servicio detenido ...");
    }

    private SSLContext createSSLContext(){
        try{
            //PasswordJKS=pr1nterMc4c0nf1gNotCh4ng3
            //Alias = mc4
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("MC4.jks"),"pr1nterMc4c0nf1gNotCh4ng3".toCharArray());

            // Create key manager
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, "pr1nterMc4c0nf1gNotCh4ng3".toCharArray());
            KeyManager[] km = keyManagerFactory.getKeyManagers();

            // Create trust manager
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);
            TrustManager[] tm = trustManagerFactory.getTrustManagers();

            // Initialize SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(km,  tm, null);

            return sslContext;
        } catch (Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    public void run(){
        SSLContext sslContext = this.createSSLContext();

        try{
            // Create server socket factory
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();

            // Create server socket
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(1914);

            System.out.println("SSL server started");
            while(!isServerDone){
                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();

                // Start the server thread
                new ServerThread(sslSocket).start();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    // Thread handling the socket from client
    static class ServerThread extends Thread {
        private SSLSocket sslSocket = null;

        ServerThread(SSLSocket sslSocket){
            this.sslSocket = sslSocket;
        }

        public void run(){
            sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());

            try{
                // Start handshake
                sslSocket.startHandshake();

                // Get session after the connection is established
                SSLSession sslSession = sslSocket.getSession();

                System.out.println("SSLSession :");
                System.out.println("\tProtocol : "+sslSession.getProtocol());
                System.out.println("\tCipher suite : "+sslSession.getCipherSuite());

                // Start handling application content
                InputStream inputStream = sslSocket.getInputStream();
                OutputStream outputStream = sslSocket.getOutputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream));

                String line = null;
                while((line = bufferedReader.readLine()) != null){
                    System.out.println("Inut : "+line);

                    if(line.trim().isEmpty()){
                        break;
                    }
                }

                // Write data
                printWriter.print("HTTP/1.1 200\r\n");
                printWriter.flush();

                sslSocket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
