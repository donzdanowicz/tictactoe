package com.kodilla.tictactoe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class TicTacToe extends Application {
    private final Image board = new Image("file:src/main/resources/board.jpg");
    private final Image x = new Image("file:src/main/resources/x.jpg");
    private final Image o = new Image("file:src/main/resources/o.jpg");
    private final Random random = new Random();
    private int difficulty = 1;
    private final String X = "X";
    private final String O = "O";

    private Button[][] button = new Button[3][3];
    private Button fireButton = new Button();
    private List<Button> buttons = new LinkedList<>();
    private List<Button> removed = new LinkedList<>();
    Label status = new Label();
    Label title = new Label();
    Button newGame = new Button();
    Button exit = new Button();
    Button easy = new Button();
    Button medium = new Button();
    Button hard = new Button();

    StackPane root = new StackPane();
    GridPane grid = new GridPane();
    Canvas canvas = new Canvas(530, 530);
    GraphicsContext gc = canvas.getGraphicsContext2D();

    Player player = new Player("John Smith");
    Player computer = new Player("Computer");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(board, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);

        status.setTranslateX(50);
        Font statusFont = new Font(12);
        status.setFont(statusFont);
        status.setPrefWidth(200);

        title.setTranslateX(50);
        title.setText("TIC \nTAC \nTOE");
        Font titleFont = new Font(40);
        title.setFont(titleFont);
        title.setTextFill(Color.BLUE);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                grid.add(button[i][j] = new Button(), j + 1, i);
                Button finalButton = button[i][j];
                finalButton.setTranslateX(20);
                finalButton.setTranslateY(0);
                finalButton.setId("");
                finalButton.setPrefSize(235, 235);
                finalButton.setStyle("-fx-opacity: 1; -fx-background-color: transparent;");
                buttons.add(finalButton);
                finalButton.setText(i + "," + j);
                finalButton.setTextFill(Color.WHITE);
                finalButton.setOnAction((e) -> {
                    finalButton.setGraphic(new ImageView(x));
                    finalButton.setId(X);
                    removed.add(finalButton);
                    status.setText("Your Turn, " + player.getPlayerName());
                    finalButton.setDisable(true);

                    if (whoWins(X) || whoWins(O)) {
                        canvas.toFront();
                        endOfGame();
                    }

                    if (removed.size() < 8 && removed.size() != 0) {
                        if (difficulty == 2 || difficulty == 3) {
                            computerFire(difficulty);
                        } else {
                            computersRandomMove();
                        }
                    }
                });
            }
        }

        newGame.setText("NEW GAME");
        newGame.setTranslateX(20);
        newGame.setTranslateY(400);
        newGame.setPrefSize(100, 19);
        newGame.setOnAction((e) -> {
            for (int i = 0; i < 9; i++) {
                buttons.get(i).setDisable(false);
                buttons.get(i).setGraphic(null);
                buttons.get(i).setId("");
                removed.removeAll(removed);
                status.setText("");
                canvas.toBack();
                gc.clearRect(0, 0, 530, 530);
            }
        });

        exit.setText("EXIT");
        exit.setTranslateX(20);
        exit.setTranslateY(0);
        exit.setPrefSize(100, 19);
        exit.setOnAction((e) -> {
            Platform.exit();
            System.exit(0);
        });

        easy.setText("Easy");
        easy.setTranslateX(0);
        easy.setTranslateY(400);
        easy.setPrefSize(80, 19);
        easy.setOnAction((e) -> {
            difficulty = 1;
            easy.setText("EASY");
            medium.setText("Medium");
            hard.setText("Hard");
        });

        medium.setText("Medium");
        medium.setTranslateX(0);
        medium.setTranslateY(200);
        medium.setPrefSize(80, 19);
        medium.setOnAction((e) -> {
            difficulty = 2;
            medium.setText("MEDIUM");
            easy.setText("Easy");
            hard.setText("Hard");
        });

        hard.setText("Hard");
        hard.setTranslateX(0);
        hard.setTranslateY(0);
        hard.setPrefSize(80, 19);
        hard.setOnAction((e) -> {
            difficulty = 3;
            hard.setText("HARD");
            easy.setText("Easy");
            medium.setText("Medium");
        });

        grid.setAlignment(Pos.TOP_LEFT);
        grid.setPadding(new Insets(0, 0, 0, 335));
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setBackground(background);
        //grid.setGridLinesVisible(true);
        grid.add(title, 4, 0);
        grid.add(status, 4, 1);
        grid.add(newGame, 4, 0);
        grid.add(exit, 4, 2);
        grid.add(easy, 5,0);
        grid.add(medium, 5,1);
        grid.add(hard, 5,2);

        root.getChildren().addAll(canvas, grid);
        Scene scene = new Scene(root, 1412, 700, Color.LIGHTGRAY);


        primaryStage.setTitle("TicTacToe");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void computersRandomMove() {
        if (removed.size() < 8) {
            int randI = random.nextInt(3);
            int randJ = random.nextInt(3);
            boolean randEquals = false;

            while (!randEquals) {

                if (removed.contains(button[randI][randJ])) {
                    randI = random.nextInt(3);
                    randJ = random.nextInt(3);
                } else {
                    randEquals = true;
                }
            }

            Button randomButton = button[randI][randJ];
            randomButton.setId(O);
            randomButton.setGraphic(new ImageView(o));
            randomButton.setDisable(true);

            removed.add(randomButton);

            if (whoWins(X) || whoWins(O)) {
                canvas.toFront();
                endOfGame();

            }
        }
    }

    public Button computerAIHard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (button[i][0].getId().equals(O) && button[i][1].getId().equals(O) && !(removed.contains(button[i][2]))) {
                    return button[i][2];
                } else if (button[i][1].getId().equals(O) && button[i][2].getId().equals(O) && !(removed.contains(button[i][0]))) {
                    return button[i][0];
                } else if (button[i][0].getId().equals(O) && button[i][2].getId().equals(O) && !(removed.contains(button[i][1]))) {
                    return button[i][1];

                } else if (button[0][j].getId().equals(O) && button[1][j].getId().equals(O) && !(removed.contains(button[2][j]))) {
                    return button[2][j];
                } else if (button[1][j].getId().equals(O) && button[2][j].getId().equals(O) && !(removed.contains(button[0][j]))) {
                    return button[0][j];
                } else if (button[0][j].getId().equals(O) && button[2][j].getId().equals(O) && !(removed.contains(button[1][j]))) {
                    return button[1][j];

                } else if (button[i][0].getId().equals(X) && button[i][1].getId().equals(X) && !(removed.contains(button[i][2]))) {
                    return button[i][2];
                } else if (button[i][1].getId().equals(X) && button[i][2].getId().equals(X) && !(removed.contains(button[i][0]))) {
                    return button[i][0];
                } else if (button[i][0].getId().equals(X) && button[i][2].getId().equals(X) && !(removed.contains(button[i][1]))) {
                    return button[i][1];

                } else if (button[0][j].getId().equals(X) && button[1][j].getId().equals(X) && !(removed.contains(button[2][j]))) {
                    return button[2][j];
                } else if (button[1][j].getId().equals(X) && button[2][j].getId().equals(X) && !(removed.contains(button[0][j]))) {
                    return button[0][j];
                } else if (button[0][j].getId().equals(X) && button[2][j].getId().equals(X) && !(removed.contains(button[1][j]))) {
                    return button[1][j];

                } else if ((button[0][0].getId().equals(O) && button[2][2].getId().equals(O))
                        || (button[0][2].getId().equals(O) && button[2][0].getId().equals(O))
                        || (button[0][0].getId().equals(X) && button[2][2].getId().equals(X))
                        || (button[0][2].getId().equals(X) && button[2][0].getId().equals(X))
                        && !(removed.contains(button[1][1]))) {
                    return button[1][1];

                } else if ((button[0][0].getId().equals(O) && button[1][1].getId().equals(O))
                        || (button[0][0].getId().equals(X) && button[1][1].getId().equals(X))
                        && !(removed.contains(button[2][2]))) {
                    return button[2][2];

                } else if ((button[1][1].getId().equals(O) && button[2][2].getId().equals(O))
                        || (button[1][1].getId().equals(X) && button[2][2].getId().equals(X))
                        && !(removed.contains(button[0][0]))) {
                    return button[0][0];

                } else if ((button[0][2].getId().equals(O) && button[1][1].getId().equals(O))
                        || (button[0][2].getId().equals(X) && button[1][1].getId().equals(X))
                        && !(removed.contains(button[2][0]))) {
                    return button[2][0];

                } else if (((button[1][1].getId().equals(O) && button[2][0].getId().equals(O))
                        || (button[1][1].getId().equals(X) && button[2][0].getId().equals(X)))
                        && !(removed.contains(button[0][2]))) {
                    return button[0][2];
                }

            }
        }
        return computerAIMedium();
    }


    public Button computerAIMedium() {
        if (!(removed.contains(button[1][1]))) {
            return button[1][1];
        } else if (!(removed.contains(button[0][1]))) {
            return button[0][1];
        } else if (!(removed.contains(button[1][0]))) {
            return button[1][0];
        } else if (!(removed.contains(button[2][1]))) {
            return button[2][1];
        } else if (!(removed.contains(button[1][2]))) {
            return button[1][2];
        } else if (!(removed.contains(button[0][0]))) {
            return button[0][0];
        } else if (!(removed.contains(button[2][0]))) {
            return button[2][0];
        } else if (!(removed.contains(button[0][2]))) {
            return button[0][2];
        } else {
            return button[2][2];
        }
    }


    public boolean whoWins(String id) {

        for (int i = 0; i < 3; i++) {
            if (button[i][0].getId().equals(id) && button[i][1].getId().equals(id) && button[i][2].getId().equals(id)) {
                drawLine(0, i * 235.0 + 30.0, 570.0, i * 235.0 + 30.0);
                return true;
            }
        }

        for (int j = 0; j < 3; j++) {
            if (button[0][j].getId().equals(id) && button[1][j].getId().equals(id) && button[2][j].getId().equals(id)) {
                drawLine(j * 228.0 + 35.0, 0.0, j * 228.0 + 35.0, 570.0);
                return true;
            }
        }

        if (button[0][0].getId().equals(id) && button[1][1].getId().equals(id) && button[2][2].getId().equals(id)) {
            drawLine(15.0, 5.0, 595.0, 610.0);
            return true;
        }

        if (button[2][0].getId().equals(id) && button[1][1].getId().equals(id) && button[0][2].getId().equals(id)) {
            drawLine(0.0, 530.0, 512.0, 0.0);
            return true;
        }

        return false;
    }

    public void endOfGame() {
        if (whoWins(X) || whoWins(O)) {
            removed.removeAll(buttons);
            for (int i = 0; i < 9; i++) {
                buttons.get(i).setDisable(true);
            }

            if (whoWins(X)) {
                status.setText(player.getPlayerName() + " wins!");
            } else {
                status.setText(computer.getPlayerName() + " wins!");
            }
        }


    }

    public void drawLine(double startX, double startY, double endX, double endY) {

        gc.setLineWidth(5);
        gc.beginPath();
        gc.moveTo(startX, startY);
        gc.lineTo(endX, endY);
        gc.stroke();
    }


    public void computerFire(int difficulty) {

        if (difficulty == 2) {
            fireButton = computerAIMedium();
        } else if (difficulty == 3) {
            fireButton = computerAIHard();
        }

            if (removed.contains(fireButton)) {
                computersRandomMove();
            } else {

                removed.add(fireButton);

                fireButton.setId(O);
                fireButton.setDisable(true);
                fireButton.setGraphic(new ImageView(o));

                if (whoWins(X) || whoWins(O)) {
                    canvas.toFront();
                    endOfGame();
                }
            }
    }
}

