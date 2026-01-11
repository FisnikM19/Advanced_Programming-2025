package ispit_juni_2023.task2;

import java.util.*;

class Comment {
    String author;
    String id;
    String content;
    String replyToId;
    List<Comment> replies;

    public Comment(String author, String id, String content, String replyToId) {
        this.author = author;
        this.id = id;
        this.content = content;
        this.replyToId = replyToId;
        this.replies = new ArrayList<>();
    }

    public void addReply(Comment reply) {
        replies.add(reply);
    }
}

class Post {

    String username;
    String postContent;

    Map<String, Comment> allComments; // Map of commentId -> Comment
    Map<String, Integer> directLikes;
    List<Comment> topLevelComments; // Comments directly on the post

    public Post(String username, String postContent) {
        this.username = username;
        this.postContent = postContent;
        allComments = new HashMap<>();
        this.directLikes = new HashMap<>();
        this.topLevelComments = new ArrayList<>();
    }

    public void addComment (String username, String commentId, String content, String replyToId) {
        Comment comment = new Comment(username, commentId, content, replyToId);
        allComments.put(commentId, comment);

        if (replyToId == null) {
            // Top-level comment
            topLevelComments.add(comment);
        } else {
            // Reply to another comment
            Comment parent = allComments.get(replyToId);
            if (parent != null) {
                parent.addReply(comment);
            }
        }
    }

    public void likeComment(String commentId) {
        directLikes.put(commentId, directLikes.getOrDefault(commentId, 0) + 1 );
    }

    // Calculate total likes for a comment (including all descendant replies)
    private int getTotalLikes(Comment comment) {
        int total = directLikes.get(comment.id);
        for (Comment reply: comment.replies) {
            total += getTotalLikes(reply);
        }
        return total;
    }

    private void printComment(Comment comment, StringBuilder sb, String indent) {
        sb.append(indent).append("Comment: ").append(comment.content).append("\n");
        sb.append(indent).append("Written by: ").append(comment.author).append("\n");
        sb.append(indent).append("Likes: ").append(directLikes.getOrDefault(comment.id, 0)).append("\n");

        // Sort replies by total likes (descending), then by ID (ascending)
        List<Comment> sortedReplies = new ArrayList<>(comment.replies);
        sortedReplies.sort(
                Comparator.comparing((Comment c)  -> getTotalLikes(c), Comparator.reverseOrder())
                        .thenComparing(c -> Integer.parseInt(c.id))
        );

        // Print each reply with increased indentation
        for (Comment reply: sortedReplies) {
            printComment(reply, sb, indent + "    ");
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Post: ").append(postContent).append("\n");
        sb.append("Written by: ").append(username).append("\n");
        sb.append("Comments:\n");

        // Sort top-level comments by total likes (descending), then by ID (ascending)
        List<Comment> sortedTopLevel = new ArrayList<>(topLevelComments);
        sortedTopLevel.sort(
                Comparator.comparing((Comment c) -> getTotalLikes(c), Comparator.reverseOrder())
                        .thenComparing(c -> Integer.parseInt(c.id))
        );

        // Print each top level comment with its replies
        for (Comment comment: sortedTopLevel) {
            printComment(comment, sb, "        ");
        }

        return sb.toString();
    }
}

public class PostTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String postAuthor = sc.nextLine();
        String postContent = sc.nextLine();

        Post p = new Post(postAuthor, postContent);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(";");
            String testCase = parts[0];

            if (testCase.equals("addComment")) {
                String author = parts[1];
                String id = parts[2];
                String content = parts[3];
                String replyToId = null;
                if (parts.length == 5) {
                    replyToId = parts[4];
                }
                p.addComment(author, id, content, replyToId);
            } else if (testCase.equals("likes")) { //likes;1;2;3;4;1;1;1;1;1 example
                for (int i = 1; i < parts.length; i++) {
                    p.likeComment(parts[i]);
                }
            } else {
                System.out.println(p);
            }

        }
    }
}

