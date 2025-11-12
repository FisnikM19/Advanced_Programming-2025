package labs.lab3.task2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// todo: complete the implementation of the Ad, AdRequest, and AdNetwork classes

class Ad implements Comparable<Ad>{
    private String id;
    private String category;
    private double bidValue;
    private double ctr;
    private String content;


    public Ad(String id, String category, double bidValue, double ctr, String content) {
        this.id = id;
        this.category = category;
        this.bidValue = bidValue;
        this.ctr = ctr * 100; // convert to percentage
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public String getContent() {
        return content;
    }

    public double getBidValue() {
        return bidValue;
    }

    public double getCtr() {
        return ctr;
    }

    @Override
    public String toString() {
        // AD001 tech (bid=2.50, ctr=12.00%) Amazing new phone
        return String.format("%s %s (bid=%.2f, ctr=%.2f%%) %s", id, category, bidValue, ctr, content);
    }

    @Override
    public int compareTo(Ad o) {
        int cmp = Double.compare(o.bidValue, this.bidValue); // descending
        if (cmp != 0) return cmp;
        return this.id.compareTo(o.id); // ascending
    }
}

class AdRequest {
    private String id;
    private String category;
    private double floorBid;
    private String keywords;

    public AdRequest(String id, String category, double floorBid, String keywords) {
        this.id = id;
        this.category = category;
        this.floorBid = floorBid;
        this.keywords = keywords;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getKeywords() {
        return keywords;
    }

    public double getFloorBid() {
        return floorBid;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] (%.2f): %s", id, category, floorBid, keywords);
    }
}

class AdNetwork {
    private List<Ad> ads;

    public AdNetwork() {
        this.ads = new ArrayList<>();
    }

    public void readAds(BufferedReader br) throws IOException {

        String line;

        while ((line = br.readLine()) != null && !line.trim().isEmpty()) {
            String[] tokens = line.split("\\s+");
            if (tokens.length < 5) continue; // ignore bad/short lines

            String id = tokens[0];
            String category = tokens[1];
            double bidValue = Double.parseDouble(tokens[2]);
            double ctr = Double.parseDouble(tokens[3]);

            // tokens[4] may be multiple words -> join remaining parts
            String content = String.join(" ", Arrays.copyOfRange(tokens, 4, tokens.length));
            ads.add(new Ad(id, category, bidValue, ctr, content));
        }

    }

    public List<Ad> placeAds(BufferedReader br, int k, PrintWriter writer) throws IOException {
        double x = 5.0;
        double y = 100.0;

        String line = br.readLine();
        String[] parts = line.split("\\s+");
        String id = parts[0];
        String category = parts[1];
        double floorBid = Double.parseDouble(parts[2]);
        // parts[3] may be of multiple words -> join remaining parts
        String keywords = String.join(" ", Arrays.copyOfRange(parts, 3, parts.length));

        AdRequest adRequest = new AdRequest(id, category, floorBid, keywords);

        //  Excludes all ads that have a bidValue less than floorBid in the ad request
        ads.removeIf(ad -> ad.getBidValue() < adRequest.getFloorBid());

        // Scored ads
        List<ScoredAd> scored = new ArrayList<>();

        for (Ad ad: ads) {
            double totalScore = relevanceScore(ad, adRequest) + x*ad.getBidValue() + y*ad.getCtr();
            scored.add(new ScoredAd(ad, totalScore));
        }

        // sorted by totalScore in descending order
        scored.sort((a, b) -> Double.compare(b.score, a.score));

        // Top k ads are taken
        List<Ad> top = new ArrayList<>();
        for (int i = 0; i < Math.min(k, scored.size()); i++) {
            top.add(scored.get(i).getAd());
        }

        // Sort top k ads by natural order (bid descending, then id ascending)
        top.sort(Ad::compareTo);

        writer.printf("Top ads for request %s:\n", adRequest.getId());
        for (Ad ad: top) {
            writer.println(ad);
        }
        writer.flush();

        return top;
    }

    private int relevanceScore(Ad ad, AdRequest req) {
        int score = 0;
        if (ad.getCategory().equalsIgnoreCase(req.getCategory())) score += 10;
        String[] adWords = ad.getContent().toLowerCase().split("\\s+");
        String[] keywords = req.getKeywords().toLowerCase().split("\\s+");
        for (String kw : keywords) {
            for (String aw : adWords) {
                if (kw.equals(aw)) score++;
            }
        }
        return score;
    }
}

// helper class
class ScoredAd {
    Ad ad;
    double score;

    ScoredAd(Ad ad, double score) {
        this.ad = ad;
        this.score = score;
    }

    public Ad getAd() {
        return ad;
    }
}

public class Main {
    public static void main(String[] args) throws IOException {
        AdNetwork network = new AdNetwork();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out));

        int k = Integer.parseInt(br.readLine().trim());

        if (k == 0) {
            network.readAds(br);
            network.placeAds(br, 1, pw);
        } else if (k == 1) {
            network.readAds(br);
            network.placeAds(br, 3, pw);
        } else {
            network.readAds(br);
            network.placeAds(br, 8, pw);
        }
        pw.flush();
    }
}