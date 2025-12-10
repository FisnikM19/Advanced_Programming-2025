package labs.lab5.additional_task_wednesday;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**TODO:
 * Method 1:
 * implement a method getAllRoomsByUsers(): Map<String, Set<String>>
 *     which returns a map where the key is username and the value is a set containing all the rooms where the user is logged in
 *
 * Method 2:
 * to implement a method getChatRoomStatistics(): Map<ChatRoom, Integer>
 *     which returns a sorted map by the ChatRoom name in descending order, and the value is the number of users in the room.
 */


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

    public String getRoomName() {
        return roomName;
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

    //TODO: Method 1
    public Map<String, Set<String>> getAllRoomsByUsers() {

        Map<String, Set<String>> result = new HashMap<>();

        // Initialize for all registered users
        registeredUsers.forEach(user -> result.put(user, new HashSet<>()));

        // Populate with room memberships
        rooms.forEach((roomName, room) ->
                room.users.forEach(user ->
                    result.get(user.getUsername()).add(roomName)
                )
        );

        return result;
    }

//    //TODO Method 1 Another approach!
//    public Map<String, Set<String>> getAllRoomsByUsers() {
//        Map<String, Set<String>> result = new HashMap<>();
//
//        // Initialize sets for all registered users
//        for (String username : registeredUsers) {
//            result.put(username, new HashSet<>());
//        }
//
//        // Iterate through all rooms and add room names to users' sets
//        for (ChatRoom room : rooms.values()) {
//            for (User user : room.users) {
//                result.get(user.getUsername()).add(room.roomName);
//            }
//        }
//
//        return result;
//    }

    //TODO: Method 2
    public Map<ChatRoom, Integer> getChatRoomStatistics() {
        Map<ChatRoom, Integer> result = new TreeMap<>(
                Comparator.comparing(ChatRoom::getRoomName).reversed() // descending order
        );

        for (ChatRoom room: rooms.values()) {
            result.put(room, room.numUsers());
        }

        return result;
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
            System.out.println("Additional method 1: " + cs.getAllRoomsByUsers()); //TODO: Method 1
            System.out.println("Additional method 2: " + cs.getChatRoomStatistics()); // TODO: Method 2
        }
    }

}
