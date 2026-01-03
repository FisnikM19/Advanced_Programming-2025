package labs.lab8.task2.sol_with_dpattern;

import java.util.ArrayList;
import java.util.Scanner;

interface QuestionState {
    void showQuestion(TriviaQuestion q, int index);
    boolean checkAnswer(TriviaQuestion q, String answer);
}

class TrueFalseState implements QuestionState {

    @Override
    public void showQuestion(TriviaQuestion q, int index) {
        System.out.println("Question " + (index+1) + ".  " + q.pointValue + " points.");
        System.out.println(q.question);
        System.out.println("Enter 'T' for true or 'F' for false.");
    }

    @Override
    public boolean checkAnswer(TriviaQuestion q, String answer) {
        return answer.charAt(0) == q.answer.charAt(0);
    }
}

class FreeFormState implements QuestionState {

    @Override
    public void showQuestion(TriviaQuestion q, int index) {
        System.out.println("Question " + (index + 1) + ".  " + q.pointValue + " points.");
        System.out.println(q.question);
    }

    @Override
    public boolean checkAnswer(TriviaQuestion q, String answer) {
        return answer.toLowerCase().equals(q.answer.toLowerCase());
    }
}

class TriviaQuestion {

    public String question;		// Actual question
    public String answer;		// Answer to question
    public int pointValue;			// Point value of question
//    public QUESTION_TYPE type;			// Question type, TRUEFALSE or FREEFORM

    public static final int TRUEFALSE = 0;
    public static final int FREEFORM = 1;

    private QuestionState state;

    public TriviaQuestion(String question, String answer, int pointValue, int type) {
        this.question = question;
        this.answer = answer;
        this.pointValue = pointValue;

        if (type == TRUEFALSE) {
            state = new TrueFalseState();
        } else {
            state = new FreeFormState();
        }
    }

    public void show(int index) {
        state.showQuestion(this, index);
    }

    public boolean check(String userAnswer) {
        return state.checkAnswer(this, userAnswer);
    }
}

class TriviaData {

    private ArrayList<TriviaQuestion> data;

    public TriviaData() {
        data = new ArrayList<>();
    }

    public void addQuestion(String q, String a, int pointValue, int type) {
        TriviaQuestion question = new TriviaQuestion(q, a, pointValue, type);
        data.add(question);
    }

    public void showQuestion(int index) {
//        TriviaQuestion q = data.get(index);
//        System.out.println("Question " + (index + 1) + ".  " + q.pointValue + " points.");
//        if (q.type == QUESTION_TYPE.TRUEFALSE) {
//            System.out.println(q.question);
//            System.out.println("Enter 'T' for true or 'F' for false.");
//        } else if (q.type == QUESTION_TYPE.FREEFORM) {
//            System.out.println(q.question);
//        }

        data.get(index).show(index);
    }

    public int numQuestions() {
        return data.size();
    }

    public TriviaQuestion getQuestion(int index) {
        return data.get(index);
    }
}

public class TriviaGame {

    public TriviaData questions;	// Questions

    public TriviaGame() {
        // Load questions
        questions = new TriviaData();
        questions.addQuestion("The possession of more than two sets of chromosomes is termed?",
                "polyploidy", 3, TriviaQuestion.FREEFORM);
        questions.addQuestion("Erling Kagge skiied into the north pole alone on January 7, 1993.",
                "F", 1, TriviaQuestion.TRUEFALSE);
        questions.addQuestion("1997 British band that produced 'Tub Thumper'",
                "Chumbawumba", 2, TriviaQuestion.FREEFORM);
        questions.addQuestion("I am the geometric figure most like a lost parrot",
                "polygon", 2, TriviaQuestion.FREEFORM);
        questions.addQuestion("Generics were introducted to Java starting at version 5.0.",
                "T", 1, TriviaQuestion.TRUEFALSE);
    }
    // Main game loop

    public static void main(String[] args) {
        int score = 0;			// Overall score
        int questionNum = 0;	// Which question we're asking
        TriviaGame game = new TriviaGame();
        Scanner keyboard = new Scanner(System.in);
        // Ask a question as long as we haven't asked them all
        while (questionNum < game.questions.numQuestions()) {
            // Show question
            game.questions.showQuestion(questionNum);
            // Get answer
            String answer = keyboard.nextLine();
            // Validate answer
            TriviaQuestion q = game.questions.getQuestion(questionNum);
//            if (q.type == QUESTION_TYPE.TRUEFALSE) {
//                if (answer.charAt(0) == q.answer.charAt(0)) {
//                    System.out.println("That is correct!  You get " + q.pointValue + " points.");
//                    score += q.pointValue;
//                } else {
//                    System.out.println("Wrong, the correct answer is " + q.answer);
//                }
//            } else if (q.type == QUESTION_TYPE.FREEDOM) {
//                if (answer.toLowerCase().equals(q.answer.toLowerCase())) {
//                    System.out.println("That is correct!  You get " + q.pointValue + " points.");
//                    score += q.pointValue;
//                } else {
//                    System.out.println("Wrong, the correct answer is " + q.answer);
//                }
//            }

            if (q.check(answer)) {
                System.out.println("That is correct!  You get " + q.pointValue + " points.");
                score += q.pointValue;
            } else {
                System.out.println("Wrong, the correct answer is " + q.answer);
            }

            System.out.println("Your score is " + score);
            questionNum++;
        }
        System.out.println("Game over!  Thanks for playing!");
    }
}

