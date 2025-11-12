package exercises_courses.task8;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

class NonExistingItemException extends Exception {
    public NonExistingItemException(int id) {
        super(String.format("Item with id %d doesn't exist", id));
    }
}

abstract class Archive {

    protected int id;
    protected Date dateArchived;

    public Archive(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Date getDateArchived() {
        return dateArchived;
    }

    public void setDateArchived(Date dateArchived) {
        this.dateArchived = dateArchived;
    }
}

class LockedArchive extends Archive {
    private Date dateToOpen;

    public LockedArchive(int id, Date dateToOpen) {
        super(id);
        this.dateToOpen = dateToOpen;
    }

    public Date getDateToOpen() {
        return dateToOpen;
    }
}

class SpecialArchive extends Archive {

    private int maxOpen;
    private int openCount;

    public SpecialArchive(int id, int maxOpen) {
        super(id);
        this.maxOpen = maxOpen;
        this.openCount = 0;
    }

    public int getMaxOpen() {
        return maxOpen;
    }

    public int getOpenCount() {
        return openCount;
    }

    public void incrementOpenCount() {
        openCount++;
    }
}

class ArchiveStore {
    private List<Archive> archives;
    private StringBuilder logs;

    public ArchiveStore(){
        archives = new ArrayList<>();
        logs = new StringBuilder();
    }

    public void archiveItem(Archive item, Date date) {
        item.setDateArchived(date);
        archives.add(item);
        logs.append(String.format("Item %d archived at %s%n", item.getId(), date));
    }

    public void openItem(int id, Date date) throws NonExistingItemException {
        Archive foundArchive = null;

        // Find the archive with the given id
        for (Archive archive: archives) {
            if (archive.getId() == id) {
                foundArchive = archive;
                break;
            }
        }

        // If not found, throw exception
        if (foundArchive == null) {
            throw new NonExistingItemException(id);
        }

        // Check if it's a LockedArchive
        if (foundArchive instanceof LockedArchive) {
            LockedArchive lockedArchive = (LockedArchive) foundArchive;
            if (date.before(lockedArchive.getDateToOpen())) {
                logs.append(String.format("Item %d cannot be opened before %s%n", id, lockedArchive.getDateToOpen()));
                return;
            }
        }

        // Check if it's a SpecialArchive
        if (foundArchive instanceof SpecialArchive) {
            SpecialArchive specialArchive = (SpecialArchive) foundArchive;
            if (specialArchive.getOpenCount() >= specialArchive.getMaxOpen()) {
                logs.append(String.format("Item %d cannot be opened more than %d times%n", id, specialArchive.getMaxOpen()));
                return;
            }
            specialArchive.incrementOpenCount();
        }

        // If all checks pass, log the opening
        logs.append(String.format("Item %d opened at date %s%n", id, date));

    }

    public String getLog() {
        return logs.toString();
    }
}



public class ArchiveStoreTest {
    public static void main(String[] args) {
        ArchiveStore store = new ArchiveStore();
        Date date = new Date(113, 10, 7);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        int n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        int i;
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            long days = scanner.nextLong();
            Date dateToOpen = new Date(date.getTime() + (days * 24 * 60
                    * 60 * 1000));
            LockedArchive lockedArchive = new LockedArchive(id, dateToOpen);
            store.archiveItem(lockedArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            int maxOpen = scanner.nextInt();
            SpecialArchive specialArchive = new SpecialArchive(id, maxOpen);
            store.archiveItem(specialArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        String openingLine = scanner.nextLine();
        String[] openItems = openingLine.trim().split("\\s+");
        for (String item : openItems) {
            int open = Integer.parseInt(item);
            try {
                store.openItem(open, date);
            } catch(NonExistingItemException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println(store.getLog());
    }
}
