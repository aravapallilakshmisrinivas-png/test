import service.ProcessingOrchestrator;
import model.ESGHolding;
import model.DataQualityReport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;
import java.util.List;

public class ESGProcessingDemoSimple {
    private static final ProcessingOrchestrator orchestrator = new ProcessingOrchestrator();

    public static void main(String[] args) throws IOException {
        System.out.println("🚀 Starting ESG Processing Demo (Simple)...");
        
        int port = findAvailablePort();
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        
        server.createContext("/api/process", new ProcessHandler());
        server.createContext("/", new HomeHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("✅ Demo started on port " + port);
        System.out.println("🌐 Open: http://localhost:" + port);
    }
    
    private static int findAvailablePort() {
        for (int port = 8080; port <= 8090; port++) {
            try (ServerSocket socket = new ServerSocket(port)) {
                return port;
            } catch (IOException e) {
                // Port is in use, try next
            }
        }
        return 8080; // Fallback
    }

    static class ProcessHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            ProcessingOrchestrator.ProcessingResult result = orchestrator.processSampleData();
            
            StringBuilder json = new StringBuilder();
            json.append("{\"success\":").append(result.isSuccess());
            json.append(",\"message\":\"").append(result.getMessage()).append("\"");
            json.append(",\"holdingsCount\":").append(result.getHoldings().size());
            json.append("}");
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.length());
            
            OutputStream os = exchange.getResponseBody();
            os.write(json.toString().getBytes());
            os.close();
        }
    }

    static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = "<!DOCTYPE html><html><head><title>ESG Demo</title>" +
                "<style>body{font-family:Arial;margin:40px;text-align:center;}" +
                "button{padding:15px 30px;font-size:18px;background:#3498db;color:white;border:none;border-radius:5px;cursor:pointer;margin:10px;}" +
                "button:hover{background:#2980b9;}" +
                "#result{margin:20px;padding:20px;background:#f8f9fa;border-radius:5px;}</style></head>" +
                "<body><h1>🌱 ESG Processing Demo</h1>" +
                "<button onclick='processData()'>📊 Process ESG Data</button>" +
                "<div id='result'></div>" +
                "<script>" +
                "function processData(){" +
                "document.getElementById('result').innerHTML='Processing...';" +
                "fetch('/api/process',{method:'POST'})" +
                ".then(r=>r.json())" +
                ".then(data=>{" +
                "document.getElementById('result').innerHTML=" +
                "'<h3>✅ Processing Complete</h3><p>'+data.message+'</p><p>Holdings processed: '+data.holdingsCount+'</p>';" +
                "})" +
                ".catch(e=>{document.getElementById('result').innerHTML='❌ Error: '+e.message;});" +
                "}" +
                "</script></body></html>";
            
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, html.length());
            
            OutputStream os = exchange.getResponseBody();
            os.write(html.getBytes());
            os.close();
        }
    }
}