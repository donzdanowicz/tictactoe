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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class TicTacToe extends Application {
    private final Image board = new Image("file:src/main/resources/board.jpg");
    private final Image x = new Image("file:src/main/resources/x.jpg");
    private final Image o = new Image("file:src/main/resources/o.jpg");
    private final Random random = new Random();
    private int difficulty = 1;
    private final String X = "X";
    private final String O = "O";
    private static int rounds = 0;
    private static int wins = 0;
    private StackPane root = new StackPane();

    private GridPane grid = new GridPane();
    private Canvas canvas = new Canvas(530, 530);
    private GraphicsContext gc = canvas.getGraphicsContext2D();
    private Button[][] button = new Button[3][3];
    private Button fireButton = new Button();
    private List<Button> buttons = new LinkedList<>();
    private List<Button> removed = new LinkedList<>();
    private Label status = new Label();
    private Label title = new Label();
    private Label roundsAndWinsCount = new Label();
    private Label inputPlayerName = new Label();
    private TextField playerName = new TextField();
    private Button submit = new Button();
    private Button newGame = new Button();
    private Button exit = new Button();
    private Button easy = new Button();
    private Button medium = new Button();
    private Button hard = new Button();
    private Button save = new Button();
    private Button load = new Button();
    private Button showRanking = new Button();

    private GridPane ranking = new GridPane();
    private Label rankingTitle = new Label();
    private Label rankingFieldNames = new Label();
    private Label rankingFieldKeys = new Label();
    private Label rankingFieldValues = new Label();
    private Button rankingClose = new Button();
    private Button rankingClear = new Button();

    private Player player = new Player("Player");
    private Player computer = new Player("Computer");

    private File savedHashMaps = new File("ranking.list");
    private Map<String, Double> map = new HashMap<>();
    private File savedStateHashMaps = new File("gameState.txt");
    private Map<Integer, String> gameStateMap = new HashMap<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(board, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);

        status.setTranslateX(50);
        status.setTranslateY(30);
        Font statusFont = new Font(12);
        status.setFont(statusFont);
        status.setPrefWidth(200);

        title.setTranslateX(120);
        title.setText("T  I C \nT A C \nT O E");
        Font titleFont = new Font(45);
        title.setFont(titleFont);
        title.setTextFill(Color.BLUE);

        roundsAndWinsCount.setTranslateX(50);
        roundsAndWinsCount.setText("Rounds: " + rounds + ". Wins: " + wins + ". Result: " + result() + "%.");
        Font roundsAndWinsFont = new Font(12);
        roundsAndWinsCount.setFont(roundsAndWinsFont);
        roundsAndWinsCount.setPrefWidth(200);

        inputPlayerName.setTranslateX(50);
        inputPlayerName.setTranslateY(170);
        inputPlayerName.setText("Your name: " + player.getPlayerName());

        playerName.setTranslateX(50);
        playerName.setTranslateY(200);
        playerName.setText("Input your name.");
        playerName.setPrefWidth(150);
        playerName.setOnMouseClicked((e) ->{
            playerName.clear();
        });

        loadMap();

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

                    if (whoWins(X) || whoWins(O) || isFull()) {
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

        submit.setText("Submit");
        submit.setTranslateX(200);
        submit.setTranslateY(200);
        submit.setPrefSize(80,19);
        submit.setOnAction((e) -> {
            player.setPlayerName(playerName.getText());
            rounds = 0;
            wins = 0;
            roundsAndWinsCount.setText("Rounds: " + rounds + ". Wins: " + wins + ". Result: " + result() + "%.");
            playerName.setText("");
            inputPlayerName.setText("Your name: " + player.getPlayerName());

        });

        newGame.setText("NEW GAME");
        newGame.setTranslateX(20);
        newGame.setTranslateY(300);
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
        exit.setTranslateY(50);
        exit.setPrefSize(100, 19);
        exit.setOnAction((e) -> {
            Platform.exit();
            System.exit(0);
        });

        easy.setText("EASY");
        easy.setTranslateX(0);
        easy.setTranslateY(300);
        easy.setPrefSize(80, 19);
        easy.setOnAction((e) -> {
            difficulty = 1;
            easy.setText("EASY");
            medium.setText("Medium");
            hard.setText("Hard");
        });

        medium.setText("Medium");
        medium.setTranslateX(0);
        medium.setTranslateY(100);
        medium.setPrefSize(80, 19);
        medium.setOnAction((e) -> {
            difficulty = 2;
            medium.setText("MEDIUM");
            easy.setText("Easy");
            hard.setText("Hard");
        });

        hard.setText("Hard");
        hard.setTranslateX(0);
        hard.setTranslateY(135);
        hard.setPrefSize(80, 19);
        hard.setOnAction((e) -> {
            difficulty = 3;
            hard.setText("HARD");
            easy.setText("Easy");
            medium.setText("Medium");
        });

        save.setText("SAVE");
        save.setTranslateX(20);
        save.setTranslateY(150);
        save.setPrefSize(100,19);
        save.setOnAction((e) -> {
            saveGameStateMap();
            for(int i = 0; i < 9; i++) {
                gameStateMap.put(i, buttons.get(i).getId());
            }
        });

        load.setText("LOAD");
        load.setTranslateX(20);
        load.setTranslateY(180);
        load.setPrefSize(100,19);
        load.setOnAction((e) -> {
            newGame.fire();
            loadGameStateMap();
            for(int i = 0; i < 9; i++) {
                buttons.get(i).setId(gameStateMap.get(i));

                if (X.equals(buttons.get(i).getId())) {

                    buttons.get(i).setGraphic(new ImageView(x));
                    buttons.get(i).setDisable(true);
                    removed.add(buttons.get(i));

                } else if (O.equals(buttons.get(i).getId())) {
                    buttons.get(i).setGraphic(new ImageView(o));
                    buttons.get(i).setDisable(true);
                    removed.add(buttons.get(i));
                }
            }
            saveGameStateMap();
        });

        showRanking.setText("RANKING");
        showRanking.setTranslateX(20);
        showRanking.setTranslateY(210);
        showRanking.setPrefSize(100,19);
        showRanking.setOnAction((e) -> {
            rankingFieldKeys.setText(convertKeyWithStream(map));
            rankingFieldValues.setText(convertValueWithStream(map));
            ranking.toFront();
        });

        grid.setAlignment(Pos.TOP_LEFT);
        grid.setPadding(new Insets(0, 0, 0, 335));
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setBackground(background);
        grid.add(title, 4, 0);
        grid.add(inputPlayerName,4,0);
        grid.add(playerName, 4, 0);
        grid.add(submit,4,0);
        grid.add(status, 4, 1);
        grid.add(roundsAndWinsCount, 4, 1);
        grid.add(newGame, 4, 0);
        grid.add(exit, 4, 2);
        grid.add(easy, 5,0);
        grid.add(medium, 5,1);
        grid.add(hard, 5,1);
        grid.add(save,4,1);
        grid.add(load, 4,1);
        grid.add(showRanking,4,1);

        rankingTitle.setText("RANKING");
        rankingTitle.setTranslateY(50);
        Font rankingFont = new Font(40);
        rankingTitle.setFont(rankingFont);

        rankingFieldNames.setText("Player Name:               Result:");
        rankingFieldNames.setTranslateY(50);

        rankingFieldKeys.setPadding(new Insets(100,0,0,0));
        rankingFieldKeys.setTranslateY(80);
        rankingFieldKeys.setTranslateX(0);

        rankingFieldValues.setPadding(new Insets(100,0,0,0));
        rankingFieldValues.setTranslateY(80);
        rankingFieldValues.setTranslateX(130);

        rankingClose.setText("CLOSE");
        rankingClose.setTranslateY(350);
        rankingClose.setOnAction((e) -> {
            ranking.toBack();
        });

        rankingClear.setText("CLEAR RANKING");
        rankingClear.setTranslateY(350);
        rankingClear.setTranslateX(100);
        rankingClear.setOnAction((e) -> {
            map.clear();
            rankingFieldKeys.setText(convertKeyWithStream(map));
            rankingFieldValues.setText(convertValueWithStream(map));
        });

        ranking.setAlignment(Pos.TOP_CENTER);
        ranking.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        ranking.add(rankingTitle,1,0);
        ranking.add(rankingFieldNames,1,1);
        ranking.add(rankingFieldKeys,1,1);
        ranking.add(rankingFieldValues,1,1);
        ranking.add(rankingClose, 1,2);
        ranking.add(rankingClear, 1,2);

        root.getChildren().addAll(ranking, canvas, grid);
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

            if (whoWins(X) || whoWins(O) || isFull()) {
                canvas.toFront();
                endOfGame();
            }
        }
    }

    public Button computerAIHard() {
        for (int i = 0; i < 3; i++) {
                if (O.equals(button[i][0].getId()) && O.equals(button[i][1].getId()) && !(removed.contains(button[i][2]))) {
                    return button[i][2];
                } else if (O.equals(button[i][1].getId()) && O.equals(button[i][2].getId()) && !(removed.contains(button[i][0]))) {
                    return button[i][0];
                } else if (O.equals(button[i][0].getId()) && O.equals(button[i][2].getId()) && !(removed.contains(button[i][1]))) {
                    return button[i][1];

                } else if (O.equals(button[0][i].getId()) && O.equals(button[1][i].getId()) && !(removed.contains(button[2][i]))) {
                    return button[2][i];
                } else if (O.equals(button[1][i].getId()) && O.equals(button[2][i].getId()) && !(removed.contains(button[0][i]))) {
                    return button[0][i];
                } else if (O.equals(button[0][i].getId()) && O.equals(button[2][i].getId()) && !(removed.contains(button[1][i]))) {
                    return button[1][i];

                } else if (X.equals(button[i][0].getId()) && X.equals(button[i][1].getId()) && !(removed.contains(button[i][2]))) {
                    return button[i][2];
                } else if (X.equals(button[i][1].getId()) && X.equals(button[i][2].getId()) && !(removed.contains(button[i][0]))) {
                    return button[i][0];
                } else if (X.equals(button[i][0].getId()) && X.equals(button[i][2].getId()) && !(removed.contains(button[i][1]))) {
                    return button[i][1];

                } else if (X.equals(button[0][i].getId()) && X.equals(button[1][i].getId()) && !(removed.contains(button[2][i]))) {
                    return button[2][i];
                } else if (X.equals(button[1][i].getId()) && X.equals(button[2][i].getId()) && !(removed.contains(button[0][i]))) {
                    return button[0][i];
                } else if (X.equals(button[0][i].getId()) && X.equals(button[2][i].getId()) && !(removed.contains(button[1][i]))) {
                    return button[1][i];

                } else if (((O.equals(button[0][0].getId()) && O.equals(button[2][2].getId()))
                        || (O.equals(button[0][2].getId()) && O.equals(button[2][0].getId()))
                        || (X.equals(button[0][0].getId()) && X.equals(button[2][2].getId()))
                        || (X.equals(button[0][2].getId()) && X.equals(button[2][0].getId())))
                        && (!(removed.contains(button[1][1])))) {
                    return button[1][1];
                }
            }
        return computerAIMedium();
    }

    public Button computerAIMedium() {
        if (((O.equals(button[0][0].getId()) && O.equals(button[1][1].getId()))
            || (X.equals(button[0][0].getId()) && X.equals(button[1][1].getId())))
            && !(removed.contains(button[2][2]))) {
            return button[2][2];

        } else if (((O.equals(button[1][1].getId()) && O.equals(button[2][2].getId()))
            || (X.equals(button[1][1].getId()) && X.equals(button[2][2].getId())))
            && !(removed.contains(button[0][0]))) {
            return button[0][0];

        } else if (((O.equals(button[0][2].getId()) && O.equals(button[1][1].getId()))
            || (X.equals(button[0][2].getId()) && X.equals(button[1][1].getId())))
            && !(removed.contains(button[2][0]))) {
            return button[2][0];

        } else if (((O.equals(button[1][1].getId()) && O.equals(button[2][0].getId()))
            || (X.equals(button[1][1].getId()) && X.equals(button[2][0].getId())))
            && !(removed.contains(button[0][2]))) {
            return button[0][2];

        } else if (!(removed.contains(button[1][1]))) {
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
                drawLine(j * 240.0 + 27.0, 0.0, j * 240.0 + 27.0, 570.0);
                return true;
            }
        }

        if (button[0][0].getId().equals(id) && button[1][1].getId().equals(id) && button[2][2].getId().equals(id)) {
            drawLine(15.0, 15.0, 595.0, 590.0);
            return true;
        }

        if (button[2][0].getId().equals(id) && button[1][1].getId().equals(id) && button[0][2].getId().equals(id)) {
            drawLine(0.0, 530.0, 520.0, 15.0);
            return true;
        }
        return false;
    }

    public void endOfGame() {
        if (whoWins(X) || whoWins(O) || isFull()) {

            if (isFull()) {
                status.setText("Draw.");
            }

            removed.removeAll(buttons);

            for (int i = 0; i < 9; i++) {
                buttons.get(i).setDisable(true);
            }

            if (whoWins(X)) {
                wins++;
                status.setText(player.getPlayerName() + " wins!");
            } else if (whoWins (O)) {
                status.setText(computer.getPlayerName() + " wins!");
            }

            rounds++;
            map.put(player.getPlayerName(), result());
            saveMap();
            roundsAndWinsCount.setText("Rounds: " + rounds + ". Wins: " + wins + ". Result: " + result() + "%.");
            System.out.println(map);
        }
    }

    public boolean isFull() {
        return removed.size() == 9;
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

        removed.add(fireButton);

        fireButton.setId(O);
        fireButton.setDisable(true);
        fireButton.setGraphic(new ImageView(o));

        if (whoWins(X) || whoWins(O) || isFull()) {
            canvas.toFront();
            endOfGame();
        }
    }

    public void saveMap() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream (new FileOutputStream(savedHashMaps));
            oos.writeObject(map);
            oos.close();
        } catch (Exception e) {
            System.out.println("Wrong file.");
        }
    }

    public void loadMap() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(savedHashMaps));
            Object readMap = ois.readObject();
            if(readMap instanceof HashMap) {
                map.putAll((HashMap) readMap);
            }
            ois.close();
        } catch (Exception e) {
            System.out.println("Wrong file.");
        }
    }

    public void saveGameStateMap() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream (new FileOutputStream(savedStateHashMaps));
            oos.writeObject(gameStateMap);
            oos.close();
        } catch (Exception e) {
            System.out.println("Wrong file.");
        }
    }

    public void loadGameStateMap() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(savedStateHashMaps));
            Object readMap = ois.readObject();
            if(readMap instanceof HashMap) {
                gameStateMap.putAll((HashMap) readMap);
            }
            ois.close();
        } catch (Exception e) {
            System.out.println("Wrong file.");
        }
    }

    public double result() {
        return Math.round(((double) wins / (double) rounds) * 100);
    }

    public String convertKeyWithStream(Map<String, Double> map) {
        return map.keySet().stream()
                .map(key -> key + "")
                .collect(Collectors.joining("\n", "", ""));
    }

    public String convertValueWithStream(Map<String, Double> map) {
        return map.keySet().stream()
                .map(key -> map.get(key) + "%")
                .collect(Collectors.joining("\n", "", ""));
    }
}