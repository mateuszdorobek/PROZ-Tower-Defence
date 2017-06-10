package controller;

import Program.Program;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import model.*;
import view.View;

import java.io.IOException;
import java.util.ArrayList;


public class GameController {
    public static Model model;
    public static View view;
    private static UpgradeController upgradeController;
    private Map map;
    private GameLoop gameLoop;
    public Stage primaryStage;

    @FXML
    private
    Pane PausedPane;
    @FXML
    private
    Canvas mainCanvas;
    @FXML
    Canvas secondCanvas;
    @FXML
    private
    TextField livesTextField;
    @FXML
    private
    TextField scoreTextField;
    @FXML
    private
    TextField cashTextField;
    @FXML
    private
    TextField costTextField;
    @FXML
    private
    Button pausedButton;
    @FXML
    private
    AnchorPane towersShopAnchorPane;
    @FXML
    AnchorPane leftPane;
    @FXML
    private
    AnchorPane mainAnchorPane;

    void setMapNumber(Integer mapNumber) throws IOException {
        model.loadMap(mapNumber);
        map = model.getMap();
    }

    void updateCoinsAndPoints() {
        scoreTextField.setText(String.valueOf(Model.currentPlayer.getPoints() + gameLoop.getGamePoints()));
        cashTextField.setText(String.valueOf(Model.currentPlayer.getCoins()));
    }

    public enum GameState {
        WIN, LOOSE
    }

    public void pressMenuButton() throws IOException {
        gameLoop.setPaused(true);
        PausedPane.toFront();
        PausedPane.setVisible(true);
    }

    void pressMenuButton(GameState state) throws IOException {
        if (state == GameState.WIN) {
            if (gameLoop != null)
                gameLoop.setPaused(true);
            pausedButton.setDisable(true);
            PausedPane.toFront();
            PausedPane.setVisible(true);
            View.gameWin(gameLoop.getGamePoints(), mainCanvas, PausedPane);
            Model.currentPlayer.missionCompleted(map.fileNum);
            Model.currentPlayer.setBasicCoins(Model.currentPlayer.getBasicCoins() + 400);
            Model.currentPlayer.setCoins(Model.currentPlayer.getBasicCoins());
            Model.currentPlayer.setHealthPoints(Model.currentPlayer.getHealthPoints());
        } else if (state == GameState.LOOSE) {
            if (gameLoop != null)
                gameLoop.setPaused(true);
            pausedButton.setDisable(true);
            PausedPane.toFront();
            PausedPane.setVisible(true);
            View.gameLoose(mainCanvas, PausedPane);
            Model.currentPlayer.setCoins(Model.currentPlayer.getBasicCoins());
            Model.currentPlayer.setHealthPoints(Model.currentPlayer.getHealthPoints());
        }
    }

    public void pressQuitButton() throws IOException {
        if (gameLoop != null)
            gameLoop.endGame();
        upgradeController = view.setUpgradeView();
        Program.setUpgradeController(upgradeController);
    }

    public void resumeButtonClicked(ActionEvent actionEvent) {
        if (gameLoop != null) {
            gameLoop.setPaused(false);
            synchronized (gameLoop.getMonitor()) {
                gameLoop.getMonitor().notifyAll();
            }
        }
        PausedPane.setVisible(!PausedPane.isVisible());
    }

    public void restartButtonClicked(ActionEvent actionEvent) throws IOException {
        gameLoop.restartGame();
        PausedPane.setVisible(false);
        gameLoopStart();
    }

    void drawMap() {
        View.drawMap(model.getMap(), mainCanvas);
        setShop();
//        xd.setLayoutX(Map.startXPosition);
//        xd.setLayoutY(Map.startYPosition);
//
//        Path path = new Path(model.getMap());
//
//        path.generatePolyline();
//        Polyline polyline = new Polyline();
//        polyline = path.getPolyline();
//
//        PathTransition transition = new PathTransition();
//        transition.setNode(xd);
//        transition.setDuration(Duration.seconds(3));
//        transition.setPath(polyline);
//        transition.setCycleCount(PathTransition.INDEFINITE);
//        transition.play();
    }

    public void gameLoopStart() {
        gameLoop = new GameLoop(mainCanvas, model, this);
        pausedButton.setDisable(false);
        gameLoop.startGame();
    }

    private void setShop() {
        ArrayList<Tower> towerList = Model.getTowerList();
        view.setTowerShop(towersShopAnchorPane, mainAnchorPane, towerList, map, mainCanvas, costTextField, livesTextField, scoreTextField, cashTextField);
    }

    void updateHealthPoints() {
        livesTextField.setText(String.valueOf(Model.currentPlayer.getCurrentHealthPoints()));
    }
}
