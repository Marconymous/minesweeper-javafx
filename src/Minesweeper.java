import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Minesweeper extends Application {
    private BorderPane root = new BorderPane();
    private GridPane fields = new GridPane();
    private boolean[][] bombs = new boolean[16][16];
    private Button[][] buttons = new Button[16][16];
    private String[][] neighbourBombs = new String[16][16];
    private char sel = 's';

    @Override
    public void start(Stage primaryStage) {
        HBox menu = new HBox();
        Button flag = new Button("\uD83C\uDFF4");
        Button pick = new Button("⛏");

        flag.setPrefSize(240, 30);
        flag.setOnAction(e -> {
            pick.setDisable(false);
            flag.setDisable(true);
            sel = 'f';
        });

        pick.setPrefSize(240, 30);
        pick.setDisable(true);
        pick.setOnAction(e -> {
            pick.setDisable(true);
            flag.setDisable(false);
            sel = 'p';
        });

        menu.getChildren().addAll(flag, pick);

        root.setTop(menu);

        generateButtons();

        Scene scene = new Scene(root, 470, 480);
        scene.getStylesheets().add("style.css");
        primaryStage.setTitle("Stop playing minesweeper and code!");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add((new Image(getClass().getResourceAsStream("/icon.png"))));
        primaryStage.show();
    }

    private boolean checkFinished() {
        for (int y = 0; y < bombs.length; y++) {
            for (int x = 0; x < bombs[y].length; x++) {
                if (bombs[y][x]) {
                    if (!buttons[y][x].getText().equals("\uD83C\uDFF4")) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void generateButtons() {
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                bombs[y][x] = (int) (Math.random() * 15) == 6;

                Button b = new Button();
                buttons[y][x] = b;

                b.setPrefSize(30, 30);
                int finalX = x;
                int finalY = y;
                b.setOnAction(e -> {
                    b.setPadding(new Insets(0));
                    if (sel == 'f') {
                        if (buttons[finalY][finalX].getText().equals(""))
                            buttons[finalY][finalX].setText("\uD83C\uDFF4");
                        else buttons[finalY][finalX].setText("");
                    }
                    else{
                        if (bombs[finalY][finalX]) {
                            if (sel == 'p') {
                                lostGame();
                            }
                        } else {
                            b.setText(neighbourBombs[finalY][finalX]);
                            showNeighbors(finalX, finalY);
                        }
                        b.setDisable(true);
                    }
                    if (checkFinished()) {
                        showFinishDialog();
                    }
                });
                fields.add(b, x, y);
            }
        }
        root.setCenter(fields);

        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                if (bombs[y][x]) {
                    neighbourBombs[y][x] = "\uD83D\uDCA3";
                } else {
                    int count = getNeighbourCount(x, y);
                    if (count == 0) {
                        neighbourBombs[y][x] = "";
                    } else {
                        neighbourBombs[y][x] = count + "";
                    }
                }
            }
        }
    }

    private void showFinishDialog() {
        for (int y = 0; y < buttons.length; y++) {
            for (int x = 0; x < buttons[y].length; x++) {
                buttons[y][x].setDisable(true);
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("You have won!");
        alert.setHeaderText(null);
        alert.setContentText("You found all bombs in the grid!");
        alert.showAndWait();
    }

    private void showGameLostDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("You have lost!");
        alert.setHeaderText(null);
        alert.setContentText("You accidentally stepped on a bomb and it went BRRRRR!");
        alert.showAndWait();
    }

    private void lostGame() {
        for (int y = 0; y < buttons.length; y++) {
            for (int x = 0; x < buttons[y].length; x++) {
                buttons[y][x].setDisable(true);
                buttons[y][x].setText(neighbourBombs[y][x]);
            }
        }
        showGameLostDialog();
    }

    private int getNeighbourCount(int x, int y) {
        int aliveNeighbours = 0;
        for (int c = y - 1; c <= (y + 1); c += 1) {
            for (int r = x - 1; r <= (x + 1); r += 1) {
                if (!((c == y) && (r == x))) {
                    if (indexWithinGrid(c, r)) {
                        if (bombs[c][r]) aliveNeighbours++;
                    }
                }
            }
        }
        return aliveNeighbours;
    }

    private boolean indexWithinGrid(int colNum, int rowNum) {
        if ((colNum < 0) || (rowNum < 0)) {
            return false;
        }
        if ((colNum >= 16) || (rowNum >= 16)) {
            return false;
        }
        return true;
    }

    private void showNeighbors(int x, int y) {
        for (int c = y - 1; c <= (y + 1); c += 1) {
            for (int r = x - 1; r <= (x + 1); r += 1) {
                if (!((c == y) && (r == x))) {
                    if (indexWithinGrid(c, r)) {
                        if (!buttons[c][r].isDisable() && neighbourBombs[c][r].equals("")) {
                            buttons[c][r].setText(neighbourBombs[c][r]);
                            buttons[c][r].setDisable(true);
                            showNeighbors(r, c);
                        } else if (buttons[c][r].getText().equalsIgnoreCase("\uD83C\uDFF4"));

                        else if (!buttons[c][r].isDisable() && (neighbourBombs[c][r].equals("1") || neighbourBombs[c][r].equals("2") || neighbourBombs[c][r].equals("3") || neighbourBombs[c][r].equals("4") || neighbourBombs[c][r].equals("5"))) {
                            buttons[c][r].setText(neighbourBombs[c][r]);
                            buttons[c][r].setDisable(true);
                        }
                    }
                }
            }
        }
    }
}
