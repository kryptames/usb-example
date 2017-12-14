package takro;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;

import org.usb4java.Device;

import practicum.McuBoard;
import practicum.McuWithPeriBoard;
 
public class TakroApp extends Application {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private TakroWorld world;
    private Canvas canvas;
    private McuWithPeriBoard[] controllers;
    
    public static void main(String[] args) {
        System.out.println("** Initializing USB");
        McuBoard.initUsb();
        launch(args);
        System.out.println("** Cleaning up USB");
        McuBoard.cleanupUsb();
    }
    private void initHardware() {
        Device[] devices = McuBoard.findBoards();
        System.out.format("** Found %d practicum board(s)\n", devices.length);
        controllers = new McuWithPeriBoard[devices.length];
        for (int i=0; i<devices.length; i++) {
            controllers[i] = new McuWithPeriBoard(devices[i]);
            System.out.format("-- Board #%d: Manufacturer: %s Product: %s\n",
                    i, controllers[i].getManufacturer(), controllers[i].getProduct());
        }        
    }
    @Override
    public void init() {
        initHardware();
        if (controllers.length == 0) {
            System.out.println("** Please attach a practicum board");
            System.out.println("** Cleaning up USB");
            McuBoard.cleanupUsb();
            Platform.exit();
            System.exit(0);
        }
    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Takro");
        primaryStage.setFullScreen(false);
        primaryStage.setResizable(false);
        Group root = new Group();
        Scene scene = new Scene(root);
        this.canvas = new Canvas(WIDTH, HEIGHT);
        this.world = new TakroWorld(canvas);
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Calibrating Controller");
        alert.setHeaderText(null);
        alert.setContentText("Put the peripheral board facing DOWN, then press OK");
        alert.showAndWait();
        int minValue = controllers[0].getLight();
        alert.setContentText("Put the peripheral board facing UP, then press OK");
        alert.showAndWait();
        int maxValue = controllers[0].getLight();
        
        this.world.addPlayer(controllers[0], minValue, maxValue, "darkred");
        
        root.getChildren().add(canvas);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        scene.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                Platform.exit();
            }
            else if (event.getCode() == KeyCode.SPACE) {
                world.serveBall();
            }
        });

        AnimationTimer timer = new AnimationTimer() {
            long prev = 0;
            @Override
            public void handle(long now) {
                if (prev > 0) {
                    world.update((now-prev)/1e9f);
                    world.draw();
                }
                prev = now;
            }
        };
        timer.start();
    }
}
