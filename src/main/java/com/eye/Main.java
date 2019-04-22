package com.eye;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import static akka.pattern.Patterns.gracefulStop;

import akka.pattern.AskTimeoutException;

import com.eye.constant.Constants;
import com.eye.msgInterface.MessageInterface;

import com.eye.myclock.ClockActor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Main extends Application {

    final private ActorSystem system = ActorSystem.create("hyf");
    private ActorRef clockActorRef;

    private List<Stage> stages = new ArrayList<>();


    @Override
    public void start(Stage primaryStage) {
        GridPane root = new GridPane();
        primaryStage.setTitle("Take care yourself");

        settingUI(primaryStage, root);

        ImplCmdMessageInterface();

        primaryStage.setOnCloseRequest(existWin());
        primaryStage.setIconified(true);
        primaryStage.getIcons().add(new Image("/images/ico2.png"));
        primaryStage.show();
    }

    private void settingUI(Stage primaryStage, GridPane root) {
        root.setVgap(20);
        root.setHgap(10);
        root.setAlignment(Pos.TOP_CENTER);
        Label label1 = new Label("\u8bbe\u7f6e\u5de5\u4f5c\u65f6\u957f\u0028\u5206\u949f\u0029"); //工作时长
        TextField textFieldWorkInterval = new TextField(""+(Constants.WORKING_INTERVAL_SECONDS)/60);

        Label label2 = new Label("\u8bbe\u7f6e\u4f11\u606f\u65f6\u957f\u0028\u5206\u949f\u0029"); //休息时长
        TextField textFieldRestInterval = new TextField(""+(Constants.REST_INTERVAL_SECONDS)/60);
        Button saveConfig = new Button("Save");//保存修改
        saveConfig.setOnMouseClicked(event -> {
            String workStr = textFieldWorkInterval.getText();
            String restStr = textFieldRestInterval.getText();
            if (!workStr.matches("^[0-9]{1,2}$")
                    || !restStr.matches("^[0-9]{1,2}$")
                    || Integer.valueOf(workStr) <= 0
                    || Integer.valueOf(restStr) <= 0
                    || Integer.valueOf(workStr) <= Integer.valueOf(restStr)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.titleProperty().set("Note!!");
                alert.headerTextProperty().set("\u65f6\u95f4\u8bbe\u7f6e\u4e0d\u5408\u7406\uff0c\u6216\u8005\u8f93\u5165\u4e86\u975e\u6cd5\u5b57\u7b26");//"时间设置不合理，或者输入了非法字符"
                alert.showAndWait();
            } else {
                Constants.WORKING_INTERVAL_SECONDS = Integer.valueOf(workStr) * 60;
                Constants.REST_INTERVAL_SECONDS = Integer.valueOf(restStr) * 60;
                Constants.saveProperties(Constants.WORKING_INTERVAL_SECONDS_KEY, Constants.WORKING_INTERVAL_SECONDS);
                Constants.saveProperties(Constants.REST_INTERVAL_SECONDS_KEY, Constants.REST_INTERVAL_SECONDS);
                restartClockActor();
                primaryStage.setIconified(true);
            }
        });
        Label setting = new Label("Setting");
        setting.setFont(Font.font(20));
        GridPane.setHalignment(setting, HPos.CENTER);
        root.add(setting, 0, 0, 2, 1);
        root.add(label1, 0, 1);
        root.add(textFieldWorkInterval, 1, 1);
        root.add(label2, 0, 2);
        root.add(textFieldRestInterval, 1, 2);
        root.add(saveConfig, 1, 3);

        primaryStage.setScene(new Scene(root, 450, 200));
    }

    private void restartClockActor() {
        try {
            CompletionStage<Boolean> stopped =
                    gracefulStop(clockActorRef, Duration.ofSeconds(5));
            stopped.toCompletableFuture().get(6, TimeUnit.SECONDS);
            // the actor has been stopped
        } catch (AskTimeoutException e) {
            // the actor wasn't stopped within 5 seconds
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

        ImplCmdMessageInterface();
    }

    private EventHandler<WindowEvent> existWin() {
        return (va) -> {
            System.out.println(" i will be closed");
            system.stop(clockActorRef);
            System.exit(1);

        };
    }

    private void ImplCmdMessageInterface() {
        MessageInterface cmdMessage = new MessageInterface() {
            @Override
            public void openWin() {
                Platform.runLater(() -> {
                    for (Screen screen : Screen.getScreens()) {
                        Rectangle2D bounds = screen.getVisualBounds();
                        Stage stage1 = createNewWin();
                        stage1.setX(bounds.getMinX());
                        stage1.setY(bounds.getMinY());
                        stage1.setMaximized(true);
                        stage1.initStyle(StageStyle.TRANSPARENT);
                        stage1.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                            int count = event.getClickCount();
                            if (count == 5) {
                                stages.forEach(Stage::close);
                            }
                        });
                        stages.add(stage1);
                    }
                    stages.forEach(Stage::show);

                });
            }

            @Override
            public void closeWin() {
                Platform.runLater(() -> {
                    stages.forEach(Stage::close);
                    stages.clear();
                });
            }
        };
        clockActorRef = system.actorOf(Props.create(ClockActor.class, cmdMessage), "clockActor");
    }

    private Stage createNewWin() {
        Background background = new Background(new BackgroundImage(new Image(Constants.BACK_IMAGE), null, null, BackgroundPosition.CENTER, BackgroundSize.DEFAULT));
        Background labelBack = new Background(new BackgroundImage(new Image("/images/labelback2.png"), null, null, BackgroundPosition.CENTER, BackgroundSize.DEFAULT));

        Stage stage = new Stage();
        BorderPane root = new BorderPane();
        Label protectLabel = new Label("\n " + Constants.ENCOURAGE_MSG + "\n\n    ");
        protectLabel.setFont(Font.font("Timer New Roman", FontPosture.ITALIC, 25));
        protectLabel.setBackground(labelBack);
        protectLabel.setAlignment(Pos.CENTER);
        protectLabel.setTextFill(Color.rgb(232, 240, 230));
        protectLabel.setPrefWidth(500);
        protectLabel.setPrefHeight(160);
        root.setCenter(protectLabel);
        root.setBackground(background);
        stage.setTitle("Have a rest");
        stage.setScene(new Scene(root));
        stage.setAlwaysOnTop(true);

        return stage;
    }


    public static void main(String[] args) {
        launch(args);

    }
}