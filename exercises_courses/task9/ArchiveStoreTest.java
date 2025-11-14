package exercises_courses.task9;

import java.time.LocalDate;
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
    protected LocalDate dateArchived;

    public Archive(int id) {
        this.id = id;
        this.dateArchived = LocalDate.now();
    }

    public int getId() {
        return id;
    }

    public void setDateArchived(LocalDate dateArchived) {
        this.dateArchived = dateArchived;
    }
}

class LockedArchive extends Archive {

    private LocalDate dateToOpen;

    public LockedArchive(int id, LocalDate dateToOpen) {
        super(id);
        this.dateToOpen = dateToOpen;
    }

    public LocalDate getDateToOpen() {
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

    public void incrementOpenCount() {
        openCount++;
    }

    public int getOpenCount() {
        return openCount;
    }
}

class ArchiveStore {
    List<Archive> archives;
    StringBuilder logs;

    public ArchiveStore() {
        this.archives = new ArrayList<>();
        logs = new StringBuilder();
    }

    public void archiveItem(Archive item, LocalDate date) {
        item.setDateArchived(date);
        archives.add(item);
        logs.append(String.format("Item %d archived at %s%n", item.id, date));
    }

    public void openItem(int id, LocalDate date) throws NonExistingItemException {
        Archive foundArchive = null;

        // Find the archive with the given id
        for (Archive archive: archives) {
            if (archive.getId() == id) {
                foundArchive = archive;
                break;
            }
        }

        if (foundArchive == null) throw new NonExistingItemException(id);

        if (foundArchive instanceof LockedArchive) {
            LockedArchive lockedArchive = (LockedArchive) foundArchive;
            if (date.isBefore(lockedArchive.getDateToOpen())) {
                logs.append(String.format("Item %d cannot be opened before %s%n", id, lockedArchive.getDateToOpen()));
                return;
            }
        }

        if (foundArchive instanceof SpecialArchive) {
            SpecialArchive specialArchive = (SpecialArchive) foundArchive;
            if (specialArchive.getOpenCount() >= specialArchive.getMaxOpen()) {
                logs.append(String.format("Item %d cannot be opened more than %d times%n", id, specialArchive.getMaxOpen()));
                return;
            }
            specialArchive.incrementOpenCount();
        }

        logs.append(String.format("Item %d opened at %s%n", id, date));
    }

    public String getLog() {
        return logs.toString();
    }
}

public class ArchiveStoreTest {
    public static void main(String[] args) {
        ArchiveStore store = new ArchiveStore();
        LocalDate date = LocalDate.of(2013, 10, 7);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        int n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        int i;
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            long days = scanner.nextLong();

            LocalDate dateToOpen = date.atStartOfDay().plusSeconds(days * 24 * 60 * 60).toLocalDate();
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
        while(scanner.hasNext()) {
            int open = scanner.nextInt();
            try {
                store.openItem(open, date);
            } catch(NonExistingItemException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println(store.getLog());
    }
}
