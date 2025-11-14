package exercises_courses.task18;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

class CategoryNotFoundException extends Exception {
    public CategoryNotFoundException(String name) {
        super(String.format("Category %s was not found", name));
    }
}

class Category implements Comparable<Category>{

    private final String categoryName;

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public int compareTo(Category other) {
        return this.categoryName.compareTo(other.categoryName);
    }

    public String getCategoryName() {
        return categoryName;
    }
}

abstract class NewsItem {

    protected String title;
    protected Date publication_date;
    protected Category category;

    public NewsItem(String title, Date publication_date, Category category) {
        this.title = title;
        this.publication_date = publication_date;
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    abstract String getTeaser();
}

class TextNewsItem extends NewsItem {

    private String text;

    public TextNewsItem(String title, Date publication_date, Category category, String text) {
        super(title, publication_date, category);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String getTeaser() {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append("\n");

        Instant pastDateInstant = publication_date.toInstant();
        Instant now = Instant.now();
        long minutes = Duration.between(pastDateInstant, now).toMinutes();
        sb.append(minutes).append("\n");
        sb.append(text.substring(0, Math.min(text.length(), 80))).append("\n");

        return sb.toString();
    }
}

class MediaNewsItem extends NewsItem{

    private String url;
    private int views;

    public MediaNewsItem(String title, Date publication_date, Category category, String url, int views) {
        super(title, publication_date, category);
        this.url = url;
        this.views = views;
    }

    public String getUrl() {
        return url;
    }

    public int getViews() {
        return views;
    }

    @Override
    public String getTeaser() {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append("\n");

        Instant pastDateInstant = publication_date.toInstant();
        Instant now = Instant.now();
        long minutes = Duration.between(pastDateInstant, now).toMinutes();
        sb.append(minutes).append("\n");
        sb.append(url).append("\n");
        sb.append(views).append("\n");

        return sb.toString();
    }
}

class FrontPage {

    private Category[] categories;
    List<NewsItem> newsItems;

    public FrontPage(Category[] categories) {
        this.categories = categories;
        this.newsItems = new ArrayList<>();
    }

    public void addNewsItem(NewsItem newsItem) {
        newsItems.add(newsItem);
    }

    public List<NewsItem> listByCategory(Category category) {
        return newsItems.stream()
                .filter(newsItem -> newsItem.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public List<NewsItem> listByCategoryName(String category) throws CategoryNotFoundException {
        Optional<Category> categoryOptional = Arrays.stream(categories)
                .filter(c -> c.getCategoryName().equals(category))
                .findAny();

        return listByCategory(categoryOptional.orElseThrow(() -> new CategoryNotFoundException(category)));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        newsItems.forEach(n -> sb.append(n.getTeaser()));
        return sb.toString();
    }
}

public class FrontPageTest {
    public static void main(String[] args) {
        // Reading
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] parts = line.split(" ");
        Category[] categories = new Category[parts.length];
        for (int i = 0; i < categories.length; ++i) {
            categories[i] = new Category(parts[i]);
        }
        int n = scanner.nextInt();
        scanner.nextLine();
        FrontPage frontPage = new FrontPage(categories);
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            cal = Calendar.getInstance();
            int min = scanner.nextInt();
            cal.add(Calendar.MINUTE, -min);
            Date date = cal.getTime();
            scanner.nextLine();
            String text = scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();
            TextNewsItem tni = new TextNewsItem(title, date, categories[categoryIndex], text);
            frontPage.addNewsItem(tni);
        }

        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int min = scanner.nextInt();
            cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -min);
            scanner.nextLine();
            Date date = cal.getTime();
            String url = scanner.nextLine();
            int views = scanner.nextInt();
            scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();
            MediaNewsItem mni = new MediaNewsItem(title, date, categories[categoryIndex], url, views);
            frontPage.addNewsItem(mni);
        }
        // Execution
        String category = scanner.nextLine();
        System.out.println(frontPage);
        for(Category c : categories) {
            System.out.println(frontPage.listByCategory(c).size());
        }
        try {
            System.out.println(frontPage.listByCategoryName(category).size());
        } catch(CategoryNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
