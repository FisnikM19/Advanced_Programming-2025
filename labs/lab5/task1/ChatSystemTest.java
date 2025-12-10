package labs.lab5.task1;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.TreeSet;

class User {

    String username;

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

}

class ChatRoom {
    String roomName;
    Set<User> users;

    public ChatRoom(String roomName) {
        this.roomName = roomName;
        this.users = new TreeSet<>(Comparator.comparing(User::getUsername)); // sorting alphabetically
    }

    public void addUser(String username) {
        User user = new User(username);

        users.add(user);
    }

    public void removeUser(String username) {
        users.removeIf(u -> u.getUsername().equals(username));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(roomName).append("\n");

        if (users.isEmpty()) {
            sb.append("EMPTY");
        } else {
            for (User user: users) {
                sb.append(user.username).append("\n");
            }
            // Remove the last newline
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();
    }

    public boolean hasUser(String username) {
        return users.stream().anyMatch(user -> user.getUsername().equals(username));
    }

    public int numUsers() {
        return users.size();
    }
}

class ChatSystem {
    Map<String, ChatRoom> rooms;
    Set<String> registeredUsers;

    public ChatSystem() {
        rooms = new TreeMap<>();
        registeredUsers = new HashSet<>();
    }

    public void addRoom(String roomName)  {

        rooms.putIfAbsent(roomName, new ChatRoom(roomName));
    }

    public void removeRoom(String roomName) {
        rooms.remove(roomName);
    }

    public ChatRoom getRoom(String roomName) throws NoSuchRoomException {
        if (!rooms.containsKey(roomName)) {
            throw new NoSuchRoomException(roomName);
        }
        return rooms.get(roomName);
    }

    public void register(String userName) {
        registeredUsers.add(userName);

        // Find the room with fewest users
        ChatRoom minRoom = rooms.values().stream()
                .min(Comparator.comparing(ChatRoom::numUsers)
                        .thenComparing(room -> room.roomName))
                .orElse(null);

        if (minRoom != null) {
            minRoom.addUser(userName);
        }
    }

    public void registerAndJoin(String userName, String roomName) throws NoSuchRoomException {
        registeredUsers.add(userName);

        if (!rooms.containsKey(roomName)) {
            throw new NoSuchRoomException(roomName);
        }

        rooms.get(roomName).addUser(userName);
    }

    public void joinRoom(String userName, String roomName) throws NoSuchRoomException, NoSuchUserException {

        if (!registeredUsers.contains(userName)) {
            throw new NoSuchUserException(userName);
        }
        if (!rooms.containsKey(roomName)) {
            throw new NoSuchRoomException(roomName);
        }

        rooms.get(roomName).addUser(userName);
    }

    public void leaveRoom(String username, String roomName) throws NoSuchUserException, NoSuchRoomException {

        if (!registeredUsers.contains(username)) {
            throw new NoSuchUserException(username);
        }
        if (!rooms.containsKey(roomName)) {
            throw new NoSuchRoomException(roomName);
        }

        rooms.get(roomName).removeUser(username);
    }

    public void followFriend(String username, String friend_username) throws NoSuchUserException {
        if (!registeredUsers.contains(username)) {
            throw new NoSuchUserException(username);
        }
        if (!registeredUsers.contains(friend_username)) {
            throw new NoSuchUserException(friend_username);
        }

        // Find all rooms where friend is a member and join them
        for (ChatRoom room: rooms.values()) {
            if (room.hasUser(friend_username)) {
                room.addUser(username);
            }
        }

    }

}

class NoSuchRoomException extends Exception {
    public NoSuchRoomException(String roomName) {
        super(roomName);
    }
}

class NoSuchUserException extends Exception {
    public NoSuchUserException(String userName) {
        super(userName);
    }
}

public class ChatSystemTest {

    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchRoomException {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if ( k == 0 ) {
            ChatRoom cr = new ChatRoom(jin.next());
            int n = jin.nextInt();
            for ( int i = 0 ; i < n ; ++i ) {
                k = jin.nextInt();
                if ( k == 0 ) cr.addUser(jin.next());
                if ( k == 1 ) cr.removeUser(jin.next());
                if ( k == 2 ) System.out.println(cr.hasUser(jin.next()));
            }
            System.out.println("");
            System.out.println(cr.toString());
            n = jin.nextInt();
            if ( n == 0 ) return;
            ChatRoom cr2 = new ChatRoom(jin.next());
            for ( int i = 0 ; i < n ; ++i ) {
                k = jin.nextInt();
                if ( k == 0 ) cr2.addUser(jin.next());
                if ( k == 1 ) cr2.removeUser(jin.next());
                if ( k == 2 ) cr2.hasUser(jin.next());
            }
            System.out.println(cr2.toString());
        }
        if ( k == 1 ) {
            ChatSystem cs = new ChatSystem();
            Method mts[] = cs.getClass().getMethods();
            while ( true ) {
                String cmd = jin.next();
                if ( cmd.equals("stop") ) break;
                if ( cmd.equals("print") ) {
                    System.out.println(cs.getRoom(jin.next())+"\n");continue;
                }
                for ( Method m : mts ) {
                    if ( m.getName().equals(cmd) ) {
                        String params[] = new String[m.getParameterTypes().length];
                        for ( int i = 0 ; i < params.length ; ++i ) params[i] = jin.next();
                        m.invoke(cs,(Object[]) params);
                    }
                }
            }
        }
    }

}
