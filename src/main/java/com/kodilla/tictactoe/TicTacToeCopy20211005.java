/*package com.kodilla.tictactoe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

public class TicTacToeCopy20211005 extends Application {
    private final Image board = new Image("file:src/main/resources/board.jpg");
    private final Image x = new Image("file:src/main/resources/x.jpg");
    private final Image o = new Image("file:src/main/resources/o.jpg");
    private Random random = new Random();

    private List<Button> buttons = new LinkedList<>();
    private List<Button> removed = new LinkedList<>();
    private List<Integer> playersMoves = new LinkedList<>();
    private List<Integer> computersMoves = new LinkedList<>();
    private boolean playerTurn = true;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(board, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);

        Player player = new Player("John Smith");
        Player computer = new Player("Computer");

        WinnerSelector winnerSelector = new WinnerSelector();
        winnerSelector.setRules();

        Label status = new Label();
        status.setTranslateX(50);
        Font statusFont = new Font(15);
        status.setFont(statusFont);
        status.setPrefWidth(200);

        Label title = new Label();
        title.setTranslateX(50);
        title.setText("TIC \nTAC \nTOE");
        Font titleFont = new Font(40);
        title.setFont(titleFont);

        GridPane grid = new GridPane();

            for (int i = 0; i < 9; i++) {
                buttons.add(new Button());

                Button button = buttons.get(i);
                button.setTranslateX(10);
                button.setTranslateY(10);
                button.setPrefSize(235, 235);

                int[] column = {1, 2, 3, 1, 2, 3, 1, 2, 3};
                int[] row = {0, 0, 0, 1, 1, 1, 2, 2, 2};
                grid.add(button, column[i], row[i]);
            }

            for (int i = 0; i < 9; i++) {

                int finalI = i;
                Button button = buttons.get(i);
                button.setOnAction((e) -> {

                    System.out.println("1: " + buttons.size());
                    button.setGraphic(new ImageView(x));
                    button.setDisable(true);
                    button.setStyle("-fx-opacity: 1; -fx-background-color: transparent;");
                    player.setButtonClicked(finalI);
                    playersMoves.add(finalI);
                    removed.add(buttons.get(finalI));

                    if (playersMoves.containsAll(winnerSelector.getWinner1()) ||
                            playersMoves.containsAll(winnerSelector.getWinner2()) ||
                            playersMoves.containsAll(winnerSelector.getWinner3()) ||
                            playersMoves.containsAll(winnerSelector.getWinner4()) ||
                            playersMoves.containsAll(winnerSelector.getWinner5()) ||
                            playersMoves.containsAll(winnerSelector.getWinner6()) ||
                            playersMoves.containsAll(winnerSelector.getWinner7()) ||
                            playersMoves.containsAll(winnerSelector.getWinner8())) {

                        //grid.getChildren().add(winnerSelector.selectLine("winner1"));
                        status.setText("Winner is: \n" + player.getPlayerName() + ".");
                        removed.addAll(buttons);
                        for (int n=0; n<9; n++) {
                            buttons.get(n).setDisable(true);
                        }
                    }

                    if (removed.size() < 8 && removed.size() != 9) {

                        int rand = random.nextInt(9);
                        boolean randEquals = false;
                        while (!randEquals) {

                            if (removed.contains(rand)) {
                            //if (rand == player.getButtonClicked() || buttons.get(rand).isDisabled()) {
                                rand = random.nextInt(9);
                                computer.setButtonClicked(rand);
                            } else {
                                randEquals = true;
                            }
                        }

                        buttons.get(rand).setGraphic(new ImageView(o));
                        buttons.get(rand).setDisable(true);

                        computersMoves.add(rand);

                        removed.add(buttons.get(rand));


                        if (computersMoves.containsAll(winnerSelector.getWinner1()) ||
                                computersMoves.containsAll(winnerSelector.getWinner2()) ||
                                computersMoves.containsAll(winnerSelector.getWinner3()) ||
                                computersMoves.containsAll(winnerSelector.getWinner4()) ||
                                computersMoves.containsAll(winnerSelector.getWinner5()) ||
                                computersMoves.containsAll(winnerSelector.getWinner6()) ||
                                computersMoves.containsAll(winnerSelector.getWinner7()) ||
                                computersMoves.containsAll(winnerSelector.getWinner8())) {

                            status.setText("Winner is: \n" + computer.getPlayerName() + ".");
                            removed.addAll(buttons);
                            for (int n=0; n<9; n++) {
                                buttons.get(n).setDisable(true);
                            }
                        }
                        //end = true;
                    }
                });

                button.setStyle("-fx-background-color: transparent;");
            }

        Button newGame = new Button();
        newGame.setText("NEW GAME");
        newGame.setTranslateX(50);
        newGame.setTranslateY(50);
        newGame.setPrefSize(100, 19);
        newGame.setOnAction((e) -> {
            for (int i = 0; i < 9; i++) {
                buttons.get(i).setDisable(false);
                buttons.get(i).setGraphic(null);
                removed.removeAll(removed);
                playersMoves.removeAll(playersMoves);
                computersMoves.removeAll(computersMoves);
                status.setText("");
            }
        });

        Button exit = new Button();
        exit.setText("EXIT");
        exit.setTranslateX(50);
        exit.setTranslateY(0);
        exit.setPrefSize(100, 19);
        exit.setOnAction((e) -> {
            Platform.exit();
            System.exit(0);
        });

        grid.setAlignment(Pos.TOP_LEFT);
        grid.setPadding(new Insets(0, 200, 0, 368));
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setBackground(background);
        grid.setGridLinesVisible(true);
        grid.add(title, 4,0);
        grid.add(status, 4,1);
        grid.add(newGame, 4, 1 );
        grid.add(exit, 4, 2);

        Scene scene = new Scene(grid, 1412, 700, Color.LIGHTGRAY);

        primaryStage.setTitle("TicTacToe");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
*/