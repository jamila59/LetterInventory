import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

import com.sun.net.httpserver.*;

public class Server {
    // Port number used to connect to this server
    private static final int PORT = Integer.parseInt(System.getenv().getOrDefault("PORT", "8000"));
    // Maximum number of matches returned in response
    private static final int MAX_MATCHES = 10;
    // JSON endpoint structure
    private static final String QUERY_TEMPLATE = "{\"items\":[%s]}";

    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length == 0) {
            throw new IllegalArgumentException("java Server [files]");
        }
        Map<LetterInventory, Set<String>> anagrams = new HashMap<>();
        for (String filename : args) {
            try (Scanner input = new Scanner(new File(filename))) {
                while (input.hasNextLine()) {
                    String s = input.nextLine();
                    LetterInventory inventory = new LetterInventory(s);
                    if (!anagrams.containsKey(inventory)) {
                        anagrams.put(inventory, new HashSet<>());
                    }
                    anagrams.get(inventory).add(s);
                }
            }
        }
        Random random = new Random(1 + 0x43);
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", (HttpExchange t) -> {
            String html = Files.readString(Paths.get("index.html"));
            send(t, "text/html; charset=utf-8", html);
        });
        server.createContext("/query", (HttpExchange t) -> {
            String s = parse("s", t.getRequestURI().getQuery().split("&"));
            if (s.equals("")) {
                send(t, "application/json", String.format(QUERY_TEMPLATE, ""));
                return;
            }
            LetterInventory target = new LetterInventory(s);
            PriorityQueue<LetterInventory> pq = new PriorityQueue<>(
                Comparator.comparingDouble(target::similarity)
                );
            for (LetterInventory inventory : anagrams.keySet()) {
                pq.add(inventory);
                if (pq.size() > MAX_MATCHES) {
                    pq.remove();
                }
            }
            List<String> matches = new ArrayList<>(MAX_MATCHES);
            while (!pq.isEmpty()) {
                LetterInventory inventory = pq.remove();
                Set<String> options = anagrams.get(inventory);
                if (options.contains(s)) {
                    matches.add(s);
                } else {
                    matches.add(randomChoice(options, random));
                }
            }
            Collections.reverse(matches);
            send(t, "application/json", String.format(QUERY_TEMPLATE, json(matches)));
        });
        server.createContext("/random", (HttpExchange t) -> {
            Set<String> options = randomChoice(anagrams.values(), random);
            send(t, "application/json", "{\"s\":\"" + randomChoice(options, random) + "\"}");
        });
        server.setExecutor(null);
        server.start();
    }

    private static String parse(String key, String... params) {
        for (String param : params) {
            String[] pair = param.split("=");
            if (pair.length == 2 && pair[0].equals(key)) {
                return pair[1];
            }
        }
        return "";
    }

    private static void send(HttpExchange t, String contentType, String data)
            throws IOException, UnsupportedEncodingException {
        t.getResponseHeaders().set("Content-Type", contentType);
        byte[] response = data.getBytes("UTF-8");
        t.sendResponseHeaders(200, response.length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(response);
        }
    }

    private static String json(Iterable<String> matches) {
        StringBuilder results = new StringBuilder();
        for (String s : matches) {
            if (results.length() > 0) {
                results.append(',');
            }
            results.append('"').append(s).append('"');
        }
        return results.toString();
    }

    private static <E> E randomChoice(Collection<E> data, Random random) {
        int index = random.nextInt(data.size());
        Iterator<E> iter = data.iterator();
        while (index > 0) {
            iter.next();
            index--;
        }
        return iter.next();
    }
}
