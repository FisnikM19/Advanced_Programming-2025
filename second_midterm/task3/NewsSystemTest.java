package second_midterm.task3;

import java.time.LocalDateTime;
import java.util.*;

class Article {

    private final String category;
    private final String author;
    private final String content;
    private final LocalDateTime timestamp;

    public Article(String category, String author, String content, LocalDateTime timestamp) {
        this.category = category;
        this.author = author;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getCategory() {
        return category;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

interface Observer {
    void update(Article article);
}

class User implements Observer {

    String username;

    List<Article> articles;

    public User(String username) {
        this.username = username;
        articles = new ArrayList<>();
    }

    @Override
    public void update(Article article) {
        articles.add(article);
    }

    public List<Article> getArticles() {
        return articles;
    }
}

class NewsSystem {
    List<String> categoryNames;
    List<String> authorNames;

    Map<String, User> users;
    Map<String, List<Observer>> categorySubscribers;
    Map<String, List<Observer>> authorSubscribers;

    public NewsSystem(List<String> categoryNames, List<String> authorNames) {
        this.categoryNames = categoryNames;
        this.authorNames = authorNames;
        users = new HashMap<>();
        categorySubscribers = new HashMap<>();
        authorSubscribers = new HashMap<>();
    }

    public void addUser(String username) {
        users.put(username,new User(username));
    }

    public void subscribeUserToCategory(String username, String categoryName) {
        Observer observer = users.get(username);
        categorySubscribers.computeIfAbsent(categoryName, k -> new ArrayList<>()).add(observer);
    }

    public void unsubscribeUserFromCategory(String username, String categoryName) {
        Observer observer = users.get(username);
        categorySubscribers.get(categoryName).remove(observer);
    }

    public void subscribeUserToAuthor(String username, String authorName) {
        Observer observer = users.get(username);
        authorSubscribers.computeIfAbsent(authorName, k -> new ArrayList<>()).add(observer);
    }

    public void unsubscribeUserFromAuthor(String username, String authorName) {
        Observer observer = users.get(username);
        authorSubscribers.get(authorName).remove(observer);
    }

    public void publishArticle(Article article) {

        List<Observer> categorySubs = categorySubscribers.get(article.getCategory());
        Set<Observer> notified = new HashSet<>();

        if (categorySubs != null) {
            for (Observer observer: categorySubs) {
                observer.update(article);
                notified.add(observer);
            }
        }

        List<Observer> authorSubs = authorSubscribers.get(article.getAuthor());

        if (authorSubs != null) {
            for (Observer observer: authorSubs) {
                if (!notified.contains(observer)) {  // Prevent double notifications
                    observer.update(article);
                }
            }
        }

    }

    public void printNewsForUser(String username) {

        System.out.println("News for user: " + username);

        User user = users.get(username);

        user.getArticles().stream()
                .sorted(Comparator.comparing(Article::getTimestamp))
                .forEach(article -> {
                    System.out.printf("[%s] %s - %s\n", article.getTimestamp(), article.getAuthor(), article.getCategory());
                    System.out.println(article.getContent());
                });
    }
}


public class NewsSystemTest {

    public static void main(String[] args) {

        // Hardcoded categories and authors
        List<String> categories = List.of(
                "Technology", "Sports", "Politics", "Health", "Science",
                "Business", "Education", "Culture", "Travel", "Entertainment"
        );

        List<String> authors = List.of(
                "MartinFowler", "JohnDoe", "AliceSmith", "BobBrown", "JaneMiller"
        );

        NewsSystem system = new NewsSystem(categories, authors);

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+", 2);
            String command = parts[0];

            switch (command) {

                case "ADD_USER":
                    system.addUser(parts[1]);
                    break;

                case "SUBSCRIBE_CATEGORY": {
                    String[] p = parts[1].split("\\s+");
                    system.subscribeUserToCategory(p[0], p[1]);
                    break;
                }

                case "UNSUBSCRIBE_CATEGORY": {
                    String[] p = parts[1].split("\\s+");
                    system.unsubscribeUserFromCategory(p[0], p[1]);
                    break;
                }

                case "SUBSCRIBE_AUTHOR": {
                    String[] p = parts[1].split("\\s+");
                    system.subscribeUserToAuthor(p[0], p[1]);
                    break;
                }

                case "UNSUBSCRIBE_AUTHOR": {
                    String[] p = parts[1].split("\\s+");
                    system.unsubscribeUserFromAuthor(p[0], p[1]);
                    break;
                }

                case "PUBLISH": {
                    // format:
                    // PUBLISH <category> <author> <timestamp> <content>
                    String[] p = parts[1].split("\\s+", 4);
                    Article article = new Article(
                            p[0],
                            p[1],
                            p[3],
                            LocalDateTime.parse(p[2])
                    );
                    system.publishArticle(article);
                    break;
                }

                case "PRINT":
                    system.printNewsForUser(parts[1]);
                    break;
            }
        }
    }
}





