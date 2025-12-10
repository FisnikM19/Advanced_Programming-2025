package labs.lab5.task3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

// todo: implement the necessary classes
class Book {
    String isbn;
    String title;
    String author;
    int year;
    int availableCopies;
    int totalBorrows;

    public Book(String isbn, String title, String author, int year) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.year = year;
        this.availableCopies = 1;
        this.totalBorrows = 0;
    }

    public int getTotalBorrows() {
        return totalBorrows;
    }

    public void incrementCopies() {
        availableCopies++;
    }

    public void decrementCopies() {
        availableCopies--;
    }

    public void incrementTotalBorrows() {
        totalBorrows++;
    }

    @Override
    public String toString() {
        return String.format("%s - \"%s\" by %s (%d), available: %d, total borrows: %d",
                isbn, title, author, year, availableCopies, totalBorrows);
    }
}

class Member {
    String id;
    String name;
    int currentBorrows;
    int totalBorrows;

    public Member(String id, String name) {
        this.id = id;
        this.name = name;
        this.currentBorrows = 0;
        this.totalBorrows = 0;
    }


    public void incrementBorrows() {
        currentBorrows++;
        totalBorrows++;
    }

    public void decrementBorrows() {
       currentBorrows--;
    }

    public int getCurrentBorrows() {
        return currentBorrows;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - borrowed now: %d, total borrows: %d",
                name, id, currentBorrows, totalBorrows);
    }
}

class LibrarySystem {
    String libraryName;

    Map<String, Member> members;
    Map<String, Book> books;

    Map<String, Queue<String>> waitingLists; // isbn -> queue of memberIds
    Map<String, Set<String>> currentBorrowers; // isbn -> set of memberIds

    public LibrarySystem(String libraryName) {
        this.libraryName = libraryName;
        this.members = new TreeMap<>();
        this.books = new TreeMap<>();
        this.waitingLists = new HashMap<>();
        this.currentBorrowers = new HashMap<>();
    }

    public void registerMember(String id, String fullName) {
        members.putIfAbsent(id, new Member(id, fullName));
    }

    public void addBook(String isbn, String title, String author, int year) {
        Book book = new Book(isbn, title, author, year);

        if (books.containsKey(isbn)) {
            books.get(isbn).incrementCopies();
        } else {
            books.put(isbn, book);
            waitingLists.put(isbn, new LinkedList<>());
            currentBorrowers.put(isbn, new TreeSet<>());
        }
    }

    public void borrowBook(String memberId, String isbn) {
        if (!books.containsKey(isbn)) {
            return;
        }

        Book book = books.get(isbn);

        if (book.availableCopies == 0) {
            waitingLists.get(isbn).add(memberId);
        } else {
            book.decrementCopies();
            book.incrementTotalBorrows();
            members.get(memberId).incrementBorrows();
            currentBorrowers.get(isbn).add(memberId);
        }
    }

    public void returnBook(String memberId, String isbn) {
        Book book = books.get(isbn);
        book.incrementCopies();
        members.get(memberId).decrementBorrows();
        currentBorrowers.get(isbn).remove(memberId);

        // Check if someone is waiting
        Queue<String> waitingList = waitingLists.get(isbn);
        if (!waitingList.isEmpty()) {
            String waitingMemberId = waitingList.poll();
            book.decrementCopies();
            book.incrementTotalBorrows();
            members.get(waitingMemberId).incrementBorrows();
            currentBorrowers.get(isbn).add(waitingMemberId);
        }
    }

    public void printMembers() {
        members.values().stream()
                .sorted(Comparator.comparing(Member::getCurrentBorrows).reversed()
                        .thenComparing(m -> m.name))
                .forEach(System.out::println);
    }

    public void printBooks() {
        books.values().stream()
                .sorted(Comparator.comparing(Book::getTotalBorrows).reversed()
                        .thenComparing(b -> b.year))
                .forEach(System.out::println);
    }

    public void printBookCurrentBorrowers(String isbn) {
        Set<String> borrowers = currentBorrowers.get(isbn);
        if (borrowers != null && !borrowers.isEmpty()) {
            System.out.println(String.join(", ", borrowers));
        }
    }

    public void printTopAuthors() {
        Map<String, Integer> authorBorrows = new TreeMap<>();

        for (Book book: books.values()) {
            authorBorrows.put(book.author, authorBorrows.getOrDefault(book.author, 0) + book.totalBorrows);
        }

        authorBorrows.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .forEach(entry -> System.out.println(entry.getKey() + " - " + entry.getValue()));
    }

}

public class LibraryTester {
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            String libraryName = br.readLine();
            //   System.out.println(libraryName); //test
            if (libraryName == null) return;

            libraryName = libraryName.trim();
            LibrarySystem lib = new LibrarySystem(libraryName);

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.equals("END")) break;
                if (line.isEmpty()) continue;

                String[] parts = line.split(" ");

                switch (parts[0]) {

                    case "registerMember": {
                        lib.registerMember(parts[1], parts[2]);
                        break;
                    }

                    case "addBook": {
                        String isbn = parts[1];
                        String title = parts[2];
                        String author = parts[3];
                        int year = Integer.parseInt(parts[4]);
                        lib.addBook(isbn, title, author, year);
                        break;
                    }

                    case "borrowBook": {
                        lib.borrowBook(parts[1], parts[2]);
                        break;
                    }

                    case "returnBook": {
                        lib.returnBook(parts[1], parts[2]);
                        break;
                    }

                    case "printMembers": {
                        lib.printMembers();
                        break;
                    }

                    case "printBooks": {
                        lib.printBooks();
                        break;
                    }

                    case "printBookCurrentBorrowers": {
                        lib.printBookCurrentBorrowers(parts[1]);
                        break;
                    }

                    case "printTopAuthors": {
                        lib.printTopAuthors();
                        break;
                    }

                    default:
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}