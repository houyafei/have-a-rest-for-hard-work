package com.eye;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.AskTimeoutException;
import com.eye.constant.Constants;
import com.eye.msgInterface.MessageInterface;
import com.eye.myclock.ClockActor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static akka.pattern.Patterns.gracefulStop;
import static com.eye.constant.Constants.setAutoStart;

public class Main extends Application {

    final private ActorSystem system = ActorSystem.create("hyf");
    private ActorRef clockActorRef;

    private List<Stage> stages = new ArrayList<>();

    private ComboBox<String> asd = new ComboBox<>();

    private CheckBox lockScreenCheck;

    @Override
    public void start(Stage primaryStage) {
//        String psth = "C:\\Users\\houya\\Pictures\\FLAMING MOUNTAIN.JPG";
//        Background background = new Background(new BackgroundImage(new Image("file:/"+psth),
//                null, null, BackgroundPosition.CENTER, BackgroundSize.DEFAULT));

        GridPane root = new GridPane();
        primaryStage.setTitle("Take care yourself");

        settingUI(primaryStage, root);

        ImplCmdMessageInterface();

//        root.setBackground(background);

        primaryStage.setOnCloseRequest(existWin());
        primaryStage.setIconified(true);
        primaryStage.getIcons().add(new Image("/images/ico2.png"));
//        primaryStage
        primaryStage.show();
    }

    private void settingUI(Stage primaryStage, GridPane root) {
        root.setVgap(20);
        root.setHgap(10);
        root.setAlignment(Pos.TOP_CENTER);

        Label label1 = new Label("\u8bbe\u7f6e\u5de5\u4f5c\u65f6\u957f\u0028\u5206\u949f\u0029"); //工作时长
        TextField textFieldWorkInterval = new TextField("" + (Constants.WORKING_INTERVAL_SECONDS) / 60);

        Label label2 = new Label("\u8bbe\u7f6e\u4f11\u606f\u65f6\u957f\u0028\u5206\u949f\u0029"); //休息时长
        TextField textFieldRestInterval = new TextField("" + (Constants.REST_INTERVAL_SECONDS) / 60);

        CheckBox autoStartCheck = new CheckBox("开机自启动");
        autoStartCheck.setSelected(Constants.IS_AUTO_START);
        autoStartCheck.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            if (new_val != Constants.IS_AUTO_START) {
                Constants.IS_AUTO_START = new_val;
                setAutoStart();
            }
        });


        lockScreenCheck = new CheckBox("持续锁屏");
        lockScreenCheck.setSelected(Constants.IS_LOCK_SCREEN);
        lockScreenCheck.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            if (new_val) {
                Constants.IS_LOCK_SCREEN = new_val;
//                setAutoStart();
                lockScreen();
                system.stop(clockActorRef);
                System.out.println("lock screen " + new_val);
            } else {
                Constants.IS_LOCK_SCREEN = new_val;
                restartClockActor();
                System.out.println("unlock screen " + new_val);
            }
        });


        Button saveConfig = new Button("保存时间设置");//保存修改
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

        ObservableList<String> data = FXCollections.observableArrayList();
        Label label3 = new Label("添加激励语言");
        TextField luckyword = new TextField("less is more");
        Button btnAdd = new Button("+");
        btnAdd.setOnMouseClicked(event -> {
            String encourageWord = luckyword.getText();
            System.out.println("--->" + encourageWord);
            boolean isSaved = false;
            for (String str : Constants.getEncourageMsgs()) {
                if (str.equals(encourageWord.trim())) {
                    System.out.println("" + isSaved);
                    isSaved = true;
                    break;
                }
            }
            if (!isSaved) {
                Constants.ENCOURAGE_MSGS += ";" + encourageWord;
                Constants.saveProperties(Constants.ENCOURAGE_MSG_KEY, Constants.ENCOURAGE_MSGS);
                data.clear();
                data.addAll(Constants.getEncourageMsgs());

            }


        });

        Label label4 = new Label("选择激励语言"); //休息时长
        asd.setItems(data);
        asd.setPromptText("选择激励语言->");
        data.addAll(Constants.getEncourageMsgs());
        asd.valueProperty().addListener((observable, oldValue, newValue) -> {
//            System.out.println("---->12"+newValue);
            if (newValue != null) {
                Constants.ENCOURAGE_MSG = newValue;
            }
        });

        Label setting = new Label("设置");
        setting.setFont(Font.font(20));
        GridPane.setHalignment(setting, HPos.CENTER);


        root.add(setting, 0, 0, 2, 1);
        root.add(label1, 0, 1);
        root.add(textFieldWorkInterval, 1, 1);
        root.add(label2, 0, 2);
        root.add(textFieldRestInterval, 1, 2);
        root.add(label3, 0, 3);
        root.add(luckyword, 1, 3);
        root.add(btnAdd, 2, 3);
        root.add(label4, 0, 4);
        root.add(asd, 1, 4);
        root.add(autoStartCheck, 0, 5);

        root.add(saveConfig, 1, 5);
        Separator separator = new Separator();
        separator.setMaxSize(450,2);
        root.add(separator, 0, 6,2,1);
        GridPane.setHalignment(lockScreenCheck, HPos.LEFT);
        lockScreenCheck.setFont(Font.font(20));
        root.add(lockScreenCheck, 0, 7,2,1);

        primaryStage.setScene(new Scene(root, 450, 350));
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
                lockScreen();
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

    private void lockScreen() {
        Platform.runLater(() -> {
            String backImagePath = Constants.getBackImagePath();
            Constants.updateSentence();
            for (Screen screen : Screen.getScreens()) {
                Rectangle2D bounds = screen.getVisualBounds();
                Stage stage1 = null;

                stage1 = createNewWin(bounds.getWidth(), bounds.getHeight(), backImagePath);

                stage1.setX(bounds.getMinX());
                stage1.setY(bounds.getMinY());
                stage1.setMaximized(true);
                stage1.initStyle(StageStyle.TRANSPARENT);
                stage1.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    int count = event.getClickCount();
                    if (count == 50) {
                        stages.forEach(Stage::close);
                        if (Constants.IS_LOCK_SCREEN) {
                            Constants.IS_LOCK_SCREEN = false;
                            lockScreenCheck.setSelected(Constants.IS_LOCK_SCREEN);
                        }
                    }
                });
                stages.add(stage1);
            }
            stages.forEach(Stage::show);

        });
    }

    private Stage createNewWin(double width, double height, String backImagePath)   {
        BackgroundSize backgroundSize = new BackgroundSize(width, height, true, true, false, true);
        Background background = new Background(new BackgroundImage(new Image(backImagePath),
                null, null, BackgroundPosition.CENTER, backgroundSize));
        Background labelBack = new Background(new BackgroundImage(new Image("/images/labelback2.png"), null, null, BackgroundPosition.CENTER, BackgroundSize.DEFAULT));

        Stage stage = new Stage();
        BorderPane root = new BorderPane();

        Label protectLabel = new Label("\n " + Constants.getRandomMsg() + "\n\n    ");
        protectLabel.setFont(Font.font("Timer New Roman", FontPosture.REGULAR, 23));
        protectLabel.setWrapText(true);
        protectLabel.setBackground(labelBack);
        protectLabel.setAlignment(Pos.CENTER);
        protectLabel.setTextFill(Color.rgb(232, 240, 230));
        protectLabel.setPrefWidth(750);
        protectLabel.setPrefHeight(200);
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